/*
 * Races - by tobiyas
 * http://
 *
 * powered by Kickstarter
 */

package de.tobiyas.races;


import java.util.logging.Logger;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import de.tobiyas.races.commands.CommandExecutor_Race;
import de.tobiyas.races.commands.CommandExecutor_RaceHelp;
import de.tobiyas.races.commands.CommandExecutor_RaceList;
import de.tobiyas.races.commands.CommandExecutor_Racechat;
import de.tobiyas.races.commands.CommandExecutor_Raceinfo;
import de.tobiyas.races.commands.CommandExecutor_Whisper;
import de.tobiyas.races.configuration.Config;
import de.tobiyas.races.datacontainer.race.RaceManager;
import de.tobiyas.races.datacontainer.traitcontainer.TraitEventManager;
import de.tobiyas.races.listeners.Listener_Entity;
import de.tobiyas.races.listeners.Listener_Player;
import de.tobiyas.util.permissions.PermissionManager;


public class Races extends JavaPlugin{
	private Logger log;
	private PluginDescriptionFile description;

	private String prefix;
	private Config config;
	
	private static Races plugin;
	
	private PermissionManager permManager;

	
	@Override
	public void onEnable(){
		plugin = this;
		log = Logger.getLogger("Minecraft");
		description = getDescription();
		prefix = "["+description.getName()+"] ";

		setupConfiguration();
		
		new TraitEventManager();
		new RaceManager();
		
		registerEvents();

		registerCommands();

		permManager = new PermissionManager(this);

		log(description.getFullName() + " fully loaded with Permissions: " + permManager.getPermissionsName());
	}
	
	private void registerCommands(){
		new CommandExecutor_Race();
		new CommandExecutor_Racechat();
		new CommandExecutor_Raceinfo();
		new CommandExecutor_RaceList();
		new CommandExecutor_RaceHelp();
		new CommandExecutor_Whisper();
	}
	
	@Override
	public void onDisable(){
		log("disabled "+description.getFullName());

	}
	public void log(String message){
		log.info(prefix+message);
	}


	private void registerEvents(){
		new Listener_Player();
		new Listener_Entity();
	}


	private void setupConfiguration(){
		config = new Config(this);
	}

	
	public Config interactConfig(){
		return config;
	}
	
	public PermissionManager getPermissionManager(){
		return permManager;
	}
	
	public static Races getPlugin(){
		return plugin;
	}

}
