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
import de.tobiyas.races.datacontainer.traitcontainer.TraitStore;
import de.tobiyas.races.datacontainer.traitcontainer.TraitsList;
import de.tobiyas.races.datacontainer.traitcontainer.UplinkReducer;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.events.EntityDamageByBlockDoubleEvent;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.events.EntityDamageByEntityDoubleEvent;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.events.EntityDamageDoubleEvent;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.events.EntityHealEvent;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.events.EntityKnockbackEvent;
import de.tobiyas.races.datacontainer.traitcontainer.traits.Trait;
import de.tobiyas.races.datacontainer.traitcontainer.traits.TraitsWithUplink;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;


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
		
		createStaticTraits();
	}
	
	private void createStaticTraits(){
		TraitStore.buildTraitByName("DeathCheckerTrait");
		TraitStore.buildTraitByName("STDAxeDamageTrait");
		TraitStore.buildTraitByName("ArmorTrait");
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
			if(clazz.isAssignableFrom(event.getClass()))
				traitsToCheck.addAll(traitList.get(clazz));
		}
		
		for(Trait trait: traitsToCheck){
			try{
				if(trait.modify(event))
					changedSomething = true;
			}catch(Exception e){
				RaceContainer raceContainer = trait.getRace();
				ClassContainer classContainer = trait.getClazz();
				String race = "NONE";
				String clazz = "NONE";
				if(raceContainer != null)
					race = raceContainer.getName();
				if(classContainer != null)
					clazz = classContainer.getName();
					
				plugin.getDebugLogger().logError("Error while executing trait: " + trait.getName() + " of race: " + 
						race + " of class: " + clazz + " event was: " + event.getEventName() + " Error was: " + e.getLocalizedMessage());
				plugin.getDebugLogger().logStackTrace(e);
			}
		}
		
		fireDamage(event);
		fireHeal(event);
		return changedSomething;
	}
	
	private boolean replaceEvent(Event event){
		if(checkFire(event)) return true;
		if(replaceDamageEvent(event)) return true;
		if(replaceHealEvent(event)) return true;
		return false;
	}
	
	private boolean checkFire(Event event){
		if(!(event instanceof EntityDamageDoubleEvent)) return false;
		EntityDamageDoubleEvent Eevent = (EntityDamageDoubleEvent) event;
		
		if(Eevent.getCause() == DamageCause.FIRE || Eevent.getCause() == DamageCause.FIRE_TICK){
			Entity entity = Eevent.getEntity();
			if(!(entity instanceof LivingEntity)) return false;
			
			if(entity.getFireTicks() <= 0 && DamageTicker.hasEffect(entity, Eevent.getCause()) != 0){
				DamageTicker.cancleEffects((LivingEntity) entity, Eevent.getCause());
				return true;
			}
		}
		
		return false;
	}
	
	private boolean replaceDamageEvent(Event event){
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
				if(DamageTicker.hasEffect(Eevent.getEntity(), DamageCause.FIRE) != 0){
					Eevent.setDamage(0);
					return true;
				}else{
					Eevent.setDamage(0);
					Eevent = new EntityDamageEvent(Eevent.getEntity(), DamageCause.FIRE, 0);
				}
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
	
	private boolean replaceHealEvent(Event event){
		if(event instanceof EntityHealEvent) return false;
		
		if(event instanceof EntityRegainHealthEvent){
			EntityRegainHealthEvent Eevent = (EntityRegainHealthEvent) event;
			EntityHealEvent ehEvent = new EntityHealEvent(Eevent);
			Eevent.setAmount(0);
			fireEventIntern(ehEvent);
			return true;
		}
		
		return false;
	}
	
	private void fireDamage(Event event){
		if(event instanceof EntityDamageDoubleEvent){
			EntityDamageDoubleEvent Eevent = (EntityDamageDoubleEvent) event;
			if(imunTicker.isImun(Eevent.getEntity())) return; //Check if imun to dmg
			if(event instanceof EntityDamageByEntityDoubleEvent) checkKnockBack(event);
			
			if(Eevent.getEntityType() == EntityType.PLAYER){
				Player player = (Player)(Eevent.getEntity());
				double dmg = Eevent.getDoubleValueDamage();
				notifyObservers(new HealthModifyContainer(player.getName(), dmg, "damage"));
				setChanged();
			}else{
				if(!(Eevent.getEntity() instanceof LivingEntity)) return;
				LivingEntity entity = (LivingEntity) Eevent.getEntity();
				
				entity.playEffect(EntityEffect.HURT);
				int damage = (int) Math.ceil(Eevent.getDoubleValueDamage());
				if(damage > 0){
					boolean fromPlayer = Eevent instanceof EntityDamageByEntityDoubleEvent && 
										((EntityDamageByEntityDoubleEvent)Eevent).getDamager() instanceof Player;
					checkDropReset(entity, Eevent.getCause(), fromPlayer);
					
					entity.damage(damage);
					entity.setLastDamageCause(Eevent);
				}
			}
			imunTicker.addToImunList(Eevent.getEntity());
		}else
		//Should never be called again (only for debuging in source)
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
	}
	
	private void fireHeal(Event event){
		if(event instanceof EntityHealEvent){
			EntityHealEvent Eevent = (EntityHealEvent) event;
			if(Eevent.getEntityType() == EntityType.PLAYER){
				Player player = (Player)(Eevent.getEntity());
				int health = Eevent.getAmount();
				notifyObservers(new HealthModifyContainer(player.getName(), health, "heal"));
				setChanged();
			}else{
				if(!(Eevent.getEntity() instanceof LivingEntity)) return;
				LivingEntity entity = (LivingEntity) Eevent.getEntity();
				int newHealth = entity.getHealth() + (int) Eevent.getDoubleValueAmount();
				if(newHealth > entity.getMaxHealth())
					newHealth = entity.getMaxHealth();
				
				entity.setHealth(newHealth);
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
	
	private void checkDropReset(LivingEntity entity, DamageCause cause, boolean fromPlayer){
		boolean reset = false;
		if(cause == DamageCause.POISON)
			reset = true;
		
		if(cause == DamageCause.ENTITY_ATTACK && fromPlayer)
			reset = true;
		
		if(cause == DamageCause.FIRE_TICK)
			reset = true;
		
		
		if(reset)
			EntityDeathManager.getManager().resetEntityHit(entity);
	}
	
	private void registerTraitIntern(Trait trait, HashSet<Class<?>> events, int priority){
		//TODO register priority
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
	
	private void unregisterTraitIntern(Trait trait){
		traitList.remove(trait);
	}
	
	public static TraitEventManager getInstance(){
		return manager;
	}
	
	public static boolean fireEvent(Event event){
		try{
			long time = System.currentTimeMillis();
			boolean result = getInstance().fireEventIntern(event);
			timings += System.currentTimeMillis() - time;
			
			return result;
		}catch(Exception e){
			Races.getPlugin().getDebugLogger().logStackTrace(e);
			return false;
		}
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
	
	public static void registerTrait(Trait trait, HashSet<Class<?>> events, int priority){
		getInstance().registerTraitIntern(trait, events, priority);
	}
	
	public void unregisterTrait(Trait trait){
		getInstance().unregisterTraitIntern(trait);
	}
	
}
