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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.JumpHeightIncreaseTrait;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.passive.AbstractPassiveTrait;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class JumpHeightIncreaseTrait extends AbstractPassiveTrait {	
	
	/**
	 * The Players jumping at the moment
	 */
	private List<String> jumpingPlayer = new LinkedList<String>();
	
	/**
	 * The Players about to fall
	 */
	private Set<String> preventNextFallDamage = new HashSet<String>();
	
	/**
	 * If the Falldamage should be prevented
	 */
	private boolean preventFalldamage = false;
	
	
	@TraitEventsUsed(registerdClasses = {PlayerMoveEvent.class, EntityDamageEvent.class})
	@Override
	public void generalInit(){
	}

	@Override
	public String getName() {
		return "JumpHeightIncreaseTrait";
	}
		
	@Override
	protected String getPrettyConfigIntern() {
		return "height: " + value + " blocks.";
	}

	@TraitConfigurationNeeded(fields = {
			@TraitConfigurationField(fieldName = "value", classToExpect = Double.class),
			@TraitConfigurationField(fieldName = "preventFalldamage", classToExpect = Boolean.class, optional = true)
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		value = (Double) configMap.get("value");
		
		if(configMap.containsKey("preventFalldamage")){
			preventFalldamage = (Boolean) configMap.get("preventFalldamage");
		}
	}
	
	
	/**
	 * The ticks to Prevent fall damage
	 */
	private final int ticksToPreventFallDamage = 30;
	
	@Override
	public TraitResults trigger(EventWrapper eventWrapper) {   Event event = eventWrapper.getEvent();
		if(!(event instanceof PlayerMoveEvent)) return TraitResults.False();
		
		PlayerMoveEvent Eevent = (PlayerMoveEvent) event;
		final Player player = Eevent.getPlayer();

		if(Eevent.getFrom().getY() < Eevent.getTo().getY()
				&& !jumpingPlayer.contains(player.getName())){
			
			jumpingPlayer.add(player.getName());
			if(preventFalldamage) {
				final String name = player.getName();
				preventNextFallDamage.add(name);
				Bukkit.getScheduler().scheduleSyncDelayedTask((JavaPlugin)plugin, new Runnable(){ 
						@Override public void run(){ preventNextFallDamage.remove(name); 
					}}, ticksToPreventFallDamage);
			}
			
			player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20 * 1, (int) value));
			return TraitResults.True();
		}
		
		if(Eevent.getFrom().getY() > Eevent.getTo().getY()){
			jumpingPlayer.remove(player.getName());
			return TraitResults.True();
		}
		
		return TraitResults.False();
	}
	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "Your Jump height will be increased.");
		return helpList;
	}
	
	@Override
	public boolean isBetterThan(Trait trait) {
		if(!(trait instanceof JumpHeightIncreaseTrait)) return false;
		JumpHeightIncreaseTrait otherTrait = (JumpHeightIncreaseTrait) trait;
		
		return value >= otherTrait.value;
	}

	@TraitInfos(category="passive", traitName="JumpHeightIncreaseTrait", visible=true)
	@Override
	public void importTrait() {
	}

	@Override
	public boolean canBeTriggered(EventWrapper wrapper) {
		Event event = wrapper.getEvent();
		if(event instanceof EntityDamageEvent){
			if(!preventFalldamage) return false;
			
			EntityDamageEvent damageEvent = (EntityDamageEvent) event;
			if(damageEvent.getEntityType() == EntityType.PLAYER){
				Player player = (Player) damageEvent.getEntity();
				if(damageEvent.getCause() == DamageCause.FALL){
					if(preventNextFallDamage.contains(player.getName())){
						damageEvent.setCancelled(true);
						return false;
					}
				}
			}
			
			return false;
		}
		
		return true;
	}
}
