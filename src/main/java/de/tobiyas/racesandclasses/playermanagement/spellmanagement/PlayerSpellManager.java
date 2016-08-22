/*******************************************************************************
 * Copyright 2014 Tobias Welther
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.tobiyas.racesandclasses.playermanagement.spellmanagement;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.resolvers.WorldResolver;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana.ManaManager;
import de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana.impl.OwnManaManager;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.AbstractBasicTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.MagicSpellTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.TraitWithCost;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.TraitWithRestrictions;
import de.tobiyas.racesandclasses.util.items.WandItem;

public class PlayerSpellManager {
	
	/**
	 * Last time an Event was triggered
	 */
	private long lastEventTime = System.currentTimeMillis();
	
	/**
	 * The Player this container belongs.
	 */
	private final RaCPlayer player;
	
	/**
	 * The {@link OwnManaManager} that the player has.
	 */
	protected final ManaManager manaManager;
	
	
	/**
	 * The Spell list of the Player.
	 */
	protected final RotatableList<MagicSpellTrait> spellList;
	
	
	/**
	 * Creates a SpellManager with a containing {@link OwnManaManager}.
	 * 
	 * @param player to create with
	 */
	public PlayerSpellManager(RaCPlayer player) {
		this.player = player;
		
		this.manaManager = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_manaManagerType().generate(player);
		this.spellList = new RotatableList<MagicSpellTrait>();
	}
	
	
	/**
	 * Rescans the Player for changed Races and Classes to update the Mana
	 * and the Spells he can cast.
	 */
	public void rescan(){
		MagicSpellTrait current = getCurrentSpell();
		
		spellRescan();
		manaManager.rescanPlayer();
		
		if(current != null)	selectSpell(current.getDisplayName());
	}
	

	
	/**
	 * Selects the Spell by name if possible.
	 * 
	 * @param displayName to select.
	 */
	private void selectSpell(String displayName) {
		for(int i = 0; i < spellList.size(); i++){
			MagicSpellTrait next = spellList.next();
			if(next instanceof TraitWithRestrictions){
				boolean canUse = ((TraitWithRestrictions) next).isInLevelRange(player.getLevelManager().getCurrentLevel());
				if(!canUse) continue;
				
				if(next.getDisplayName().equalsIgnoreCase(displayName)) return;
			}
		}
	}


	/**
	 * Rescans the Spells the player can cast
	 */
	private void spellRescan(){		
		List<MagicSpellTrait> spellList = new LinkedList<MagicSpellTrait>();
		
		if(player == null || !player.isOnline() ||WorldResolver.isOnDisabledWorld(player.getPlayer())){
			this.spellList.setEntries(spellList);
			return;
		}
		
		//Obtain all spells:
		Set<Trait> traits = TraitHolderCombinder.getSkillTreeReducedTraitsOfPlayer(player);
		for(Trait trait : traits){
			if(trait instanceof MagicSpellTrait){
				spellList.add((MagicSpellTrait) trait);
			}
		}
		
		Collections.sort(spellList);
		this.spellList.setEntries(spellList);
	}
	
	
	
	/**
	 * Changes to the next Spell in the List.
	 * 
	 * @return the next Spell.
	 */
	public MagicSpellTrait changeToNextSpell(){
		if(spellList.size() == 0) return null;
		if(System.currentTimeMillis() - lastEventTime < 100) return null;
		lastEventTime = System.currentTimeMillis();
		
		for(int i = 0; i < spellList.size(); i++){
			MagicSpellTrait next = spellList.next();
			if(next instanceof TraitWithRestrictions){
				boolean canUse = ((TraitWithRestrictions) next).isInLevelRange(player.getLevelManager().getCurrentLevel());
				if(canUse) return next;
			}else{
				return next;
			}
		}
		
		return null;
	}
	

	/**
	 * Changes to the Previous Spell in the List.
	 * 
	 * @return the next Spell.
	 */
	public TraitWithCost changeToPrevSpell() {
		if(spellList.size() == 0) return null;
		if(System.currentTimeMillis() - lastEventTime < 100) return null;
		lastEventTime = System.currentTimeMillis();
		
		for(int i = 0; i < spellList.size(); i++){
			TraitWithCost next = spellList.previous();
			if(next instanceof TraitWithRestrictions){
				boolean canUse = ((TraitWithRestrictions) next).isInLevelRange(player.getLevelManager().getCurrentLevel());
				if(canUse) return next;
			}else{
				return next;
			}
		}
		
		return null;
	}
	
	
	/**
	 * Returns all Spells available.
	 * 
	 * @return all spells available.
	 */
	public List<MagicSpellTrait> getAllSpells(){
		List<MagicSpellTrait> spells = this.spellList.getAllEntries();
		java.util.Iterator<MagicSpellTrait> it = spells.iterator();
		while(it.hasNext()){
			if(!it.next().isInLevelRange(player.getLevelManager().getCurrentLevel())) {
				it.remove();
			}
		}
		
		return spells;
	}
	
	
	/**
	 * Returns the {@link OwnManaManager} to check Spell casting or
	 * Mana Capabilities.
	 * 
	 * @return the OwnManaManager of the Player
	 */
	public ManaManager getManaManager(){
		return manaManager;
	}


	/**
	 * Returns the current Spell.
	 * 
	 * If no spells are present, null is returned.
	 * 
	 * @return the current spell.
	 */
	public MagicSpellTrait getCurrentSpell() {
		MagicSpellTrait current = spellList.currentEntry();
		if(current == null) return changeToNextSpell();
		
		if(current instanceof TraitWithRestrictions){
			boolean canUse = ((TraitWithRestrictions) current).isInLevelRange(player.getLevelManager().getCurrentLevel());
			if(!canUse) return changeToNextSpell();
		}
		
		return current;
	}
	
	
	/**
	 * Ticks the Container.
	 */
	public void tick(){
		manaManager.tick();
	}
	

	
	/**
	 * Checks if the player can cast the spell
	 * @param type to check
	 * @param cost to check
	 * @param castMaterial to check (may be null if type != ITEM)
	 * 
	 * @return true if he can, false if not
	 */
	public boolean canCastSpell(TraitWithCost trait){
		double cost = trait.getCost(player);
		switch(trait.getCostType()){
			case HEALTH: return player.getHealth() > cost;
			case MANA: return getManaManager().hasEnoughMana(cost);
			case ITEM: return playerHasItem(trait.getCastMaterialType(player), trait.getCastMaterialDamage(player), trait.getCastMaterialName(player), (int)cost);
			case HUNGER : return player.getPlayer().getFoodLevel() >= cost;
			case EXP : return player.getLevelManager().canRemove((int)cost);
			default: return false;
		}
	}
	
	/**
	 * Checks if the player has the Item.
	 * @param material to check
	 * @param damage to check
	 * @param name to check
	 * @return true if has.
	 */
	private boolean playerHasItem(Material material, short damage, String name, int amount){
		if(!player.isOnline()) return false;
		if(name != null) name = ChatColor.stripColor(name);
		
		Player player = this.player.getPlayer();
		PlayerInventory inv = player.getInventory();
		
		//Check for items:
		for(ItemStack item : inv){
			if(item == null) continue;
			
			//Check if item is correct:
			if(item.getType() != material) continue;
			if(item.getDurability() != damage) continue;
			
			//Check for name.
			if(name != null && !name.isEmpty()){
				if(!item.getItemMeta().hasDisplayName()) continue;
				
				String itemName = item.getItemMeta().getDisplayName();
				itemName = ChatColor.stripColor(itemName);
				
				if(!itemName.equalsIgnoreCase(name)) continue;
			}
			
			//If done, check!
			amount -= item.getAmount();
			if(amount <= 0) return true;
		}
		
		return false;
	}
	
	/**
	 * Removes the Item.
	 * @param material to check
	 * @param damage to check
	 * @param name to check
	 * @param amount to remove
	 */
	private void removeItem(Material material, short damage, String name, int amount){
		if(!player.isOnline()) return;
		if(name != null) name = ChatColor.stripColor(name);
		
		Player player = this.player.getPlayer();
		PlayerInventory inv = player.getInventory();
		
		//Check for items:
		for(int i = 0; i < inv.getSize(); i++){
			ItemStack item = inv.getItem(i);
			if(item == null) continue;
			
			//Check if item is correct:
			if(item.getType() != material) continue;
			if(item.getDurability() != damage) continue;
			
			//Check for name.
			if(name != null && !name.isEmpty()){
				if(!item.getItemMeta().hasDisplayName()) continue;
				
				String itemName = item.getItemMeta().getDisplayName();
				itemName = ChatColor.stripColor(itemName);
				
				if(!itemName.equalsIgnoreCase(name)) continue;
			}
			
			//If done, remove!
			int newAmount = item.getAmount() - amount;
			item.setAmount(newAmount);
			inv.setItem(i, item);
			
			amount = -newAmount;
			if(amount <= 0) return;
		}
	}
	
	/**
	 * Removes the spell cost from the player
	 * 
	 * @param trait to remove the cost from
	 */
	public void removeCost(TraitWithCost trait) {
		double cost = trait.getCost(player);
		switch(trait.getCostType()){
			case HEALTH: player.getHealthManager().damage(cost);break;
			case MANA: getManaManager().drownMana(cost); break;
			case ITEM: removeItem(trait.getCastMaterialType(player), trait.getCastMaterialDamage(player), trait.getCastMaterialName(player), (int)cost); break;
			case HUNGER: 
				int oldFoodLevel = player.getPlayer().getFoodLevel();
				int newFoodLevel = (int) (oldFoodLevel - cost);
				player.getPlayer().setFoodLevel(newFoodLevel < 0 ? 0 : newFoodLevel);
				
			case EXP:
				player.getLevelManager().removeExp((int) cost);
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
			TraitWithCost spell = spellList.currentEntry();
			if(spell instanceof AbstractBasicTrait){
				//first check Display name
				String name = ((AbstractBasicTrait) spell).getDisplayName();
				if(name.equalsIgnoreCase(spellName)){
					return true;
				}
				
				//second check Trait Name.
				name = ((AbstractBasicTrait) spell).getName();
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
		TraitWithCost currentSpell = spellList.currentEntry();
		if(currentSpell == null) return false;
		RacesAndClasses plugin = RacesAndClasses.getPlugin();
		
		@SuppressWarnings("deprecation")
		List<Block> blocks = player.getPlayer().getLineOfSight((HashSet<Byte>)null, 100);
		Block lookingAt = blocks.iterator().next();
		ItemStack wandItem = new ItemStack(plugin.getConfigManager().getGeneralConfig().getConfig_itemForMagic().generateItem());
		
		plugin.fireEventIntern(new PlayerInteractEvent(player.getPlayer(), Action.LEFT_CLICK_AIR, wandItem, 
				lookingAt, BlockFace.UP));
		return true;
	}
	
	
	/**
	 * Checks if the item passed is a Wand.
	 * @param item to check against
	 * @return true if the item passed is a wand for the player
	 */
	public boolean isWandItem(ItemStack item){
		if(player == null) return false;
		if(item == null) return false;
		
		RacesAndClasses plugin = RacesAndClasses.getPlugin();
		
		Set<WandItem> wands = new HashSet<>();
		wands.add(plugin.getConfigManager().getGeneralConfig().getConfig_itemForMagic());
		
		AbstractTraitHolder classHolder = player.getclass();
		if(classHolder != null){
			wands.addAll(classHolder.getAdditionalWandMaterials());
		}
		
		AbstractTraitHolder raceHolder = player.getRace();
		if(raceHolder != null){
			wands.addAll(raceHolder.getAdditionalWandMaterials());
		}
		
		for(WandItem wand : wands) if(wand.isItem(item)) return true;
		return false;
	}

}
