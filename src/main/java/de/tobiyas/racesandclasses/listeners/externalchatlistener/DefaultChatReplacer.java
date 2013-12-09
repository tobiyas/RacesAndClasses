package de.tobiyas.racesandclasses.listeners.externalchatlistener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;

public class DefaultChatReplacer implements Listener {

	/**
	 * The Plugin to call stuff on.
	 */
	private final RacesAndClasses plugin;
	
	
	public DefaultChatReplacer() {
		this.plugin = RacesAndClasses.getPlugin();
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void asyncChat(AsyncPlayerChatEvent event){
		if(plugin.getConfigManager().getGeneralConfig().isConfig_channels_enable()){
			//plugin is already handling chat.
			return;
		}
		
		String format = event.getFormat();
		if(format == null) return;
		
		String senderName = event.getPlayer().getName();
		
		String raceTag = "";
		String classTag = "";

		AbstractTraitHolder holder = plugin.getRaceManager().getHolderOfPlayer(senderName);
		if(holder != null){
			raceTag = holder.getTag();
			if("".equals(raceTag)){
				raceTag = "[" + holder.getName() + "]";
			}
		}
		
		holder = plugin.getClassManager().getHolderOfPlayer(senderName);
		if(holder != null){
			classTag = holder.getTag();
			if("".equals(classTag)){
				classTag = "[" + holder.getName() + "]";
			}
		}
		
		String raceReplacement = "{race}";
		String raceReplacementRegex = "\\{race\\}";

		String classReplacement = "{class}";
		String classReplacementRegex = "\\{class\\}";
		
	    if (format.contains(raceReplacement)) {
	      String lastColor = ChatColor.getLastColors(event.getMessage().split(raceReplacementRegex)[0]);
	      raceTag += lastColor;
	      format = format.replaceAll(raceReplacementRegex, raceTag);
	    }

	    if (format.contains(classReplacement)) {
			String lastColor = ChatColor.getLastColors(event.getMessage().split(classReplacementRegex)[0]);
			classTag += lastColor;
			format = format.replaceAll(classReplacementRegex, classTag);
	    }
	    
	    event.setFormat(format);
	}
}
