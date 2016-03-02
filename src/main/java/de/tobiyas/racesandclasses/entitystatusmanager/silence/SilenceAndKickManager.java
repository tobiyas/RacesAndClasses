package de.tobiyas.racesandclasses.entitystatusmanager.silence;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.MagicSpellTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.TraitWithCost;

public class SilenceAndKickManager {

	/**
	 * The map of people that are silenced -> when they are silenced.
	 */
	private Map<UUID,Long> silenceMap = new HashMap<>();
	
	/**
	 * The map of people that are silenced -> when they are silenced.
	 */
	private Map<UUID,Set<MagicSpellTrait>> currentlyCastingMap = new HashMap<>();
	
	
	
	
	/**
	 * Notifies that the player starts channeling.
	 * @param entityID to start.
	 * @param spell to start.
	 */
	public void startsChannel(UUID entityID, MagicSpellTrait spell){
		double channelTime = spell.getChannelingTime();
		if(channelTime > 0) {
			Set<MagicSpellTrait> traits = currentlyCastingMap.get(entityID);
			if(traits == null) { traits = new HashSet<>(); currentlyCastingMap.put(entityID, traits); }
			traits.add(spell);
		}
	}
	
	
	/**
	 * Ends the current Channel.
	 * @param id to remove.
	 */
	public void endChannel(UUID entityID, TraitWithCost trait){
		Set<MagicSpellTrait> traits = currentlyCastingMap.get(entityID);
		if(traits == null) return;
		
		traits.remove(traits);
	}
	
	
	/**
	 * If the Entity is channeling.
	 * @param entityID that is channeling.
	 * @return true if is channeling.
	 */
	public boolean isChanneling(UUID entityID){
		Set<MagicSpellTrait> traits = currentlyCastingMap.get(entityID);
		if(traits != null && !traits.isEmpty()) return false;
		
		for(MagicSpellTrait trait : traits) if(trait.isKickable()) return true;
		return false;
	}
	
	/**
	 * Kicks the Entity that is channeling.
	 * @param entityID that is channeling.
	 * @param timeInMs to kick.
	 * @return true kicked something.
	 */
	public boolean kickChanneling(UUID entityID, long timeInMs){
		Set<MagicSpellTrait> traits = currentlyCastingMap.get(entityID);
		if(traits == null || traits.isEmpty()) return false;
		
		int kicked = 0;
		Iterator<MagicSpellTrait> it = traits.iterator();
		while(it.hasNext()){
			MagicSpellTrait trait = it.next();
			if(!trait.isKickable()) continue;
			
			trait.gotKicked(entityID);
			it.remove();
			kicked++;
		}
		
		if(kicked > 0) silenceMap.put(entityID, System.currentTimeMillis() + timeInMs);
		return kicked > 0;
	}
	
	
	/**
	 * Silences the Entity.
	 * @param entityID that is channeling.
	 * @param timeInMs to kick.
	 * @return true kicked something.
	 */
	public boolean silence(UUID entityID, long timeInMs){
		boolean interrupted = kickChanneling(entityID, timeInMs);
		if(!interrupted) silenceMap.put(entityID, System.currentTimeMillis() + timeInMs);
		return interrupted;
	}
	
	
	/**
	 * If the Entity is silenced.
	 * @param entityID to check
	 * @return true if is silenced.
	 */
	public boolean isSilenced(UUID entityID){
		if(entityID == null || !silenceMap.containsKey(entityID)) return false;
		return System.currentTimeMillis() < silenceMap.get(entityID);
	}
	
	
	/**
	 * Returns the rest time the entity is silenced (in MiliSeconds).
	 * @param entityID to get.
	 */
	public long getRestSilenceTime(UUID entityID){
		if(entityID == null || !silenceMap.containsKey(entityID)) return 0;
		long rest = silenceMap.get(entityID) - System.currentTimeMillis();
		return Math.max(0, rest);
	}
	
}
