package de.tobiyas.racesandclasses.playermanagement.skilltree;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
	private final Set<Trait> presentTraits = new HashSet<>();
	
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
	public boolean hasTrait(Trait trait){
		return presentTraits.contains(trait);
	}
	
	
	/**
	 * removes the Trait from the list of traits present.
	 * @param trait
	 */
	public void removeTrait(Trait trait){
		presentTraits.remove(trait);
	}
	
	
	/**
	 * Adds the Trait to the Player.
	 * @param trait to add.
	 */
	public void addTrait(Trait trait){
		if(!hasTrait(trait)) presentTraits.add(trait);
	}
	
	
	/**
	 * Gets all Traits of the Player
	 * @return all traits.
	 */
	public Collection<Trait> getTraits(){
		return new HashSet<>(presentTraits);
	}
	
	
	/**
	 * @return the free Skillpoints of the Player
	 */
	public int getFreeSkillpoints(){
		int level = player.getLevelManager().getCurrentLevel();
		int every = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_skillpointEveryXLevel();
		int free = level / every;
		
		//Remove points for the Skills already present.
		for(Trait trait : presentTraits) free -= trait.getSkillPointCost();
		return Math.max(0, free);
	}


	/**
	 * Saves the Container.
	 */
	public void save() {
		YAMLConfigExtended config = YAMLPersistenceProvider.getLoadedPlayerFile(player);
		if(!config.getValidLoad()) return;
		
		List<String> learned = new LinkedList<>();
		for(Trait trait : presentTraits) learned.add(trait.getDisplayName());
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
		for(String name : learned){
			for(Trait trait : traits){
				if(name.equalsIgnoreCase(trait.getDisplayName())){
					addTrait(trait);
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
