package de.tobiyas.races.datacontainer.traitcontainer;

import java.io.File;
import java.io.FileFilter;
import java.util.HashSet;
import java.util.Set;

import de.tobiyas.races.Races;
import de.tobiyas.races.datacontainer.race.RaceContainer;
import de.tobiyas.races.util.statics.TraitsList;

public class TraitStore {

	public static Trait buildTraitByName(String traitName, RaceContainer container){
		Class<?> clazz = TraitsList.getClassOfTrait(traitName);
		
		try{
			Trait trait = (Trait) clazz.getConstructor(RaceContainer.class).newInstance(container);
			return trait;
		}
		catch(Exception e){
			Races.getPlugin().log("Could not Construct trait: " + traitName);
			return null;
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
			//TODO: Load Trait
		}
		
		return traits;
	}
}
