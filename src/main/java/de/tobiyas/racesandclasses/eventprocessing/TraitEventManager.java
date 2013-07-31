package de.tobiyas.racesandclasses.eventprocessing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.eventprocessing.worldresolver.WorldResolver;
import de.tobiyas.racesandclasses.listeners.interneventproxy.Listener_Proxy;
import de.tobiyas.racesandclasses.traitcontainer.TraitStore;
import de.tobiyas.racesandclasses.traitcontainer.container.TraitsList;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.Trait;


public class TraitEventManager{
	private RacesAndClasses plugin;
	private static long timings = 0;
	private static long calls = 0;
	
	private static TraitEventManager manager;
	private HashMap<Class<?>, HashSet<Trait>> traitList;
	private HashMap<Integer, Long> eventIDs;
	
	private EntityDeathManager entityDeathManager;
	private ImunTicker imunTicker;
	
	private List<String> registeredEventsAsName = new LinkedList<String>();


	/**
	 * Creates the whole Trait Event system
	 */
	public TraitEventManager(){
		plugin = RacesAndClasses.getPlugin();
		TraitsList.initTraits();
		manager = this;
		traitList = new HashMap<Class<?>, HashSet<Trait>>();
		eventIDs = new HashMap<Integer, Long>();
		new DoubleEventRemover(this);
		entityDeathManager = new EntityDeathManager();
		imunTicker = new ImunTicker();
	}
	
	/**
	 * Inits the system by registering all needed stuff
	 */
	public void init(){
		entityDeathManager.init();
		imunTicker.init();
		
		createStaticTraits();
	}
	
	/**
	 * Creates all Traits that are present for ALL players.
	 */
	private void createStaticTraits(){
		TraitStore.buildStaticTraitByName("DeathCheckerTrait");
		TraitStore.buildStaticTraitByName("STDAxeDamageTrait");
		TraitStore.buildStaticTraitByName("ArmorTrait");
	}
	
	/**
	 * Fires a synchronous Event intern
	 * 
	 * @param event
	 * @return
	 */
	private boolean fireEventIntern(Event event){
		calls ++;
		boolean changedSomething = false;
		if(eventIDs.containsKey(event.hashCode())){
			return false;
		}else{
			eventIDs.put(event.hashCode(), System.currentTimeMillis());
		}
		
		if(checkDisabledPerWorld(event)) return false;
		
		HashSet<Trait> traitsToCheck = new HashSet<Trait>();
		for(Class<?> clazz : traitList.keySet()){
			if(clazz.isAssignableFrom(event.getClass()))
				traitsToCheck.addAll(traitList.get(clazz));
		}
		
		for(Trait trait: traitsToCheck){
			try{
				plugin.getStatistics().traitTriggered(trait);
				if(trait.modify(event)){
					changedSomething = true;
				}
			}catch(Exception e){
				String holderName = trait.getTraitHolder().getName();
				
				plugin.getDebugLogger().logError("Error while executing trait: " + trait.getName() + " of holder: " + 
						holderName + " event was: " + event.getEventName() + " Error was: " + e.getLocalizedMessage());
				plugin.getDebugLogger().logStackTrace(e);
			}
		}
		
		plugin.getStatistics().eventTriggered();
		return changedSomething;
	}
	
	/**
	 * Checks if the world is on the disabled list.
	 * 
	 * True if it is, false if not.
	 * 
	 * @param event
	 * @return
	 */
	private boolean checkDisabledPerWorld(Event event) {
		List<String> worldsDisabledOn = plugin.getConfigManager().getGeneralConfig().getConfig_worldsDisabled();
		
		String worldName = WorldResolver.getWorldNameOfEvent(event);
		
		if(worldsDisabledOn.contains(worldName)){
			return true;
		}
		
		return false;
	}

	
	public void cleanEventList(){
		LinkedList<Integer> toRemove = new LinkedList<Integer>();
		long currentTime = System.currentTimeMillis();
		
		for(Integer inT : eventIDs.keySet()){
			long oldVal = eventIDs.get(inT);
			if((currentTime - oldVal) > 500){
				toRemove.add(inT);
			}
		}
		
		for(Integer inT : toRemove){
			eventIDs.remove(inT);
		}
		
	}
	
	@SuppressWarnings(value = "unused")
	private void checkDropReset(LivingEntity entity, DamageCause cause, boolean fromPlayer){
		boolean reset = false;
		if(cause == DamageCause.POISON)
			reset = true;
		
		if(cause == DamageCause.ENTITY_ATTACK && fromPlayer)
			reset = true;
		
		if(cause == DamageCause.FIRE_TICK)
			reset = true;
		
		
		if(reset){
			EntityDeathManager.getManager().resetEntityHit(entity);
		}
	}
	
	private void registerTraitIntern(Trait trait, HashSet<Class<? extends Event>> events, int priority){
		//TODO register priority
		for(Class<? extends Event> clazz : events){
			HashSet<Trait> traits = traitList.get(clazz);
			if(traits == null){
				traits = new HashSet<Trait>();
				traitList.put(clazz, traits);
				
				EventPriority eventPriority = EventPriority.NORMAL;
				try{
					eventPriority = EventPriority.values()[priority];
				}catch(IndexOutOfBoundsException exp){
				}
				
				try{
					plugin.getServer().getPluginManager().registerEvent(clazz, new Listener_Proxy(), eventPriority, new Simple_event_executor(), plugin);
					registeredEventsAsName.add(clazz.getCanonicalName());
				}catch(Exception exp){
					plugin.log("Could not register Event: " + clazz.getCanonicalName() + " of trait: " + trait.getName() 
							+ ". Exception: " + exp.getLocalizedMessage());
					
					plugin.getDebugLogger().logStackTrace(exp);
				}
			}
			
			traits.add(trait);
		}
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
			RacesAndClasses.getPlugin().getDebugLogger().logStackTrace(e);
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
	
	public static void registerTrait(Trait trait, HashSet<Class<? extends Event>> events, int priority){
		getInstance().registerTraitIntern(trait, events, priority);
	}
	
	public void unregisterTrait(Trait trait){
		getInstance().unregisterTraitIntern(trait);
	}
	
	/**
	 * @return the registeredEventsCount
	 */
	public List<String> getRegisteredEventsAsName() {
		return registeredEventsAsName;
	}
}
