package de.tobiyas.racesandclasses.listeners.npc;

import static de.tobiyas.racesandclasses.translation.languages.Keys.already_have_race;
import static de.tobiyas.racesandclasses.translation.languages.Keys.no_race_selected;
import static de.tobiyas.racesandclasses.translation.languages.Keys.no_race_to_select;
import static de.tobiyas.racesandclasses.translation.languages.Keys.open_holder;
import net.citizensnpcs.api.event.NPCRightClickEvent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.configuration.global.GeneralConfig;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.gui.HolderInventory;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.classevent.PreClassSelectEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.raceevent.PreRaceSelectEvent;

public class Listener_NPCInteract implements Listener {

	/**
	 * The Plugin to use.
	 */
	private final RacesAndClasses plugin;
	
	
	public Listener_NPCInteract() {
		this.plugin = RacesAndClasses.getPlugin();
		//only register if Citizens is present.
		if(Bukkit.getPluginManager().getPlugin("Citizens") != null) Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	
	@EventHandler
	public void playerInteractNPCEvent(NPCRightClickEvent event){
		if(event.isCancelled()) return;
		
		String npcName = event.getNPC().getFullName();
		Player player = event.getClicker();
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
		
		
		GeneralConfig config = plugin.getConfigManager().getGeneralConfig();
		if(config.getConfig_npc_select_race().contains(npcName)){
			AbstractTraitHolder currentRace = racPlayer.getRace();
			if(currentRace != plugin.getRaceManager().getDefaultHolder()){
				LanguageAPI.sendTranslatedMessage(player, already_have_race,
						"race", currentRace.getDisplayName());
				
				return;
			}
			
			PreRaceSelectEvent ccEvent = new PreRaceSelectEvent(player, (RaceContainer) plugin.getRaceManager().getDefaultHolder());
			plugin.getServer().getPluginManager().callEvent(ccEvent);
			
			if(ccEvent.isCancelled()){
				player.sendMessage(ChatColor.RED + "[RaC] + " + ccEvent.getCancelMessage());
				return;
			}
			
			HolderInventory holderInventory = new HolderInventory(player, plugin.getRaceManager());
			
			if(holderInventory.getNumberOfHolder() <= 0){
				LanguageAPI.sendTranslatedMessage(player, no_race_to_select);
				return;
			}
			
			player.openInventory(holderInventory);
			LanguageAPI.sendTranslatedMessage(player, open_holder,
					"holder", "Race");
			return;
		}
		

		if(config.getConfig_npc_change_race().contains(npcName)){
			AbstractTraitHolder currentRace = racPlayer.getRace();
			if(currentRace == plugin.getRaceManager().getDefaultHolder()){
				LanguageAPI.sendTranslatedMessage(player, no_race_selected);
				return;
			}
			
			PreRaceSelectEvent ccEvent = new PreRaceSelectEvent(player, (RaceContainer) plugin.getRaceManager().getDefaultHolder());
			plugin.getServer().getPluginManager().callEvent(ccEvent);
			
			if(ccEvent.isCancelled()){
				player.sendMessage(ChatColor.RED + "[RaC] + " + ccEvent.getCancelMessage());
				return;
			}
			
			HolderInventory holderInventory = new HolderInventory(player, plugin.getRaceManager());
			if(holderInventory.getNumberOfHolder() <= 0){
				LanguageAPI.sendTranslatedMessage(player, no_race_to_select);
				return;
			}
			
			player.openInventory(holderInventory);
			LanguageAPI.sendTranslatedMessage(player, open_holder,
					"holder", "Race");
			return;
		}

		
		if(config.getConfig_npc_select_class().contains(npcName)){
			AbstractTraitHolder currentClass = racPlayer.getclass();
			if(currentClass != plugin.getClassManager().getDefaultHolder()){
				LanguageAPI.sendTranslatedMessage(player,"already_have_class",
						"clasname", currentClass.getDisplayName());
				return;
			}
			
			PreClassSelectEvent ccEvent = new PreClassSelectEvent(player, (ClassContainer) plugin.getClassManager().getDefaultHolder());
			plugin.getServer().getPluginManager().callEvent(ccEvent);
			
			if(ccEvent.isCancelled()){
				player.sendMessage(ChatColor.RED + "[RaC] + " + ccEvent.getCancelMessage());
				return;
			}
			
			HolderInventory holderInventory = new HolderInventory(player, plugin.getClassManager());
			if(holderInventory.getNumberOfHolder() <= 0){
				LanguageAPI.sendTranslatedMessage(player, "no_class_to_select");
				return;
			}
			
			player.openInventory(holderInventory);
			LanguageAPI.sendTranslatedMessage(player, "open_holder",
					"holder", "Class");
			return;
			
		}
		
		if(config.getConfig_npc_change_class().contains(npcName)){
			AbstractTraitHolder currentClass = racPlayer.getclass();
			if(currentClass == plugin.getClassManager().getDefaultHolder()){
				LanguageAPI.sendTranslatedMessage(player, "no_class_on_change");
				return;
			}
			
			PreClassSelectEvent ccEvent = new PreClassSelectEvent(player, (ClassContainer) plugin.getClassManager().getDefaultHolder());
			plugin.getServer().getPluginManager().callEvent(ccEvent);
			
			if(ccEvent.isCancelled()){
				player.sendMessage(ChatColor.RED + "[RaC] + " + ccEvent.getCancelMessage());
				return;
			}
			
			HolderInventory holderInventory = new HolderInventory(player, plugin.getClassManager());
			if(holderInventory.getNumberOfHolder() <= 0){
				LanguageAPI.sendTranslatedMessage(player,"no_class_to_select");
				return;
			}
			
			player.openInventory(holderInventory);
			LanguageAPI.sendTranslatedMessage(player, "open_holder",
					"holder", "Class");
			return;
		}
		
	}
}
