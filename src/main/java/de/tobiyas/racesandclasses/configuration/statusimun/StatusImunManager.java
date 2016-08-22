package de.tobiyas.racesandclasses.configuration.statusimun;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.util.config.YAMLConfigExtended;

public class StatusImunManager {

	/**
	 * The map of name -> Status imun.
	 */
	public final Map<String,StatusImunContainer> containerMap = new HashMap<>();
	
	
	/**
	 * Reload the Manager.
	 */
	public void reload(){
		containerMap.clear();
		
		File file = new File(RacesAndClasses.getPlugin().getDataFolder(), "StatusImun.yml");
		if(!file.exists()) try{ file.createNewFile(); }catch(Throwable exp){}
		
		YAMLConfigExtended config = new YAMLConfigExtended(file).load();
		for(String name : config.getRootChildren()){
			List<String> imun = config.getStringList(name);
			if(imun == null || imun.isEmpty()) continue;
			
			Collection<StatusEffect> effects = new HashSet<>();
			for(String imunString : imun) {
				StatusEffect ef = StatusEffect.resolve(imunString);
				if(ef != null) effects.add(ef);
			}
			
			containerMap.put(name, new StatusImunContainer(name, effects));
		}
	}
	
	
	/**
	 * If the Name is imun against the Status effect.
	 * @param name to use.
	 * @param effect to use.
	 * @return true if is imun.
	 */
	public boolean isImun(String name, StatusEffect effect){
		if(name == null || name.isEmpty() || effect == null) return false;
		
		StatusImunContainer container = containerMap.get(name);
		return container == null ? false : container.isImun(effect);
	}
	
}
