/*******************************************************************************
 * Copyright 2014 Tobias Welther
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
/*
 * Races - by tobiyas
 * http://
 *
 * powered by Kickstarter
 */

package de.tobiyas.racesandclasses;


import static de.tobiyas.racesandclasses.statistics.StartupStatisticCategory.ChannelManager;
import static de.tobiyas.racesandclasses.statistics.StartupStatisticCategory.ClassManager;
import static de.tobiyas.racesandclasses.statistics.StartupStatisticCategory.ConfigTotal;
import static de.tobiyas.racesandclasses.statistics.StartupStatisticCategory.CooldownManager;
import static de.tobiyas.racesandclasses.statistics.StartupStatisticCategory.ManagerConstructor;
import static de.tobiyas.racesandclasses.statistics.StartupStatisticCategory.PlayerManager;
import static de.tobiyas.racesandclasses.statistics.StartupStatisticCategory.RaceManager;
import static de.tobiyas.racesandclasses.statistics.StartupStatisticCategory.TraitCopy;
import static de.tobiyas.racesandclasses.statistics.StartupStatisticCategory.TraitManager;
import static de.tobiyas.racesandclasses.statistics.StartupStatisticCategory.TutorialManager;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.gravitydevelopment.updater.Updater;
import net.gravitydevelopment.updater.Updater.UpdateType;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;

import com.avaje.ebean.EbeanServer;

import de.tobiyas.racesandclasses.addins.spawning.RaceSpawnManager;
import de.tobiyas.racesandclasses.chat.channels.ChannelManager;
import de.tobiyas.racesandclasses.commands.CommandInterface;
import de.tobiyas.racesandclasses.commands.bind.CommandExecutor_BindTrait;
import de.tobiyas.racesandclasses.commands.bind.CommandExecutor_UseTrait;
import de.tobiyas.racesandclasses.commands.chat.CommandExecutor_LocalChat;
import de.tobiyas.racesandclasses.commands.chat.CommandExecutor_Whisper;
import de.tobiyas.racesandclasses.commands.chat.channels.CommandExecutor_BroadCast;
import de.tobiyas.racesandclasses.commands.chat.channels.CommandExecutor_Channel;
import de.tobiyas.racesandclasses.commands.chat.channels.CommandExecutor_Racechat;
import de.tobiyas.racesandclasses.commands.classes.CommandExecutor_Class;
import de.tobiyas.racesandclasses.commands.config.CommandExecutor_ConfigRegenerate;
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
import de.tobiyas.racesandclasses.commands.health.CommandExecutor_Mana;
import de.tobiyas.racesandclasses.commands.health.CommandExecutor_RaceGod;
import de.tobiyas.racesandclasses.commands.health.CommandExecutor_RaceHeal;
import de.tobiyas.racesandclasses.commands.health.CommandExecutor_ShowTraits;
import de.tobiyas.racesandclasses.commands.help.CommandExecutor_PermissionCheck;
import de.tobiyas.racesandclasses.commands.help.CommandExecutor_RaceHelp;
import de.tobiyas.racesandclasses.commands.help.CommandExecutor_RacesVersion;
import de.tobiyas.racesandclasses.commands.help.CommandExecutor_TraitList;
import de.tobiyas.racesandclasses.commands.level.Command_RACLevel;
import de.tobiyas.racesandclasses.commands.races.CommandExecutor_Race;
import de.tobiyas.racesandclasses.commands.racespawn.CommandExecutor_RaceSpawn;
import de.tobiyas.racesandclasses.commands.reflect.CommandMap;
import de.tobiyas.racesandclasses.commands.statistics.CommandExecutor_Statistics;
import de.tobiyas.racesandclasses.commands.tutorial.CommandExecutor_RacesTutorial;
import de.tobiyas.racesandclasses.configuration.managing.ConfigManager;
import de.tobiyas.racesandclasses.cooldown.CooldownManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.loadingerrors.TraitHolderLoadingErrorHandler;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceManager;
import de.tobiyas.racesandclasses.entitystatusmanager.hots.HotsManager;
import de.tobiyas.racesandclasses.entitystatusmanager.poison.PoisonManager;
import de.tobiyas.racesandclasses.entitystatusmanager.buffs.BuffManager;
import de.tobiyas.racesandclasses.entitystatusmanager.debuff.DebuffManager;
import de.tobiyas.racesandclasses.entitystatusmanager.stun.StunManager;
import de.tobiyas.racesandclasses.eventprocessing.TraitEventManager;
import de.tobiyas.racesandclasses.hotkeys.HotkeyManager;
import de.tobiyas.racesandclasses.infight.InFightManager;
import de.tobiyas.racesandclasses.listeners.RaCListenerRegister;
import de.tobiyas.racesandclasses.persistence.PersistenceStorageManager;
import de.tobiyas.racesandclasses.persistence.converter.ConverterChecker;
import de.tobiyas.racesandclasses.persistence.db.AlternateEbeanServerImpl;
import de.tobiyas.racesandclasses.persistence.file.YAMLPersistanceSaver;
import de.tobiyas.racesandclasses.playermanagement.PlayerManager;
import de.tobiyas.racesandclasses.statistics.StartupStatisticCategory;
import de.tobiyas.racesandclasses.statistics.StatisticGatherer;
import de.tobiyas.racesandclasses.traitcontainer.TraitStore;
import de.tobiyas.racesandclasses.traitcontainer.container.TraitsList;
import de.tobiyas.racesandclasses.translation.TranslationManagerHolder;
import de.tobiyas.racesandclasses.tutorial.TutorialManager;
import de.tobiyas.racesandclasses.util.bukkit.versioning.BukkitVersion;
import de.tobiyas.racesandclasses.util.bukkit.versioning.BukkitVersionBuilder;
import de.tobiyas.racesandclasses.util.consts.Consts;
import de.tobiyas.racesandclasses.util.permissions.VaultHook;
import de.tobiyas.racesandclasses.util.traitutil.DefaultTraitCopy;
import de.tobiyas.util.UtilsUsingPlugin;
import de.tobiyas.util.inventorymenu.BasicSelectionInterface;
import de.tobiyas.util.metrics.SendMetrics;


public class RacesAndClasses extends UtilsUsingPlugin implements Listener{
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
	 * The Buffmanager to use.
	 */
	protected BuffManager buffManager;
	
	/**
	 * The Debuffmanager to use.
	 */
	protected DebuffManager debuffManager;
	
	/**
	 * The race Spawn Manager
	 */
	protected RaceSpawnManager raceSpawnManager;
	
	/**
	 * The Hotkey Manager
	 */
	protected HotkeyManager hotkeyManger;
	
	/**
	 * The Hots Manger
	 */
	protected HotsManager hotsManager;
	
	/**
	 * The InFight Manager to use.
	 */
	protected InFightManager inFightManager;
	
	/**
	 * The alternative EBean Impl of the Bukkit Ebean Interface
	 */
	protected AlternateEbeanServerImpl alternateEbeanServer;
	
	/**
	 * A list of commands registered.
	 */
	protected final List<CommandInterface> commands = new LinkedList<CommandInterface>();
	
	
	
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
			getDebugLogger().enable();
			getDebugLogger().setAlsoToPlugin(true);
			
			VaultHook.init(this);
			
			description = getDescription();
			prefix = "[" + description.getName() + "] ";
			
			//temporary debug logger
			getDebugLogger().setAlsoToPlugin(true);
			
			checkIfCBVersionGreaterRequired();
			VaultHook.getHook(); //try vault hooking.
						
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
		if(version.getBukkitMainVersion() < Consts.minimalBukkitMainVersion &&
				version.getBukkitSubVersion() < Consts.minimalBukkitSubVersion &&
				version.getBukkitSubSubVersion() < Consts.minimalBukkitRevVersion){
			log("Bukkit Version is below 1.6. Compatibility mode is used. Double values will be rounded.");
		}
		
	}

	private void initManagers(){
		long currentTime = System.currentTimeMillis();
		
		setupConfiguration();
		YAMLPersistanceSaver.start(true);
		
		ConfigTotal.timeInMiliSeconds = System.currentTimeMillis() - currentTime;
		currentTime = System.currentTimeMillis();
		
		//copy default traits if non existent and enabled
		if(configManager.getGeneralConfig().isConfig_copyDefaultTraitsOnStartup()){
			DefaultTraitCopy.copyDefaultTraits();
		}

		TraitCopy.timeInMiliSeconds = System.currentTimeMillis() - currentTime;
		currentTime = System.currentTimeMillis();
		
		//Create all Managers //TODO check if needs reinit.
		TraitEventManager traitEventManager = new TraitEventManager();
		
		if(tutorialManager == null) tutorialManager = new TutorialManager();
		if(raceManager == null) raceManager = new RaceManager();
		if(classManager == null) classManager = new ClassManager();
		if(playerManager == null) playerManager = new PlayerManager();
		if(channelManager == null) channelManager = new ChannelManager();
		
		if(cooldownManager == null) cooldownManager = new CooldownManager();
		if(stunManager == null) stunManager = new StunManager();
		if(buffManager == null) buffManager = new BuffManager();
		if(poisonManager == null) poisonManager = new PoisonManager();
		if(raceSpawnManager == null) raceSpawnManager = new RaceSpawnManager(this);
		if(hotkeyManger == null) hotkeyManger = new HotkeyManager();
		if(inFightManager == null) inFightManager = new InFightManager();
		if(debuffManager == null) debuffManager = new DebuffManager();
		
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
		
		//Buff-Manager
		buffManager.init();
		
		//Debuff-Manager
		debuffManager.init();
		
		//race Spawn Manager
		raceSpawnManager.load();
		
		//infight Manager.
		inFightManager.reload();
	}
	

	private void registerCommands(){
		long currentTime = System.currentTimeMillis();
		
		commands.clear();
		
		commands.add(new CommandExecutor_Race());
		commands.add(new CommandExecutor_Racechat());
		commands.add(new CommandExecutor_RaceHelp());
		commands.add(new CommandExecutor_Whisper());
		commands.add(new CommandExecutor_TraitList());
		commands.add(new CommandExecutor_RaceHeal());
		commands.add(new CommandExecutor_RaceConfig());
		commands.add(new CommandExecutor_RaceDebug());
		commands.add(new CommandExecutor_Class());
		commands.add(new CommandExecutor_HP());
		commands.add(new CommandExecutor_Mana());
		commands.add(new CommandExecutor_Channel());
		commands.add(new CommandExecutor_RaceGod());
		commands.add(new CommandExecutor_BroadCast());
		commands.add(new CommandExecutor_LocalChat());
		commands.add(new CommandExecutor_PlayerInfo());
		commands.add(new CommandExecutor_RacesTutorial());
		commands.add(new CommandExecutor_PermissionCheck());
		
		commands.add(new CommandExecutor_RacesReload());
		commands.add(new CommandExecutor_RacesVersion());
		commands.add(new CommandExecutor_Statistics());
		commands.add(new CommandExecutor_ShowTraits());
		commands.add(new CommandExecutor_Edit());
		commands.add(new CommandExecutor_SaveNow());
		
		commands.add(new CommandExecutor_ForceRace());
		commands.add(new CommandExecutor_ForceClass());
		commands.add(new CommandExecutor_ConfigRegenerate());
		
		commands.add(new CommandExecutor_RaceSpawn());
		
		commands.add(new Command_RACLevel());
		commands.add(new CommandExecutor_BindTrait());
		commands.add(new CommandExecutor_UseTrait());
		
		
		//remove all disabled commands.
		List<String> disabledCommands = configManager.getGeneralConfig().getConfig_general_disable_commands();
		Iterator<CommandInterface> it = commands.iterator();
		while(it.hasNext()){
			CommandInterface command = it.next();
			if(disabledCommands.contains(command.getCommandName().toLowerCase())){
				it.remove();
			}
		}
		
		CommandMap.registerCommands(commands, this);		
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
			VaultHook.shutdown();
		}

		BasicSelectionInterface.closeAllInvs();
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
		
		long currentTime = System.currentTimeMillis();
		configManager.reload();
		setupDebugLogger();
		
		long timeTaken = System.currentTimeMillis() - currentTime;
		StartupStatisticCategory.PlayerConfigs.timeInMiliSeconds = timeTaken;
	}
	
	private void setupDebugLogger(){		
		if(!configManager.getGeneralConfig().isConfig_enableDebugOutputs()){
			getDebugLogger().disable();
		}
		
		if(!configManager.getGeneralConfig().isConfig_enableErrorUpload()){
			getDebugLogger().enableUploads(false);
		}
	
		getDebugLogger().setAlsoToPlugin(true);
	}
	
	
	private void registerAllCommandsAsError() {
		for(CommandInterface command : this.commands){
			new CommandExecutor_EmptyCommand(command.getCommandName());
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
		raceSpawnManager.save(false);
		Bukkit.getScheduler().cancelTasks(this);
		TraitStore.destroyClassLoaders();
		
		stunManager.deinit();
		poisonManager.deinit();
		hotkeyManger.shutdown();
		debuffManager.shutdown();
		

		if(!Consts.disableBDSupport) {
			alternateEbeanServer.onShutdown();
			alternateEbeanServer = null;
		}
		
		TranslationManagerHolder.shutdown();
		
		if(!configManager.getGeneralConfig().isConfig_savePlayerDataToDB()){
			log("Doing some YML file flushing. This can take a while.");
			YAMLPersistanceSaver.flushNow(false, false);
			YAMLPersistanceSaver.stop();
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
		if(Consts.disableBDSupport) return super.getDatabase();
		
		if(alternateEbeanServer == null){
			initEbeanServer();
		}

		return alternateEbeanServer.getDatabase();
	}


	private void initEbeanServer() {
		if(Consts.disableBDSupport) return;
		
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

	public RaceSpawnManager getRaceSpawnManager() {
		return raceSpawnManager;
	}

	/**
	 * This gets the HotsManager of the Plugin.
	 * <br>It's loaded over an Lazy init.
	 * 
	 * @return {@link HotsManager}
	 */
	public HotsManager getHotsManager() {
		if(hotsManager == null) hotsManager = new HotsManager();
		return hotsManager;
	}

	public HotkeyManager getHotkeyManager() {
		return hotkeyManger;
	}
	
	public InFightManager getInFightManager(){
		return inFightManager;
	}

	public BuffManager getBuffManager() {
		return buffManager;
	}

	public DebuffManager getDebuffManager() {
		return debuffManager;
	}
	
}
