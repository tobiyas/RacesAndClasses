package de.tobiyas.races.datacontainer.traitcontainer;

import java.io.File;
import java.io.FileFilter;
import java.lang.annotation.AnnotationFormatError;
import java.util.HashSet;
import java.util.Set;

import de.tobiyas.races.Races;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.TraitEventManager;
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
	
	
	public static Set<Trait> importFromFileSystem(){
		Races plugin = Races.getPlugin();
		
		HashSet<Trait> traits = new HashSet<Trait>();
		
		File traitDir = new File(plugin.getDataFolder() + File.separator + "traits" + File.separator);
		if(!traitDir.exists()){
			traitDir.mkdirs();
			return traits;
		}
		
		File[] possibleTraits = traitDir.listFiles(new FileFilter() {
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".jar");
            }
        });
		
		for(File file : possibleTraits){
			System.out.println("possible Trait: " + file.toString());
			//TODO: Load Trait From File
		}
		
		return traits;
	}
}
