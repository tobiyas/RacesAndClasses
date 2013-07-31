package de.tobiyas.racesandclasses.chat.channels.container;

import java.util.HashSet;

import org.bukkit.Bukkit;

import de.tobiyas.racesandclasses.RacesAndClasses;

public class ChannelTicker implements Runnable {

	private RacesAndClasses plugin;
	
	protected static ChannelTicker ticker = null;
	protected int currentTask;
	protected HashSet<ChannelContainer> channels;
	
	
	public ChannelTicker(){
		plugin = RacesAndClasses.getPlugin();
		
		channels = new HashSet<ChannelContainer>();
		currentTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 20, 20);
	}
	
	private void disableIntern(){
		channels.clear();
		Bukkit.getScheduler().cancelTask(currentTask);
		ticker = null;
	}
	
	public static void init(){
		if(ticker == null){
			ticker = new ChannelTicker();
		}
	}
	
	public static void disable(){
		if(ticker != null){
			ticker.disableIntern();
		}
	}
	
	public static void registerChannel(ChannelContainer container){
		init();
		ticker.channels.add(container);
	}
	
	public static void unregisterChannel(ChannelContainer container){
		init();
		ticker.channels.remove(container);
	}
	
	@Override
	public void run() {
		for(ChannelContainer container : channels){
			if(container == null) {
				channels.remove(container);
			}else{
				container.tick();
			}
		}
	}

}
