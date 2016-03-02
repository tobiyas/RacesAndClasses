package de.tobiyas.racesandclasses.playermanagement.skilltree;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.racesandclasses.persistence.file.YAMLPersistenceProvider;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.util.config.YAMLConfigExtended;

public class PlayerSkillTreeManager {
	
	/**
	 * The Path for the Learned Traits.
	 */
	private static final String PRESENT_TRAITS_PATH = "learnedTraits";
	
	
	/**
	 * The set of Traits the player has selected.
	 */
	private final Map<Trait,Integer> presentTraits = new HashMap<>();
	
	/**
	 * The player this belongs to.
	 */
	private final RaCPlayer player;
	
	
	
	public PlayerSkillTreeManager(RaCPlayer player) {
		this.player = player;
	}


	/**
	 * Returns true if the Player has this trait.
	 * @param trait to check
	 * @return true if has.
	 */
	public int getLevel(Trait trait){
		return presentTraits.containsKey(trait) ? presentTraits.get(trait) : 0;
	}
	
	
	/**
	 * removes the Trait from the list of traits present.
	 * @param trait to remove.
	 */
	public void removeTrait(Trait trait){
		presentTraits.remove(trait);
	}
	
	
	/**
	 * Adds the Trait to the Player.
	 * @param trait to add.
	 */
	public void setTraitLevel(Trait trait, int level){
		presentTraits.put(trait, level);
	}
	
	
	/**
	 * Gets all Traits of the Player
	 * @return all traits.
	 */
	public Map<Trait,Integer> getTraitsWithLevels(){
		return new HashMap<>(presentTraits);
	}
	
	
	/**
	 * @return the free Skillpoints of the Player
	 */
	public int getFreeSkillpoints(){
		int level = player.getLevelManager().getCurrentLevel();
		int every = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_skillpointEveryXLevel();
		int free = level / every;
		
		//Remove points for the Skills already present.
		for(Map.Entry<Trait,Integer> entry : presentTraits.entrySet()) {
			for(int i = 1; i <= entry.getValue(); i++){
				free -= entry.getKey().getSkillPointCost(i);
			}
		}
		
		return Math.max(0, free);
	}


	/**
	 * Saves the Container.
	 */
	public void save() {
		YAMLConfigExtended config = YAMLPersistenceProvider.getLoadedPlayerFile(player);
		if(!config.getValidLoad()) return;
		
		List<String> learned = new LinkedList<>();
		for(Map.Entry<Trait,Integer> entry : presentTraits.entrySet()) {
			learned.add(entry.getKey().getDisplayName() + "@" + entry.getValue());
		}
		
		config.set(PRESENT_TRAITS_PATH, learned);
	}
	
	/**
	 * Loads the Skills from the Config..
	 */
	public PlayerSkillTreeManager reloadFromConfig() {
		this.presentTraits.clear();
		
		YAMLConfigExtended config = YAMLPersistenceProvider.getLoadedPlayerFile(player);
		if(!config.getValidLoad()) return this;
		
		List<String> learned = config.getStringList(PRESENT_TRAITS_PATH);
		Collection<Trait> traits = TraitHolderCombinder.getAllTraitsOfPlayer(player);
		for(String line : learned){
			String[] split = line.split(Pattern.quote("@"));
			String name = split[0];
			int level = 1; try{level = Integer.parseInt(split[1]);}catch(Throwable exp){}
			
			for(Trait trait : traits){
				if(name.equalsIgnoreCase(trait.getDisplayName())){
					setTraitLevel(trait, level);
					break;
				}
			}
		}
		
		return this;
	}


	/**
	 * Clears the current Skills.
	 */
	public void clearSkills() {
		this.presentTraits.clear();
	}
	
}
