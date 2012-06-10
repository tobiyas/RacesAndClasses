package de.tobiyas.races.datacontainer.traitcontainer.eventmanagement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Observable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import de.tobiyas.races.Races;
import de.tobiyas.races.datacontainer.health.EntityDamageDoubleEvent;
import de.tobiyas.races.datacontainer.health.HealthManager;
import de.tobiyas.races.datacontainer.health.HealthModifyContainer;
import de.tobiyas.races.datacontainer.traitcontainer.TraitsList;
import de.tobiyas.races.datacontainer.traitcontainer.UplinkReducer;
import de.tobiyas.races.datacontainer.traitcontainer.traits.Trait;
import de.tobiyas.races.datacontainer.traitcontainer.traits.TraitsWithUplink;


public class TraitEventManager extends Observable{
	private Races plugin;
	
	private static TraitEventManager manager;
	private HashMap<Class<?>, HashSet<Trait>> traitList;
	private HashMap<Integer, Long> eventIDs;
	
	private UplinkReducer uplinkReducer;
	

	public TraitEventManager(){
		plugin = Races.getPlugin();
		TraitsList.initTraits();
		manager = this;
		traitList = new HashMap<Class<?>, HashSet<Trait>>();
		eventIDs = new HashMap<Integer, Long>();
		new DoubleEventRemover(this);
		uplinkReducer = new UplinkReducer();
	}
	
	public void init(){
		addObserver(HealthManager.getHealthManager());
		notifyObservers(null);
		setChanged();
	}
	
	private boolean fireEventIntern(Event event){
		boolean changedSomething = false;
		if(eventIDs.containsKey(event.hashCode()))
			return false;
		else
			eventIDs.put(event.hashCode(), System.currentTimeMillis());
		
		HashSet<Trait> traitsToCheck = new HashSet<Trait>();
		for(Class<?> clazz : traitList.keySet()){			
			if(event.getClass().isAssignableFrom(clazz))
				traitsToCheck.addAll(traitList.get(clazz));
		}
				
		for(Trait trait: traitsToCheck){
			try{
				if(trait.modify(event))
					changedSomething = true;
			}catch(Exception e){
				plugin.getDebugLogger().logError("Error while executing trait: " + trait.getName() + " of race: " + 
						trait.getRace().getName() + " event was: " + event.getEventName() + " Error was: " + e.getLocalizedMessage());
				
				e.printStackTrace();
			}
		}
		
		if(event instanceof EntityDamageDoubleEvent){
			EntityDamageDoubleEvent Eevent = (EntityDamageDoubleEvent) event;
			if(Eevent.getEntityType() == EntityType.PLAYER){
				Player player = (Player)(Eevent.getEntity());
				double dmg = Eevent.getDoubleValueDamage();
				Eevent.setDamage(0);
				notifyObservers(new HealthModifyContainer(player.getName(), dmg, "damage"));
				setChanged();
			}else{
				if(!(Eevent.getEntity() instanceof LivingEntity)) return false;
				LivingEntity entity = (LivingEntity) Eevent.getEntity();
				entity.damage(Eevent.getDamage());
			}
		}else
		if(event instanceof EntityDamageEvent){
			EntityDamageEvent Eevent = (EntityDamageEvent) event;
			if(Eevent.getEntityType() == EntityType.PLAYER){
				Player player = (Player)(Eevent.getEntity());
				int dmg = Eevent.getDamage();
				notifyObservers(new HealthModifyContainer(player.getName(), dmg, "damage"));
				setChanged();
				Eevent.setDamage(0);
			}else{
				if(!(Eevent.getEntity() instanceof LivingEntity)) return false;
				LivingEntity entity = (LivingEntity) Eevent.getEntity();
				entity.damage(Eevent.getDamage());
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
	
	public void registerTrait(Trait trait, HashSet<Class<?>> events){
		for(Class<?> clazz : events){
			HashSet<Trait> traits = traitList.get(clazz);
			if(traits == null){
				traits = new HashSet<Trait>();
				traitList.put(clazz, traits);
			}
			
			traits.add(trait);
		}
		
		if(trait instanceof TraitsWithUplink)
			uplinkReducer.registerTrait((TraitsWithUplink) trait);
	}
	
	public void unregisterTrait(Trait trait){
		traitList.remove(trait);
	}
	
	public static TraitEventManager getInstance(){
		return manager;
	}
	
	public static boolean fireEvent(Event event){
		return getInstance().fireEventIntern(event);
	}
	
}
