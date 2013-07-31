package de.tobiyas.racesandclasses.healthmanagement.display;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.util.consts.Consts;

public class ChatHealthBar extends AbstractHealthDisplay{

	/**
	 * Inits the display with a Player to post to.
	 * 
	 * @param player
	 */
	public ChatHealthBar(String playerName) {
		super(playerName);
	}

	@Override
	public void display(double currentHealth, double maxHealth) {
		String barString = calcForHealth(currentHealth, maxHealth, Consts.healthBarLength);
		
		int pre = (int) Math.floor(currentHealth);
		int after = (int) Math.floor(currentHealth * 100D) % 100;
		
		String healthAsNumbers = ChatColor.YELLOW + " " + getColorOfPercent(currentHealth, maxHealth) + 
				pre + "." + after + ChatColor.YELLOW + "/" + ChatColor.GREEN + maxHealth;
		
		
		Player player = Bukkit.getPlayer(playerName);
		if(player != null && player.isOnline()){
			player.sendMessage(ChatColor.YELLOW + "Health: " + barString + healthAsNumbers);
		}
	}
	
	
	
}
