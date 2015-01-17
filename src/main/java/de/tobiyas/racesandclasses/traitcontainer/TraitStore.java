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
package de.tobiyas.racesandclasses.traitcontainer;

import java.io.File;
import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.eventprocessing.TraitEventManager;
import de.tobiyas.racesandclasses.traitcontainer.container.TraitsList;
import de.tobiyas.racesandclasses.traitcontainer.exceptions.TraitNotFoundException;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.bypasses.NeedMC1_6;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.bypasses.NeedMC1_7;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.bypasses.NeedsOtherPlugins;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.util.bukkit.versioning.CertainVersionChecker;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;


public class TraitStore {
	
	
	/**
	 * All Classloaders loaded.
	 */
	private static Set<ClassLoader> classLoaders = new HashSet<ClassLoader>();

	
	/**
	 * Clears all Classloaders linked
	 */
	public static void destroyClassLoaders(){
		for(ClassLoader loader : classLoaders){
			loader.clearAssertionStatus(); //TODO remove classloader somehow
		}
		
		classLoaders.clear();
	}
	
	
	/**
	 * Constructs a trait for the passed name.
	 * It is important, that traitName and holders are NEVER null!
	 * 
	 * If something gone Wrong while initialization, null is returned.
	 * 
	 * @param traitName of the trait
	 * @param holders of Trait
	 * @return the constructed Trait or null
	 */
	public static Trait buildTraitByName(String traitName, AbstractTraitHolder holder){
		if(traitName == null || holder == null){
			return null;
		}
		
		try{
			Trait trait = buildTrait(traitName, holder);
			
			registerTrait(trait);
			return trait;
			
		}catch(TraitNotFoundException e){
			RacesAndClasses.getPlugin().log("Trait not found: " + e.getLocalizedMessage());
		}catch(AnnotationFormatError e){
			RacesAndClasses.getPlugin().log("Could not find Annotation for: " + traitName + 
					" Error was: " + e.getLocalizedMessage());
		}catch(TraitConfigurationFailedException exp){
			RacesAndClasses.getPlugin().log("Coild not Construct trait: " + traitName + ". Problem was: " 
					+ exp.getLocalizedMessage());
		}catch(Exception e){
			RacesAndClasses.getPlugin().log("Could not Construct trait: " + traitName);
			RacesAndClasses.getPlugin().getDebugLogger().logStackTrace(e);
		}
		
		return null;
	}
	
	/**
	 * Builds a Trait and sets his holders to the one wanted.
	 * 
	 * @param traitName
	 * @param holders
	 * @return
	 * @throws Exception
	 */
	private static Trait buildTrait(String traitName, AbstractTraitHolder holder) throws Exception{
		Class<? extends Trait> clazz = TraitsList.getClassOfTrait(traitName);
		if(clazz == null){
			throw new TraitNotFoundException(traitName);
		}
		
		Trait trait = (Trait) clazz.getConstructor().newInstance();
		if(trait == null){
			throw new TraitNotFoundException(traitName);
		}
		
		trait.addTraitHolder(holder);
		
		return trait;
	}
	
	/**
	 * Registers a trait for Uplink and event recieving
	 * 
	 * @param trait to register
	 * @return if worked or not
	 * 
	 * @throws AnnotationFormatError
	 */
	private static boolean registerTrait(Trait trait) throws AnnotationFormatError{
		try{
			Set<Class<? extends Event>> wantedEvents = new HashSet<Class<? extends Event>>();
			int traitPriority = -1;
			
			Class<? extends Object> toInspect = trait.getClass();
			while(toInspect != null && toInspect != Object.class){
				try{
					Method method = toInspect.getMethod("generalInit");
					TraitEventsUsed annotation = method.getAnnotation(TraitEventsUsed.class);
					if(annotation != null){
						if(annotation.traitPriority() > traitPriority){
							traitPriority = annotation.traitPriority();
						}
						
						Collections.addAll(wantedEvents, annotation.registerdClasses());
						Collections.addAll(wantedEvents, annotation.bypassClasses());
					}
				}catch(Exception exp){
					continue;
				}finally{
					toInspect = toInspect.getSuperclass();
				}
			}
			
			//No events wanted? So don't bother them.
			if(!wantedEvents.isEmpty()) TraitEventManager.registerTrait(trait, wantedEvents, traitPriority);
			return true;
		}catch(AnnotationFormatError e){
			throw e;
		}catch(Exception e){
			RacesAndClasses.getPlugin().getDebugLogger().logStackTrace(e);
			return false;
		}
	}
	
	/**
	 * Imports all jar files from the traits dire.
	 */
	public static void importFromFileSystem(){
		RacesAndClasses plugin = RacesAndClasses.getPlugin();
		
		File traitDir = new File(plugin.getDataFolder() + File.separator + "traits" + File.separator);
		if(!traitDir.exists()){
			traitDir.mkdirs();
			return;
		}
		
		List<File> possibleTraits = getAllTraitsOfDir(traitDir);
				
		for(File file : possibleTraits){
			try{
				loadExternalTrait(file);
			}catch(Throwable exception){
				RacesAndClasses.getPlugin().log("Could not load file: " + file.toString());
				continue;
			}
		}

	}
	
	/**
	 * Finds recursively all '.jar' files in the Trait directory.
	 * 
	 * @param dir
	 * @return
	 */
	private static List<File> getAllTraitsOfDir(File dir){
		List<File> traitFileList = new LinkedList<File>();
		for(File file : dir.listFiles()){
			if(file.isDirectory()){
				//directory -> research in this one
				traitFileList.addAll(getAllTraitsOfDir(file));
			}else{
				//file -> check if .jar -> add to list
				if(file.getAbsolutePath().endsWith(".jar")){
					traitFileList.add(file);
				}
			}
		}
		
		return traitFileList;
	}
	
	@SuppressWarnings("unchecked")
	private static void loadExternalTrait(File file){
		try{			
			//old but working: //TODO replace by new Classloader with no leaking of memory
			URLClassLoader clazzLoader = URLClassLoader.newInstance(new URL[]{file.toURI().toURL()}, RacesAndClasses.getPlugin().getClass().getClassLoader());
			
			classLoaders.add(clazzLoader);
			 
	        JarFile jarFile = new JarFile(file);
	        Enumeration<JarEntry> entries = jarFile.entries();
	        
	        Set<Class<Trait>> clazzArray = new HashSet<Class<Trait>>();
	        
	        while (entries.hasMoreElements()) {
	            JarEntry element = entries.nextElement();
	            if (element.getName().endsWith(".class")) {
	                try {
	                    Class<?> clazz = clazzLoader.loadClass(element.getName().replaceAll(".class", "").replaceAll("/", "."));
	                    if(clazz != null){
	                    	if(Trait.class.isAssignableFrom(clazz)){
	                    		
	                    		if(clazz.isAnnotationPresent(NeedMC1_6.class)){
	                    			if(!CertainVersionChecker.isAbove1_6()){
	                    				//We need MC > 1.6 But do not have it.
	                    				continue;	                    				
	                    			}
	                    		}
	                    		
	                    		if(clazz.isAnnotationPresent(NeedMC1_7.class)){
	                    			if(!CertainVersionChecker.isAbove1_7()){
	                    				//We need MC > 1.7 But do not have it.
	                    				continue;	                    				
	                    			}
	                    		}
	                    		
	                    		if(clazz.isAnnotationPresent(NeedsOtherPlugins.class)){
	                    			boolean doBreak = false;
	                    			for(String pluginName : clazz.getAnnotation(NeedsOtherPlugins.class).neededPlugins()){
	                    				if(Bukkit.getPluginManager().getPlugin(pluginName) == null) {
	                    					doBreak = true;
	                    					break;
	                    				}
	                    			}
	                    			
	                    			//some depends not found.
	                    			if(doBreak) continue;
	                    		}
	                    		
	                    		
	                    		
	                    		clazzArray.add((Class<Trait>) clazz);
	                    	}
	                    }
	                } catch (Throwable e) {
	                	RacesAndClasses.getPlugin().getDebugLogger().logError("Could not load Java Class: " 
	                			+ element.getName() + ". In: " + jarFile.getName());
	                	
	                	continue;
	                }
	            }
	        }

	        boolean hasImportInfos = false;
	        for(Class<Trait> clazz : clazzArray){
	        	try{
					if (clazz != null) {
						Trait trait = clazz.newInstance();
				        
						boolean isPresent = trait.getClass().getMethod("importTrait").isAnnotationPresent(TraitInfos.class);
				        if(isPresent){
				        	TraitInfos annotation = trait.getClass().getMethod("importTrait").getAnnotation(TraitInfos.class);
				        	TraitsList.addTraitToList(annotation.traitName(), clazz, annotation.category(), annotation.visible());
				        	trait.importTrait();
				        	hasImportInfos = true;
				        }else{
				        	throw new AnnotationFormatError("Annotation: Import could not be found for class: " + clazz);
				        }
					}
	        	}catch(AnnotationFormatError e){
	        		RacesAndClasses.getPlugin().log(e.getLocalizedMessage());
	        	}
	        }
	        
	        jarFile.close();
	        
	        if(!hasImportInfos){
	        	throw new AnnotationFormatError("Annotation: Import could not be found for file: " + file.getName());
	        }
	        
		}catch (NoClassDefFoundError e) {
			String message = "Unable to load " + file.getName() + ". Probably it was written for a previous Races version!";
			RacesAndClasses.getPlugin().log(message);
			RacesAndClasses.getPlugin().logStackTrace(message, e);
			return;
		} catch (AnnotationFormatError e){
			//Not interesting since the Plugin needed is not loaded!
			//RacesAndClasses.getPlugin().log(e.getLocalizedMessage());
		} catch (Throwable e) {
			String message = "The trait " + file.getName() + " failed to load for an unknown reason.";
			RacesAndClasses.getPlugin().log(message);
			RacesAndClasses.getPlugin().logStackTrace(message, e);
		} 
	}

	/**
	 * Builds a static trait with NO holders.
	 * 
	 * @param string
	 */
	public static Trait buildTraitWithoutHolderByName(String traitName) {
		try{
			Trait trait = buildTrait(traitName, null);
			
			registerTrait(trait);
			return trait;
			
		}catch(TraitNotFoundException e){
			RacesAndClasses.getPlugin().log(e.getLocalizedMessage());
		}catch(AnnotationFormatError e){
			RacesAndClasses.getPlugin().log("Could not find Annotation for: " + traitName);
		}catch(Exception e){
			RacesAndClasses.getPlugin().log("Could not Construct trait: " + traitName);
		}
		
		return null;
	}
}
