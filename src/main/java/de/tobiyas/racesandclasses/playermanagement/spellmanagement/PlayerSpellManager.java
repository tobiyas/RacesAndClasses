package de.tobiyas.racesandclasses.playermanagement.spellmanagement;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.magic.MagicSpellTrait;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier;

public class PlayerSpellManager {
	
	/**
	 * Last time an Event was triggered
	 */
	private long lastEventTime = System.currentTimeMillis();
	
	/**
	 * The Player this container belongs.
	 */
	private final String playerName;
	
	/**
	 * The {@link ManaManager} that the player has.
	 */
	protected final ManaManager manaManager;
	
	
	/**
	 * The Spell list of the Player.
	 */
	protected final RotatableList<MagicSpellTrait> spellList;
	
	
	/**
	 * Creates a SpellManager with a containing {@link ManaManager}.
	 * 
	 * @param playerName to create with
	 */
	public PlayerSpellManager(String playerName) {
		this.playerName = playerName;
		this.manaManager = new ManaManager(playerName);
		this.spellList = new RotatableList<MagicSpellTrait>();
	}
	
	
	/**
	 * Rescans the Player for changed Races and Classes to update the Mana
	 * and the Spells he can cast.
	 */
	public void rescan(){
		spellRescan();
		manaManager.rescanPlayer();
	}
	
	
	/**
	 * Rescans the Spells the player can cast
	 */
	private void spellRescan(){
		List<MagicSpellTrait> spellList = new LinkedList<MagicSpellTrait>();
		
		Set<Trait> traits = TraitHolderCombinder.getAllTraitsOfPlayer(playerName);
		for(Trait trait : traits){
			if(trait instanceof MagicSpellTrait){
				spellList.add((MagicSpellTrait) trait);
			}
		}
		
		this.spellList.setEntries(spellList);
	}
	
	/**
	 * Changes to the next Spell in the List.
	 * 
	 * @return the next Spell.
	 */
	public MagicSpellTrait changeToNextSpell(){
		if(System.currentTimeMillis() - lastEventTime < 100) return null;
		
		lastEventTime = System.currentTimeMillis();
		return spellList.next();
	}
	
	
	/**
	 * Returns the {@link ManaManager} to check Spell casting or
	 * Mana Capabilities.
	 * 
	 * @return the ManaManager of the Player
	 */
	public ManaManager getManaManager(){
		return manaManager;
	}


	/**
	 * Returns the current Spell.
	 * 
	 * If no spells are present, null is returned.
	 * 
	 * @return
	 */
	public MagicSpellTrait getCurrentSpell() {
		return spellList.currentEntry();
	}
	
	
	/**
	 * Checks if the player can cast the spell
	 * 
	 * @param trait to check
	 * @return true if he can, false if not
	 */
	public boolean canCastSpell(MagicSpellTrait trait){
		double cost = trait.getCost();
		Player player = Bukkit.getPlayer(playerName);
		
		switch(trait.getCostType()){
			case HEALTH: return RacesAndClasses.getPlugin().getPlayerManager()
					.getHealthOfPlayer(playerName) > cost;
			
			case MANA: return getManaManager().hasEnoughMana(trait);
			
			case ITEM: return player.getInventory().contains(trait.getCastMaterialType(), (int) cost);
			
			default: return false;
		}
	}
	
	/**
	 * Removes the spell cost from the player
	 * 
	 * @param player
	 */
	public void removeCost(MagicSpellTrait trait) {
		Player player = Bukkit.getPlayer(playerName);
		
		switch(trait.getCostType()){
			case HEALTH: CompatibilityModifier.BukkitPlayer.safeSetHealth(RacesAndClasses.getPlugin()
					.getPlayerManager().getHealthOfPlayer(playerName) - trait.getCost(), player); 
							break;
			
			case MANA: getManaManager().playerCastSpell(trait); break;
			
			case ITEM: player.getInventory().removeItem(new ItemStack(trait.getCastMaterialType(), (int) trait.getCost())); break;
		}
	}

	
	/**
	 * Returns the amount of spells the SpellManager contains.
	 * 
	 * @return
	 */
	public int getSpellAmount() {
		return spellList.size();
	}
}
