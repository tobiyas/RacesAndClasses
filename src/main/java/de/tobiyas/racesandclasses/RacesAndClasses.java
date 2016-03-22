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
import static de.tobiyas.racesandclasses.statistics.StartupStatisticCategory.TraitManager;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;

import de.tobiyas.racesandclasses.addins.AddinManager;
import de.tobiyas.racesandclasses.chat.channels.ChannelManager;
import de.tobiyas.racesandclasses.commands.CommandRegisterer;
import de.tobiyas.racesandclasses.configuration.managing.ConfigManager;
import de.tobiyas.racesandclasses.cooldown.CooldownManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.loadingerrors.TraitHolderLoadingErrorHandler;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceManager;
import de.tobiyas.racesandclasses.entitystatusmanager.buffs.BuffManager;
import de.tobiyas.racesandclasses.entitystatusmanager.debuff.DebuffManager;
import de.tobiyas.racesandclasses.entitystatusmanager.dot.DotManager;
import de.tobiyas.racesandclasses.entitystatusmanager.hots.HotsManager;
import de.tobiyas.racesandclasses.entitystatusmanager.silence.SilenceAndKickManager;
import de.tobiyas.racesandclasses.entitystatusmanager.stun.StunManager;
import de.tobiyas.racesandclasses.eventprocessing.TraitEventManager;
import de.tobiyas.racesandclasses.hotkeys.HotkeyManager;
import de.tobiyas.racesandclasses.infight.InFightManager;
import de.tobiyas.racesandclasses.listeners.RaCListenerRegister;
import de.tobiyas.racesandclasses.playermanagement.PlayerManager;
import de.tobiyas.racesandclasses.playermanagement.playerdisplay.scoreboard.PlayerScoreboardUpdater;
import de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana.ManaXPBarRunner;
import de.tobiyas.racesandclasses.saving.PlayerSavingManager;
import de.tobiyas.racesandclasses.saving.dataconverter.PlayerDataConverter;
import de.tobiyas.racesandclasses.statistics.StartupStatisticCategory;
import de.tobiyas.racesandclasses.statistics.StatisticGatherer;
import de.tobiyas.racesandclasses.traitcontainer.TraitStore;
import de.tobiyas.racesandclasses.traitcontainer.container.TraitsList;
import de.tobiyas.racesandclasses.translation.TranslationManagerHolder;
import de.tobiyas.racesandclasses.util.bukkit.versioning.BukkitVersion;
import de.tobiyas.racesandclasses.util.bukkit.versioning.BukkitVersionBuilder;
import de.tobiyas.racesandclasses.util.consts.Consts;
import de.tobiyas.racesandclasses.util.permissions.VaultHookProvider;
import de.tobiyas.racesandclasses.util.traitutil.DefaultTraitCopy;
import de.tobiyas.util.UtilsUsingPlugin;
import de.tobiyas.util.inventorymenu.BasicSelectionInterface;
import de.tobiyas.util.metrics.SendMetrics;
import de.tobiyas.util.vollotile.helper.PermanentActionBarMessages;
import net.gravitydevelopment.updater.Updater;
import net.gravitydevelopment.updater.Updater.UpdateType;


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
	 * The StunManager of the plugin
	 */
	protected StunManager stunManager;
	
	/**
	 * The poisonManager of the Plugin
	 */
	protected DotManager dotManager;
	
	/**
	 * The Buffmanager to use.
	 */
	protected BuffManager buffManager;
	
	/**
	 * The Debuffmanager to use.
	 */
	protected DebuffManager debuffManager;
	
	/**
	 * The Hotkey Manager
	 */
	protected HotkeyManager hotkeyManger;
	
	/**
	 * The Hots Manger
	 */
	protected HotsManager hotsManager;
	
	/**
	 * The Manager for silences and kicks.
	 */
	protected SilenceAndKickManager silenceAndKickManager;
	
	/**
	 * The InFight Manager to use.
	 */
	protected InFightManager inFightManager;
	
	/**
	 * the manager for the Addins.
	 */
	protected AddinManager addinManager;
	
	
	
	//Empty Constructor for Bukkit.
	public RacesAndClasses(){}
	
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
	public void pluginEnable(){
		isInShutdown = false;
		statistics = new StatisticGatherer(System.currentTimeMillis());
		
		//Check for Vault and warn if not present!
		if(!isVaultPresent()){
			this.getLogger().warning("RacesAndClasses WARNING:  Plugin Vault is not found! "
					+ "Please download it!");
		}
		
		
		//Use the Command register here.
		CommandRegisterer commandRegister = new CommandRegisterer(this);
		
		//We seal the startup away to prevent further erroring afterwards.
		try{
			plugin = this;
			getDebugLogger().enable();
			getDebugLogger().setAlsoToPlugin(true);
			
			VaultHookProvider.init(this);
			
			description = getDescription();
			prefix = "[" + description.getName() + "] ";
			
			//temporary debug logger
			getDebugLogger().setAlsoToPlugin(true);
			
			checkIfCBVersionGreaterRequired();
						
			fullReload(false, false);
			
			commandRegister.registerCommands();
			
			initMetrics();
			loadingDoneMessage();
		}catch(Exception e){
			log("An Error has occured during startup sequence: " + e.getLocalizedMessage());
			getDebugLogger().logStackTrace(e);
			
			errored = true;
			commandRegister.registerAllCommandsAsError();
		}
	}
	
	
	@Override
	protected void firstTick() {
		super.firstTick();
		
		//Register the Events on the First Server Tick!
		registerEvents();
		registerTasks();
		
		//Init vault Hook if vault is present.
		if(isVaultPresent()) VaultHookProvider.init(this);
	}
	
	
	/**
	 * Registers all tasks needed.
	 */
	private void registerTasks() {
		new ManaXPBarRunner().start();
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
		//Start DB converting here:
		PlayerDataConverter.checkForConvertAndConvert();
		PlayerSavingManager.reload();
		
		ConfigTotal.timeInMiliSeconds = System.currentTimeMillis() - currentTime;
		currentTime = System.currentTimeMillis();

		//Remove old traits:
		DefaultTraitCopy.removeOldDefaultTraits();
		
		//Create all Managers //TODO check if needs reinit.
		TraitEventManager traitEventManager = new TraitEventManager();
		
		if(raceManager == null) raceManager = new RaceManager();
		if(classManager == null) classManager = new ClassManager();
		if(playerManager == null) playerManager = new PlayerManager();
		if(channelManager == null) channelManager = new ChannelManager();
		
		if(cooldownManager == null) cooldownManager = new CooldownManager();
		if(stunManager == null) stunManager = new StunManager();
		if(buffManager == null) buffManager = new BuffManager();
		if(dotManager == null) dotManager = new DotManager();
		if(hotsManager == null) hotsManager = new HotsManager();
		if(silenceAndKickManager == null) silenceAndKickManager = new SilenceAndKickManager();
		if(hotkeyManger == null) hotkeyManger = new HotkeyManager();
		if(inFightManager == null) inFightManager = new InFightManager();
		if(debuffManager == null) debuffManager = new DebuffManager();
		if(addinManager == null) addinManager = new AddinManager(this);
		
		ManagerConstructor.timeInMiliSeconds = System.currentTimeMillis() - currentTime;
		currentTime = System.currentTimeMillis();
		
		//init of Managers
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
		dotManager.init();
		
		//Buff-Manager
		buffManager.init();
		
		//Debuff-Manager
		debuffManager.init();
		
		//infight Manager.
		inFightManager.reload();
		
		//Init the Addin-manager.
		addinManager.reload();
		
		//Start the Cooldown Updater.
		PlayerScoreboardUpdater.start();
		
		//Init translation-Manager:
		TranslationManagerHolder.init();
	}
	
	
	private void initMetrics(){
		if(configManager.getGeneralConfig().isConfig_metrics_enabled()){
			SendMetrics.sendMetrics(this, configManager.getGeneralConfig().isConfig_enableDebugOutputs());
		}
	}
	
	@Override
	public void onDisable(){
		isInShutdown = true;
		
		if(!errored){
			shutDownSequenz(false);
			VaultHookProvider.shutdown();
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
		
		log(" loaded: " + traits + races + classes + channels + events);
		log(" " + description.getName() + " Version: '" + Consts.detailedVersionString + "' fully loaded with Permissions: " 
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
		
		initManagers();
		return System.currentTimeMillis() - time;
	}
	
	private void shutDownSequenz(boolean useGC){
		PlayerScoreboardUpdater.stop();
		PermanentActionBarMessages.kill();
		
		playerManager.shutdown();
		getDebugLogger().shutDown();
		plugin.reloadConfig();
		cooldownManager.shutdown();
		channelManager.saveChannels();
		Bukkit.getScheduler().cancelTasks(this);
		TraitStore.destroyClassLoaders();
		
		stunManager.deinit();
		dotManager.deinit();
		hotkeyManger.shutdown();
		debuffManager.shutdown();
		
		addinManager.shutdown();
		
		TranslationManagerHolder.shutdown();
		
		if(useGC) System.gc();
		
		//Close all open persistence connections
		PlayerSavingManager.shutdown();
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
	 * Returns the addin Manager for further addin-calls.
	 */
	public AddinManager getAddinManager() {
		return addinManager;
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


	public DotManager getDotManager() {
		return dotManager;
	}

	/**
	 * This gets the HotsManager of the Plugin.
	 * @return {@link HotsManager}
	 */
	public HotsManager getHotsManager() {
		return hotsManager;
	}
	
	public SilenceAndKickManager getSilenceAndKickManager() {
		return silenceAndKickManager;
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
	
	
	public static boolean isVaultPresent(){
		return Bukkit.getPluginManager().getPlugin("Vault") != null; 
	}
	
	
	private static boolean isInShutdown = false;
	public static boolean isBukkitInShutdownMode(){
		return isInShutdown;
	}
	
}
