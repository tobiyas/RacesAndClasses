package de.tobiyas.races.datacontainer.traitcontainer.eventmanagement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Observable;

import org.bukkit.EntityEffect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.util.Vector;

import de.tobiyas.races.Races;
import de.tobiyas.races.datacontainer.health.HealthManager;
import de.tobiyas.races.datacontainer.health.HealthModifyContainer;
import de.tobiyas.races.datacontainer.health.damagetickers.DamageTicker;
import de.tobiyas.races.datacontainer.traitcontainer.TraitsList;
import de.tobiyas.races.datacontainer.traitcontainer.UplinkReducer;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.events.EntityDamageByBlockDoubleEvent;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.events.EntityDamageByEntityDoubleEvent;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.events.EntityDamageDoubleEvent;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.events.EntityKnockbackEvent;
import de.tobiyas.races.datacontainer.traitcontainer.traits.Trait;
import de.tobiyas.races.datacontainer.traitcontainer.traits.TraitsWithUplink;


public class TraitEventManager extends Observable{
	private Races plugin;
	private static long timings = 0;
	private static long calls = 0;
	
	private static TraitEventManager manager;
	private HashMap<Class<?>, HashSet<Trait>> traitList;
	private HashMap<Integer, Long> eventIDs;
	
	private UplinkReducer uplinkReducer;
	private EntityDeathManager entityDeathManager;
	private ImunTicker imunTicker;
	

	public TraitEventManager(){
		plugin = Races.getPlugin();
		TraitsList.initTraits();
		manager = this;
		traitList = new HashMap<Class<?>, HashSet<Trait>>();
		eventIDs = new HashMap<Integer, Long>();
		new DoubleEventRemover(this);
		uplinkReducer = new UplinkReducer();
		entityDeathManager = new EntityDeathManager();
		imunTicker = new ImunTicker();
	}
	
	public void init(){
		entityDeathManager.init();
		imunTicker.init();
		addObserver(HealthManager.getHealthManager());
		notifyObservers(null);
		setChanged();
	}
	
	private boolean fireEventIntern(Event event){		
		calls ++;
		boolean changedSomething = false;
		if(eventIDs.containsKey(event.hashCode()))
			return false;
		else
			eventIDs.put(event.hashCode(), System.currentTimeMillis());
		
		if(replaceEvent(event)) return true;
				
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
		
		fireDamage(event);
		return changedSomething;
	}
	
	private boolean replaceEvent(Event event){
		if(event instanceof EntityDamageDoubleEvent) return false;
		
		if(event instanceof EntityDamageByEntityEvent){
			EntityDamageByEntityEvent Eevent = (EntityDamageByEntityEvent) event;				
			EntityDamageByEntityDoubleEvent edEvent = new EntityDamageByEntityDoubleEvent(Eevent);
			Eevent.setDamage(0);
			fireEventIntern(edEvent);
			return true;
		}
		
		if(event instanceof EntityDamageByBlockEvent){
			EntityDamageByBlockEvent Eevent = (EntityDamageByBlockEvent) event;
			EntityDamageByBlockDoubleEvent edEvent = new EntityDamageByBlockDoubleEvent(Eevent);
			Eevent.setDamage(0);
			fireEventIntern(edEvent);
			return true;
		}
		
		if(event instanceof EntityDamageEvent){
			EntityDamageEvent Eevent = (EntityDamageEvent) event;
			
			if(Eevent.getCause() == DamageCause.FIRE_TICK){
				Eevent.setDamage(0);
				return true;
			}
			
			if((Eevent.getCause() == DamageCause.FIRE || 
				Eevent.getCause() == DamageCause.LAVA) 
				&& Eevent.getEntity() instanceof LivingEntity){
				
				if(DamageTicker.hasEffect(Eevent.getEntity(), DamageCause.FIRE) != 0)
					DamageTicker.cancleEffects((LivingEntity) Eevent.getEntity(), DamageCause.FIRE);
				
				new DamageTicker((LivingEntity) Eevent.getEntity(), Eevent.getEntity().getFireTicks() / 20, 1, DamageCause.FIRE);
			}
			
			EntityDamageDoubleEvent edEvent = new EntityDamageDoubleEvent(Eevent);
			Eevent.setDamage(0);
			fireEventIntern(edEvent);
			return true;
		}
		return false;
	}
	
	private void fireDamage(Event event){
		if(event instanceof EntityDamageDoubleEvent){
			EntityDamageDoubleEvent Eevent = (EntityDamageDoubleEvent) event;
			if(imunTicker.isImun(Eevent.getEntity())) return; //Check if imun to dmg
			
			if(event instanceof EntityDamageByEntityDoubleEvent) checkKnockBack(event);
			checkDeath(Eevent);
			
			if(Eevent.getEntityType() == EntityType.PLAYER){
				Player player = (Player)(Eevent.getEntity());
				double dmg = Eevent.getDoubleValueDamage();
				notifyObservers(new HealthModifyContainer(player.getName(), dmg, "damage"));
				setChanged();
			}else{
				if(!(Eevent.getEntity() instanceof LivingEntity)) return;
				LivingEntity entity = (LivingEntity) Eevent.getEntity();
				
				entity.playEffect(EntityEffect.HURT);
				entity.damage((int) Eevent.getDoubleValueDamage());
			}
			imunTicker.addToImunList(Eevent.getEntity());
		}else
		//Should never be called again (only for debuging in)
		if(event instanceof EntityDamageEvent){
			EntityDamageEvent Eevent = (EntityDamageEvent) event;
			if(Eevent.getEntityType() == EntityType.PLAYER){
				Player player = (Player)(Eevent.getEntity());
				int dmg = Eevent.getDamage();
				notifyObservers(new HealthModifyContainer(player.getName(), dmg, "damage"));
				setChanged();
				Eevent.setDamage(0);
			}else{
				if(!(Eevent.getEntity() instanceof LivingEntity)) return;
				LivingEntity entity = (LivingEntity) Eevent.getEntity();
				
				entity.playEffect(EntityEffect.HURT);
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
	}
	
	
	private void checkKnockBack(Event event){
		if(!(event instanceof EntityDamageByEntityDoubleEvent)) return;
		EntityDamageByEntityDoubleEvent Eevent = (EntityDamageByEntityDoubleEvent) event;
		Entity target = Eevent.getEntity();
		Entity damager = Eevent.getDamager();
		
		if(target == null || damager == null || target.isDead())
			return;
		
		if(imunTicker.isImun(target))
			return;
		
		if(target.getType() == EntityType.ENDER_DRAGON)
			return;
		
		EntityKnockbackEvent knockbackEvent = new EntityKnockbackEvent(target, damager);
		fireEventIntern(knockbackEvent);
				
		Vector velocity = target.getVelocity();
		velocity = velocity.add(knockbackEvent.getKnockback());
		target.setVelocity(velocity);
	}
	
	private void checkDeath(EntityDamageDoubleEvent event){
		Entity entity = event.getEntity();
		if(!(entity instanceof LivingEntity)) return;
		if(entity instanceof Player) return;
		if(imunTicker.isImun(entity)) return;
		
		LivingEntity lEntity = (LivingEntity) entity;
		int currentHP = lEntity.getHealth();
		if(currentHP - event.getDoubleValueDamage() <= 0)
			entityDeathManager.addToDie(lEntity);
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
		long time = System.currentTimeMillis();
		boolean result = getInstance().fireEventIntern(event);
		timings += System.currentTimeMillis() - time;
		
		return result;
	}
	
	public static long timingResults(){
		long time = new Long(timings);
		timings = 0;
		return time;
	}
	
	public static long getCalls(){
		long tempCalls = new Long(calls);
		calls = 0;
		return tempCalls;
	}
	
	public static ImunTicker getImunTicker(){
		return getInstance().imunTicker;
	}
	
}
