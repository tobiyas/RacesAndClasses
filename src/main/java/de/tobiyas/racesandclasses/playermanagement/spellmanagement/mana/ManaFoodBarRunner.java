package de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import de.tobiyas.racesandclasses.RacesAndClasses;

public class ManaFoodBarRunner extends BukkitRunnable implements Listener {

	private final ManaManager manaManageer;
	
	
	public ManaFoodBarRunner(ManaManager manaManager) {
		this.manaManageer = manaManager;
	}
	
	
	/**
	 * Starts this runnable.
	 */
	public void start(){
		this.runTaskTimer(RacesAndClasses.getPlugin(), 10, 10);
		Bukkit.getPluginManager().registerEvents(this, RacesAndClasses.getPlugin());
	}

	@Override
	public void run() {
		if(!manaManageer.getPlayer().isOnline()) return;
		
		Player player = manaManageer.getPlayer().getPlayer();
		player.setFoodLevel(getLevel());
	}
	
	
	private int getLevel(){
		double current = this.manaManageer.getCurrentMana();
		double max = this.manaManageer.getMaxMana();
		
		double percent = current / max;
		int calcedLevel = (int)Math.floor(percent * 20d);
		if(current == 0) calcedLevel = 0;
		
		return calcedLevel;
	}
	
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void foodLevelChange(FoodLevelChangeEvent event){
		event.setCancelled(true);
		event.setFoodLevel(getLevel());
	}

}
