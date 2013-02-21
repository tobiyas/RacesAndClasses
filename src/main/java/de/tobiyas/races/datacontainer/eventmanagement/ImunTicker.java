package de.tobiyas.races.datacontainer.eventmanagement;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import de.tobiyas.races.Races;

public class ImunTicker implements Runnable {

	private HashMap<Entity, Integer> imunMap;
	private Races plugin;
	
	private int imunTicks;
	private int taskID = -1;
	
	private boolean useInternSystem;
	
	public ImunTicker(){
		imunMap = new HashMap<Entity, Integer>();
		plugin = Races.getPlugin();
		useInternSystem = true;
	}
	
	public void init(){
		synchronized(imunMap){
			imunMap.clear();
			imunTicks = (int) Math.ceil(plugin.getGeneralConfig().getConfig_imunBetweenDamage() / 50);
			if(taskID > 0)
				Bukkit.getScheduler().cancelTask(taskID);
			
			taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 1, 1);
		}
		
		useInternSystem = plugin.getGeneralConfig().getConfig_useInternImunSystem();
	}
	
	public boolean isImun(LivingEntity entity){
		if(!useInternSystem)
			return entity.getNoDamageTicks() != 0;
		return imunMap.containsKey(entity);
	}
	
	public boolean isImun(Entity entity){
		if(entity instanceof LivingEntity)
			return isImun((LivingEntity) entity);
		
		return false;
	}
	
	public void addToImunList(LivingEntity entity){
		if(!useInternSystem){
			if(entity.getNoDamageTicks() != 0)
				return;
			
			entity.setNoDamageTicks(entity.getMaximumNoDamageTicks());
			return;
		}
		if(imunMap.containsKey(entity))
			return;
		
		synchronized(imunMap){
			imunMap.put(entity, imunTicks);
		}
	}
	
	public void addToImunList(Entity entity){
		if(entity instanceof LivingEntity)
			addToImunList((LivingEntity) entity);
		return;
	}
	
	@Override
	public void run() {
		if(useInternSystem){
			HashMap<Entity, Integer> toCopy = new HashMap<Entity, Integer>();
			
			synchronized(imunMap){
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
	}

}
