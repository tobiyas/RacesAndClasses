package de.tobiyas.racesandclasses.entitystatusmanager.stun;

import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.StunAPI;
import de.tobiyas.util.schedule.DebugBukkitRunnable;

public class StunReduceContainer {

	
	private int stunImunFactor = 0;
	
	private boolean tempImun = false;
	
	private BukkitTask task = null;
	
	/**
	 * The Entity to check.
	 */
	private final Entity entity;
	
	
	public StunReduceContainer(Entity entity) {
		this.entity = entity;
	}
	
	
	public void start(){
		if(task == null){
			task = new DebugBukkitRunnable("StunReduceTask") {
				@Override
				public void runIntern() {
					if(StunAPI.StunEntity.getRemainingStunTimeInTicks(entity) <= 0){
						reduce();
					}
				}
			}.runTaskTimer(RacesAndClasses.getPlugin(), 20, 20);
		}
	}
	
	
	public void stop(){
		if(task != null){
			task.cancel();
			task = null;
		}
	}
	
	
	public void notifyStun(){
		stunImunFactor += 30;
		stunImunFactor = Math.min(100, stunImunFactor);
		
		if(stunImunFactor >= 100) tempImun = true;
		
		stop();
	}
	
	
	public void notifyStunStop(){
		start();
	}
	
	
	public void reduce(){
		stunImunFactor -= 10;
		stunImunFactor = Math.max(0, stunImunFactor);
		
		if(stunImunFactor <= 0) tempImun = false;
	}
	
	
	/**
	 * The Ticks to apply to this guy.
	 * 
	 * @param ticks that may be stunned from the ticks passed.
	 * 
	 * @return the left stun ticks.
	 */
	public int getReducedTicks(int ticks){
		if(tempImun) return 0;
		
		double percent = 1d - ((double)stunImunFactor  / 100d);
		return (int) Math.round((double)ticks * percent);
	}
}
