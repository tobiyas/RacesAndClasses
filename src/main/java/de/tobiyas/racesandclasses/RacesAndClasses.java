/*
 * Races - by tobiyas
 * http://
 *
 * powered by Kickstarter
 */

package de.tobiyas.racesandclasses;


import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import de.tobiyas.racesandclasses.chat.channels.ChannelManager;
import de.tobiyas.racesandclasses.commands.chat.CommandExecutor_LocalChat;
import de.tobiyas.racesandclasses.commands.chat.CommandExecutor_Whisper;
import de.tobiyas.racesandclasses.commands.chat.channels.CommandExecutor_BroadCast;
import de.tobiyas.racesandclasses.commands.chat.channels.CommandExecutor_Channel;
import de.tobiyas.racesandclasses.commands.chat.channels.CommandExecutor_Racechat;
import de.tobiyas.racesandclasses.commands.classes.CommandExecutor_Class;
import de.tobiyas.racesandclasses.commands.config.CommandExecutor_RaceConfig;
import de.tobiyas.racesandclasses.commands.debug.CommandExecutor_RaceDebug;
import de.tobiyas.racesandclasses.commands.general.CommandExecutor_EmptyCommand;
import de.tobiyas.racesandclasses.commands.general.CommandExecutor_PlayerInfo;
import de.tobiyas.racesandclasses.commands.general.CommandExecutor_RacesReload;
import de.tobiyas.racesandclasses.commands.health.CommandExecutor_HP;
import de.tobiyas.racesandclasses.commands.health.CommandExecutor_RaceGod;
import de.tobiyas.racesandclasses.commands.health.CommandExecutor_RaceHeal;
import de.tobiyas.racesandclasses.commands.help.CommandExecutor_RaceHelp;
import de.tobiyas.racesandclasses.commands.help.CommandExecutor_RacesVersion;
import de.tobiyas.racesandclasses.commands.help.CommandExecutor_TraitList;
import de.tobiyas.racesandclasses.commands.races.CommandExecutor_Race;
import de.tobiyas.racesandclasses.commands.statistics.CommandExecutor_Statistics;
import de.tobiyas.racesandclasses.commands.tutorial.CommandExecutor_RacesTutorial;
import de.tobiyas.racesandclasses.configuration.global.ConfigManager;
import de.tobiyas.racesandclasses.configuration.member.MemberConfigManager;
import de.tobiyas.racesandclasses.configuration.traits.TraitConfigManager;
import de.tobiyas.racesandclasses.cooldown.CooldownManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceManager;
import de.tobiyas.racesandclasses.eventprocessing.TraitEventManager;
import de.tobiyas.racesandclasses.healthmanagement.HealthManager;
import de.tobiyas.racesandclasses.listeners.RaCListenerRegister;
import de.tobiyas.racesandclasses.statistics.StatisticGatherer;
import de.tobiyas.racesandclasses.traitcontainer.container.TraitsList;
import de.tobiyas.racesandclasses.tutorial.TutorialManager;
import de.tobiyas.racesandclasses.util.bukkit.versioning.BukkitVersion;
import de.tobiyas.racesandclasses.util.bukkit.versioning.BukkitVersionBuilder;
import de.tobiyas.racesandclasses.util.consts.Consts;
import de.tobiyas.racesandclasses.util.tasks.DebugTask;
import de.tobiyas.racesandclasses.util.traitutil.DefaultTraitCopy;
import de.tobiyas.util.debug.logger.DebugLogger;
import de.tobiyas.util.metrics.SendMetrics;
import de.tobiyas.util.permissions.PermissionManager;


public class RacesAndClasses extends JavaPlugin{
	/**
	 * Set if currently in testing mode.
	 */
	public boolean testingMode = false;
	
	/**
	 * The Logger to call logging stuff on
	 */
	protected DebugLogger debugLogger;
	
	/**
	 * The Plugin description File. AKA plugin.yml
	 */
	protected PluginDescriptionFile description;

	/**
	 * The Prefix of the plugin pasted to the Console / Debugger
	 */
	protected String prefix;
	
	/**
	 * The Configuration Manager of the Plugin
	 */
	protected ConfigManager configManager;
	
	/**
	 * The Health Manager of the Plugin
	 */
	protected HealthManager healthManager;
	
	/**
	 * The CooldownManager of the Plugin
	 */
	protected CooldownManager cooldownManager;
	
	/**
	 * The singleton plugin Instance used.
	 */
	protected static RacesAndClasses plugin;
	
	/**
	 * The Permission manager handling Permission checks
	 */
	protected PermissionManager permManager;
	
	/**
	 * tells if an Error occurred on startup.
	 */
	protected boolean errored = false;
	
	/**
	 * The Statistic Gatherer gathering his stuff.
	 * ALL HAIL THE MIGHTY NOM NOM!
	 */
	protected StatisticGatherer statistics;
	
	/**
	 * The RaceManager of the Plugin
	 */
	protected RaceManager raceManager;
	
	/**
	 * The Class manager of the plugin
	 */
	protected ClassManager classManager;

	
	//Empty Constructor for Bukkit.
	public RacesAndClasses(){
	}
	
	
	@Override
	public void onEnable(){
		statistics = new StatisticGatherer(System.currentTimeMillis());
		//We seal the startup away to prevent further erroring afterwards.
		try{
			plugin = this;
			
			description = getDescription();
			prefix = "["+description.getName()+"] ";
			
			//temp debug logger
			debugLogger = new DebugLogger(this);
			debugLogger.setAlsoToPlugin(true);
			
			checkIfCBVersionGreaterRequired();
						
			fullReload(false, false);
			
			registerEvents();
			registerCommands();
			
			initMetrics();
			loadingDoneMessage();
		}catch(Exception e){
			log("An Error has accured during startup sequence: " + e.getLocalizedMessage());
			debugLogger.logStackTrace(e);
			
			errored = true;
			registerAllCommandsAsError();
		}
	}
	
	
	private void checkIfCBVersionGreaterRequired() {
		BukkitVersion version = BukkitVersionBuilder.getbukkitBuildNumber();
		if(version.getBukkitMainVersion() < Consts.minimalBukkitMainVersion ||
				version.getBukkitSubVersion() < Consts.minimalBukkitSubVersion ||
				version.getBukkitSubSubVersion() < Consts.minimalBukkitRevVersion){
			log("Bukkit Version is below 1.6. Compatibility mode is used. Double values will be rounded.");
		}
		
	}

	private void initManagers(){
		DebugTask.initDebugger();
		setupConfiguration();
		
		//copy default traits if non existent and enabled
		if(configManager.getGeneralConfig().isConfig_copyDefaultTraitsOnStartup()){
			DefaultTraitCopy.copyDefaultTraits();
		}

		//Create all Managers
		MemberConfigManager memberConfigManager = new MemberConfigManager();
		TraitConfigManager traitConfigManager = new TraitConfigManager();
		
		TraitEventManager traitEventManager = new TraitEventManager();
		raceManager = new RaceManager();
		classManager = new ClassManager();
		healthManager = new HealthManager();
		ChannelManager channelManager = new ChannelManager();
		permManager = new PermissionManager(this);
		
		cooldownManager = new CooldownManager();
		
		
		
		//init of Managers
		//Tutorials
		TutorialManager.init();
		if(!configManager.getGeneralConfig().isConfig_tutorials_enable()){
			TutorialManager.disable();
		}
		
		//Cooldown Manager
		cooldownManager.init();
		
		//Member Config
		memberConfigManager.reload();
		//Trait Config Manager
		traitConfigManager.init();
		//Trait Event Manager
		traitEventManager.init();
		//Race Manager
		raceManager.init();
		//Class Manager
		if(configManager.getGeneralConfig().isConfig_classes_enable()){
			classManager.init();
		}
		//Health Manager
		healthManager.init();
		//Channel Manager
		if(configManager.getGeneralConfig().isConfig_channels_enable()){
			channelManager.init();
		}
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
		new CommandExecutor_Statistics();
	}
	
	private void initMetrics(){
		if(configManager.getGeneralConfig().isConfig_metrics_enabled()){
			SendMetrics.sendMetrics(this, configManager.getGeneralConfig().isConfig_enableDebugOutputs());
		}
	}
	
	@Override
	public void onDisable(){
		if(!errored){
			shutDownSequenz(false);
		}
		
		log("disabled " + description.getFullName());

	}
	public void log(String message){
		if(debugLogger == null){
			this.getLogger().log(Level.INFO ,prefix + message);
		}else{
			debugLogger.log(prefix + message);
		}
	}
	
	public DebugLogger getDebugLogger(){
		return debugLogger;
	}


	private void registerEvents(){
		RaCListenerRegister.registerCustoms();
		RaCListenerRegister.registerProxys();
		RaCListenerRegister.registerGeneral();
	}
	
	private void loadingDoneMessage(){
		String traits = TraitsList.getAllVisibleTraits().size() + " traits";
		String races = ", " + plugin.getRaceManager().listAllVisibleHolders().size() + " races";
		
		String classes = "";
		if(configManager.getGeneralConfig().isConfig_classes_enable()){
			classes = ", " + plugin.getClassManager().getAllHolderNames().size() + " classes";
		}
		
		String channels = "";
		if(configManager.getGeneralConfig().isConfig_channels_enable()){
			channels = ", " + ChannelManager.GetInstance().listAllChannels().size() + " channels";
		}
		
		String events = ", hooked " + TraitEventManager.getInstance().getRegisteredEventsAsName().size() + " Events";
		
		log("loaded: " + traits + races + classes + channels + events);
		log(description.getFullName() + " fully loaded with Permissions: " + permManager.getPermissionsName());
	}


	private void setupConfiguration(){
		configManager = new ConfigManager();
		configManager.init();
		setupDebugLogger();
	}
	
	private void setupDebugLogger(){
		debugLogger = new DebugLogger(this);
		if(!configManager.getGeneralConfig().isConfig_enableDebugOutputs()){
			debugLogger.disable();
		}
		
		if(!configManager.getGeneralConfig().isConfig_enableErrorUpload()){
			debugLogger.enableUploads(false);
		}
	
		debugLogger.setAlsoToPlugin(true);
	}
	
	private void registerAllCommandsAsError() {
		Set<String> commands = plugin.getDescription().getCommands().keySet();
		for(String command : commands){
			new CommandExecutor_EmptyCommand(command);
		}
	}

	
	
	public ConfigManager getConfigManager(){
		return configManager;
	}
	
	
	public PermissionManager getPermissionManager(){
		return permManager;
	}
	
	public static RacesAndClasses getPlugin(){
		return plugin;
	}

	public long fullReload(boolean shutDownBefore, boolean useGC){
		long time = System.currentTimeMillis();
		if(shutDownBefore){
			shutDownSequenz(useGC);
		}
			
		initManagers();
		return System.currentTimeMillis() - time;
	}
	
	private void shutDownSequenz(boolean useGC){
		healthManager.saveHealthContainer();
		debugLogger.shutDown();
		plugin.reloadConfig();
		cooldownManager.shutdown();
		ChannelManager.GetInstance().saveChannels();
		TutorialManager.shutDown();
		Bukkit.getScheduler().cancelTasks(this);
		if(useGC)
			System.gc();
	}
	
	
	/**
	 * Fires an Event to the Bukkit Plugin event service
	 * 
	 * @param event the event to call
	 */
	public void fireEventToBukkit(Event event){
		this.getServer().getPluginManager().callEvent(event);
	}
	
	
	/**
	 * Fires an Event to the internal {@link TraitEventManager}.
	 */
	public void fireEventIntern(Event event){
		TraitEventManager.fireEvent(event);
	}
	
	
	/**
	 * Returns the {@link CooldownManager}
	 */
	public CooldownManager getCooldownManager(){
		return cooldownManager;
	}

	
	/**
	 * Returns the {@link StatisticGatherer}
	 */
	public StatisticGatherer getStatistics(){
		return statistics;
	}


	/**
	 * @return the raceManager
	 */
	public RaceManager getRaceManager() {
		return raceManager;
	}


	/**
	 * @return the classManager
	 */
	public ClassManager getClassManager() {
		return classManager;
	}
}
