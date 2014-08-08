package de.tobiyas.racesandclasses.listeners.equipement;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LevelAPI;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.eventprocessing.events.inventoryitemevents.PlayerEquipsArmorEvent;

public class Listener_raceClassRestrictionOnItems implements Listener {

	public final static String CLASS_RESTRICTION_PRE = "needs class: ";
	public final static String RACE_RESTRICTION_PRE = "needs race: ";
	public final static String LEVEL_RESTRICTION_PRE = "needs level: ";
	
	
	public Listener_raceClassRestrictionOnItems() {
		Bukkit.getPluginManager().registerEvents(this, RacesAndClasses.getPlugin());
	}

	
	@EventHandler
	public void playerInteract(PlayerInteractEvent event){
		RaCPlayer player = RaCPlayerManager.get().getPlayer(event.getPlayer());
		ItemStack item = event.getItem();
		
		
		if(!playerMayEquip(player, item)){
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You may not use this item.");
		}
	}
	
	@EventHandler
	public void playerEquip(PlayerEquipsArmorEvent event){
		RaCPlayer player = RaCPlayerManager.get().getPlayer(event.getPlayer());
		ItemStack item = event.getArmorItem();
		
		if(!playerMayEquip(player, item)){
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You may not equip this item.");
		}
	}
	
	
	/**
	 * Checks if the player may equip this item.
	 * 
	 * @param player to check
	 * @param item to check.
	 * 
	 * @return true if you can use it
	 */
	private boolean playerMayEquip(RaCPlayer player, ItemStack item){
		if(item == null) return true;
		
		if(!item.hasItemMeta()) return true;
		
		ItemMeta meta = item.getItemMeta();
		if(!meta.hasLore()) return true;
		
		for(String loreString : meta.getLore()){
			loreString = loreString.toLowerCase();
			loreString = ChatColor.stripColor(loreString);
			
			if(loreString.startsWith(CLASS_RESTRICTION_PRE)){
				String needed = loreString.split(": ")[1];
				ClassContainer classContainer = player.getclass();
				if(classContainer == null){
					return false;
				}else {
					Set<String> classes = new HashSet<String>();
					classes.add(classContainer.getDisplayName());
					for(AbstractTraitHolder parent : classContainer.getParents()){
						classes.add(parent.getDisplayName());
					}
					
					boolean found = false;
					for(String race : classes){
						if(race.equalsIgnoreCase(needed)) found = true;
					}
					
					if(!found) return false;
				}
				
			}
			
			if(loreString.startsWith(RACE_RESTRICTION_PRE)){
				String needed = loreString.split(": ")[1];
				RaceContainer raceContainer = player.getRace();
				if(raceContainer == null){
					return false;
				}else {
					Set<String> races = new HashSet<String>();
					races.add(raceContainer.getDisplayName());
					for(AbstractTraitHolder parent : raceContainer.getParents()){
						races.add(parent.getDisplayName());
					}
					
					boolean found = false;
					for(String race : races){
						if(race.equalsIgnoreCase(needed)) found = true;
					}
					
					if(!found) return false;
				}
			}

			if(loreString.startsWith(LEVEL_RESTRICTION_PRE)){
				String needed = loreString.split(": ")[1];
				int neededLevel = 0;
				try{
					neededLevel = Integer.parseInt(needed);
				}catch(NumberFormatException exp){}
				
				int currentLevel = LevelAPI.getCurrentLevel(player.getPlayer());
				if(currentLevel < neededLevel) return false;
			}
		}
		
		return true;
	}
}
