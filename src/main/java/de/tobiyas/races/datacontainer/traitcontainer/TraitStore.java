package de.tobiyas.races.datacontainer.traitcontainer;

import java.io.File;
import java.io.FileFilter;
import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import de.tobiyas.races.Races;
import de.tobiyas.races.datacontainer.eventmanagement.TraitEventManager;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitcontainer.imports.ImportTrait;
import de.tobiyas.races.datacontainer.traitcontainer.imports.TraitPlugin;
import de.tobiyas.races.datacontainer.traitcontainer.traits.Trait;
import de.tobiyas.races.datacontainer.traitcontainer.traits.Trait.TraitInfo;


public class TraitStore {

	public static Trait buildTraitByName(String traitName, RaceContainer container){
		try{
			Trait trait = buildTrait(traitName);
			trait.setRace(container);
			
			registerTrait(trait);
			return trait;
		
		}catch(TraitNotFoundException e){
			Races.getPlugin().log(e.getLocalizedMessage());		
		}catch(AnnotationFormatError e){
			Races.getPlugin().log("Could not find Annotation for: " + traitName);
		}catch(Exception e){
			Races.getPlugin().log("Could not Construct trait: " + traitName);
		}
		
		return null;
	}
	
	public static Trait buildTraitByName(String traitName, ClassContainer container){
		try{
			Trait trait = buildTrait(traitName);
			trait.setClazz(container);
			
			registerTrait(trait);
			return trait;
		
		}catch(TraitNotFoundException e){
			Races.getPlugin().log(e.getLocalizedMessage());		
		}catch(AnnotationFormatError e){
			Races.getPlugin().log("Could not find Annotation for: " + traitName);
		}catch(Exception e){
			Races.getPlugin().log("Could not Construct trait: " + traitName);
		}
		
		return null;
	}
	
	public static Trait buildTraitByName(String traitName){
		try{
			Trait trait = buildTrait(traitName);
			
			registerTrait(trait);
			return trait;
			
		}catch(TraitNotFoundException e){
			Races.getPlugin().log(e.getLocalizedMessage());
		}catch(AnnotationFormatError e){
			Races.getPlugin().log("Could not find Annotation for: " + traitName);
		}catch(Exception e){
			Races.getPlugin().log("Could not Construct trait: " + traitName);
		}
		
		return null;
	}
	
	private static Trait buildTrait(String traitName) throws Exception{
		Class<?> clazz = TraitsList.getClassOfTrait(traitName);
		Trait trait = (Trait) clazz.getConstructor().newInstance();
		if(trait == null)
			throw new TraitNotFoundException(traitName);
		
		return trait;
	}
	
	private static boolean registerTrait(Trait trait) throws AnnotationFormatError{
		try{
			TraitInfo annotation = trait.getClass().getMethod("generalInit").getAnnotation(TraitInfo.class);
			if(annotation == null)
				throw new AnnotationFormatError("No Annotation found");
			
			int traitPrio = annotation.traitPriority();
			Class<?>[] traitRegistration = annotation.registerdClasses();
			trait.generalInit();
			
			HashSet<Class<?>> registeredClasses = new HashSet<Class<?>>();
			for(Class<?> clazz : traitRegistration)
				registeredClasses.add(clazz);
			
			TraitEventManager.registerTrait(trait, registeredClasses, traitPrio);
			return true;
		}catch(AnnotationFormatError e){
			throw e;
		}catch(Exception e){
			Races.getPlugin().getDebugLogger().logStackTrace(e);
			return false;
		}
	}
	
	
	public static void importFromFileSystem(){
		Races plugin = Races.getPlugin();
		
		File traitDir = new File(plugin.getDataFolder() + File.separator + "traits" + File.separator);
		if(!traitDir.exists()){
			traitDir.mkdirs();
			return;
		}
		
		File[] possibleTraits = traitDir.listFiles(new FileFilter() {
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".jar");
            }
        });
		
		for(File file : possibleTraits){
			try{
				loadExternalTrait(file);
			}catch(Exception exception){
				Races.getPlugin().log("Could not load file: " + file.toString());
				continue;
			}
		}

	}
	
	@SuppressWarnings("unchecked")
	private static void loadExternalTrait(File file){
		try{
			URLClassLoader clazzLoader = URLClassLoader.newInstance(new URL[]{file.toURI().toURL()}, Races.getPlugin().getClass().getClassLoader());			
			 
	        JarFile jarFile = new JarFile(file);
	        Enumeration<JarEntry> entries = jarFile.entries();
	        
	        Set<Class<TraitPlugin>> clazzArray = new HashSet<Class<TraitPlugin>>();
	        
	        while (entries.hasMoreElements()) {
	            JarEntry element = entries.nextElement();
	            if (element.getName().endsWith(".class")) {
	                try {
	                    Class<?> clazz = clazzLoader.loadClass(element.getName().replaceAll(".class", "").replaceAll("/", "."));
	                    if(clazz != null){
	                    	if(TraitPlugin.class.isAssignableFrom(clazz))
	                    		clazzArray.add((Class<TraitPlugin>) clazz);
	                    }
	                } catch (Exception e) {
	                	continue;
	                }
	            }
	        }

	        boolean hasClass = false;
	        for(Class<TraitPlugin> clazz : clazzArray){
	        	try{
					if (clazz != null) {
						Constructor<?> constructor = clazz.getConstructor();
						TraitPlugin trait = (TraitPlugin) constructor.newInstance();
				        
						boolean isPresent = trait.getClass().getMethod("importTrait").isAnnotationPresent(ImportTrait.class);
				        if(isPresent){
				        	ImportTrait annotation = trait.getClass().getMethod("importTrait").getAnnotation(ImportTrait.class);
				        	TraitsList.addTraitToList(annotation.traitName(), clazz, annotation.category(), annotation.visible());
				        	trait.importTrait();
				        	hasClass = true;
				        }else
				        	throw new AnnotationFormatError("Annotation: Import could not be found for class: " + clazz);
					}
	        	}catch(AnnotationFormatError e){
	        		Races.getPlugin().log(e.getLocalizedMessage());
	        	}
	        }
	        
	        jarFile.close();
	        
	        if(!hasClass)
	        	throw new AnnotationFormatError("Annotation: Import could not be found for file: " + file.getName());
	        
		}catch (NoClassDefFoundError e) {
			Races.getPlugin().log("Unable to load " + file.getName() + ". Probably it was written for a previous Races version!");
			return;
		} catch (AnnotationFormatError e){
			Races.getPlugin().log(e.getLocalizedMessage());
		} catch (Exception e) {
			Races.getPlugin().log("The trait " + file.getName() + " failed to load for an unknown reason.");
			Races.getPlugin().getDebugLogger().logStackTrace(e);
		} 
	}
}
