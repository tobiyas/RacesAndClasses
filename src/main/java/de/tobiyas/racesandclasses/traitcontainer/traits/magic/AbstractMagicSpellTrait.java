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
package de.tobiyas.racesandclasses.traitcontainer.traits.magic;

import static de.tobiyas.racesandclasses.translation.languages.Keys.magic_change_spells;
import static de.tobiyas.racesandclasses.translation.languages.Keys.magic_dont_have_enough;
import static de.tobiyas.racesandclasses.translation.languages.Keys.magic_no_spells;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapperFactory;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.PlayerAction;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.MagicSpellTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.TraitRestriction;
import de.tobiyas.racesandclasses.traitcontainer.traits.pattern.AbstractActivatableTrait;
import de.tobiyas.racesandclasses.translation.languages.Keys;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;
import de.tobiyas.racesandclasses.vollotile.Vollotile;
import de.tobiyas.util.naming.MCPrettyName;

public abstract class AbstractMagicSpellTrait extends AbstractActivatableTrait implements MagicSpellTrait {

	//static naming for YML elements
	public static final String COST_TYPE_PATH= "costType";
	public static final String COST_PATH= "cost";
	public static final String ITEM_TYPE_PATH= "item";
	
	public static final String CHANNELING_PATH = "channeling";
	
	
	/**
	 * This map is to prevent instant retriggers!
	 */
	private static final Map<String, Long> lastCastMap = new HashMap<String, Long>(); 
	
	/**
	 * The Map of currently channeling players.
	 */
	private static final Map<UUID, ChannelingContainer> channelingMap = new HashMap<UUID, ChannelingContainer>();
	
	/**
	 * The Cost of the Spell.
	 * 
	 * It has the default Cost of 0.
	 */
	protected double cost = 0;
	
	/**
	 * The Material for casting with {@link CostType#ITEM}
	 */
	protected Material materialForCasting = Material.FEATHER;
	
	/**
	 * The CostType of the Spell.
	 * 
	 * It has the Default CostType: {@link CostType#MANA}.
	 */
	protected CostType costType = CostType.MANA;
	
	/**
	 * The Channeling time.
	 */
	protected double channelingTime = 0;
	
	
	/**
	 * The Plugin to call stuff on
	 */
	protected final RacesAndClasses plugin = RacesAndClasses.getPlugin();
	
	/**
	 * The ID of the scheduler.
	 */
	private int bukkitSchedulerID = -1;

	
	@TraitEventsUsed(registerdClasses = {PlayerInteractEvent.class})
	@Override
	public void generalInit(){
		if(channelingTime > 0){
			bukkitSchedulerID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
				
				@Override
				public void run() {
					Iterator<Map.Entry<UUID,ChannelingContainer>> entryIt = channelingMap.entrySet().iterator();
					while(entryIt.hasNext()){
						Map.Entry<UUID, ChannelingContainer> entry = entryIt.next();
						RaCPlayer player = RaCPlayerManager.get().getPlayer(entry.getKey());
						if(player == null || !player.isOnline() 
								|| player.getLocation().distanceSquared(entry.getValue().loc) > 0.5){
							
							entryIt.remove();
							entry.getValue().task.cancel();
							LanguageAPI.sendTranslatedMessage(player, Keys.magic_chaneling_failed, "trait_name", 
									AbstractMagicSpellTrait.this.getDisplayName());
						}
						
					}
				}
			}, 10, 10);
		}
	}
	
	
	@Override
	public void deInit(){
		super.deInit();
		
		if(bukkitSchedulerID > 0){
			Bukkit.getScheduler().cancelTask(bukkitSchedulerID);
			bukkitSchedulerID = -1;
		}
	}

	
	@Override
	public boolean canBeTriggered(EventWrapper wrapper){
		if(canOtherEventBeTriggered(wrapper)) return true;
		
		if(channelingMap.containsKey(wrapper.getPlayer().getName())) return false;
		PlayerAction action = wrapper.getPlayerAction();
		if(!(action == PlayerAction.CAST_SPELL || action == PlayerAction.CHANGE_SPELL)) return false;
		
		RaCPlayer player = wrapper.getPlayer();
		
		boolean playerHasWandInHand = checkWandInHand(player);
		
		//early out for not wand in hand.
		if(!playerHasWandInHand) return false;
		
		//check if the Spell is the current selected Spell
		if(this != player.getSpellManager().getCurrentSpell()) return false;
		
		return true;
	}
	
	/**
	 * This is a pre-call to {@link #canBeTriggered(Event)}.
	 * When returning true, true will be passed.
	 * 
	 * @param event that wants to be triggered
	 * 
	 * @return true if interested, false if not.
	 */
	protected boolean canOtherEventBeTriggered(EventWrapper wrapper){
		return false;
	}


	@Override
	public boolean triggerButHasUplink(EventWrapper eventWrapper) {
		PlayerAction action = eventWrapper.getPlayerAction();
		if(action != PlayerAction.NONE){
			
			boolean playerHasWandInHand = checkWandInHand(eventWrapper.getPlayer());
			if(action == PlayerAction.CHANGE_SPELL && playerHasWandInHand){
				changeMagicSpell(eventWrapper.getPlayer());
				
				return true;
			}
			
			if(action == PlayerAction.CAST_SPELL){
				return playerHasWandInHand;
			}
		}
		
		//We ignore all other events except the change spell action.
		return true;
	}
	
	
	
	/**
	 * Checks if the Player has a Wand in his hand.
	 * 
	 * @param player to check
	 * @return true if he has, false otherwise.
	 */
	public boolean checkWandInHand(RaCPlayer player) {
		ItemStack holding = player.getPlayer().getItemInHand();		
		return player.getSpellManager().isWandItem(holding);
	}


	@Override
	public void triggerButDoesNotHaveEnoghCostType(EventWrapper wrapper){		
		PlayerAction action = wrapper.getPlayerAction();
		if(action == PlayerAction.CHANGE_SPELL){
			changeMagicSpell(wrapper.getPlayer());
			return;
		}
		
		String costTypeString = getCostType().name();
		if(getCostType() == CostType.ITEM){
			costTypeString = MCPrettyName.getPrettyName(
					getCastMaterialType(),
					(short) 0,
					MCPrettyName.EN);
		}
		
		LanguageAPI.sendTranslatedMessage(wrapper.getPlayer(), magic_dont_have_enough, 
				"cost_type", costTypeString, 
				"trait_name", getDisplayName());
	}


	@Override
	public TraitResults trigger(EventWrapper eventWrapper) {
		Event event = eventWrapper.getEvent();
		final TraitResults result = new TraitResults();
		
		if(event instanceof PlayerInteractEvent){
			PlayerInteractEvent playerInteractEvent = (PlayerInteractEvent) event;
			final RaCPlayer player = eventWrapper.getPlayer();
			
			boolean playerHasWandInHand = checkWandInHand(player);
			//early out for not wand in hand.
			if(!playerHasWandInHand) return result.setTriggered(false);
			
			//check if the Spell is the current selected Spell
			if(this != player.getSpellManager().getCurrentSpell()) return  result.setTriggered(false);
			
			Action action = playerInteractEvent.getAction();
			if(action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK){
				if(lastCastMap.containsKey(player.getUniqueId())){
					if(System.currentTimeMillis() - lastCastMap.get(player.getUniqueId()) < 100){
						//2 casts directly after each other.
						//lets cancle the second
						return result.setTriggered(false);
					}else{
						lastCastMap.remove(player.getUniqueId());
					}
					
				}
				
				return trigger(player);				
			}
			
			if(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK){
				changeMagicSpell(player);
				return  result.setTriggered(false);
			}
		}
		
		return otherEventTriggered(eventWrapper, result);
	}

	
	/**
	 * This triggers when NO {@link PlayerInteractEvent} is triggered.
	 * 
	 * @param event that triggered
	 * @return true if triggering worked and Mana should be drained.
	 */
	protected TraitResults otherEventTriggered(EventWrapper eventWrapper, TraitResults result){
		return result;
	}


	/**
	 * Changes the current magic spell.
	 * 
	 * @param player the player triggering the spell
	 * 
	 * @return true if the Spell could be changed, false if not.
	 */
	public boolean changeMagicSpell(RaCPlayer player){
		if(player.getSpellManager().getCurrentSpell() == null) return false;
		
		if(player.getSpellManager().getSpellAmount() == 0){
			LanguageAPI.sendTranslatedMessage(player.getPlayer(), magic_no_spells);
			return true;
		}

		boolean toPrev = player.getPlayer().isSneaking();
		
		MagicSpellTrait nextSpell = null;
		if(toPrev){
			nextSpell = player.getSpellManager().changeToPrevSpell();
		}else{
			nextSpell = player.getSpellManager().changeToNextSpell();
		}
		
		if(nextSpell != null){
			DecimalFormat formatter = new DecimalFormat("###.#");
			
			String costName = formatter.format(nextSpell.getCost());
			String costTypeString = nextSpell.getCostType() == CostType.ITEM 
							? nextSpell.getCastMaterialType().name() 
							: nextSpell.getCostType().name();
			String newSpellName = ((Trait)nextSpell).getDisplayName();
							
			LanguageAPI.sendTranslatedMessage(player.getPlayer(), magic_change_spells, 
					"trait_name", newSpellName,
					"cost", costName, 
					"cost_type", costTypeString
					);
			return true;
		}else{
			//switching too fast.
			return true;
		}
	}
	
	
	/**
	 * This method is called, when the caster uses THIS magic spell.
	 * 
	 * @param player the Player triggering the spell.
	 * @param result to modify
	 */
	protected abstract void magicSpellTriggered(RaCPlayer player, TraitResults result);
	
	
	//This is just for Mana + CostType
	@TraitConfigurationNeeded( fields = {
			@TraitConfigurationField(fieldName = COST_PATH, classToExpect = Double.class, optional = true),
			@TraitConfigurationField(fieldName = COST_TYPE_PATH, classToExpect = String.class, optional = true),
			@TraitConfigurationField(fieldName = ITEM_TYPE_PATH, classToExpect = Material.class, optional = true),
			@TraitConfigurationField(fieldName = CHANNELING_PATH, classToExpect = Double.class, optional = true),
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		if(configMap.containsKey(COST_PATH)){
			cost = configMap.getAsDouble(COST_PATH);
		}
		
		if(configMap.containsKey(COST_TYPE_PATH)){
			String costTypeName = configMap.getAsString(COST_TYPE_PATH);
			costType = CostType.tryParse(costTypeName);
			if(costType == null){
				throw new TraitConfigurationFailedException(getName() + " is incorrect configured. costType could not be read.");
			}
			
			if(costType == CostType.ITEM){
				if(!configMap.containsKey(ITEM_TYPE_PATH)){
					throw new TraitConfigurationFailedException(getName() + " is incorrect configured. 'costType' was ITEM but no Item is specified at 'item'.");
				}
				
				materialForCasting = configMap.getAsMaterial(ITEM_TYPE_PATH);
				if(materialForCasting == null){
					throw new TraitConfigurationFailedException(getName() + " is incorrect configured."
							+ " 'costType' was ITEM but the item read is not an Item. Items are CAPITAL. "
							+ "See 'https://github.com/Bukkit/Bukkit/blob/master/src/main/java/org/bukkit/Material.java' for all Materials. "
							+ "Alternative use an ItemID.");
				}
			}
		}
		
		if(configMap.containsKey(CHANNELING_PATH)){
			channelingTime = configMap.getAsDouble(CHANNELING_PATH);
		}
	}


	@Override
	public double getCost(){
		return cost;
	}

	
	@Override
	public CostType getCostType(){
		return costType;
	}


	@Override
	public Material getCastMaterialType() {
		return materialForCasting;
	}

	@Override
	public boolean isStackable(){
		return true;
	}
	
	@Override
	public boolean needsCostCheck(EventWrapper wrapper){
		return wrapper.getEvent() instanceof PlayerInteractEvent;
	}
	
	@Override
	public void triggerButHasRestriction(TraitRestriction restriction,
			EventWrapper wrapper) {
		
		if(restriction == TraitRestriction.Costs){
			if(!checkWandInHand(wrapper.getPlayer())) return;
			
			triggerButDoesNotHaveEnoghCostType(wrapper);
		}else{
			if(wrapper.getPlayerAction() == PlayerAction.CHANGE_SPELL){
				changeMagicSpell(wrapper.getPlayer());
			}
		}
	}
	
	
	@Override
	public boolean isBindable() {
		return true;
	}
	
	
	@Override
	public TraitResults trigger(final RaCPlayer player) {
		final TraitResults result = TraitResults.False();
		
		if(channelingTime > 0){
			BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
				@Override
				public void run() {
					if(channelingMap.containsKey(player.getUniqueId())){
						//check restrictions for the New Trigger.
						if(checkRestrictions(EventWrapperFactory.buildOnlyWithplayer(player)) != TraitRestriction.None) return;
						
						magicSpellTriggered(player, result);
						if(result.isTriggered() && result.isRemoveCostsAfterTrigger()){
							player.getSpellManager().removeCost(AbstractMagicSpellTrait.this);
						}
						if(result.isTriggered() && result.isSetCooldownOnPositiveTrigger()){
							setCooldownIfNeeded(player);
						}
					}
				}
			}, (long) (channelingTime * 20));
			
			channelingMap.put(player.getUniqueId(), new ChannelingContainer(player.getLocation(), task));
			result.setTriggered(false);
		}else{
			magicSpellTriggered(player, result);					
			if(result.isTriggered() && result.isRemoveCostsAfterTrigger()){
				player.getSpellManager().removeCost(this);
				if(onUseParticles != null){
					
					Vollotile.get().sendOwnParticleEffectToAll(
							onUseParticles,
							player.getLocation().add(0, 1, 0), 
							0, 
							10);
				}
			}
		}
		
		return result;
	}
	
	
	@Override
	protected void evaluateIntern(RaCPlayer player, TraitResults result) {
		if(result.isTriggered() 
				&& result.isRemoveCostsAfterTrigger()){
			//The spell was triggered! So start removing stuff!
			player.getSpellManager().removeCost(this);
		}
	}
	
	
	@Override
	protected TraitRestriction checkForFurtherRestrictions(EventWrapper wrapper) {
		if(!wrapper.getPlayer().getSpellManager().canCastSpell(this)){
			return TraitRestriction.Costs;
		}
		return super.checkForFurtherRestrictions(wrapper);
	}
	
	
	protected static class ChannelingContainer{
		final Location loc;
		final BukkitTask task;
		
		public ChannelingContainer(Location loc, BukkitTask task) {
			this.loc = loc;
			this.task = task;
		}
		
		
	}
}
