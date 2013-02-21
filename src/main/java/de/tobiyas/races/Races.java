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
import de.tobiyas.races.commands.chat.CommandExecutor_LocalChat;
import de.tobiyas.races.commands.chat.CommandExecutor_Whisper;
import de.tobiyas.races.commands.chat.channels.CommandExecutor_BroadCast;
import de.tobiyas.races.commands.chat.channels.CommandExecutor_Channel;
import de.tobiyas.races.commands.chat.channels.CommandExecutor_Racechat;
import de.tobiyas.races.commands.classes.CommandExecutor_Class;
import de.tobiyas.races.commands.config.CommandExecutor_RaceConfig;
import de.tobiyas.races.commands.debug.CommandExecutor_RaceDebug;
import de.tobiyas.races.commands.general.CommandExecutor_PlayerInfo;
import de.tobiyas.races.commands.general.CommandExecutor_RacesReload;
import de.tobiyas.races.commands.health.CommandExecutor_HP;
import de.tobiyas.races.commands.health.CommandExecutor_RaceGod;
import de.tobiyas.races.commands.health.CommandExecutor_RaceHeal;
import de.tobiyas.races.commands.help.CommandExecutor_RaceHelp;
import de.tobiyas.races.commands.help.CommandExecutor_RacesVersion;
import de.tobiyas.races.commands.help.CommandExecutor_TraitList;
import de.tobiyas.races.commands.races.CommandExecutor_Race;
import de.tobiyas.races.commands.tutorial.CommandExecutor_RacesTutorial;
import de.tobiyas.races.configuration.global.ChannelConfig;
import de.tobiyas.races.configuration.global.ConfigManager;
import de.tobiyas.races.configuration.global.GeneralConfig;
import de.tobiyas.races.configuration.member.MemberConfigManager;
import de.tobiyas.races.configuration.traits.TraitConfigManager;
import de.tobiyas.races.datacontainer.eventmanagement.TraitEventManager;
import de.tobiyas.races.datacontainer.health.HealthManager;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassManager;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceManager;
import de.tobiyas.races.datacontainer.traitcontainer.TraitsList;
import de.tobiyas.races.listeners.Listener_Entity;
import de.tobiyas.races.listeners.Listener_Player;
import de.tobiyas.races.tutorial.TutorialManager;
import de.tobiyas.races.util.tasks.DebugTask;
import de.tobiyas.util.debug.logger.DebugLogger;
import de.tobiyas.util.metrics.SendMetrics;
import de.tobiyas.util.permissions.PermissionManager;


public class Races extends JavaPlugin{
	public boolean testingMode = false;
	private Logger log;
	private DebugLogger debugLogger;
	
	private PluginDescriptionFile description;

	private String prefix;
	private ConfigManager configManager;
	
	private HealthManager hManager;
	
	private static Races plugin;
	
	private PermissionManager permManager;

	
	@Override
	public void onEnable(){
		try{
			plugin = this;
			log = Logger.getLogger("Minecraft");
			
			description = getDescription();
			prefix = "["+description.getName()+"] ";
			
			fullReload(false, false);
			
			registerEvents();
			registerCommands();
			
			initMetrics();
			loadingDoneMessage();
		}catch(Exception e){
			log("An Error has accured during startup sequence:");
			debugLogger.logStackTrace(e);
			e.printStackTrace();
		}
	}
	
	private void initManagers(){
		DebugTask.initDebugger();
		setupConfiguration();
		
		MemberConfigManager mcManager = new MemberConfigManager();
		TraitConfigManager tcManager = new TraitConfigManager();
		
		TraitEventManager tManager = new TraitEventManager();
		RaceManager rManager = new RaceManager();
		ClassManager cManager = new ClassManager();
		hManager = new HealthManager();
		ChannelManager chanManager = new ChannelManager();
		permManager = new PermissionManager(this);
		
		
		//init of Managers
		//Tutorials
		TutorialManager.init();
		if(!plugin.getGeneralConfig().getConfig_tutorials_enable())
			TutorialManager.disable();
		//Member Config
		mcManager.init();
		//Trait Config Manager
		tcManager.init();
		//Trait Event Manager
		tManager.init();
		//Race Manager
		rManager.init();
		//Class Manager
		if(plugin.getGeneralConfig().getConfig_classes_enable())
			cManager.init();
		//Health Manager
		hManager.init();
		//Channel Manager
		if(plugin.getGeneralConfig().getConfig_channels_enable())
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
		new CommandExecutor_LocalChat();
		new CommandExecutor_PlayerInfo();
		new CommandExecutor_RacesTutorial();
		
		new CommandExecutor_RacesReload();
		new CommandExecutor_RacesVersion();
	}
	
	private void initMetrics(){
		if(getGeneralConfig().getConfig_metrics_enabled())
			SendMetrics.sendMetrics(this, getGeneralConfig().getConfig_enableDebugOutputs());
	}
	
	@Override
	public void onDisable(){
		shutDownSequenz(false);
		
		log("disabled " + description.getFullName());

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
		String traits = TraitsList.getAllVisibleTraits().size() + " traits";
		String races = ", " + RaceManager.getManager().listAllVisibleRaces().size() + " races";
		
		String classes = "";
		if(plugin.getGeneralConfig().getConfig_classes_enable())
			classes = ", " +ClassManager.getInstance().getClassNames().size() + " classes";
		
		String channels = "";
		if(plugin.getGeneralConfig().getConfig_channels_enable())
			channels = ", " + ChannelManager.GetInstance().listAllChannels().size() + " channels";
		
		log("loaded: " + traits + races + classes + channels);
		log(description.getFullName() + " fully loaded with Permissions: " + permManager.getPermissionsName());
	}


	private void setupConfiguration(){
		configManager = new ConfigManager();
		configManager.init();
		setupDebugLogger();
	}
	
	private void setupDebugLogger(){
		debugLogger = new DebugLogger(this);
		if(!getGeneralConfig().getConfig_enableDebugOutputs())
			debugLogger.disable();
		
		if(!getGeneralConfig().getConfig_enableErrorUpload())
			debugLogger.enableUploads(false);
		
		debugLogger.setAlsoToPlugin(getGeneralConfig().getConfig_enableDebugWriteThrough());
	}

	
	public GeneralConfig getGeneralConfig(){
		return configManager.getGeneralConfig();
	}
	
	public ChannelConfig getChannelConfig(){
		return configManager.getChannelConfig();
	}
	
	public PermissionManager getPermissionManager(){
		return permManager;
	}
	
	public static Races getPlugin(){
		return plugin;
	}

	public long fullReload(boolean shutDownBefore, boolean useGC){
		long time = System.currentTimeMillis();
		if(shutDownBefore)
			shutDownSequenz(useGC);
		
		initManagers();
		return System.currentTimeMillis() - time;
	}
	
	private void shutDownSequenz(boolean useGC){
		hManager.saveHealthContainer();
		debugLogger.shutDown();
		plugin.reloadConfig();
		ChannelManager.GetInstance().saveChannels();
		TutorialManager.shutDown();
		Bukkit.getScheduler().cancelTasks(this);
		if(useGC)
			System.gc();
	}

}
