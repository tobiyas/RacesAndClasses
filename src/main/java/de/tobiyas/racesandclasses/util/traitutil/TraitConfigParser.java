/*******************************************************************************
 * Copyright 2014 Tobias Welther
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.tobiyas.racesandclasses.util.traitutil;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.RemoveSuperConfigField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.util.config.YAMLConfigExtended;

public class TraitConfigParser {

	public static void configureTraitFromYAML(YamlConfiguration config, String traitPath, Trait trait) throws TraitConfigurationFailedException{
		TraitConfiguration configurationMap = new TraitConfiguration();
		
		try{
			ConfigurationSection traitConfig = config.getConfigurationSection(traitPath);
			for(String pathEntry : traitConfig.getKeys(true)){
				Object value = traitConfig.get(pathEntry);
				
				//Only use the non null values.
				if(value != null){
					configurationMap.put(pathEntry, value);
				}
			}
			
			List<TraitConfigurationField> annotationList = getAllTraitConfigFieldsOfTrait(trait);
			List<RemoveSuperConfigField> removedFields = getAllTraitRemoveFieldsOfTrait(trait);
			
			for(TraitConfigurationField field : annotationList){
				boolean optional = field.optional();
				boolean isPresent = configurationMap.containsKey(field.fieldName());
				boolean isRemovedField = false;
				
				for(RemoveSuperConfigField removedField : removedFields){
					if(removedField.name().equalsIgnoreCase(field.fieldName())){
						isRemovedField = true;
						break;
					}
				}
				
				if(optional && !isPresent){
					continue;
				}
				
				//We don't check any removed fields.
				if(isRemovedField){
					continue;
				}

				if(!optional && !isPresent){
					throw new TraitConfigurationFailedException("Field: '" + traitPath + "." + field.fieldName() + 
							"' not found in ConfigTotal for Trait: " + trait.getName());
				}
				
				Class<?> classToExpect = field.classToExpect();

				Object toCheck = configurationMap.get(field.fieldName());
				if(toCheck == null){
					throw new TraitConfigurationFailedException("Field: '" + traitPath + "." + field.fieldName() + 
							"' not found in Config for Trait: " + trait.getName() + " in Holder: %HOLDER%" +
							". Wanted a " + classToExpect.getCanonicalName() + " But found NOTHING.");
				}
				
				if(classToExpect == Integer.class){
					try{
						if(toCheck instanceof Integer){
							continue;
						}
						
						int value = Integer.parseInt(toCheck.toString());
						configurationMap.put(field.fieldName(), value);
						continue;
					}catch(NumberFormatException exp){
						throw new TraitConfigurationFailedException("Field: '" + traitPath + "." + field.fieldName() + 
								"' not found in ConfigTotal for Trait: " + trait.getName() + 
								". Found a " + toCheck.getClass().getCanonicalName() + " but wanted a " + classToExpect.getCanonicalName());
					}
				}
				
				if(classToExpect == String.class){
					try{
						String value = YAMLConfigExtended.replaceUmlauts(toCheck.toString());
						configurationMap.put(field.fieldName(), value);
						continue;
					}catch(Exception exp){
						throw new TraitConfigurationFailedException("Field: '" + traitPath + "." + field.fieldName() + 
								"' not found in ConfigTotal for Trait: " + trait.getName() + 
								". Found a " + toCheck.getClass().getCanonicalName() + " but wanted a " + classToExpect.getCanonicalName());
					}
				}

				if(classToExpect == Double.class){
					try{
						if(toCheck instanceof Double){
							continue;
						}
						
						if(toCheck instanceof Integer){
							configurationMap.put(field.fieldName(), (double) ((Integer)toCheck));
							continue;
						}
						
						double value = Double.MIN_VALUE;
						try{
							value = Double.parseDouble(toCheck.toString());
						}catch(NumberFormatException exp){
							value = Integer.parseInt(toCheck.toString());
						}
						
						configurationMap.put(field.fieldName(), value);
						continue;
					}catch(NumberFormatException exp){
						throw new TraitConfigurationFailedException("Field: '" + traitPath + "." + field.fieldName() + 
								"' not found in ConfigTotal for Trait: " + trait.getName() + 
								". Found a " + toCheck.getClass().getCanonicalName() + " but wanted a " + classToExpect.getCanonicalName());
					}
				}
				
				if(classToExpect == Boolean.class){
					try{
						if(toCheck instanceof Boolean){
							continue;
						}
						
						boolean value = Boolean.parseBoolean(toCheck.toString());
						configurationMap.put(field.fieldName(), value);
						continue;
					}catch(NumberFormatException exp){
						throw new TraitConfigurationFailedException("Field: '" + traitPath + "." + field.fieldName() + 
								"' not found in ConfigTotal for Trait: " + trait.getName() + 
								". Found a " + toCheck.getClass().getCanonicalName() + " but wanted a " + classToExpect.getCanonicalName());
					}
				}
				
				if(classToExpect == List.class){
					try{
						if(toCheck instanceof List){
							continue;
						}
						
						List<String> stringList = Arrays.asList(toCheck.toString().replaceAll(" ", "").split(","));
						for(int i = 0; i < stringList.size(); i++) stringList.set(i, YAMLConfigExtended.replaceUmlauts(stringList.get(i)));
						configurationMap.put(field.fieldName(), stringList);
						continue;
					}catch(NumberFormatException exp){
						throw new TraitConfigurationFailedException("Field: '" + traitPath + "." + field.fieldName() + 
								"' not found in ConfigTotal for Trait: " + trait.getName() + 
								". Found a " + toCheck.getClass().getCanonicalName() + " but wanted a " + classToExpect.getCanonicalName());
					}
				}

				if(classToExpect == Material.class){
					try{
						if(toCheck instanceof Material){
							continue;
						}
						
						Material mat = null;
						if(toCheck instanceof Integer){
							int value = (Integer) toCheck;
							mat = Material.getMaterial(value); // We still want to support IDs.
						}else{
							String value = toCheck.toString();
							try{
								int matID = Integer.parseInt(value);
								mat = Material.getMaterial(matID);
							}catch(NumberFormatException exp){}
							
							if(mat == null){
								try{
									mat = Material.valueOf(value);
								}catch(IllegalArgumentException exp){
									throw new NumberFormatException();
								}
							}
						}

						if(mat == null){
							throw new NumberFormatException(); //early out.
						}
						
						configurationMap.put(field.fieldName(), mat);
						continue;
						
					}catch(NumberFormatException exp){
						throw new TraitConfigurationFailedException("Field: '" + traitPath + "." + field.fieldName() + 
								"' not found in Config for Trait: " + trait.getName() + " for Holder: %HOLDER%" +
								". Found a " + toCheck.getClass().getCanonicalName() + " but wanted a " + classToExpect.getCanonicalName());
					}
				}
				
				//rest... Try it at least.
				if(classToExpect.isAssignableFrom(toCheck.getClass())){
					configurationMap.put(field.fieldName(), classToExpect.cast(toCheck));
					continue;
				}

				throw new TraitConfigurationFailedException("Field: '" + traitPath + "." + field.fieldName() + 
						"' not found in Config for Trait: " + trait.getName() + " for Holder: %HOLDER%" +
						". Found a " + toCheck.getClass().getCanonicalName() + " but wanted a " + classToExpect.getCanonicalName());
				
			}
			
			try{
				trait.setConfiguration(configurationMap);
			}catch(TraitConfigurationFailedException exp){
				//we have an error here.
				throw exp;
			}catch(Exception exp){
				if(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_enableDebugOutputs()){
					RacesAndClasses.getPlugin().logStackTrace("Configuring Trait failed: " + trait.getDisplayName() + ". Error in logs.", exp);
				}
				
				throw new TraitConfigurationFailedException("Configuration of: " + trait.getDisplayName() 
						+ " of: %HOLDER% Failed. Check your ConfigTotal! There seems to be a wrong / unset value! Check the Documentation for Value references.");
			}
		}catch(TraitConfigurationFailedException exp){
			throw exp;
		}catch(NullPointerException exp){
			RacesAndClasses.getPlugin().getDebugLogger().logStackTrace(exp);
			throw new TraitConfigurationFailedException("Got a Nullpointer! Please report. Writing error to Error.log. Error in: " + trait.getName());
		} catch (SecurityException e) {
			throw new TraitConfigurationFailedException("No Annotation found in Trait: " + trait.getName());
		} catch(NumberFormatException exp){
			throw new TraitConfigurationFailedException("A number could not be read correct at: " + trait.getName());
		}catch(Exception exp){
			throw new TraitConfigurationFailedException("An unknown Exception has occured at Trait: " + trait.getName() 
					+ ". Exception: " + exp.getLocalizedMessage());
		}
	}
	
	
	/**
	 * Returns all ConfigFiels from a Trait.
	 * 
	 * @param trait to search through
	 * 
	 * @return list of all {@link TraitConfigurationField}s.
	 */
	public static List<TraitConfigurationField> getAllTraitConfigFieldsOfTrait(Class<? extends Trait> traitClass){
		List<TraitConfigurationField> annotationList = new LinkedList<TraitConfigurationField>();
		
		Class<? extends Object> classTocheck = traitClass;
		
		while(classTocheck != null && classTocheck != Trait.class){
			try{
				Method method = classTocheck.getMethod("setConfiguration", TraitConfiguration.class);
				if(method == null || !method.isAnnotationPresent(TraitConfigurationNeeded.class)){
					throw new NoSuchMethodException();
				}

				TraitConfigurationNeeded neededConfig = method
						.getAnnotation(TraitConfigurationNeeded.class);
				
				if(neededConfig != null){
					Collections.addAll(annotationList, neededConfig.fields());
				}
					
			}catch(NoSuchMethodException exp){
				continue;
			}finally{
				classTocheck = classTocheck.getSuperclass();
			}			
		}
		
		return annotationList;
	}

	/**
	 * Returns all Removed Fields from a Trait.
	 * 
	 * @param trait to search through
	 * 
	 * @return list of all {@link RemoveSuperConfigField}s.
	 */
	public static List<RemoveSuperConfigField> getAllTraitRemovedFieldsOfTrait(Class<? extends Trait> traitClass){
		List<RemoveSuperConfigField> annotationList = new LinkedList<RemoveSuperConfigField>();
		
		Class<? extends Object> classTocheck = traitClass;
		
		while(classTocheck != null && classTocheck != Trait.class){
			try{
				Method method = classTocheck.getMethod("setConfiguration", TraitConfiguration.class);
				if(method == null || !method.isAnnotationPresent(TraitConfigurationNeeded.class)){
					throw new NoSuchMethodException();
				}
				
				TraitConfigurationNeeded neededConfig = method
						.getAnnotation(TraitConfigurationNeeded.class);
				
				if(neededConfig != null){
					Collections.addAll(annotationList, neededConfig.removedFields());
				}
				
			}catch(NoSuchMethodException exp){
				continue;
			}finally{
				classTocheck = classTocheck.getSuperclass();
			}			
		}
		
		return annotationList;
	}
	
	/**
	 * Returns all ConfigFiels from a Trait.
	 * 
	 * @param trait to search through
	 * 
	 * @return list of all {@link TraitConfigurationField}s.
	 */
	public static List<TraitConfigurationField> getAllTraitConfigFieldsOfTrait(Trait trait){
		return getAllTraitConfigFieldsOfTrait(trait.getClass());
	}

	/**
	 * Returns all Removed Fields from a Trait.
	 * 
	 * @param trait to search through
	 * 
	 * @return list of all {@link RemoveSuperConfigField}s.
	 */
	public static List<RemoveSuperConfigField> getAllTraitRemoveFieldsOfTrait(Trait trait){
		return getAllTraitRemovedFieldsOfTrait(trait.getClass());
	}
}
