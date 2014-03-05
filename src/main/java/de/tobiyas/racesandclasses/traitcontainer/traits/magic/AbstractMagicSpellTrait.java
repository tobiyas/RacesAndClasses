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

import static de.tobiyas.racesandclasses.translation.languages.Keys.cooldown_is_ready_again;
import static de.tobiyas.racesandclasses.translation.languages.Keys.magic_change_spells;
import static de.tobiyas.racesandclasses.translation.languages.Keys.magic_dont_have_enough;
import static de.tobiyas.racesandclasses.translation.languages.Keys.magic_no_spells;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.APIs.MessageScheduleApi;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.PlayerAction;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.AbstractBasicTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.MagicSpellTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.translation.languages.Keys;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;
import de.tobiyas.util.naming.MCPrettyName;

public abstract class AbstractMagicSpellTrait extends AbstractBasicTrait implements MagicSpellTrait {

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
	private static final Map<String, Location> channelingMap = new HashMap<String, Location>();
	
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
					Iterator<Map.Entry<String,Location>> entryIt = channelingMap.entrySet().iterator();
					while(entryIt.hasNext()){
						Map.Entry<String, Location> entry = entryIt.next();
						Player player = Bukkit.getPlayer(entry.getKey());
						if(player == null || !player.isOnline() 
								|| player.getLocation().distanceSquared(entry.getValue()) > 0.5){
							entryIt.remove();
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
		
		Player player = wrapper.getPlayer();
		
		boolean playerHasWandInHand = checkWandInHand(player);
		
		//early out for not wand in hand.
		if(!playerHasWandInHand) return false;
		
		//check if the Spell is the current selected Spell
		if(this != plugin.getPlayerManager().getSpellManagerOfPlayer(player.getName()).getCurrentSpell()) return false;
		
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
			
			if(action == PlayerAction.CHANGE_SPELL){
				changeMagicSpell(eventWrapper.getPlayer());
				
				return true;
			}
			
			if(action == PlayerAction.CAST_SPELL){
				boolean playerHasWandInHand = checkWandInHand(eventWrapper.getPlayer());
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
	public boolean checkWandInHand(Player player) {
		return plugin.getPlayerManager().getSpellManagerOfPlayer(player.getName())
				.isWandItem(player.getItemInHand());
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
					"en_US");
		}
			
		LanguageAPI.sendTranslatedMessage(wrapper.getPlayer(), magic_dont_have_enough, 
				"cost_type", costTypeString, 
				"trait_name", getDisplayName());
	}


	@Override
	public TraitResults trigger(Event event) {
		final TraitResults result = new TraitResults();
		
		if(event instanceof PlayerInteractEvent){
			PlayerInteractEvent playerInteractEvent = (PlayerInteractEvent) event;
			final Player player = playerInteractEvent.getPlayer();
			
			boolean playerHasWandInHand = checkWandInHand(player);
			//early out for not wand in hand.
			if(!playerHasWandInHand) return result.setTriggered(false);
			
			//check if the Spell is the current selected Spell
			if(this != plugin.getPlayerManager().getSpellManagerOfPlayer(player.getName()).getCurrentSpell()) return  result.setTriggered(false);
			
			Action action = playerInteractEvent.getAction();
			if(action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK){
				final String playerName = player.getName();
				if(lastCastMap.containsKey(playerName)){
					if(System.currentTimeMillis() - lastCastMap.get(playerName) < 100){
						//2 casts directly after each other.
						//lets cancle the second
						return result.setTriggered(false);
					}else{
						lastCastMap.remove(playerName);
					}
					
				}
				
				lastCastMap.put(player.getName(), System.currentTimeMillis());
				
				if(channelingTime > 0){
					channelingMap.put(playerName, player.getLocation());
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						
						@Override
						public void run() {
							if(channelingMap.containsKey(playerName)){
								magicSpellTriggered(player, result);
								if(result.isTriggered() && result.isRemoveCostsAfterTrigger()){
									plugin.getPlayerManager().getSpellManagerOfPlayer(playerName).removeCost(AbstractMagicSpellTrait.this);
								}
								if(result.isTriggered() && result.isSetCooldownOnPositiveTrigger()){
									setCooldownIfNeeded(player);
								}
							}
						}
					}, (long) (channelingTime / 20));
					result.setTriggered(false);
				}else{
					magicSpellTriggered(player, result);					
					if(result.isTriggered() && result.isRemoveCostsAfterTrigger()){
						plugin.getPlayerManager().getSpellManagerOfPlayer(playerName).removeCost(this);
					}
				}
				
				return result;
			}
			
			if(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK){
				changeMagicSpell(player);
				return  result.setTriggered(false);
			}
		}
		
		return otherEventTriggered(event, result);
	}
	
	/**
	 * Sets the cooldown of a player
	 * 
	 * @param player to set
	 */
	protected void setCooldownIfNeeded(Player player) {
		String playerName = player.getName();
		String cooldownName = "trait." + getDisplayName();
		
		int uplinkTraitTime = getMaxUplinkTime();
		if(uplinkTraitTime > 0){
			plugin.getCooldownManager().setCooldown(playerName, cooldownName, uplinkTraitTime);
			
			String cooldownDownMessage = LanguageAPI.translateIgnoreError(cooldown_is_ready_again)
					.replace("trait_name", getDisplayName())
					.build();
			
			MessageScheduleApi.scheduleMessageToPlayer(player.getName(), uplinkTraitTime, cooldownDownMessage);
		}
	}
	
	/**
	 * This triggers when NO {@link PlayerInteractEvent} is triggered.
	 * 
	 * @param event that triggered
	 * @return true if triggering worked and Mana should be drained.
	 */
	protected TraitResults otherEventTriggered(Event event, TraitResults result){
		return result;
	}


	/**
	 * Changes the current magic spell.
	 * 
	 * @param player the player triggering the spell
	 * 
	 * @return true if the Spell could be changed, false if not.
	 */
	protected boolean changeMagicSpell(Player player){
		String playerName = player.getName();
		
		if(plugin.getPlayerManager().getSpellManagerOfPlayer(playerName).getCurrentSpell() == null) return false;
		
		if(plugin.getPlayerManager().getSpellManagerOfPlayer(player.getName()).getSpellAmount() == 0){
			LanguageAPI.sendTranslatedMessage(player, magic_no_spells);
			return true;
		}

		boolean toPrev = player.isSneaking();
		
		MagicSpellTrait nextSpell = null;
		if(toPrev){
			nextSpell = plugin.getPlayerManager().getSpellManagerOfPlayer(playerName).changeToPrevSpell();
		}else{
			nextSpell = plugin.getPlayerManager().getSpellManagerOfPlayer(playerName).changeToNextSpell();			
		}
		
		if(nextSpell != null){
			DecimalFormat formatter = new DecimalFormat("###.#");
			
			String costName = formatter.format(nextSpell.getCost());
			String costTypeString = nextSpell.getCostType() == CostType.ITEM 
							? nextSpell.getCastMaterialType().name() 
							: nextSpell.getCostType().name();
			String newSpellName = ((Trait)nextSpell).getDisplayName();
							
			LanguageAPI.sendTranslatedMessage(player, magic_change_spells, 
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
	protected abstract void magicSpellTriggered(Player player, TraitResults result);
	
	
	//This is just for Mana + CostType
	@TraitConfigurationNeeded( fields = {
			@TraitConfigurationField(fieldName = COST_PATH, classToExpect = Double.class, optional = true),
			@TraitConfigurationField(fieldName = COST_TYPE_PATH, classToExpect = String.class, optional = true),
			@TraitConfigurationField(fieldName = ITEM_TYPE_PATH, classToExpect = Material.class, optional = true),
			@TraitConfigurationField(fieldName = CHANNELING_PATH, classToExpect = Double.class, optional = true)
		})
	@Override
	public void setConfiguration(Map<String, Object> configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		if(configMap.containsKey(COST_PATH)){
			cost = (Double) configMap.get(COST_PATH);
		}
		
		if(configMap.containsKey(COST_TYPE_PATH)){
			String costTypeName = (String) configMap.get(COST_TYPE_PATH);
			costType = CostType.tryParse(costTypeName);
			if(costType == null){
				throw new TraitConfigurationFailedException(getName() + " is incorrect configured. costType could not be read.");
			}
			
			if(costType == CostType.ITEM){
				if(!configMap.containsKey(ITEM_TYPE_PATH)){
					throw new TraitConfigurationFailedException(getName() + " is incorrect configured. 'costType' was ITEM but no Item is specified at 'item'.");
				}
				
				materialForCasting = (Material) configMap.get(ITEM_TYPE_PATH);
				if(materialForCasting == null){
					throw new TraitConfigurationFailedException(getName() + " is incorrect configured."
							+ " 'costType' was ITEM but the item read is not an Item. Items are CAPITAL. "
							+ "See 'https://github.com/Bukkit/Bukkit/blob/master/src/main/java/org/bukkit/Material.java' for all Materials. "
							+ "Alternative use an ItemID.");
				}
			}
			
		}
		
		if(configMap.containsKey(CHANNELING_PATH)){
			channelingTime = (Double) configMap.get(CHANNELING_PATH);
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
		return false;
	}
	
	@Override
	public boolean needsCostCheck(EventWrapper wrapper){
		return wrapper.getEvent() instanceof PlayerInteractEvent;
	}
}
