package de.tobiyas.racesandclasses.util.traitutil;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import de.tobiyas.racesandclasses.traitcontainer.interfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitConfigurationNeeded;

public class TraitConfigParser {

	public static void configureTraitFromYAML(YamlConfiguration config, String traitPath, Trait trait) throws TraitConfigurationFailedException{
		Map<String, String> configurationMap = new HashMap<String, String>();
		
		try{
			ConfigurationSection traitConfig = config.getConfigurationSection(traitPath);
			for(String pathEntry : traitConfig.getKeys(true)){
				String value = traitConfig.getString(pathEntry);
				
				//Only use the non null values.
				if(value != null){
					configurationMap.put(pathEntry, value);					
				}
			}
			
			TraitConfigurationNeeded neededConfig = trait.getClass().getMethod("setConfiguration", Map.class).getAnnotation(TraitConfigurationNeeded.class);
			if(neededConfig == null){
				throw new NullPointerException();
			}
			
			//check if all config options are present in the Config file
			for(String configOption : neededConfig.neededFields()){
				if(!configurationMap.containsKey(configOption)){
					throw new TraitConfigurationFailedException("Field: '" + traitPath + "." + configOption + 
							"' not found in Config for Trait: " + trait.getName());
				}
			}
			
			trait.setConfiguration(configurationMap);
		}catch(NullPointerException exp){
			throw new TraitConfigurationFailedException("No Annotation found in Trait: " + trait.getName());
		} catch (SecurityException e) {
			throw new TraitConfigurationFailedException("No Annotation found in Trait: " + trait.getName());
		} catch (NoSuchMethodException e) {
			throw new TraitConfigurationFailedException("No Annotation found in Trait: " + trait.getName());
		}catch(NumberFormatException exp){
			throw new TraitConfigurationFailedException("A number could not be read correct at: " + trait.getName());
		}catch(Exception exp){
			throw new TraitConfigurationFailedException("An unknown Exception has occured at Trait: " + trait.getName() 
					+ ". Exception: " + exp.getLocalizedMessage());
		}
	}
}
