package de.tobiyas.racesandclasses.eventprocessing;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.util.consts.Consts;

public class EntityDeathManager implements Runnable{

	private static EntityDeathManager entityDeathManager;
	private HashMap<LivingEntity, Integer> toDie;
	
	private RacesAndClasses plugin;
	private int taskID = -1;
	
	public EntityDeathManager(){
		plugin = RacesAndClasses.getPlugin();
		toDie = new HashMap<LivingEntity, Integer>();
		entityDeathManager = this;
		
	}
	
	public void init(){
		toDie.clear();
		if(taskID == -1)
			taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 20, 20);
	}
	
	public void deInit(){
		Bukkit.getScheduler().cancelTask(taskID);
		taskID = -1;
		toDie.clear();
	}
	
	public boolean canDrop(LivingEntity entity){
		return toDie.containsKey(entity);
	}
	
	public boolean willDrop(LivingEntity entity){
		if(!toDie.containsKey(entity)) return false;
		int left = toDie.remove(entity);
		return left >= 0;
	}
	
	public void resetEntityHit(LivingEntity entity){
		toDie.put(entity, Consts.secondsPlayerHit);
	}
	
	public static EntityDeathManager getManager(){
		return entityDeathManager;
	}

	@Override
	public void run() {
		HashMap<LivingEntity, Integer> newList = new HashMap<LivingEntity, Integer>();
		for(LivingEntity entity : toDie.keySet()){
			int time = toDie.get(entity);
			time --;
			if(time > 0){
				newList.put(entity, time);
			}
		}
		
		toDie.clear();
		toDie.putAll(newList);
	}
	
}
