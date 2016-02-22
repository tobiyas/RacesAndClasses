package de.tobiyas.racesandclasses.playermanagement.display.scoreboard;

import org.bukkit.scheduler.BukkitTask;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import de.tobiyas.util.schedule.DebugBukkitRunnable;

public class PlayerScoreboardUpdater extends DebugBukkitRunnable {

	private static final int TICK_TIME = 10;
	
	
	/**
	 * The Instance to use.
	 */
	private static BukkitTask instance;
	
	
	private PlayerScoreboardUpdater() {
		super("RaCPlayerScoreboardUpdater");
	}
	

	@Override
	public void runIntern() {
		for(RaCPlayer racPlayer : RaCPlayerManager.get().getOnlinePlayers()){
			PlayerRaCScoreboardManager manager = racPlayer.getScoreboardManager();
			
			manager.getUpdater().updateCooldown();
			manager.getUpdater().updateSpells();
			manager.getUpdater().updateArrows();
			manager.getUpdater().updateGeneral();
			
			manager.tick();
		}
	}
	
	
	
	/**
	 * Starts the Updater.
	 */
	public static void start(){
		stop();
		
		RacesAndClasses plugin = RacesAndClasses.getPlugin();
		instance = new PlayerScoreboardUpdater().runTaskTimer(plugin, TICK_TIME, TICK_TIME);
	}


	/**
	 * Stops the Updater.
	 */
	public static void stop() {
		if(instance != null) {
			instance.cancel();
			instance = null;
		}
	}

}
