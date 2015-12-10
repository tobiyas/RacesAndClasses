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
package de.tobiyas.racesandclasses.persistence.file;

import java.util.Set;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.util.config.YAMLConfigExtended;
import de.tobiyas.util.schedule.DebugBukkitRunnable;

public class YAMLPersistanceSaver {

	
	/**
	 * Loads all Configs
	 * 
	 * @param async if the Configs should be load async.
	 */
	public static void loadAllConfigs(boolean async){
		BukkitRunnable runnable = new DebugBukkitRunnable("YMLPersistanceLoader"){
			@Override
			protected void runIntern() {
				YAMLPersistenceProvider.rescanKnownPlayers();
				
				Set<RaCPlayer> players = YAMLPersistenceProvider.getAllPlayersKnown();
				for(RaCPlayer player : players){
					YAMLPersistenceProvider.getLoadedPlayerFile(player);
				}
			}
		} ;
		
		RacesAndClasses plugin = RacesAndClasses.getPlugin();
		//schedule this somewhat behind to not be in Server Start.
		if(async){
			runnable.runTaskAsynchronously(plugin);
		}else{
			runnable.runTaskLater(plugin, 1);
		}
	}
	
	
	
	/**
	 * The resave intervall of the Saving in ticks.
	 */
	protected static int RE_SAVE_INTERVALL = 10 * 60 * 20; // 10min
	
	
	/**
	 * The TaskID of the Scheduling task.
	 */
	protected static BukkitTask bukkitTask = null;
	
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
			
			Set<RaCPlayer> players = YAMLPersistenceProvider.getAllPlayersKnown();
			for(RaCPlayer player : players){
				YAMLConfigExtended config = YAMLPersistenceProvider.getLoadedPlayerFile(player);
				
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
			if(debugOutputs && flushed > 0){
				RacesAndClasses.getPlugin().log("YML File flushing done. Flushed " + flushed + " files to Disc. "
						+ "Took " + timeTaken + " ms.");
			}
			
			plugin.getStatistics().eventTime("yml_save", timeTaken);
		}
		
		if(bukkitTask != null) bukkitTask.cancel();
		bukkitTask = null;
		
		if(reregister){
			start(async);
		}
	}
	
	protected static void saveEverything(){
		RacesAndClasses plugin = RacesAndClasses.getPlugin();
		
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
	public static void start(final boolean async){
		if(bukkitTask != null) return; // already running
		
		RacesAndClasses plugin = RacesAndClasses.getPlugin();
		BukkitRunnable rescedule = new DebugBukkitRunnable("YMLFlushTask"){
			@Override
			protected void runIntern() {
				flushNow(async, true);
			}
		};
		
		if(async){
			bukkitTask = rescedule.runTaskLater(plugin, RE_SAVE_INTERVALL);
		}else{
			bukkitTask = rescedule.runTaskLaterAsynchronously(plugin, RE_SAVE_INTERVALL);
		}
	}
	
	
	/**
	 * Stops the flushing of the YML files.
	 */
	public static void stop(){
		if(bukkitTask != null){
			bukkitTask.cancel();
			bukkitTask = null;
		}
	}
}
