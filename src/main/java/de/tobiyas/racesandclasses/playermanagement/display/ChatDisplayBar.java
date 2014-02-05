package de.tobiyas.racesandclasses.playermanagement.display;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.util.consts.Consts;

public class ChatDisplayBar extends AbstractDisplay{

	
	
	/**
	 * Inits the display with a Player to post to.
	 * 
	 * @param player to display to
	 * @param displayInfo the type of display to show
	 */
	public ChatDisplayBar(String playerName, DisplayInfos displayInfos) {
		super(playerName, displayInfos);
	}

	
	@Override
	public void display(double currentAmount, double maxAmount) {
		if(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_disableAllChatBars()) return;
		
		String barString = "";
		if(!displayInfo.useName()){
			barString = calcForHealth(currentAmount, maxAmount, Consts.displayBarLength);
		}
		
		int pre = (int) Math.floor(currentAmount);
		int after = (int) Math.floor(currentAmount * 100D) % 100;
		
		String healthAsNumbers = "";
		
		if(displayInfo.onlyUseOneValue()){
			healthAsNumbers = String.valueOf( currentAmount );
		}else{
			healthAsNumbers = colorMedium + " " + getColorOfPercent(currentAmount, maxAmount) + 
					pre + "." + after + colorMedium + "/" + colorHigh + maxAmount;			
		}
		
		
		Player player = Bukkit.getPlayer(playerName);
		if(player != null && player.isOnline()){
			player.sendMessage(displayInfo.getMidValueColor() + displayInfo.getName() + ": " + barString + healthAsNumbers);
		}
	}
	
	
	
}
