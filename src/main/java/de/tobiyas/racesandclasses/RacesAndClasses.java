/*
 * Races - by tobiyas
 * http://
 *
 * powered by Kickstarter
 */

package de.tobiyas.racesandclasses;


import static de.tobiyas.racesandclasses.statistics.StartupStatisticCategory.ChannelManager;
import static de.tobiyas.racesandclasses.statistics.StartupStatisticCategory.ClassManager;
import static de.tobiyas.racesandclasses.statistics.StartupStatisticCategory.Config;
import static de.tobiyas.racesandclasses.statistics.StartupStatisticCategory.CooldownManager;
import static de.tobiyas.racesandclasses.statistics.StartupStatisticCategory.ManagerConstructor;
import static de.tobiyas.racesandclasses.statistics.StartupStatisticCategory.PlayerManager;
import static de.tobiyas.racesandclasses.statistics.StartupStatisticCategory.RaceManager;
import static de.tobiyas.racesandclasses.statistics.StartupStatisticCategory.TraitCopy;
import static de.tobiyas.racesandclasses.statistics.StartupStatisticCategory.TraitManager;
import static de.tobiyas.racesandclasses.statistics.StartupStatisticCategory.TutorialManager;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import javax.persistence.PersistenceException;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import de.tobiyas.racesandclasses.chat.channels.ChannelManager;
import de.tobiyas.racesandclasses.chat.channels.container.ChannelSaveContainer;
import de.tobiyas.racesandclasses.commands.chat.CommandExecutor_LocalChat;
import de.tobiyas.racesandclasses.commands.chat.CommandExecutor_Whisper;
import de.tobiyas.racesandclasses.commands.chat.channels.CommandExecutor_BroadCast;
import de.tobiyas.racesandclasses.commands.chat.channels.CommandExecutor_Channel;
import de.tobiyas.racesandclasses.commands.chat.channels.CommandExecutor_Racechat;
import de.tobiyas.racesandclasses.commands.classes.CommandExecutor_Class;
import de.tobiyas.racesandclasses.commands.config.CommandExecutor_RaceConfig;
import de.tobiyas.racesandclasses.commands.debug.CommandExecutor_Edit;
import de.tobiyas.racesandclasses.commands.debug.CommandExecutor_RaceDebug;
import de.tobiyas.racesandclasses.commands.general.CommandExecutor_EmptyCommand;
import de.tobiyas.racesandclasses.commands.general.CommandExecutor_PlayerInfo;
import de.tobiyas.racesandclasses.commands.general.CommandExecutor_RacesReload;
import de.tobiyas.racesandclasses.commands.health.CommandExecutor_HP;
import de.tobiyas.racesandclasses.commands.health.CommandExecutor_RaceGod;
import de.tobiyas.racesandclasses.commands.health.CommandExecutor_RaceHeal;
import de.tobiyas.racesandclasses.commands.health.CommandExecutor_ShowTraits;
import de.tobiyas.racesandclasses.commands.help.CommandExecutor_PermissionCheck;
import de.tobiyas.racesandclasses.commands.help.CommandExecutor_RaceHelp;
import de.tobiyas.racesandclasses.commands.help.CommandExecutor_RacesVersion;
import de.tobiyas.racesandclasses.commands.help.CommandExecutor_TraitList;
import de.tobiyas.racesandclasses.commands.races.CommandExecutor_Race;
import de.tobiyas.racesandclasses.commands.statistics.CommandExecutor_Statistics;
import de.tobiyas.racesandclasses.commands.tutorial.CommandExecutor_RacesTutorial;
import de.tobiyas.racesandclasses.configuration.managing.ConfigManager;
import de.tobiyas.racesandclasses.configuration.member.database.DBConfigOption;
import de.tobiyas.racesandclasses.cooldown.CooldownManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.PlayerHolderAssociation;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.loadingerrors.TraitHolderLoadingErrorHandler;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceManager;
import de.tobiyas.racesandclasses.eventprocessing.TraitEventManager;
import de.tobiyas.racesandclasses.listeners.RaCListenerRegister;
import de.tobiyas.racesandclasses.playermanagement.PlayerManager;
import de.tobiyas.racesandclasses.playermanagement.PlayerSavingContainer;
import de.tobiyas.racesandclasses.statistics.StatisticGatherer;
import de.tobiyas.racesandclasses.traitcontainer.container.TraitsList;
import de.tobiyas.racesandclasses.tutorial.TutorialManager;
import de.tobiyas.racesandclasses.util.bukkit.versioning.BukkitVersion;
import de.tobiyas.racesandclasses.util.bukkit.versioning.BukkitVersionBuilder;
import de.tobiyas.racesandclasses.util.consts.Consts;
import de.tobiyas.racesandclasses.util.persistence.DBConverter;
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
	 * The Player Manager of the Plugin
	 */
	protected PlayerManager playerManager;
	
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
	
	/**
	 * The Channel manager used to manage channel activity
	 */
	protected ChannelManager channelManager;
	
	/**
	 * The Tutorial Manager to start / step the tutorial on.
	 */
	protected TutorialManager tutorialManager;

	
	
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
			prefix = "[" + description.getName() + "] ";
			
			//temporary debug logger
			debugLogger = new DebugLogger(this);
			debugLogger.setAlsoToPlugin(true);
			
			checkIfCBVersionGreaterRequired();
						
			fullReload(false, false);
			
			registerEvents();
			registerCommands();
			
			initMetrics();
			loadingDoneMessage();
			
		}catch(Exception e){
			log("An Error has occured during startup sequence: " + e.getLocalizedMessage());
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
		long currentTime = System.currentTimeMillis();
		
		DebugTask.initDebugger();
		checkForDBTransfer();
		setupConfiguration();
		
		Config.timeInMiliSeconds = System.currentTimeMillis() - currentTime;
		currentTime = System.currentTimeMillis();
		
		//copy default traits if non existent and enabled
		if(configManager.getGeneralConfig().isConfig_copyDefaultTraitsOnStartup()){
			DefaultTraitCopy.copyDefaultTraits();
		}

		TraitCopy.timeInMiliSeconds = System.currentTimeMillis() - currentTime;
		currentTime = System.currentTimeMillis();
		
		//Create all Managers
		TraitEventManager traitEventManager = new TraitEventManager();
		
		tutorialManager = new TutorialManager();
		raceManager = new RaceManager();
		classManager = new ClassManager();
		playerManager = new PlayerManager();
		channelManager = new ChannelManager();
		permManager = new PermissionManager(this);
		
		cooldownManager = new CooldownManager();
		
		ManagerConstructor.timeInMiliSeconds = System.currentTimeMillis() - currentTime;
		currentTime = System.currentTimeMillis();
		
		//init of Managers
		//Tutorials
		tutorialManager.reload();
		if(!configManager.getGeneralConfig().isConfig_tutorials_enable()){
			tutorialManager.disable();
		}
		
		TutorialManager.timeInMiliSeconds = System.currentTimeMillis() - currentTime;
		currentTime = System.currentTimeMillis();
		
		//Cooldown Manager
		cooldownManager.init();
		
		CooldownManager.timeInMiliSeconds = System.currentTimeMillis() - currentTime;
		currentTime = System.currentTimeMillis();

		//Trait Event Manager
		traitEventManager.init();
		
		TraitManager.timeInMiliSeconds = System.currentTimeMillis() - currentTime;
		currentTime = System.currentTimeMillis();
		
		//Race Manager
		raceManager.init();
		
		RaceManager.timeInMiliSeconds = System.currentTimeMillis() - currentTime;
		currentTime = System.currentTimeMillis();
		
		//Class Manager
		if(configManager.getGeneralConfig().isConfig_classes_enable()){
			classManager.init();
		}
		
		ClassManager.timeInMiliSeconds = System.currentTimeMillis() - currentTime;
		currentTime = System.currentTimeMillis();
		
		//Player Manager
		playerManager.init();
		
		PlayerManager.timeInMiliSeconds = System.currentTimeMillis() - currentTime;
		currentTime = System.currentTimeMillis();
		
		//Channel Manager
		if(configManager.getGeneralConfig().isConfig_channels_enable()){
			channelManager.init();
		}
		
		ChannelManager.timeInMiliSeconds = System.currentTimeMillis() - currentTime;
	}
	
	/**
	 * Converts the PlayerData YML file to DB
	 */
	private void checkForDBTransfer() {
		DBConverter.convertAll();
	}


	private void registerCommands(){
		long currentTime = System.currentTimeMillis();
		
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
		new CommandExecutor_PermissionCheck();
		
		new CommandExecutor_RacesReload();
		new CommandExecutor_RacesVersion();
		new CommandExecutor_Statistics();
		new CommandExecutor_ShowTraits();
		new CommandExecutor_Edit();
		
		if(System.currentTimeMillis() - currentTime > 1000){
			log("Took too long to Init all commands! Please report this. Time taken: " + (System.currentTimeMillis() - currentTime) + " mSecs.");
		}
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
			this.getLogger().log(Level.INFO , prefix + message);
		}else{
			debugLogger.log(message);
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
		if(TraitHolderLoadingErrorHandler.evalAndSave()){
			log("There where errors in the races.yml or classes.yml. Look into 'HolderStartupErrors.log' for more infos.");
		}

		
		String traits = TraitsList.getAllVisibleTraits().size() + " traits";
		String races = ", " + plugin.getRaceManager().listAllVisibleHolders().size() + " races";
		
		String classes = "";
		if(configManager.getGeneralConfig().isConfig_classes_enable()){
			classes = ", " + plugin.getClassManager().getAllHolderNames().size() + " classes";
		}
		
		String channels = "";
		if(configManager.getGeneralConfig().isConfig_channels_enable()){
			channels = ", " + channelManager.listAllChannels().size() + " channels";
		}
		
		String events = ", hooked " + TraitEventManager.getInstance().getRegisteredEventsAsName().size() + " Events";
		
		log("loaded: " + traits + races + classes + channels + events);
		log(description.getName() + " Version: '" + Consts.detailedVersionString + "' fully loaded with Permissions: " 
				+ permManager.getPermissionsName());
		
	}


	private void setupConfiguration(){
		configManager = new ConfigManager();
		
		//We pre reload this to have global vars ready.
		configManager.getGeneralConfig().reload();
		if(configManager.getGeneralConfig().isConfig_savePlayerDataToDB()){
			this.checkForDBTransfer();
		}
		
		configManager.reload();
		setupDebugLogger();
	}
	
	private void setupDebugLogger(){
		if(debugLogger == null){
			debugLogger = new DebugLogger(this);
		}
		
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
		
		checkDBAccess();
		initManagers();
		return System.currentTimeMillis() - time;
	}
	
	private void shutDownSequenz(boolean useGC){
		playerManager.savePlayerContainer();
		debugLogger.shutDown();
		plugin.reloadConfig();
		cooldownManager.shutdown();
		channelManager.saveChannels();
		tutorialManager.shutDown();
		Bukkit.getScheduler().cancelTasks(this);
		if(useGC){
			System.gc();
		}
	}
	
	
	/**
	 * Checks if the DB has already created all important ORMs or not.
	 * If not it generates everything.
	 */
	private void checkDBAccess(){
		try {
			if(getDatabase() == null){
				installDDL();
				return;
			}
			
			for(Class<?> dbClasses : getDatabaseClasses()){
				getDatabase().find(dbClasses).findRowCount();			
			}
			
        } catch (PersistenceException ex) {
            log("Installing database for " + getDescription().getName() + " due to first time usage");
            installDDL();
        }
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
	
	
	/**
	 * @return the channelManager
	 * @return
	 */
	public ChannelManager getChannelManager(){
		return channelManager;
	}


	/**
	 * @return the playerManager
	 */
	public PlayerManager getPlayerManager() {
		return playerManager;
	}


	/**
	 * @return the tutorialManager
	 */
	public TutorialManager getTutorialManager() {
		return tutorialManager;
	}
	
	
	 @Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> list = new LinkedList<Class<?>>();
        list.add(DBConfigOption.class);
        list.add(PlayerSavingContainer.class);
        list.add(PlayerHolderAssociation.class);
        list.add(ChannelSaveContainer.class);
        //TODO add all DB classes here.
        
        return list;
    }
}
