package de.tobiyas.racesandclasses.entitystatusmanager.hots;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.entity.LivingEntity;

public class HotContainer {

	/**
	 * This is the containing entity.
	 */
	private final LivingEntity entity;
	
	/**
	 * The Hots running on this entity.
	 */
	private final Set<Hot> hots = new HashSet<Hot>();
	
	
	public HotContainer(LivingEntity entity) {
		this.entity = entity;
	}

	
	
	/**
	 * Adds a hot.
	 * 
	 * @param toAdd
	 */
	public boolean addHot(Hot toAdd){
		removeInvalid();
		
		if(!toAdd.canStack()){
			String id = toAdd.getId();
			for(Hot hot : hots){
				if(hot.getId().equals(id)){
					return false;
				}
			}
		}
		
		hots.add(toAdd);
		return true;
	}
	
	
	/**
	 * Removes all Invalid containers.
	 */
	private void removeInvalid(){
		Iterator<Hot> it = hots.iterator();
		while(it.hasNext()){
			if(!it.next().stillValid()) it.remove();
		}
	}
	
	
	/**
	 * Ticks the container.
	 */
	public void tick(){
		Iterator<Hot> it = hots.iterator();
		while(it.hasNext()){
			Hot hot = it.next();
			if(!hot.stillValid()) { it.remove(); continue; }
			
			hot.tick();
			
			if(!hot.stillValid()) { it.remove(); continue; }
		}
	}
	
	
	/**
	 * Checks if this container is still valid.
	 * @return
	 */
	public boolean stillValid(){
		return entity.isValid();
	}



	/**
	 * Clears the Hots.
	 */
	public void reset() {
		hots.clear();
	}



	/**
	 * Returns a Set of active Hots.
	 * This is a copied set. The Hots are still valid though.
	 * 
	 * @return the set of hots.
	 */
	public Set<Hot> getAllActiveHots() {
		this.removeInvalid();
		
		return new HashSet<Hot>(hots);
	}



	/**
	 * Clears the hot with the ID passed off the target.
	 * 
	 * @param id to check
	 */
	public void clearHot(String id) {
		Iterator<Hot> it = hots.iterator();
		while(it.hasNext()){
			Hot hot = it.next();
			if(hot.getId().equals(id)) it.remove();
		}
	}

	/**
	 * Clears the passed Hot from the Entity.
	 * 
	 * @param hot to remove
	 */
	public void clearHot(Hot hot) {
		hots.remove(hot);
	}
	
}
