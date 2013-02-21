package de.tobiyas.races.commands.debug;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import de.tobiyas.races.Races;
import de.tobiyas.races.datacontainer.eventmanagement.TraitEventManager;
import de.tobiyas.races.util.consts.Consts;

public class DebugTimingEvents implements Runnable{

	private Races plugin;
	
	private CommandSender sender;
	private long timing;
	private long calls;
	private long startTime;
	
	private int seconds;
	
	private int currentID;
	
	public DebugTimingEvents(CommandSender sender){
		plugin = Races.getPlugin();
		this.sender = sender;
		currentID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, 20);
		
		timing = 0;
		seconds = 0;
		
		TraitEventManager.timingResults();
		TraitEventManager.getCalls();
		startTime = System.currentTimeMillis();
	}

	@Override
	public void run() {		
		timing += TraitEventManager.timingResults();
		calls += TraitEventManager.getCalls();
		
		seconds++;
		
		if(seconds > Consts.timingLength){
			long takenMS = System.currentTimeMillis() - startTime;
			double percent = timing*100 / takenMS;
			
			if(sender != null){
				sender.sendMessage(ChatColor.GREEN + "Time taken in " + ChatColor.LIGHT_PURPLE + takenMS + ChatColor.GREEN +
									" milliseconds: " + ChatColor.LIGHT_PURPLE + timing +
									ChatColor.GREEN + " ms. This is: " + ChatColor.LIGHT_PURPLE + percent + ChatColor.GREEN + 
									"% of the total computing time. There where " + ChatColor.LIGHT_PURPLE + calls + ChatColor.GREEN + " Event-calls.");
			}
			
			Bukkit.getScheduler().cancelTask(currentID);
			return;
		}
	}
}
