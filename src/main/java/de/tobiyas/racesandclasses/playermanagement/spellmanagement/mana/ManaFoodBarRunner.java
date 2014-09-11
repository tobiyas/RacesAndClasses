package de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import de.tobiyas.racesandclasses.RacesAndClasses;

public class ManaFoodBarRunner extends BukkitRunnable implements Listener {

	private final ManaManager manaManageer;
	
	/**
	 * The plugin to use.
	 */
	private final RacesAndClasses plugin;
	
	
	public ManaFoodBarRunner(ManaManager manaManager) {
		this.manaManageer = manaManager;
		this.plugin = RacesAndClasses.getPlugin();
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
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_useFoodManaBar()) return;
		if(!manaManageer.getPlayer().isOnline()) return;
		
		Player player = manaManageer.getPlayer().getPlayer();
		player.setFoodLevel(getLevel());
		player.setSaturation(20);
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
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_useFoodManaBar()) return;
		
		event.setCancelled(true);
	}
	
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void damageByFoodlevel(EntityDamageEvent event){
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_useFoodManaBar()) return;
		if(event.getCause() != DamageCause.STARVATION) return;
		
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void healByFoodlevel(EntityRegainHealthEvent event){
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_useFoodManaBar()) return;
		if(event.getRegainReason() != RegainReason.SATIATED) return;
		
		event.setCancelled(true);
	}

	
	
}
