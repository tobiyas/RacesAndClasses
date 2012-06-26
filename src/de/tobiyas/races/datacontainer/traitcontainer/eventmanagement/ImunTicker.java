package de.tobiyas.races.datacontainer.traitcontainer.eventmanagement;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import de.tobiyas.races.Races;

public class ImunTicker implements Runnable {

	private HashMap<Entity, Integer> imunMap;
	private Races plugin;
	
	private int imunTicks;
	private int taskID = -1;
	
	public ImunTicker(){
		imunMap = new HashMap<Entity, Integer>();
		plugin = Races.getPlugin();
	}
	
	public void init(){
		imunMap.clear();
		imunTicks = (int) Math.ceil(plugin.interactConfig().getconfig_imunBetweenDamage() / 50);
		if(taskID > 0)
			Bukkit.getScheduler().cancelTask(taskID);
		
		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 1, 1);
	}
	
	public boolean isImun(Entity entity){
		return imunMap.containsKey(entity);
	}
	
	public void addToImunList(Entity entity){
		if(imunMap.containsKey(entity))
			return;
		
		imunMap.put(entity, imunTicks);
	}
	
	@Override
	public void run() {
		HashMap<Entity, Integer> toCopy = new HashMap<Entity, Integer>();
		
		for(Entity entity : imunMap.keySet()){
			if(entity == null || entity.isDead())
				continue;
				
			int time = imunMap.get(entity);
			time--;
			if(time > 0)
				toCopy.put(entity, time);
		}
		
		imunMap.clear();
		imunMap.putAll(toCopy);
	}

}
