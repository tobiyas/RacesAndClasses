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
import de.tobiyas.races.commands.CommandExecutor_TraitList;
import de.tobiyas.races.commands.CommandExecutor_Whisper;
import de.tobiyas.races.configuration.global.Config;
import de.tobiyas.races.datacontainer.health.HealthManager;
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
	
	private HealthManager hManager;
	
	private static Races plugin;
	
	private PermissionManager permManager;

	
	@Override
	public void onEnable(){
		plugin = this;
		log = Logger.getLogger("Minecraft");
		description = getDescription();
		prefix = "["+description.getName()+"] ";

		setupConfiguration();
		
		
		initManagers();		
		
		registerEvents();

		registerCommands();

		permManager = new PermissionManager(this);

		log(description.getFullName() + " fully loaded with Permissions: " + permManager.getPermissionsName());
	}
	
	private void initManagers(){
		TraitEventManager tManager = new TraitEventManager();
		RaceManager rManager = new RaceManager();
		hManager = new HealthManager();
		
		tManager.init();
		rManager.init();
		hManager.init();
	}
	
	private void registerCommands(){
		new CommandExecutor_Race();
		new CommandExecutor_Racechat();
		new CommandExecutor_Raceinfo();
		new CommandExecutor_RaceList();
		new CommandExecutor_RaceHelp();
		new CommandExecutor_Whisper();
		new CommandExecutor_TraitList();
	}
	
	@Override
	public void onDisable(){
		hManager.saveHealthContainer();
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
