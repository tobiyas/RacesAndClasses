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

import java.io.File;
import java.util.Set;

import net.gravitydevelopment.updater.Updater;
import net.gravitydevelopment.updater.Updater.UpdateType;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;

import com.avaje.ebean.EbeanServer;

import de.tobiyas.racesandclasses.chat.channels.ChannelManager;
import de.tobiyas.racesandclasses.commands.chat.CommandExecutor_LocalChat;
import de.tobiyas.racesandclasses.commands.chat.CommandExecutor_Whisper;
import de.tobiyas.racesandclasses.commands.chat.channels.CommandExecutor_BroadCast;
import de.tobiyas.racesandclasses.commands.chat.channels.CommandExecutor_Channel;
import de.tobiyas.racesandclasses.commands.chat.channels.CommandExecutor_Racechat;
import de.tobiyas.racesandclasses.commands.classes.CommandExecutor_Class;
import de.tobiyas.racesandclasses.commands.config.CommandExecutor_RaceConfig;
import de.tobiyas.racesandclasses.commands.debug.CommandExecutor_Edit;
import de.tobiyas.racesandclasses.commands.debug.CommandExecutor_RaceDebug;
import de.tobiyas.racesandclasses.commands.debug.CommandExecutor_SaveNow;
import de.tobiyas.racesandclasses.commands.force.CommandExecutor_ForceClass;
import de.tobiyas.racesandclasses.commands.force.CommandExecutor_ForceRace;
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
import de.tobiyas.racesandclasses.commands.level.Command_RACLevel;
import de.tobiyas.racesandclasses.commands.races.CommandExecutor_Race;
import de.tobiyas.racesandclasses.commands.statistics.CommandExecutor_Statistics;
import de.tobiyas.racesandclasses.commands.tutorial.CommandExecutor_RacesTutorial;
import de.tobiyas.racesandclasses.configuration.managing.ConfigManager;
import de.tobiyas.racesandclasses.cooldown.CooldownManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.loadingerrors.TraitHolderLoadingErrorHandler;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceManager;
import de.tobiyas.racesandclasses.entitystatusmanager.poison.PoisonManager;
import de.tobiyas.racesandclasses.entitystatusmanager.stun.StunManager;
import de.tobiyas.racesandclasses.eventprocessing.TraitEventManager;
import de.tobiyas.racesandclasses.listeners.RaCListenerRegister;
import de.tobiyas.racesandclasses.persistence.PersistenceStorageManager;
import de.tobiyas.racesandclasses.persistence.converter.ConverterChecker;
import de.tobiyas.racesandclasses.persistence.db.AlternateEbeanServerImpl;
import de.tobiyas.racesandclasses.persistence.file.YAMLPeristanceSaver;
import de.tobiyas.racesandclasses.playermanagement.PlayerManager;
import de.tobiyas.racesandclasses.statistics.StatisticGatherer;
import de.tobiyas.racesandclasses.traitcontainer.TraitStore;
import de.tobiyas.racesandclasses.traitcontainer.container.TraitsList;
import de.tobiyas.racesandclasses.translation.TranslationManagerHolder;
import de.tobiyas.racesandclasses.tutorial.TutorialManager;
import de.tobiyas.racesandclasses.util.bukkit.versioning.BukkitVersion;
import de.tobiyas.racesandclasses.util.bukkit.versioning.BukkitVersionBuilder;
import de.tobiyas.racesandclasses.util.consts.Consts;
import de.tobiyas.racesandclasses.util.traitutil.DefaultTraitCopy;
import de.tobiyas.util.UtilsUsingPlugin;
import de.tobiyas.util.metrics.SendMetrics;


public class RacesAndClasses extends UtilsUsingPlugin{
	/**
	 * Set if currently in testing mode.
	 */
	public boolean testingMode = false;
	
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
	
	/**
	 * The StunManager of the plugin
	 */
	protected StunManager stunManager;
	
	/**
	 * The poisonManager of the Plugin
	 */
	protected PoisonManager poisonManager;
	
	/**
	 * The alternative EBean Impl of the Bukkit Ebean Interface
	 */
	protected AlternateEbeanServerImpl alternateEbeanServer;
	
	
	
	//Empty Constructor for Bukkit.
	public RacesAndClasses(){
	}
	
	/**
	 * ONLY FOR TESTING!!!
	 * 
	 * @param loader
	 * @param description
	 * @param dataFolder
	 * @param file
	 */
	protected RacesAndClasses(PluginDescriptionFile description, File dataFolder) {
		super(Bukkit.getServer(), null, description, dataFolder, null);
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
			getDebugLogger().setAlsoToPlugin(true);
			
			checkIfCBVersionGreaterRequired();
						
			fullReload(false, false);
			
			registerEvents();
			registerCommands();
			
			initMetrics();
			loadingDoneMessage();
			
		}catch(Exception e){
			log("An Error has occured during startup sequence: " + e.getLocalizedMessage());
			getDebugLogger().logStackTrace(e);
			
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
		
		setupConfiguration();
		YAMLPeristanceSaver.start(true); //TODO check async
		
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
		
		cooldownManager = new CooldownManager();
		stunManager = new StunManager();
		poisonManager = new PoisonManager();
		
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
		if(plugin.getConfigManager().getGeneralConfig().isConfig_enableRaces()){
			raceManager.init();
		}
		
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

		//Stun manager
		stunManager.init();
		
		//Poison manager
		poisonManager.init();
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
		new CommandExecutor_SaveNow();
		
		new CommandExecutor_ForceRace();
		new CommandExecutor_ForceClass();
		
		new Command_RACLevel();
		
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
		
		plugin = null;
		log("disabled " + description.getFullName());

	}
	

	private void registerEvents(){
		RaCListenerRegister.registerCustoms();
		RaCListenerRegister.registerProxys();
		RaCListenerRegister.registerGeneral();
		RaCListenerRegister.registerChatListeners();
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
				+ getPermissionManager().getPermissionsName());
		
		if(configManager.getGeneralConfig().isConfig_useAutoUpdater()){
			checkForUpdates();
		}
	}
	
	private void checkForUpdates(){
		new Updater(
				this, 
				Consts.CURSE_ID_FOR_PLUGIN, 
				this.getFile(), 
				UpdateType.DEFAULT, 
				true
			);
	}


	private void setupConfiguration(){
		configManager = new ConfigManager();
		
		//We pre reload this to have global vars ready.
		configManager.getGeneralConfig().reload();
		PersistenceStorageManager.startup();
		ConverterChecker.checkAllConvertionsNeeded();
		
		configManager.reload();
		setupDebugLogger();
	}
	
	private void setupDebugLogger(){		
		if(!configManager.getGeneralConfig().isConfig_enableDebugOutputs()){
			getDebugLogger().disable();
		}else{
			getDebugLogger().shutDown();
		}
		
		if(!configManager.getGeneralConfig().isConfig_enableErrorUpload()){
			getDebugLogger().enableUploads(false);
		}
	
		getDebugLogger().setAlsoToPlugin(true);
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
		getDebugLogger().shutDown();
		plugin.reloadConfig();
		cooldownManager.shutdown();
		getConfigManager().getMemberConfigManager().shutDown();
		channelManager.saveChannels();
		tutorialManager.shutDown();
		Bukkit.getScheduler().cancelTasks(this);
		TraitStore.destroyClassLoaders();
		
		stunManager.deinit();
		poisonManager.deinit();
		
		alternateEbeanServer.onShutdown();
		alternateEbeanServer = null;
		
		TranslationManagerHolder.shutdown();
		
		if(!configManager.getGeneralConfig().isConfig_savePlayerDataToDB()){
			log("Doing some YML file flushing. This can take a while.");
			YAMLPeristanceSaver.flushNow(false, false);
			YAMLPeristanceSaver.stop();
			log("YML file flushing done.");
		}
		
		if(useGC){
			System.gc();
		}
		
		//Close all open persistence connections
		PersistenceStorageManager.shutdownPersistence();
	}
	
	
	/**
	 * Checks if the DB has already created all important ORMs or not.
	 * If not it generates everything.
	 */
	private void checkDBAccess(){
		getDatabase();
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
	public EbeanServer getDatabase(){
		if(alternateEbeanServer == null){
			initEbeanServer();
		}

		return alternateEbeanServer.getDatabase();
	}


	private void initEbeanServer() {
		alternateEbeanServer = new AlternateEbeanServerImpl(this);
		alternateEbeanServer.initializeLocalSQLite();
	}
	
	/**
	 * Gets the plugin's classLoader.
	 * 
	 * @return
	 */
	public ClassLoader getPluginsClassLoader(){
		return super.getClassLoader();
	}


	public StunManager getStunManager() {
		return stunManager;
	}


	public PoisonManager getPoisonManager() {
		return poisonManager;
	}
	
	
}
