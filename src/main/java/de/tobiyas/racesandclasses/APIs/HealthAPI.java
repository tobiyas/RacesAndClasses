package de.tobiyas.racesandclasses.APIs;

import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;

public class HealthAPI {


	/**
	 * Returns the current Health of this player.
	 * 
	 * @param player to check
	 * 
	 * @return the current health
	 */
	public static double getCurrentHealth(Player player){
		if(player == null) return 0;
		
		RaCPlayer pl = RaCPlayerManager.get().getPlayer(player);
		return pl.getHealth();
	}
	
	
	/**
	 * Returns the Max Health of the Player.
	 * 
	 * @param player to get.
	 * 
	 * @return the max health.
	 */
	public static double getMaxHealth(Player player){
		if(player == null) return 0;
		
		RaCPlayer pl = RaCPlayerManager.get().getPlayer(player);
		return pl.getMaxHealth();
	}
	
	
	/**
	 * Adds Max Health Bonus for the Player.
	 * 
	 * @param player to get.
	 * @param key to save for.
	 * @param value to save.
	 */
	public static void addMaxHealthBonus(Player player, String key, double value){
		if(player == null) return;
		
		RaCPlayer pl = RaCPlayerManager.get().getPlayer(player);
		pl.getHealthManager().addMaxHealthBonus(key, value);
	}
	
	/**
	 * Removes the Max Health Bonus passed.
	 * 
	 * @param player to get.
	 * @param key to remove.
	 */
	public static void removeMaxHealthBonus(Player player, String key){
		if(player == null) return;
		
		RaCPlayer pl = RaCPlayerManager.get().getPlayer(player);
		pl.getHealthManager().removeMaxHealthBonus(key);
	}
	
	/**
	 * Damages the Player.
	 * 
	 * @param player to get.
	 * @param value to damage.
	 */
	public static void damage(Player player, double value){
		if(player == null) return;
		
		RaCPlayer pl = RaCPlayerManager.get().getPlayer(player);
		pl.getHealthManager().damage(value);
	}
	
	/**
	 * Heals the Player.
	 * 
	 * @param player to get.
	 * @param value to heal.
	 */
	public static void heal(Player player, double value){
		if(player == null) return;
		
		RaCPlayer pl = RaCPlayerManager.get().getPlayer(player);
		pl.getHealthManager().heal(value);
	}

}
