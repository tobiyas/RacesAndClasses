package de.tobiyas.racesandclasses.APIs;

import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;

public class ManaAPI {


	/**
	 * Returns the current Mana of this player.
	 * 
	 * @param player to check
	 * 
	 * @return the current mana
	 */
	public static double getCurrentMana(Player player){
		if(player == null) return 0;
		
		RaCPlayer pl = RaCPlayerManager.get().getPlayer(player);
		return pl.getManaManager().getCurrentMana();
	}
	
	
	/**
	 * Returns the Max mana of the Player.
	 * 
	 * @param player to get.
	 * @return the max mana.
	 */
	public static double getMaxMana(Player player){
		if(player == null) return 0;
		
		RaCPlayer pl = RaCPlayerManager.get().getPlayer(player);
		return pl.getManaManager().getMaxMana();
	}
	
	
	/**
	 * Returns the Max mana of the Player.
	 * 
	 * @param player to get.
	 * @param key to save for.
	 * @param value to save.
	 * 
	 * @return the max mana.
	 */
	public static void addMaxManaBonus(Player player, String key, double value){
		if(player == null) return;
		
		RaCPlayer pl = RaCPlayerManager.get().getPlayer(player);
		pl.getManaManager().addMaxManaBonus(key, value);
	}
	
	/**
	 * Returns the Max mana of the Player.
	 * 
	 * @param player to get.
	 * @param key to save for.
	 * @param value to save.
	 * 
	 * @return the max mana.
	 */
	public static void removeMaxManaBonus(Player player, String key){
		if(player == null) return;
		
		RaCPlayer pl = RaCPlayerManager.get().getPlayer(player);
		pl.getManaManager().removeMaxManaBonus(key);
	}
	
	/**
	 * Drains the mana of the Player.
	 * 
	 * @param player to get.
	 * @param value to drown.
	 * 
	 * @return the max mana.
	 */
	public static void drainMana(Player player, double value){
		if(player == null) return;
		
		RaCPlayer pl = RaCPlayerManager.get().getPlayer(player);
		pl.getManaManager().drownMana(value);
	}
	
	/**
	 * Drains the mana of the Player.
	 * 
	 * @param player to get.
	 * @param value to drown.
	 * 
	 * @return the max mana.
	 */
	public static void fillMana(Player player, double value){
		if(player == null) return;
		
		RaCPlayer pl = RaCPlayerManager.get().getPlayer(player);
		pl.getManaManager().fillMana(value);
	}
}
