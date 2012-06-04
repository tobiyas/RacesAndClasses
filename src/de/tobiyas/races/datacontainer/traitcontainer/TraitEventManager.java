package de.tobiyas.races.datacontainer.traitcontainer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Observable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import de.tobiyas.races.Races;
import de.tobiyas.races.datacontainer.health.HealthManager;
import de.tobiyas.races.datacontainer.health.HealthModifyContainer;
import de.tobiyas.races.datacontainer.traitcontainer.traits.Trait;
import de.tobiyas.races.datacontainer.traitcontainer.traits.TraitsWithUplink;


public class TraitEventManager extends Observable{
	
	private Races plugin;
	
	private static TraitEventManager manager;
	private LinkedList<Trait> traitList;
	private HashMap<Integer, Long> eventIDs;
	
	private UplinkReducer uplinkReducer;
	

	public TraitEventManager(){
		plugin = Races.getPlugin();
		TraitsList.initTraits();
		manager = this;
		traitList = new LinkedList<Trait>();
		eventIDs = new HashMap<Integer, Long>();
		new DoubleEventRemover(this);
		uplinkReducer = new UplinkReducer();
	}
	
	public void init(){
		addObserver(HealthManager.getHealthManager());
		notifyObservers(null);
		setChanged();
	}
	
	public boolean modifyEvent(Event event){
		boolean changedSomething = false;
		if(eventIDs.containsKey(event.hashCode()))
			return false;
		else
			eventIDs.put(event.hashCode(), System.currentTimeMillis());
		
		for(Trait trait: traitList){
			try{
				if(trait.modify(event))
					changedSomething = true;
			}catch(Exception e){
				plugin.getDebugLogger().logError("Error while executing trait: " + trait.getName() + " of race: " + 
						trait.getRace().getName() + " event was: " + event.getEventName() + " Error was: " + e.getLocalizedMessage());
				
				e.printStackTrace();
			}
		}
		
		if(event instanceof EntityDamageEvent){
			EntityDamageEvent Eevent = (EntityDamageEvent) event;
			if(Eevent.getEntityType() == EntityType.PLAYER){
				Player player = (Player)(Eevent.getEntity());
				int dmg = Eevent.getDamage();
				Eevent.setDamage(0);
				notifyObservers(new HealthModifyContainer(player.getName(), dmg, "damage"));
				setChanged();
			}
		}
		
		if(event instanceof EntityRegainHealthEvent){
			EntityRegainHealthEvent Eevent = (EntityRegainHealthEvent) event;
			if(Eevent.getEntityType() == EntityType.PLAYER){
				Player player = (Player)(Eevent.getEntity());
				int health = Eevent.getAmount();
				Eevent.setAmount(0);
				notifyObservers(new HealthModifyContainer(player.getName(), health, "heal"));
				setChanged();
			}
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
		if(trait instanceof TraitsWithUplink)
			uplinkReducer.registerTrait((TraitsWithUplink) trait);
	}
	
	public void unregisterTrait(Trait trait){
		traitList.remove(trait);
	}
	
	public static TraitEventManager getTraitEventManager(){
		return manager;
	}
	
}
