package de.tobiyas.racesandclasses.traitcontainer;

import java.io.File;
import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.event.Event;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.eventprocessing.TraitEventManager;
import de.tobiyas.racesandclasses.traitcontainer.container.TraitsList;
import de.tobiyas.racesandclasses.traitcontainer.exceptions.TraitNotFoundException;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.Trait;


public class TraitStore {
	
	
	/**
	 * Constructs a trait for the passed name.
	 * It is important, that traitName and holder are NEVER null!
	 * 
	 * If something gone Wrong while initialization, null is returned.
	 * 
	 * @param traitName of the trait
	 * @param holder of Trait
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
			RacesAndClasses.getPlugin().log(e.getLocalizedMessage());
		}catch(AnnotationFormatError e){
			RacesAndClasses.getPlugin().log("Could not find Annotation for: " + traitName);
		}catch(Exception e){
			RacesAndClasses.getPlugin().log("Could not Construct trait: " + traitName);
		}
		
		return null;
	}
	
	/**
	 * Builds a Trait and sets his holder to the one wanted.
	 * 
	 * @param traitName
	 * @param holder
	 * @return
	 * @throws Exception
	 */
	private static Trait buildTrait(String traitName, AbstractTraitHolder holder) throws Exception{
		Class<?> clazz = TraitsList.getClassOfTrait(traitName);
		Trait trait = (Trait) clazz.getConstructor().newInstance();
		if(trait == null){
			throw new TraitNotFoundException(traitName);
		}
		
		trait.setTraitHolder(holder);
		
		return trait;
	}
	
	/**
	 * Registers a trait for Uplink and event recieving
	 * 
	 * @param trait
	 * @return
	 * @throws AnnotationFormatError
	 */
	@SuppressWarnings("unchecked") //This is sage because of checking before
	private static boolean registerTrait(Trait trait) throws AnnotationFormatError{
		try{
			TraitEventsUsed annotation = trait.getClass().getMethod("generalInit").getAnnotation(TraitEventsUsed.class);
			if(annotation == null)
				throw new AnnotationFormatError("No Annotation found");
			
			int traitPrio = annotation.traitPriority();
			Class<?>[] traitRegistration = annotation.registerdClasses();
			trait.generalInit();
			
			HashSet<Class<? extends Event>> registeredClasses = new HashSet<Class<? extends Event>>();
			for(Class<?> clazz : traitRegistration){
				if(Event.class.isAssignableFrom(clazz)){
					registeredClasses.add((Class<? extends Event>) clazz);
				}
			}
			
			TraitEventManager.registerTrait(trait, registeredClasses, traitPrio);
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
			}catch(Exception exception){
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
			URLClassLoader clazzLoader = URLClassLoader.newInstance(new URL[]{file.toURI().toURL()}, RacesAndClasses.getPlugin().getClass().getClassLoader());			
			 
	        JarFile jarFile = new JarFile(file);
	        Enumeration<JarEntry> entries = jarFile.entries();
	        
	        Set<Class<Trait>> clazzArray = new HashSet<Class<Trait>>();
	        
	        while (entries.hasMoreElements()) {
	            JarEntry element = entries.nextElement();
	            if (element.getName().endsWith(".class")) {
	                try {
	                    Class<?> clazz = clazzLoader.loadClass(element.getName().replaceAll(".class", "").replaceAll("/", "."));
	                    if(clazz != null){
	                    	if(Trait.class.isAssignableFrom(clazz))
	                    		clazzArray.add((Class<Trait>) clazz);
	                    }
	                } catch (Exception e) {
	                	continue;
	                }
	            }
	        }

	        boolean hasClass = false;
	        for(Class<Trait> clazz : clazzArray){
	        	try{
					if (clazz != null) {
						Constructor<?> constructor = clazz.getConstructor();
						Trait trait = (Trait) constructor.newInstance();
				        
						boolean isPresent = trait.getClass().getMethod("importTrait").isAnnotationPresent(TraitInfos.class);
				        if(isPresent){
				        	TraitInfos annotation = trait.getClass().getMethod("importTrait").getAnnotation(TraitInfos.class);
				        	TraitsList.addTraitToList(annotation.traitName(), clazz, annotation.category(), annotation.visible());
				        	trait.importTrait();
				        	hasClass = true;
				        }else{
				        	throw new AnnotationFormatError("Annotation: Import could not be found for class: " + clazz);
				        }
					}
	        	}catch(AnnotationFormatError e){
	        		RacesAndClasses.getPlugin().log(e.getLocalizedMessage());
	        	}
	        }
	        
	        jarFile.close();
	        
	        if(!hasClass){
	        	throw new AnnotationFormatError("Annotation: Import could not be found for file: " + file.getName());
	        }
	        
		}catch (NoClassDefFoundError e) {
			RacesAndClasses.getPlugin().log("Unable to load " + file.getName() + ". Probably it was written for a previous Races version!");
			return;
		} catch (AnnotationFormatError e){
			RacesAndClasses.getPlugin().log(e.getLocalizedMessage());
		} catch (Exception e) {
			RacesAndClasses.getPlugin().log("The trait " + file.getName() + " failed to load for an unknown reason.");
			RacesAndClasses.getPlugin().getDebugLogger().logStackTrace(e);
		} 
	}

	/**
	 * Builds a static trait with NO holder.
	 * 
	 * @param string
	 */
	public static Trait buildTraitWithoutHolderByName(String traitName) {
		try{
			Trait trait = buildTrait(traitName, null);
			
			registerTrait(trait);
			trait.generalInit();
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
