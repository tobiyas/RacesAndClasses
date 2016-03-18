package de.tobiyas.racesandclasses.playermanagement.playerdisplay;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.util.vollotile.VollotileCodeManager;
import de.tobiyas.util.vollotile.helper.PermanentActionBarMessages;

public class PlayerActionBarDisplay {

	/**
	 * The Player to use.
	 */
	private final RaCPlayer player;
	
	/**
	 * The Messager to use.
	 */
	private final PermanentActionBarMessages messages;
	
	/**
	 * If actionbars are supported
	 */
	private final boolean supportsActionBars;
	
	/**
	 * The Segments to display.
	 */
	private Set<DisplaySegment> segments = new HashSet<>();
	
	
	
	public PlayerActionBarDisplay(RaCPlayer player) {
		this.player = player;
		this.messages = PermanentActionBarMessages.get(RacesAndClasses.getPlugin());
		this.supportsActionBars = VollotileCodeManager.getVollotileCode().getVersion().hasActionBar();
	}
	
	
	
	
	/**
	 * Displays the Line.
	 */
	public void display(){
		if(!supportsActionBars) return;
		if(!this.player.isOnline()) return;
		
		Player player = this.player.getPlayer();
		String line = generateLine();
		
		if(line.isEmpty()) messages.removeMessage(player);
		else messages.setMessage(player, line);
	}
	
	
	/**
	 * Generates the Line to display.
	 * @return the genetated line.
	 */
	private String generateLine(){
		String format = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_actionbar_format();
		for(DisplaySegment segment : segments){
			format = format.replace("%"+segment.getName()+"%", segment.getDisplayString())
							.replace("%"+segment.getName().toUpperCase()+"%", segment.getDisplayString());
		}
		
		return ChatColor.translateAlternateColorCodes('&', format);
	}
	
	
	/**
	 * Sets the Value.
	 * @param key to use
	 * @param displayString to use
	 * @param priority to use.
	 */
	public void setSegment(String key, String displayString){
		for(DisplaySegment segment : segments){
			if(segment.getName().equals(key)) {
				segment.setDisplayString(displayString);
				return;
			}
		}
		
		DisplaySegment newSegment = new DisplaySegment(key, displayString);
		segments.add(newSegment);
	}
	
}
