package de.tobiyas.races.datacontainer.traitcontainer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.InputStreamReader;
import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Constructor;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import de.tobiyas.races.Races;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.TraitEventManager;
import de.tobiyas.races.datacontainer.traitcontainer.imports.TraitPlugin;
import de.tobiyas.races.datacontainer.traitcontainer.imports.TraitPlugin.Import;
import de.tobiyas.races.datacontainer.traitcontainer.traits.Trait;
import de.tobiyas.races.datacontainer.traitcontainer.traits.Trait.TraitInfo;

public class TraitStore {

	public static Trait buildTraitByName(String traitName, RaceContainer container){
		Class<?> clazz = TraitsList.getClassOfTrait(traitName);
		
		try{
			Trait trait = (Trait) clazz.getConstructor(RaceContainer.class).newInstance(container);
			if(trait == null)
				throw new TraitNotFoundException(traitName, container.getName());	
			
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
		Class<?> clazz = TraitsList.getClassOfTrait(traitName);
		try{
			Trait trait = (Trait) clazz.getConstructor(ClassContainer.class).newInstance(container);
			
			if(trait == null)
				throw new TraitNotFoundException(traitName, container.getName());
			
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
		Class<?> clazz = TraitsList.getClassOfTrait(traitName);
		try{
			Trait trait = (Trait) clazz.getConstructor().newInstance();
			if(trait == null)
				throw new TraitNotFoundException(traitName);
			
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
				//TODO: Load Trait From File
			}catch(Exception exception){
				Races.getPlugin().log("Could not load file: " + file.toString());
				continue;
			}
		}

	}
	
	private static void loadExternalTrait(File file){
		 try
		    {
		      JarFile jarFile = new JarFile(file);
		      Enumeration<JarEntry> entries = jarFile.entries();

		      String mainClass = null;
		      while (entries.hasMoreElements()) {
		        JarEntry element = (JarEntry)entries.nextElement();
		        if (element.getName().equalsIgnoreCase("skill.info")) { //check what this does
		          BufferedReader reader = new BufferedReader(new InputStreamReader(jarFile.getInputStream(element)));
		          mainClass = reader.readLine().substring(12);
		          break;
		        }
		      }

		      if (mainClass != null) {
		        Class<?> clazz = Class.forName(mainClass, true, Races.getPlugin().getClass().getClassLoader());
		        Class<?> traitClass = clazz.asSubclass(Trait.class);
		        Constructor<?> ctor = traitClass.getConstructor();
		        Trait trait = (Trait)ctor.newInstance();
		        if(trait instanceof TraitPlugin){
		        	TraitPlugin newTrait = (TraitPlugin) trait;
		        	Import annotation = newTrait.getClass().getMethod("importTrait").getAnnotation(Import.class);
		        	if(annotation == null){
		        		
		        	}
		        }
		        return;
		      }
		      throw new Exception();
		    }
		    catch (NoClassDefFoundError e) {
		      Races.getPlugin().log("Unable to load " + file.getName() + " skill was written for a previous Heroes version!");
		      return;
		    } catch (ClassNotFoundException e) {
		      Races.getPlugin().log("Unable to load " + file.getName() + " skill was written for a previous Heroes version!");
		      return;
		    } catch (Exception e) {
		      Races.getPlugin().log("The skill " + file.getName() + " failed to load for an unknown reason.");
		    } return;
	}
}
