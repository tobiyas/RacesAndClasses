/*******************************************************************************
 * Copyright 2014 Tobias Welther
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.tobiyas.racesandclasses.eventprocessing;

import static de.tobiyas.racesandclasses.translation.languages.Keys.arrow_change;
import static de.tobiyas.racesandclasses.translation.languages.Keys.cooldown_is_ready_again;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.APIs.MessageScheduleApi;
import de.tobiyas.racesandclasses.datacontainer.arrow.ArrowManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapperFactory;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.PlayerAction;
import de.tobiyas.racesandclasses.eventprocessing.events.traittrigger.PostTraitTriggerEvent;
import de.tobiyas.racesandclasses.listeners.interneventproxy.Listener_Proxy;
import de.tobiyas.racesandclasses.playermanagement.display.scoreboard.PlayerRaCScoreboardManager.SBCategory;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.TraitStore;
import de.tobiyas.racesandclasses.traitcontainer.container.TraitsList;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.bypasses.ByPassWorldDisabledCheck;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.bypasses.BypassHolderCheck;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.bypasses.StaticTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.MagicSpellTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.TraitRestriction;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.TraitWithCost;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.TraitWithRestrictions;
import de.tobiyas.racesandclasses.traitcontainer.traits.arrows.AbstractArrow;
import de.tobiyas.racesandclasses.util.friend.EnemyChecker.FriendDetectEvent;
import de.tobiyas.racesandclasses.util.traitutil.TraitBypassCheck;


public class TraitEventManager{
	private RacesAndClasses plugin;
	private static long timings = 0;
	private static long calls = 0;
	
	private static TraitEventManager manager;
	private HashMap<Class<?>, Set<Trait>> traitList;
	private HashMap<Integer, Long> eventIDs;
	
	private int errorsPerMin = 0;
	private boolean spamPreventionActive = false;
	
	
	private List<String> registeredEventsAsName = new LinkedList<String>();


	/**
	 * Creates the whole Trait Event system
	 */
	public TraitEventManager(){
		plugin = RacesAndClasses.getPlugin();
		TraitsList.initStaticTraits();
		TraitsList.initInternalTraits();
		TraitStore.importFromFileSystem();
		
		manager = this;
		traitList = new HashMap<Class<?>, Set<Trait>>();
		eventIDs = new HashMap<Integer, Long>();
		new DoubleEventRemover(this);
		
		Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
			@Override
			public void run() {
				errorsPerMin = 0;
			}
		}, 20 * 60, 20 * 60);
	}
	
	/**
	 * Inits the system by registering all needed stuff
	 */
	public void init(){
		createStaticTraits();
	}
	
	/**
	 * Creates all Traits that are present for ALL players.
	 */
	private void createStaticTraits(){
		TraitStore.buildTraitWithoutHolderByName("DeathCheckerTrait").generalInit();
		TraitStore.buildTraitWithoutHolderByName("STDAxeDamageTrait").generalInit();
		TraitStore.buildTraitWithoutHolderByName("ArmorTrait").generalInit();
	}
	
	/**
	 * Fires a synchronous Event intern
	 * 
	 * @param event that was fired
	 * @return if the Event was progressed by the plugin
	 */
	private boolean fireEventIntern(Event event){
		if(event instanceof FriendDetectEvent) return false;
		
		calls ++;
		boolean changedSomething = false;
		if(eventIDs.containsKey(event.hashCode())){
			return false;
		}else{
			eventIDs.put(event.hashCode(), System.currentTimeMillis());
		}
		
		EventWrapper eventWrapper = EventWrapperFactory.buildFromEvent(event);
		if(eventWrapper == null) return false; //we can't process this event.
		
		boolean disabledOnWorld = checkDisabledPerWorld(eventWrapper.getWorld());
		
		Set<Trait> traitsToCheck = new HashSet<Trait>();
		for(Class<?> clazz : traitList.keySet()){
			if(clazz.isAssignableFrom(event.getClass())){
				traitsToCheck.addAll(traitList.get(clazz));
			}
		}
		
		
		for(Trait trait: traitsToCheck){
			long timeBefore = System.currentTimeMillis();
			if(trait == null) continue;
			try{
				//first check if the Trait has a DisabledWorldBypass
				if(disabledOnWorld){
					if(!trait.getClass().isAnnotationPresent(ByPassWorldDisabledCheck.class)){
						continue;
					}
				}
				
				//Player player = trait.getReleventPlayer(event); //TODO check
				RaCPlayer player = eventWrapper.getPlayer();
				
				//Check if Static Trait -> Always interested!
				//Check if Player has Trait.
				//If BypassHolderCheck annotation is present, it should be checked.
				if(!(trait instanceof StaticTrait)){
					if(player == null){
						continue;
					}else{
						if(!(trait.getClass().isAnnotationPresent(BypassHolderCheck.class))){
							if(!TraitHolderCombinder.getReducedTraitsOfPlayer(player).contains(trait)){
								continue;
							}
						}	
					}
				}
				
				boolean hasBypassForEvent = TraitBypassCheck.hasBypass(trait.getClass(), event.getClass());
				
				//check if the Spell is changed.
				if(trait instanceof MagicSpellTrait && event instanceof PlayerInteractEvent && !hasBypassForEvent){
					//only let the current magic spell continue for interaction events
					TraitWithCost magicTrait = (TraitWithCost) trait;
					if(player.getSpellManager().getCurrentSpell() != magicTrait){
						continue;
					}
				}
				
				//check if the Arrow needs changed
				if(trait instanceof AbstractArrow 
						&& eventWrapper.getPlayerAction() == PlayerAction.CHANGE_ARROW
						&& player.getArrowManager().hasAnyArrow()
						&& player.getArrowManager().getCurrentArrow() == trait
						&& !hasBypassForEvent){
					
					//not we have a sure Arrow switch.
					boolean foreward = !player.isSneaking();
					AbstractArrow newArrow = foreward ? player.getArrowManager().nextArrow() : player.getArrowManager().previousArrow();
					
					if(newArrow != null && newArrow != trait){
						if(!plugin.getConfigManager().getGeneralConfig().isConfig_enable_permanent_scoreboard()){
							player.getScoreboardManager().updateSelectAndShow(SBCategory.Arrows);
						}
						
						LanguageAPI.sendTranslatedMessage(player.getPlayer(), arrow_change, "trait_name", newArrow.getDisplayName());
					}
					continue;
				}
				
				
				//Check if arrow is currently equiped:
				if(trait instanceof AbstractArrow && !hasBypassForEvent) {
					ArrowManager arrowManager = player.getArrowManager();
					if(!arrowManager.hasAnyArrow() || arrowManager.getCurrentArrow() != trait) continue;
				}

				
				//Check restrictions before calling.
				if(player != null && trait instanceof TraitWithRestrictions && !hasBypassForEvent){
					TraitRestriction restriction = ((TraitWithRestrictions) trait).checkRestrictions(eventWrapper);
					if(restriction != TraitRestriction.None) continue;
				}
				
				//Trait is not interested in the event
				if(!hasBypassForEvent && !trait.canBeTriggered(eventWrapper)){
					continue;
				}
				
				if(trait instanceof TraitWithCost && !hasBypassForEvent){
					TraitWithCost costTrait = (TraitWithCost) trait;
					if(!player.getSpellManager().canCastSpell(costTrait)){
						costTrait.triggerButDoesNotHaveEnoghCostType(eventWrapper);
						continue;
					}
				}
				
				plugin.getStatistics().traitTriggered(trait); //Statistic gathering
				TraitResults result =  trait.trigger(eventWrapper);
				if(result.isTriggered()){
					changedSomething = true;
					
					//fire event to event system. To let others see.
					RacesAndClasses.getPlugin().fireEventToBukkit(new PostTraitTriggerEvent(eventWrapper, trait));
					
					if(trait instanceof TraitWithRestrictions && player != null && result.isSetCooldownOnPositiveTrigger()){
						TraitWithRestrictions restrictionTrait = (TraitWithRestrictions) trait;
						String playerName = player.getName();
						String cooldownName = restrictionTrait.getCooldownName();
						
						int uplinkTraitTime = restrictionTrait.getMaxUplinkTime();
						if(uplinkTraitTime > 0){
							plugin.getCooldownManager().setCooldown(playerName, cooldownName, uplinkTraitTime);
							
							MessageScheduleApi.scheduleTranslateMessageToPlayer(player.getName(), uplinkTraitTime, 
									cooldown_is_ready_again, "trait_name", trait.getDisplayName());
						}
					}
				}
			}catch(Exception e){
				//spam prevention is active.
				if(spamPreventionActive) return changedSomething;
				errorsPerMin++;
				
				//more than 20 Errors / min = spam prevention.
				if(errorsPerMin > 20){
					spamPreventionActive = true;
					plugin.log("Getting too many Errors for Trait Events! "
							+ "Spam preventions is suppressing them all to save your log from beeing flooded!");
				}
				
				String holderName = trait.getTraitHolders()==null ? "static" : trait.getTraitHolders().toString();
				
				plugin.getDebugLogger().logError("Error while executing trait: " + trait.getName() + " of holders: " + 
						holderName + " event was: " + event.getEventName() + " Error was: " + e.getLocalizedMessage());
				plugin.getDebugLogger().logStackTrace(e);
			}finally{
				plugin.getStatistics().eventTime(trait.getName(), System.currentTimeMillis() - timeBefore);
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
	private boolean checkDisabledPerWorld(World world) {
		List<String> worldsDisabledOn = plugin.getConfigManager().getGeneralConfig().getConfig_worldsDisabled();
		
		String worldName = world == null ? "" : world.getName();
		for(String disabledWorldName : worldsDisabledOn){
			if(disabledWorldName.equalsIgnoreCase(worldName)){
				return true;
			}
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
	
	
	private void registerTraitIntern(Trait trait, Set<Class<? extends Event>> events, int priority){
		//TODO register priority
		for(Class<? extends Event> clazz : events){
			Set<Trait> traits = traitList.get(clazz);
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
	
	
	public static void registerTrait(Trait trait, Set<Class<? extends Event>> events, int priority){
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
