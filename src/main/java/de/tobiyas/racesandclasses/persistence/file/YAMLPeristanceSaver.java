package de.tobiyas.racesandclasses.persistence.file;

import java.util.Set;

import org.bukkit.Bukkit;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.util.config.YAMLConfigExtended;

public class YAMLPeristanceSaver {

	
	/**
	 * Loads all Configs
	 * 
	 * @param async if the Configs should be load async.
	 */
	@SuppressWarnings("deprecation")
	public static void loadAllConfigs(boolean async){
		Runnable runnable = new Runnable() {
			
			@Override
			public void run() {
				YAMLPersistenceProvider.rescanKnownPlayers();
				
				Set<String> playerNames = YAMLPersistenceProvider.getAllPlayersKnown();
				for(String playerName : playerNames){
					YAMLPersistenceProvider.getLoadedPlayerFile(playerName);
				}
			}
		} ;
		
		RacesAndClasses plugin = RacesAndClasses.getPlugin();
		//schedule this somewhat behind to not be in Server Start.
		if(async){
			Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, runnable, 10);
		}else{
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, runnable, 10);
		}
	}
	
	
	
	/**
	 * The resave intervall of the Saving in ticks.
	 */
	protected static int RE_SAVE_INTERVALL = 10 * 60 * 20; // 10min -> * 60 seconds -> * 20 ticks
	
	
	/**
	 * The TaskID of the Scheduling task.
	 */
	protected static int bukkitTaskId = -1;
	
	/**
	 * Flushes the Configs to the Files.
	 * 
	 * @param async if the Configs should be saved async
	 */
	public static void flushNow(final boolean async, final boolean reregister){
		RacesAndClasses plugin = RacesAndClasses.getPlugin();
		
		//Only flush if we do NOT use the DB.
		boolean useDB = plugin.getConfigManager().getGeneralConfig().isConfig_savePlayerDataToDB();
		boolean debugOutputs = plugin.getConfigManager().getGeneralConfig().isConfig_enableDebugOutputs();
		if(!useDB){
			int flushed = 0;
			long timeBefore = System.currentTimeMillis();
			
			saveEverything();
			YAMLPersistenceProvider.rescanKnownPlayers();
			
			Set<String> playerNames = YAMLPersistenceProvider.getAllPlayersKnown();
			for(String playerName : playerNames){
				YAMLConfigExtended config = 
						YAMLPersistenceProvider.getLoadedPlayerFile(playerName);
				
				if(config.isDirty()){
					flushed++;
				}
				
				if(async){
					config.saveAsync();
				}else{
					config.save();
				}
			}
			
			long timeTaken = System.currentTimeMillis() - timeBefore;
			if(debugOutputs){
				RacesAndClasses.getPlugin().log("YML File flushing done. Flushed " + flushed + " files to Disc. "
						+ "Took " + timeTaken + " ms.");
			}
			
			plugin.getStatistics().eventTime("yml_save", timeTaken);
		}
		
		if(!Bukkit.getScheduler().isCurrentlyRunning(bukkitTaskId)){
			Bukkit.getScheduler().cancelTask(bukkitTaskId);
		}
		
		bukkitTaskId = -1;
		
		if(reregister){
			start(async);
		}
	}
	
	protected static void saveEverything(){
		RacesAndClasses plugin = RacesAndClasses.getPlugin();
		plugin.getConfigManager().getMemberConfigManager().saveConfigs();
		
		plugin.getRaceManager().saveAll();
		plugin.getClassManager().saveAll();
		
		plugin.getPlayerManager().savePlayerContainer();
		
		plugin.getChannelManager().saveChannels();
	}
	
	
	/**
	 * Starts the Flushing of the YML Files.
	 * 
	 * @param async if the files should be flushed async or sync.
	 */
	@SuppressWarnings("deprecation")
	public static void start(final boolean async){
		if(bukkitTaskId > 0) return; // already running
		
		RacesAndClasses plugin = RacesAndClasses.getPlugin();
		Runnable rescedule = new Runnable() {
			
			@Override
			public void run() {
				flushNow(async, true);
			}
		};
		
		if(async){
			bukkitTaskId = Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, rescedule, RE_SAVE_INTERVALL);
		}else{
			bukkitTaskId = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, rescedule, RE_SAVE_INTERVALL);
		}
	}
	
	
	/**
	 * Stops the flushing of the YML files.
	 */
	public static void stop(){
		if(bukkitTaskId > 0){
			Bukkit.getScheduler().cancelTask(bukkitTaskId);
			bukkitTaskId = -1;
		}
	}
}
