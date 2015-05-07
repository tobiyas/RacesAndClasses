package de.tobiyas.racesandclasses.entitystatusmanager.buffs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import de.tobiyas.racesandclasses.RacesAndClasses;

public class BuffManager implements Listener {

	/**
	 * The Buffs per ID.
	 */
	private final Map<UUID,Set<BuffContainer>> buffMap = new HashMap<UUID, Set<BuffContainer>>();
	
	
	
	public BuffManager() {
		RacesAndClasses.getPlugin().registerEvents(this);
	}
	
	
	/**
	 * inits and cleans the Container.
	 */
	public void init(){
		buffMap.clear();
	}
	
	
	
	/**
	 * Returns a Set of active Buffs.
	 * 
	 * @param id to get from.
	 * 
	 * @return a set of active buffs.
	 */
	public Set<BuffContainer> get(UUID id){
		Set<BuffContainer> buffs = buffMap.get(id);
		if(buffs == null){
			buffs = new HashSet<BuffContainer>();
			buffMap.put(id, buffs);
			return buffs;
		}
		
		Iterator<BuffContainer> it = buffs.iterator();
		long now = System.currentTimeMillis();
		while(it.hasNext()){
			if(it.next().getEnds() < now) it.remove();
		}
		
		return buffs;
	}
	
	
	
	/**
	 * Adds a buff to the buff list.
	 * 
	 * @param id to add
	 * @param name to add to
	 * @param endTime to set
	 */
	public void addBuff(UUID id, String name, long endTime){
		Set<BuffContainer> buffs = get(id);
		removedOrUsedBuff(id, name);
		
		buffs.add(new BuffContainer(endTime, name));
	}
	

	/**
	 * The ID used a buff.
	 * 
	 * @param id that used it
	 * @param name to remove
	 */
	public void removedOrUsedBuff(UUID id, String name){
		Set<BuffContainer> buffs = get(id);
		for(BuffContainer container : buffs){
			if(name.equals(container.getName())){
				buffs.remove(container);
				break;
			}
		}
	}
	
	
	
	@EventHandler
	public void playerDied(PlayerDeathEvent event){
		buffMap.remove(event.getEntity().getUniqueId());
	}
	
}
