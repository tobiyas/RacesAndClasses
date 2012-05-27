package de.tobiyas.races.datacontainer.traitcontainer;

import java.util.HashMap;
import java.util.LinkedList;

import org.bukkit.event.Event;


public class TraitEventManager{
	
	private static TraitEventManager manager;
	private LinkedList<Trait> traitList;
	private HashMap<Integer, Long> eventIDs;
	

	public TraitEventManager(){
		TraitsList.initTraits();
		manager = this;
		traitList = new LinkedList<Trait>();
		eventIDs = new HashMap<Integer, Long>();
		new DoubleEventRemover(this);
	}
	
	public boolean modifyEvent(Event event){
		boolean changedSomething = false;
		if(eventIDs.containsKey(event.hashCode()))
			return false;
		else
			eventIDs.put(event.hashCode(), System.currentTimeMillis());
		
		for(Trait trait: traitList){
			if(trait.modify(event))
				changedSomething = true;
		}
		
		return changedSomething;
	}
	
	public void cleanEventList(){
		LinkedList<Integer> toRemove = new LinkedList<Integer>();
		long currentTime = System.currentTimeMillis();
		
		for(Integer inT : eventIDs.keySet()){
			long oldVal = eventIDs.get(inT);
			if((currentTime - oldVal) > 500)
				toRemove.add(inT);
		}
		
		for(Integer inT : toRemove)
			eventIDs.remove(inT);
		
	}
	
	public void registerTrait(Trait trait){
		traitList.add(trait);
	}
	
	public void unregisterTrait(Trait trait){
		traitList.remove(trait);
	}
	
	public static TraitEventManager getTraitEventManager(){
		return manager;
	}
	
}
