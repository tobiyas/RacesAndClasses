package de.tobiyas.racesandclasses.persistence.file;

import java.io.File;
import java.util.Set;

import org.bukkit.Bukkit;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.ClassAPI;
import de.tobiyas.racesandclasses.APIs.RaceAPI;
import de.tobiyas.racesandclasses.util.consts.Consts;
import de.tobiyas.util.config.YAMLConfigExtended;

public class YAMLOldDataRemover {

	
	/**
	 * The Range for deleting.
	 */
	private static final long DELETE_RANGE = 1000 * 60 * 60 * 24;
	
	/**
	 * If we are currently removing.
	 */
	private static boolean isRemoving = false;
	
	
	/**
	 * Removes old files.
	 * <br>This will be run on the Calling Thread.
	 * 
	 * @return 0 if none are removed. return -1 if already running.
	 * Return X to see how many are deleted.
	 */
	public static int removeOldFiles(){
		if(isRemoving) return -1;
		
		int oldDays = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_general_remove_old_data_days();
		String defaultRaceName = RaceAPI.getDefaultRaceName();
		String defaultClassName = ClassAPI.getDefaultClassName();
		
		isRemoving = true;
		Set<String> notLoaded = YAMLPersistenceProvider.getNotLoadedPlayers();
		long terminationDate = System.currentTimeMillis() - (DELETE_RANGE * oldDays);
		boolean checkEmpty = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_general_remove_old_data_check_empty();
		
		int removed = 0;
		for(String name : notLoaded){
			File playerFile = new File(Consts.playerDataPath, name);
			if(playerFile.exists()){
				YAMLConfigExtended temp = new YAMLConfigExtended(playerFile).load();
				long lastLogin = temp.getLong("lastOnline", 0);
				
				//check for the empty Player.
				if(checkEmpty){
					String raceName = temp.getString("race",null);
					String className = temp.getString("class",null);
					
					if(!equals(raceName, defaultRaceName) || !equals(className, defaultClassName)){
						continue;
					}
				}
				
				if(lastLogin < terminationDate){
					if(playerFile.delete()) removed++;
				}
			}
		}
		
		RacesAndClasses.getPlugin().log("Removed " + removed + " old data files.");
		isRemoving = false;
		return removed;
	}
	
	private static boolean equals(String arg1,String arg2){
		if(arg1 == null && arg2 == null) return true;
		if(arg1 == null || arg2 == null) return false;
		
		return arg1.equals(arg2);
	}
	
	
	/**
	 * Removes all old files Async.
	 * 
	 * @return true if is done, false if NOT started.
	 */
	public static boolean removeOldFilesAsync(){
		if(isRemoving) return false;
		
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				removeOldFiles();
			}
		});
		
		thread.start();
		return true;
	}
	
	/**
	 * Removes all old files Async.
	 * 
	 * @param callback the callback is called when the files are done.
	 * 
	 * @return true if is done, false if NOT started.
	 */
	public static boolean removeOldFilesAsync(final RemoveCallback callback){
		if(isRemoving) return false;
		
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				callback.setResult(removeOldFiles());
				Bukkit.getScheduler().runTask(RacesAndClasses.getPlugin(), callback);
			}
		});
		
		thread.start();
		return true;
	}
	
	
	/**
	 * Checks if the Clearing is running.
	 * @return
	 */
	public static boolean isRunning(){
		return isRemoving;
	}
	
	
	
	/**
	 * A Callback for the Removing.
	 * 
	 * @author Tobiyas
	 *
	 */
	public static abstract class RemoveCallback implements Runnable{
		
		private int cleared = -1;
		
		public void setResult(int cleared){
			this.cleared = cleared;
		}
		
		
		@Override
		public final void run() {
			if(cleared < 0) alreadyRunning();
			if(cleared >= 0) cleared(cleared);
		}
		
		
		public abstract void cleared(int cleared);
		public abstract void alreadyRunning();
	}

}
