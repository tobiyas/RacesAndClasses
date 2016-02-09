/*******************************************************************************
 * Copyright 2014 Tob
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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.activate.HealOthersTrait;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.configuration.traits.TraitConfig;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.eventprocessing.TraitEventManager;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.PlayerAction;
import de.tobiyas.racesandclasses.eventprocessing.events.entitydamage.EntityHealEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.entitydamage.EntityHealOtherEntityEvent;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.AbstractBasicTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.translation.languages.Keys;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;


public class HealOthersTrait extends AbstractBasicTrait {

	private double value;
	
	private static Material itemIDInHand = Material.STRING;
	private static boolean consume = true;	

	
	@TraitInfos(category="activate", traitName="HealOthersTrait", visible=true)
	@Override
	public void importTrait() {
	}

	@SuppressWarnings("deprecation")
	@TraitEventsUsed(registerdClasses = {PlayerInteractEntityEvent.class, PlayerInteractEvent.class})
	@Override
	public void generalInit() {
		TraitConfig config = plugin.getConfigManager().getTraitConfigManager().getConfigOfTrait(getName());
		if(config != null){
			itemIDInHand = Material.getMaterial((Integer) config.getValue("trait.iteminhand", Material.STRING.getId()));
			consume = (Boolean) config.getValue("trait.consume", true);
		}
	}

	@Override
	public String getName() {
		return "HealOthersTrait";
	}


	@Override
	protected String getPrettyConfigIntern(){
		return "heals: " + value;
	}

	@TraitConfigurationNeeded(fields = {
		@TraitConfigurationField(fieldName = "value", classToExpect = Double.class)
	})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		value = (Double) configMap.get("value");
	}

	@Override
	public TraitResults trigger(EventWrapper eventWrapper) {   Event event = eventWrapper.getEvent();
		if(event instanceof PlayerInteractEntityEvent || event instanceof PlayerInteractEvent){

			boolean isSneaking = ((PlayerEvent) event).getPlayer().isSneaking();
			if(!isSneaking){
				return TraitResults.False();
			}
			
			if(event instanceof PlayerInteractEntityEvent){
				PlayerInteractEntityEvent Eevent = (PlayerInteractEntityEvent) event;
				
				Entity target = Eevent.getRightClicked();
				if(target != null && target instanceof Player){
					return new TraitResults(playerInteractsWithPlayer(Eevent.getPlayer(), (Player) target));
				}
				
				return TraitResults.False();
			}
			
			if(event instanceof PlayerInteractEvent){
				PlayerInteractEvent Eevent = (PlayerInteractEvent) event;
				
				Action action = Eevent.getAction();
				
				if(action == Action.RIGHT_CLICK_AIR){
					Player player = Eevent.getPlayer();					
					return new TraitResults(playerInteractWithSelf(player));
				}
				
				return TraitResults.False();
			}
		}
		return TraitResults.False();
	}
	
	private boolean playerInteractWithSelf(Player player) {
		double maxHealth = CompatibilityModifier.BukkitPlayer.safeGetMaxHealth(player);
		double currentHealth = CompatibilityModifier.BukkitPlayer.safeGetHealth(player);
		
		if(currentHealth >= maxHealth) {
			LanguageAPI.sendTranslatedMessage(player, Keys.trait_heal_target_full);
			return false;
		}
		
		double amount = modifyToPlayer(RaCPlayerManager.get().getPlayer(player), value, "value");
		if(currentHealth + amount > maxHealth) amount = maxHealth - currentHealth;
		
		EntityHealEvent entityHealEvent = new EntityHealOtherEntityEvent(player, amount, RegainReason.MAGIC, player);
		TraitEventManager.fireEvent(entityHealEvent);
		if(entityHealEvent.isCancelled()) return false;
		
		amount = CompatibilityModifier.EntityHeal.safeGetAmount(entityHealEvent);
		
		//Never overheal! this gives an Exception!
		if(currentHealth + amount > maxHealth) amount = maxHealth - currentHealth;		
		
		if(!entityHealEvent.isCancelled() && amount >= 0){
			double newHealth = currentHealth + amount;
			
			CompatibilityModifier.BukkitPlayer.safeSetHealth(newHealth, player);
			
			Location loc = entityHealEvent.getEntity().getLocation();
			loc.getWorld().playEffect(loc, Effect.POTION_BREAK, 1);
			LanguageAPI.sendTranslatedMessage(player, Keys.trait_healed_target_success, 
					"target", player.getName());

			
			if(consume){
				int newAmount = player.getItemInHand().getAmount() - 1;
				if(newAmount == 0){
					player.setItemInHand(new ItemStack(Material.AIR));
				}else{
					player.getItemInHand().setAmount(newAmount);
				}
			}
			
			return true;
		}
		
		return false;
	}
	

	private boolean playerInteractsWithPlayer(Player playerInteracting, Player target) {
		double maxHealth = CompatibilityModifier.BukkitPlayer.safeGetMaxHealth(target);
		double currentHealth = CompatibilityModifier.BukkitPlayer.safeGetHealth(target);
		
		if(currentHealth >= maxHealth){
			LanguageAPI.sendTranslatedMessage(playerInteracting, Keys.trait_heal_target_full);
			return false;
		}
		
		double amount = modifyToPlayer(RaCPlayerManager.get().getPlayer(playerInteracting), value, "value");
		if(currentHealth + amount > maxHealth) amount = maxHealth - currentHealth;
		
		Player targetPlayer = (Player) target;
		EntityHealEvent entityHealEvent = new EntityHealOtherEntityEvent(targetPlayer, value, RegainReason.MAGIC, playerInteracting);
		TraitEventManager.fireEvent(entityHealEvent);
		if(entityHealEvent.isCancelled()) return false;
		
		amount = CompatibilityModifier.EntityHeal.safeGetAmount(entityHealEvent);
		if(currentHealth + amount > maxHealth) amount = maxHealth - currentHealth;
		
		if(!entityHealEvent.isCancelled() && amount != 0){
			double newHealth = currentHealth + amount;
			
			CompatibilityModifier.BukkitPlayer.safeSetHealth(newHealth, target);
			
			Location loc = entityHealEvent.getEntity().getLocation();
			loc.getWorld().playEffect(loc, Effect.POTION_BREAK, 1);
			LanguageAPI.sendTranslatedMessage(playerInteracting, Keys.trait_healed_target_success, 
					"target", target.getName());
			
			LanguageAPI.sendTranslatedMessage(targetPlayer, Keys.trait_healed_other_success, 
					"healer", playerInteracting.getName());

			if(consume){
				int newAmount = playerInteracting.getItemInHand().getAmount() - 1;
				if(newAmount == 0){
					playerInteracting.setItemInHand(new ItemStack(Material.AIR));
				}else{
					playerInteracting.getItemInHand().setAmount(newAmount);
				}
			}
			
			return true;
		}
		
		return false;
	}

	@Override
	public boolean isBetterThan(Trait trait) {
		if(!(trait instanceof HealOthersTrait)) return false;
		HealOthersTrait otherTrait = (HealOthersTrait) trait;
		
		return value >= otherTrait.value;
	}

	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "The trait lets you heal others or yourself for a certain value.");
		helpList.add(ChatColor.YELLOW + "It can be used by right-clicking another player with a " 
				+ ChatColor.LIGHT_PURPLE + itemIDInHand.name() + ChatColor.YELLOW + " in hands.");
		return helpList;
	}

	
	@Override
	public boolean triggerButHasUplink(EventWrapper wrapper){
		RaCPlayer player = wrapper.getPlayer();
		if(player.getPlayer().getItemInHand().getType() == itemIDInHand){
			return false;
		}
		
		return true;
	}
	
	@Override
	public boolean canBeTriggered(EventWrapper wrapper) {
		PlayerAction action = wrapper.getPlayerAction();
		RaCPlayer player = wrapper.getPlayer();
		
		if(action == PlayerAction.INTERACT_ENTITY
			|| action == PlayerAction.INTERACT_BLOCK
			|| action == PlayerAction.INTERACT_AIR){

			boolean isSneaking = player.isSneaking();
			if(!isSneaking){
				return false;
			}
			
			if(action == PlayerAction.INTERACT_ENTITY){
				Entity target = wrapper.getEntityTarget();
				if(target != null && target instanceof Player){
					if(player.getPlayer().getItemInHand().getType() != itemIDInHand) return false;
					return true;
				}
				
				return false;
			}
			
			if(action == PlayerAction.INTERACT_AIR || action == PlayerAction.INTERACT_BLOCK){
				if(player.getPlayer().getItemInHand().getType() != itemIDInHand) return false;
				return true;
			}
		}
		return false;
	}

}
