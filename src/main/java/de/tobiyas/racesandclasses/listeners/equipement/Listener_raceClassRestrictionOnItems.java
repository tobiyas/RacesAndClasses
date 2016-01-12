package de.tobiyas.racesandclasses.listeners.equipement;

import java.util.HashSet;
import java.util.List;
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
import de.tobiyas.util.collections.ListCreateUtils;

public class Listener_raceClassRestrictionOnItems implements Listener {

	public final static List<String> CLASS_RESTRICTION_PRE = ListCreateUtils.multi("needs class: ", "benötigt klasse: ");
	public final static List<String> RACE_RESTRICTION_PRE = ListCreateUtils.multi("needs race: ", "benötigt rasse: ");
	public final static List<String> LEVEL_RESTRICTION_PRE = ListCreateUtils.multi("needs level: ", "benötigt level: ");
	
	/**
	 * The plugin to use.
	 */
	private final RacesAndClasses plugin;
	
	
	public Listener_raceClassRestrictionOnItems() {
		this.plugin = RacesAndClasses.getPlugin();
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	
	@EventHandler
	public void playerInteract(PlayerInteractEvent event){
		if(plugin.getConfigManager().getGeneralConfig().isConfig_disableArmorChecking()) return;
		
		RaCPlayer player = RaCPlayerManager.get().getPlayer(event.getPlayer());
		ItemStack item = event.getItem();
		
		
		if(!playerMayEquip(player, item)){
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You may not use this item.");
		}
	}
	
	@EventHandler
	public void playerEquip(PlayerEquipsArmorEvent event){
		if(plugin.getConfigManager().getGeneralConfig().isConfig_disableArmorChecking()) return;
		
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
			
			for(String recog : CLASS_RESTRICTION_PRE){
				if(loreString.startsWith(recog)){
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
			}
			
			
			for(String recog : RACE_RESTRICTION_PRE){
				if(loreString.startsWith(recog)){
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
			}

			for(String recog : LEVEL_RESTRICTION_PRE){
				if(loreString.startsWith(recog)){
					String needed = loreString.split(": ")[1];
					int neededLevel = 0;
					try{
						neededLevel = Integer.parseInt(needed);
					}catch(NumberFormatException exp){}
					
					int currentLevel = LevelAPI.getCurrentLevel(player.getPlayer());
					if(currentLevel < neededLevel) return false;
				}
			}
		}
		
		return true;
	}
}
