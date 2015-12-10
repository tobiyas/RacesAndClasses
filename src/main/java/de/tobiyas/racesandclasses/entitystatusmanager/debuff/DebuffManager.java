package de.tobiyas.racesandclasses.entitystatusmanager.debuff;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.bukkit.scheduler.BukkitTask;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.util.schedule.DebugBukkitRunnable;

public class DebuffManager {

	/**
	 * The Amount of ticks per Tick.
	 */
	private final int TICKS_PER_TICK = 10;;
	
	
	/**
	 * The Debuffs that are present.
	 */
	private final Set<Debuff> debuffs = new HashSet<Debuff>();
	
	/**
	 * The task that is running.
	 */
	private BukkitTask task;
	
	/**
	 * Boots up the manager.
	 */
	public DebuffManager() {
	}
	
	
	/**
	 * Shuts everything down.
	 */
	public void shutdown() {
		debuffs.clear();
		
		if(task != null){
			task.cancel();
			task = null;
		}
	}

	
	/**
	 * inits everything.
	 */
	public void init() {
		debuffs.clear();
		
		if(task != null){
			task.cancel();
			task = null;
		}
		
		task = new DebugBukkitRunnable("DebuffTick"){
			@Override
			protected void runIntern() {
				if(debuffs.isEmpty()) return;
				
				tick();
			}
		}.runTaskTimer(RacesAndClasses.getPlugin(), TICKS_PER_TICK, TICKS_PER_TICK);
	}
	
	
	/**
	 * Returns the Debuffs of the Entity ID.
	 * 
	 * @param id to get
	 * 
	 * @return all Debuffs of this entity.
	 */
	public Set<Debuff> getAllDebuffs(UUID id){
		Set<Debuff> idDebuffs = new HashSet<Debuff>();
		synchronized (debuffs) {
			for(Debuff debuff : debuffs){
				if(debuff.getEntityID().equals(id)) idDebuffs.add(debuff);
			}
		}
		
		return idDebuffs;
	}
	
	
	/**
	 * Registers the Debuff.
	 * <br>This cancels all Debuff with the same name.
	 * 
	 * @param debuff to register.
	 */
	public void register(Debuff debuff){
		for(Debuff toCheck : debuffs){
			if(toCheck.getDisplayName().equals(debuff)){
				debuffs.remove(toCheck);
				break;
			}
		}
		
		this.debuffs.add(debuff);
	}
	
	
	/**
	 * Cancels the Debuff passed.
	 * 
	 * @param debuff to cancel
	 */
	public void cancel(Debuff toCancel){
		for(Debuff debuff : debuffs){
			if(debuff == toCancel){
				debuffs.remove(debuff);
				break;
			}
		}
	}
	
	
	/**
	 * Ticks all Debuffs and removes the ones done.
	 */
	protected void tick(){
		synchronized (debuffs) {
			Iterator<Debuff> it = debuffs.iterator();
			
			while(it.hasNext()){
				for(int i = 0; i < TICKS_PER_TICK; i++){
					Debuff next = it.next();
					next.tick();
					
					if(next.done()) {
						it.remove();
						break;
					}
				}
				
			}
		}
	}
	
}
