package de.tobiyas.racesandclasses.listeners.externalchatlistener;

import net.milkbowl.vault.chat.Chat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.ClassAPI;
import de.tobiyas.racesandclasses.APIs.RaceAPI;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.classevent.AfterClassChangedEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.raceevent.AfterRaceChangedEvent;

public class VaultChatListener implements Listener{

	private final RacesAndClasses plugin;
	
	public VaultChatListener() {
		plugin = RacesAndClasses.getPlugin();
		
		//no vault - no problem!
		if(Bukkit.getPluginManager().getPlugin("Vault") == null) return;
		Bukkit.getPluginManager().registerEvents(this, plugin);
		
		initOnline();
	}

	/**
	 * Inits the online players.
	 */
	private void initOnline() {
		for(Player player : Bukkit.getOnlinePlayers()){
			initPlayer(player);
		}
	}
	
	
	/**
	 * Inits a player.
	 * @param player to init
	 */
	private void initPlayer(Player player){
		try{
			RegisteredServiceProvider<Chat> chatProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
			if (chatProvider != null) {
	        	String className = ClassAPI.getClassNameOfPlayer(player);
	        	String raceName = RaceAPI.getRaceNameOfPlayer(player);
	        	
	            chatProvider.getProvider().setPlayerInfoString(player, "class", className);
	            chatProvider.getProvider().setPlayerInfoString(player, "race", raceName);
			}
		}catch(Throwable exp){}
	}
	
	
	@EventHandler
	public void classChanged(AfterClassChangedEvent event){
		initPlayer(event.getPlayer());
	}

	@EventHandler
	public void classChanged(AfterRaceChangedEvent event){
		initPlayer(event.getPlayer());
	}

}
