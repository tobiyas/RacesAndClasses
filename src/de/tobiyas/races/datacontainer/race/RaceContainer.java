package de.tobiyas.races.datacontainer.race;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.configuration.file.YamlConfiguration;

import de.tobiyas.races.Races;
import de.tobiyas.races.datacontainer.traitcontainer.Trait;
import de.tobiyas.races.datacontainer.traitcontainer.TraitStore;
import de.tobiyas.races.datacontainer.traitcontainer.traits.arrows.NormalArrow;

public class RaceContainer {

	private YamlConfiguration config;
	private String raceName;
	
	private String raceTag;
	private int raceMaxHealth;
	
	private HashSet<Trait> traits;
	
	private static RaceManager manager = RaceManager.getManager();
	
	private RaceContainer(YamlConfiguration config, String name){
		this.config = config;
		this.raceName = name;
		
		readConfigSection();
		readTraitSection();	
	}
	
	private void readConfigSection(){
		raceTag = config.getString("races." + raceName + ".config.racetag");
		raceMaxHealth = config.getInt("races." + raceName + ".config.raceMaxHealth", Races.getPlugin().interactConfig().getconfig_defaultHealth());
	}
	
	private void readTraitSection(){
		traits = new HashSet<Trait>();
		
		Set<String> traitNames = config.getConfigurationSection("races." + raceName + ".traits").getKeys(false);
		if(traitNames == null || traitNames.size() == 0) return;
		
		for(String traitName : traitNames){
			Trait trait = TraitStore.buildTraitByName(traitName, this);
			if(trait != null){
				Object value = config.getString("races." + raceName + ".traits." + traitName);
				trait.setValue(value);
				traits.add(trait);
			}
		}
		
		addSTDTraits();
	}
	
	private void addSTDTraits(){
		traits.add(new NormalArrow(this));
	}
	
	/**
	 * loads a RaceContainer from an YamlConfig
	 * 
	 * @param config
	 * @param name
	 * @return the container
	 */
	public static RaceContainer loadRace(YamlConfiguration config, String name){
		RaceContainer container = new RaceContainer(config, name);
		
		return container;
	}
	
	public boolean containsPlayer(String player){
		RaceContainer container = manager.getRaceOfPlayer(player);
		if(container == null) return false;
		return container.getName().equals(raceName);
	}
	
	public String getName(){
		return raceName;
	}
	
	public String getTag(){
		return raceTag;
	}
	
	public Set<Trait> getTraits(){
		return traits;
	}
	
	public Set<Trait> getVisibleTraits(){
		Set<Trait> traitSet = new HashSet<Trait>();
		for(Trait trait : traits)
			if(trait.isVisible())
				traitSet.add(trait);
		
		return traitSet;
	}
	
	public int getRaceMaxHealth(){
		return raceMaxHealth;
	}
	
}
