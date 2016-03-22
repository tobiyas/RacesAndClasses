package de.tobiyas.racesandclasses.playermanagement.skilltree;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.saving.PlayerSavingData;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;

public class PlayerSkillTreeManager {
	
	/**
	 * The set of Traits the player has selected.
	 */
	private final Map<Trait,Integer> presentTraits = new HashMap<>();
	
	/**
	 * The player this belongs to.
	 */
	private final RaCPlayer player;
	
	/**
	 * The Data container to use.
	 */
	private final PlayerSavingData data;
	
	
	
	public PlayerSkillTreeManager(RaCPlayer player, PlayerSavingData data) {
		this.player = player;
		this.data = data;
		
		//Reload from data.
		this.reloadFromData();
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
		
		save();
	}
	
	
	/**
	 * Adds the Trait to the Player.
	 * @param trait to add.
	 */
	public void setTraitLevel(Trait trait, int level){
		presentTraits.put(trait, level);
		
		save();
	}
	
	/**
	 * Saves the Data to the Data.
	 */
	private void save(){
		Map<String,Integer> skillTree = new HashMap<>();
		for(Map.Entry<Trait,Integer> entry : presentTraits.entrySet()){
			String name = entry.getKey().getDisplayName();
			int level = entry.getValue();
			
			if(level > 0) skillTree.put(name, level);
		}
		
		data.overrideSkilltree(skillTree);
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
	 * Loads the Skills from the Config..
	 */
	private PlayerSkillTreeManager reloadFromData() {
		this.presentTraits.clear();
		
		Collection<Trait> traits = TraitHolderCombinder.getAllTraitsOfPlayer(player);
		Map<String,Integer> skillTree = data.getSkillTree();
		for(Map.Entry<String,Integer> entry : skillTree.entrySet()){
			String name = entry.getKey();
			int level = entry.getValue();
			
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
		data.clearSkilltree();
	}


	/**
	 * Replaces the current skills with the new ones passed.
	 * @param toApply the map to apply.
	 */
	public void replaceWithNew(Map<Trait, Integer> toApply) {
		this.presentTraits.clear();
		
		if(toApply != null && !toApply.isEmpty()) this.presentTraits.putAll(toApply);
		save();
	}
	
}
