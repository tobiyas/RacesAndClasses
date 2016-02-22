/*******************************************************************************
 * Copyright 2014 Tob
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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.SwimmingSpeedTrait;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.HolderSelectedEvent;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.RemoveSuperConfigField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.pattern.TickEverySecondsTrait;
import de.tobiyas.racesandclasses.util.inventory.InventoryResync;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class SwimmingSpeedTrait extends TickEverySecondsTrait {

	/**
	 * The Level to set (1-3).
	 */
	private int level = 3;
	
	/**
	 * if new Boots should be generated if not present.
	 */
	private boolean generateNewBootsIfNotPresent = false;
	
	/**
	 * The Players with opened Inventories.
	 */
	private Set<UUID> playerWithOpenedInventories = new HashSet<UUID>();
	
	/**
	 * The Players with active Enchants.
	 */
	private Set<UUID> playerWithActiveEnchants = new HashSet<UUID>();
	
	/**
	 * The Players with generated Boots.
	 */
	private Set<UUID> playerWithGeneratedBoots = new HashSet<UUID>();
	
	
	@TraitEventsUsed(registerdClasses = {PlayerLoginEvent.class, HolderSelectedEvent.class})
	@Override
	public void generalInit() {
		super.generalInit();
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@Override
	public String getName() {
		return "SwimmingSpeedTrait";
	}


	
	@TraitInfos(category="passive", traitName="SwimmingSpeedTrait", visible=true)
	@Override
	public void importTrait() {
	}

	@Override
	public boolean isBetterThan(Trait trait) {
		if(trait instanceof SwimmingSpeedTrait){
			return level > ((SwimmingSpeedTrait)trait).level;
		}
		return false;
	}


	
	@TraitConfigurationNeeded(fields = {
			@TraitConfigurationField(fieldName = "level", classToExpect = Integer.class, optional = true),
			@TraitConfigurationField(fieldName = "generateNewBoots", classToExpect = Boolean.class, optional = true)
		}, removedFields = {
			@RemoveSuperConfigField(name = "seconds")
	})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		configMap.put("seconds", 1);
		super.setConfiguration(configMap);
		
		if(configMap.containsKey("level")){
			this.level = configMap.getAsInt("level");
		}
		
		if(configMap.containsKey("generateNewBoots")){
			this.generateNewBootsIfNotPresent = configMap.getAsBool("generateNewBoots");
		}
	}

	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "The trait changes (increases or decreases) the movement speed of a Player underwater.");
		return helpList;
	}
	

	@Override
	public boolean canBeTriggered(EventWrapper wrapper) {
		boolean canBeTriggered = super.canBeTriggered(wrapper);
		if(!canBeTriggered){
			removeEnchant(wrapper.getPlayer());
			playerWithActiveEnchants.remove(wrapper.getPlayer().getUniqueId());
			return false;
		}
		
		return true;
	}

	/**
	 * Removes an Enchantment from the Player.
	 * 
	 * @param racPlayer to use.
	 */
	private void removeEnchant(RaCPlayer racPlayer) {
		if(!racPlayer.isOnline()) return;
		Player player = racPlayer.getPlayer();
		
		ItemStack boots = player.getInventory().getBoots();
		if(boots == null || boots.getType() == Material.AIR) return;
		
		boots.removeEnchantment(Enchantment.DEPTH_STRIDER);
		if(playerWithGeneratedBoots.contains(racPlayer.getUniqueId())){
			boots = null;
			playerWithGeneratedBoots.remove(player.getUniqueId());
		}
		
		player.getInventory().setBoots(boots);
	}
	

	/**
	 * Adds an Enchantment to the Player.
	 * 
	 * @param racPlayer to use.
	 */
	private void addEnchant(RaCPlayer racPlayer) {
		if(!racPlayer.isOnline()) return;
		Player player = racPlayer.getPlayer();
		
		ItemStack boots = player.getInventory().getBoots();
		if(boots == null || boots.getType() == Material.AIR) {
			if(!generateNewBootsIfNotPresent) return;
			
			boots = new ItemStack(Material.LEATHER_BOOTS);
			playerWithGeneratedBoots.add(player.getUniqueId());
		}
		
		//if already has enchant. Remove him from the Naughty list.
		if(boots.getEnchantmentLevel(Enchantment.DEPTH_STRIDER) > 0){
			playerWithActiveEnchants.remove(racPlayer.getUniqueId());
			return;
		}
		
		boots.addUnsafeEnchantment(Enchantment.DEPTH_STRIDER, level);
		player.getInventory().setBoots(boots);
	}
	

	@Override
	protected String getPrettyConfigIntern() {
		return "Swims " + level + "x faster underwater";
	}

	@Override
	protected boolean tickDoneForPlayer(RaCPlayer player) {
		if(!player.isOnline()) return false;
		
		Location playerLocation = player.getLocation();
		Material standingIn = playerLocation.getBlock().getType();
		
		if(playerWithOpenedInventories.contains(player.getUniqueId())) return false;
		
		boolean alreadyOnList = playerWithActiveEnchants.contains(player.getUniqueId());
		boolean isLiquid = false;
		if(standingIn == Material.WATER || standingIn == Material.STATIONARY_WATER
				|| standingIn == Material.LAVA || standingIn == Material.STATIONARY_LAVA){
			isLiquid = true;
		}
		
		if(alreadyOnList && !isLiquid){
			playerWithActiveEnchants.remove(player.getUniqueId());
			removeEnchant(player);
			return true;
		}
		
		if(!alreadyOnList && isLiquid){
			playerWithActiveEnchants.add(player.getUniqueId());
			addEnchant(player);
			return true;
		}
		
		return true;
	}
	
	
	@EventHandler
	public void playerOpenInventory(InventoryOpenEvent event){
		HumanEntity player = event.getPlayer();
		if(!(player instanceof Player)) return;
		
		UUID id = player.getUniqueId();
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(id);
		if(racPlayer == null) return;
		
		if(!racPlayer.getTraits().contains(this)) return;
		this.playerWithOpenedInventories.add(id);
		
		if(playerWithActiveEnchants.contains(id)) {
			removeEnchant(racPlayer);
			this.playerWithActiveEnchants.remove(id);
		}
	}
	
	@EventHandler
	public void playerCloseInventory(InventoryCloseEvent event){
		HumanEntity player = event.getPlayer();
		if(!(player instanceof Player)) return;
		
		UUID id = player.getUniqueId();
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(id);
		if(racPlayer == null) return;
		
		this.playerWithOpenedInventories.remove(id);
		this.playerWithActiveEnchants.remove(racPlayer.getUniqueId());
	}
	
	
	@EventHandler
	public void playerClickBoots(InventoryClickEvent event){
		HumanEntity player = event.getWhoClicked();
		if(!(player instanceof Player)) return;
		
		UUID id = player.getUniqueId();
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(id);
		if(racPlayer == null) return;
		if(!playerWithActiveEnchants.contains(id)) return;
		
		if(event.getView().getType() != InventoryType.PLAYER) return;
		if(event.getSlot() == 8 && event.getRawSlot() == 8) {
			event.setCancelled(true);
			InventoryResync.resync((Player) player);
		}
	}
	
	@EventHandler
	public void playerLeavesServer(PlayerQuitEvent event){
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(event.getPlayer());
		if(playerWithActiveEnchants.contains(racPlayer.getUniqueId())) removeEnchant(racPlayer);
	}

	
	@Override
	protected String getPrettyConfigurationPre() {
		return "";
	}
	
	
	@Override
	public void deInit() {
		super.deInit();
		
		//remove ALL actives.
		for(UUID active : playerWithActiveEnchants){
			Player pl = Bukkit.getPlayer(active);
			if(pl != null){
				RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(pl);
				removeEnchant(racPlayer);
			}
		}
	}
}
