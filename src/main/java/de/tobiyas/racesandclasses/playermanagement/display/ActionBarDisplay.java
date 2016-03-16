package de.tobiyas.racesandclasses.playermanagement.display;

import org.bukkit.ChatColor;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.util.vollotile.helper.PermanentActionBarMessages;

public class ActionBarDisplay extends AbstractDisplay {

	/**
	 * The Message to use.
	 */
	private final PermanentActionBarMessages messager;
	
	
	public ActionBarDisplay(RaCPlayer player, DisplayInfos displayInfo) {
		super(player, displayInfo);
		
		this.messager = PermanentActionBarMessages.get(RacesAndClasses.getPlugin());
	}
	

	@Override
	public void display(double currentValue, double maxValue) {
		if(maxValue < 1) {
			if(player.isOnline()) messager.removeMessage(player.getPlayer());
			return;
		}
		
		String toShow = calcForHealth(currentValue, maxValue, 15, '\u2666');
		toShow = ChatColor.DARK_BLUE.toString() + '\u2739' + " [" + toShow + ChatColor.DARK_BLUE + "]";
		toShow += " " + (int)currentValue + "/" + (int)maxValue;
		
		if(player.isOnline()) messager.setMessage(player.getPlayer(), toShow);
	}
	
	
	@Override
	public void unregister() {
		super.unregister();
		messager.removeMessage(player);
	}
	
	
	@Override
	public DisplayType getType() {
		return DisplayType.Actionbar;
	}

}
