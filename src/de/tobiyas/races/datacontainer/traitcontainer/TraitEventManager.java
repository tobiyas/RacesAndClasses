package de.tobiyas.races.datacontainer.traitcontainer;

import java.util.ArrayList;
import java.util.LinkedList;

import org.bukkit.event.Event;

import de.tobiyas.races.util.statics.TraitsList;

public class TraitEventManager{
	
	private static TraitEventManager manager;
	private LinkedList<Trait> traitList;

	public TraitEventManager(){
		TraitsList.initTraits();
		manager = this;
		traitList = new LinkedList<Trait>();
	}
	
	public ArrayList<ReturnFilter> modifyEvent(Event event){
		ArrayList<ReturnFilter> filters = new ArrayList<ReturnFilter>();
		
		for(Trait trait: traitList){
			ReturnFilter value = trait.modify(event);
			
			if(value != null)
				filters.add(value);
		}
		
		return filters;
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
