/*
 * Races - by tobiyas
 * http://
 *
 * powered by Kickstarter
 */

package de.tobiyas.races;


import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import de.tobiyas.races.chat.channels.ChannelManager;
import de.tobiyas.races.commands.chat.CommandExecutor_BroadCast;
import de.tobiyas.races.commands.chat.CommandExecutor_Racechat;
import de.tobiyas.races.commands.chat.CommandExecutor_Whisper;
import de.tobiyas.races.commands.chat.channels.CommandExecutor_Channel;
import de.tobiyas.races.commands.classes.CommandExecutor_Class;
import de.tobiyas.races.commands.config.CommandExecutor_RaceConfig;
import de.tobiyas.races.commands.debug.CommandExecutor_RaceDebug;
import de.tobiyas.races.commands.general.CommandExecutor_RacesReload;
import de.tobiyas.races.commands.health.CommandExecutor_HP;
import de.tobiyas.races.commands.health.CommandExecutor_RaceGod;
import de.tobiyas.races.commands.health.CommandExecutor_RaceHeal;
import de.tobiyas.races.commands.help.CommandExecutor_RaceHelp;
import de.tobiyas.races.commands.help.CommandExecutor_TraitList;
import de.tobiyas.races.commands.races.CommandExecutor_Race;
import de.tobiyas.races.configuration.global.Config;
import de.tobiyas.races.configuration.member.MemberConfigManager;
import de.tobiyas.races.configuration.traits.TraitConfigManager;
import de.tobiyas.races.datacontainer.health.HealthManager;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassManager;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceManager;
import de.tobiyas.races.datacontainer.traitcontainer.TraitsList;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.TraitEventManager;
import de.tobiyas.races.listeners.Listener_Entity;
import de.tobiyas.races.listeners.Listener_Player;
import de.tobiyas.util.debug.logger.DebugLogger;
import de.tobiyas.util.metrics.SendMetrics;
import de.tobiyas.util.permissions.PermissionManager;


public class Races extends JavaPlugin{
	private Logger log;
	private DebugLogger debugLogger;
	
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
		
		fullReload(false, false);
		
		registerEvents();
		registerCommands();
		
		initMetrics();
		loadingDoneMessage();
	}
	
	private void initManagers(){
		setupConfiguration();
		
		MemberConfigManager mcManager = new MemberConfigManager();
		TraitConfigManager tcManager = new TraitConfigManager();
		
		TraitEventManager tManager = new TraitEventManager();
		RaceManager rManager = new RaceManager();
		ClassManager cManager = new ClassManager();
		hManager = new HealthManager();
		ChannelManager chanManager = new ChannelManager();
		permManager = new PermissionManager(this);
		
		mcManager.init();
		tcManager.init();
		tManager.init();
		rManager.init();
		if(plugin.interactConfig().getconfig_classes_enable())
			cManager.init();
		hManager.init();
		if(plugin.interactConfig().getconfig_channels_enable())
			chanManager.init();
	}
	
	private void registerCommands(){
		new CommandExecutor_Race();
		new CommandExecutor_Racechat();
		new CommandExecutor_RaceHelp();
		new CommandExecutor_Whisper();
		new CommandExecutor_TraitList();
		new CommandExecutor_RaceHeal();
		new CommandExecutor_RaceConfig();
		new CommandExecutor_RaceDebug();
		new CommandExecutor_Class();
		new CommandExecutor_HP();
		new CommandExecutor_Channel();
		new CommandExecutor_RaceGod();
		new CommandExecutor_BroadCast();
		
		new CommandExecutor_RacesReload();
	}
	
	private void initMetrics(){
		if(interactConfig().getconfig_metrics_enabled())
			SendMetrics.sendMetrics(this);
	}
	
	@Override
	public void onDisable(){
		hManager.saveHealthContainer();
		debugLogger.shutDown();
		log("disabled "+description.getFullName());

	}
	public void log(String message){
		log.info(prefix+message);
	}
	
	public DebugLogger getDebugLogger(){
		return debugLogger;
	}


	private void registerEvents(){
		new Listener_Player();
		new Listener_Entity();
	}
	
	private void loadingDoneMessage(){
		String traits = TraitsList.getAllTraits().size() + " traits";
		String races = ", " + RaceManager.getManager().listAllRaces().size() + " races";
		
		String classes = "";
		if(plugin.interactConfig().getconfig_classes_enable())
			classes = ", " +ClassManager.getInstance().getClassNames().size() + " classes";
		
		String channels = "";
		if(plugin.interactConfig().getconfig_channels_enable())
			channels = ", " + ChannelManager.GetInstance().listAllChannels().size() + " channels";
		
		log("loaded: " + traits + races + classes + channels);
		log(description.getFullName() + " fully loaded with Permissions: " + permManager.getPermissionsName());
	}


	private void setupConfiguration(){
		config = new Config(this);
		setupDebugLogger();
	}
	
	private void setupDebugLogger(){
		debugLogger = new DebugLogger(this);
		if(!config.getconfig_enableDebugOutputs())
			debugLogger.disable();
		if(!config.getconfig_enableDebugOutputs())
			debugLogger.setAlsoToPlugin(false);
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

	public long fullReload(boolean shutDownBefore, boolean useGC){
		long time = System.currentTimeMillis();
		if(shutDownBefore){
			hManager.saveHealthContainer();
			debugLogger.shutDown();
			plugin.reloadConfig();
			ChannelManager.GetInstance().saveChannels();
			Bukkit.getScheduler().cancelTasks(this);
			if(useGC)
				System.gc();
		}
		initManagers();
		
		return System.currentTimeMillis() - time;
	}

}
