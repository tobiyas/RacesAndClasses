package de.tobiyas.racesandclasses.playermanagement.spellmanagement;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.traitcontainer.traits.magic.MagicSpellTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.magic.MagicSpellTrait.CostType;

public class ManaManager {

	/**
	 * The Plugin to call Stuff on
	 */
	private RacesAndClasses plugin = RacesAndClasses.getPlugin();
	
	/**
	 * The Player this mana Manager belongs.
	 */
	private final String playerName;
	
	
	/**
	 * The Maximal Mana the Player contains.
	 */
	private double maxMana;
	
	
	/**
	 * The current Mana the Player contains.
	 */
	private double currentMana;
	
	
	
	/**
	 * Generates a new Mana Manager for the Player passed.
	 * 
	 * @param playerName
	 */
	public ManaManager(String playerName) {
		this.playerName = playerName;
		this.maxMana = 0;
		this.currentMana = 0;
	}
	
	
	/**
	 * Rescans the Player and resets the Max Mana if needed.
	 */
	public void rescanPlayer(){
		maxMana = 0;
		
		RaceContainer race = (RaceContainer) plugin.getRaceManager().getHolderOfPlayer(playerName);
		if(race != null){
			maxMana += race.getManaBonus();
		}
		
		ClassContainer clazz = (ClassContainer) plugin.getClassManager().getHolderOfPlayer(playerName);
		if(clazz != null){
			maxMana += clazz.getManaBonus();
		}
		
		if(currentMana > maxMana){
			currentMana = maxMana;
		}
		
		//TODO output Mana
	}

	
	/**
	 * A player tries to cast a spell.
	 * Returns true if the spell may be casted.
	 * Returns false if the player does not have enough Mana.
	 * 
	 * When Mana is used, the Mana is removed from the mana Pool.
	 * 
	 * @param spellToCast to cast
	 * @return true if spell cast is permitted, false otherwise.
	 */
	public boolean playerCastSpell(MagicSpellTrait spellToCast){
		if(!hasEnoughMana(spellToCast)) return false;
		
		currentMana -= spellToCast.getCost();
		return true;
	}
	
	
	
	/**
	 * Checks if the Player has enough Mana for the Spell.
	 * 
	 * WARNING: Only returns true if the Spell actually needs Mana!
	 * 
	 * @param spell to check
	 * @return true if has enough Mana, false if not, or no Mana spell.
	 */
	public boolean hasEnoughMana(MagicSpellTrait spell){
		if(spell.getCostType() != CostType.MANA) return false;
		return hasEnoughMana(spell.getCost());
	}
	
	
	/**
	 * Fills the current Mana Pool of the player by a specific Value.
	 * It returns the resulting current mana Pool.
	 * 
	 * If the Mana Pool exceeds it's max size, it is filled but not overfilled.
	 * 
	 * @param value to fill
	 * @return the Current Mana Pool size aftert filling.
	 */
	public double fillMana(double value){
		currentMana += value;
		if(currentMana > maxMana){
			currentMana = maxMana;
		}
		
		return currentMana;
	}
	
	
	/**
	 * Drowns the current Mana Pool of the player by a specific Value.
	 * It returns the resulting current mana Pool.
	 * 
	 * If the Mana Pool exceeds to be empty (0), it is emptied but will not be negative.
	 * 
	 * @param value to drown
	 * @return the Current Mana Pool size after drowning.
	 */
	public double drownMana(double value){
		currentMana -= value;
		if(currentMana < 0){
			currentMana = 0;
		}
		
		return currentMana;
	}

	
	/**
	 * Checks if the Player has enough Mana.
	 * 
	 * @param manaNeeded the mana needed.
	 * @return true if has enough, false if not.
	 */
	public boolean hasEnoughMana(double manaNeeded){
		return currentMana - manaNeeded >= 0;
	}
	
	

	/**
	 * @return the maxMana
	 */
	public double getMaxMana() {
		return maxMana;
	}


	/**
	 * @return the currentMana
	 */
	public double getCurrentMana() {
		return currentMana;
	}


	/**
	 * @return the playerName
	 */
	public String getPlayerName() {
		return playerName;
	}
}
