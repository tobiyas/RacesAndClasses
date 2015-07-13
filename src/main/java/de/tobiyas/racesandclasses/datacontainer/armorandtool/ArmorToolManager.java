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
package de.tobiyas.racesandclasses.datacontainer.armorandtool;

import java.util.HashSet;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.resolvers.WorldResolver;
import de.tobiyas.racesandclasses.util.items.ItemUtils;
import de.tobiyas.racesandclasses.util.items.ItemUtils.ArmorSlot;
import de.tobiyas.racesandclasses.util.items.ItemUtils.ItemQuality;

public class ArmorToolManager {

	private HashSet<AbstractItemPermission> itemPerms;
	private RaCPlayer player;
	
	public ArmorToolManager(RaCPlayer player){
		this.player = player;
		this.itemPerms = new HashSet<AbstractItemPermission>();
	}
	
	
	public void rescanPermission(){
		itemPerms.clear();
		if(player != null && !player.isOnline() && WorldResolver.isOnDisabledWorld(player)){
			itemPerms.add(new AllItemsPermission());
			return;
		}
		
		AbstractTraitHolder container = player.getRace();
		if(container != null){
			for(ItemQuality quality : container.getArmorPerms()){
				addPerm(quality);
			}
		}
			
		//Add ItemIDs or other here.
		
		AbstractTraitHolder classContainer = player.getclass();
		if(classContainer != null){
			for(ItemQuality quality : classContainer.getArmorPerms()){
				addPerm(quality);
			}
		}
		
		addDefaultPerm();
	}
	
	
	private void addDefaultPerm() {
		//permission for HEADS (skull, ...)
		itemPerms.add(new ItemPermission(Material.SKULL_ITEM));
		itemPerms.add(new ItemPermission(Material.PUMPKIN));
		
		//Other stuff to put into armor slots //TODO ?
	}

	
	/**
	 * Adds a Permission to the Permission list
	 * 
	 * @param quality
	 */
	private void addPerm(ItemQuality quality){
		for(AbstractItemPermission perm : itemPerms){
			if(perm.isAlreadyRegistered(quality)) {
				return;
			}
		}
		
		itemPerms.add(new MaterialArmorPermission(quality));
	}
	
	
	/**
	 * Checks if the item is permitted to be used
	 * 
	 * @param stack to check
	 * @return true if permission granted, false otherwise
	 */
	public boolean hasPermissionForItem(ItemStack stack){
		if(stack == null || stack.getType() == Material.AIR){
			return true;
		}
		
		for(AbstractItemPermission permission : itemPerms){
			if(permission.hasPermission(stack)){
				return true;
			}
		}
		
		return false;
	}
	
	
	/**
	 * Calculates the damage to the Armor
	 * 
	 * @param damage
	 * @param cause
	 * 
	 * @return the Damage after modification
	 */
	public double calcDamageToArmor(double damage, DamageCause cause){
		if(player == null){
			return damage;
		}
		
		//check Cause
		switch(cause){
			case DROWNING:
			case FIRE_TICK:
			case FIRE:
			case LAVA:
			case FALL:
			case POISON:
			case STARVATION:
			case SUICIDE:
			case LIGHTNING: return damage;
			default: break;
		}
		
		double playerArmor = getArmorLevel(player.getPlayer());
		double playerDamageReduce = 1D - ((8D * playerArmor) / 200D);
		return damage * playerDamageReduce;
	}

	
	/**
	 * Returns the Toughness of the Armor.
	 * 
	 * @param player to get from.
	 * @return the toughness.
	 */
	public int getArmorLevel(Player player) {
		//TODO fix me somehow!
		/*int armorLevel = 0;
		ItemStack inventory[] = player.getInventory().getArmorContents();
		
		for(ItemStack stack : inventory){
			//armorLevel += ItemUtils.getArmorValueOfItem(stack);
		}
		*/
		return 0;
	}

	/**
	 * Checks if Armor is equipped that is not valid to the current permissions.
	 * If found, it is stored in Inventory or thrown away if no place.
	 * 
	 * @return true if settings is correct. False if something got thrown away.
	 */
	public boolean checkArmorNotValidEquiped() {
		if(player == null){
			return true;
		}
		
		boolean disableArmorCheck = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_disableArmorChecking();
		if(disableArmorCheck) return true;
		

		ItemStack helmet = ItemUtils.getItemInArmorSlotOfPlayer(player.getPlayer(), ArmorSlot.HELMET);
		ItemStack chest = ItemUtils.getItemInArmorSlotOfPlayer(player.getPlayer(), ArmorSlot.CHESTPLATE);
		ItemStack legs = ItemUtils.getItemInArmorSlotOfPlayer(player.getPlayer(), ArmorSlot.LEGGINGS);
		ItemStack boots = ItemUtils.getItemInArmorSlotOfPlayer(player.getPlayer(), ArmorSlot.BOOTS);
		
		boolean everythingOK = true;
		
		//check helmet
		if(!hasPermissionForItem(helmet)){
			if(!player.getPlayer().getInventory().addItem(helmet).isEmpty()){
				player.getPlayer().getWorld().dropItem(player.getLocation(), helmet);
			}
			
			player.getPlayer().getInventory().setHelmet(new ItemStack(Material.AIR));
			everythingOK = false;
		}
		
		//check chest
		if(!hasPermissionForItem(chest)){
			if(!player.getPlayer().getInventory().addItem(chest).isEmpty()){
				player.getPlayer().getWorld().dropItem(player.getLocation(), chest);
			}
			
			player.getPlayer().getInventory().setChestplate(new ItemStack(Material.AIR));
			everythingOK = false;
		}
		
		//check legs
		if(!hasPermissionForItem(legs)){
			if(!player.getPlayer().getInventory().addItem(legs).isEmpty()){
				player.getPlayer().getWorld().dropItem(player.getLocation(), legs);
			}
			
			player.getPlayer().getInventory().setLeggings(new ItemStack(Material.AIR));
			everythingOK = false;
		}
		
		//check boots
		if(!hasPermissionForItem(boots)){
			if(!player.getPlayer().getInventory().addItem(boots).isEmpty()){
				player.getPlayer().getWorld().dropItem(player.getLocation(), boots);
			}
			
			player.getPlayer().getInventory().setBoots(new ItemStack(Material.AIR));
			everythingOK = false;
		}
		
		return everythingOK;
	}
	
	
}
