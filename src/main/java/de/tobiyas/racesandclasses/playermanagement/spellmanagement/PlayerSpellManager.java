package de.tobiyas.racesandclasses.playermanagement.spellmanagement;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.racesandclasses.eventprocessing.worldresolver.WorldResolver;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.AbstractBasicTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.MagicSpellTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
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
		
		if(WorldResolver.isOnDisabledWorld(playerName)){
			this.spellList.setEntries(spellList);
			return;
		}
		
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
			
			case HUNGER : return player.getFoodLevel() >= cost;
			
			default: return false;
		}
	}
	
	/**
	 * Removes the spell cost from the player
	 * 
	 * @param trait to remove the cost from
	 */
	public void removeCost(MagicSpellTrait trait) {
		Player player = Bukkit.getPlayer(playerName);
		
		switch(trait.getCostType()){
			case HEALTH: CompatibilityModifier.BukkitPlayer.safeSetHealth(RacesAndClasses.getPlugin()
					.getPlayerManager().getHealthOfPlayer(playerName) - trait.getCost(), player); 
							break;
			
			case MANA: getManaManager().playerCastSpell(trait); break;
			
			case ITEM: player.getInventory().removeItem(new ItemStack(trait.getCastMaterialType(), (int) trait.getCost())); break;
			
			case HUNGER: 
				int oldFoodLevel = player.getFoodLevel();
				int newFoodLevel = (int) (oldFoodLevel - trait.getCost());
				player.setFoodLevel(newFoodLevel < 0 ? 0 : newFoodLevel);
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


	/**
	 * Changes the current spell to the one posted.
	 * 
	 * @param spellName to set. This is the display name!!!
	 * 
	 * @return true if it worked, false otherwise.
	 */
	public boolean changeToSpell(String spellName) {
		if(getSpellAmount() == 0) return false;
		
		for(int i = 0; i < spellList.size(); i++){
			MagicSpellTrait spell = spellList.currentEntry();
			if(spell instanceof AbstractBasicTrait){
				String name = ((AbstractBasicTrait) spell).getDisplayName();
				if(name.equalsIgnoreCase(spellName)){
					return true;
				}
			}
			
			spellList.next();
		}
		
		return false;
	}


	/**
	 * Tries to cast the current spell.
	 * 
	 * @return true if worked, false otherwise.
	 */
	public boolean tryCastCurrentSpell() {
		MagicSpellTrait currentSpell = spellList.currentEntry();
		if(currentSpell == null) return false;
		RacesAndClasses plugin = RacesAndClasses.getPlugin();
		
		Player player = Bukkit.getPlayer(playerName);
		@SuppressWarnings("deprecation") //we need this to get the next block
		List<Block> blocks = player.getLineOfSight(null, 100);
		Block lookingAt = blocks.iterator().next();
		ItemStack wandItem = new ItemStack(plugin.getConfigManager().getGeneralConfig().getConfig_itemForMagic());
		
		plugin.fireEventIntern(new PlayerInteractEvent(player, Action.LEFT_CLICK_AIR, wandItem, 
				lookingAt, BlockFace.UP));
		return true;
	}
}
