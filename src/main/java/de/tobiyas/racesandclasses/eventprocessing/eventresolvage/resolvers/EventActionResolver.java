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
package de.tobiyas.racesandclasses.eventprocessing.eventresolvage.resolvers;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.PlayerAction;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier;

public class EventActionResolver {

	/**
	 * Resolves an action from the event passed.
	 * 
	 * @param event to resolve
	 * @param player the player involved
	 * 
	 * @return the resolved Action
	 */
	public static PlayerAction resolveAction(Event event, Player player){
		RacesAndClasses plugin = RacesAndClasses.getPlugin();
		
		if(event instanceof PlayerInteractEntityEvent){
			return PlayerAction.INTERACT_ENTITY;
		}
		
		if(event instanceof PlayerInteractEvent){
			//check player change spell first
			
			ItemStack itemInHands = ((PlayerInteractEvent) event).getItem();
			if(plugin.getPlayerManager().getSpellManagerOfPlayer(player.getUniqueId()).isWandItem(itemInHands)){
				if(((PlayerInteractEvent) event).getAction() == Action.RIGHT_CLICK_BLOCK
						|| ((PlayerInteractEvent) event).getAction() == Action.RIGHT_CLICK_AIR){
					
					return PlayerAction.CHANGE_SPELL;
				}
				
				if(((PlayerInteractEvent) event).getAction() == Action.LEFT_CLICK_BLOCK
						|| ((PlayerInteractEvent) event).getAction() == Action.LEFT_CLICK_AIR){
					
					return PlayerAction.CAST_SPELL;
				}
			}
			
			if(((PlayerInteractEvent) event).getAction() == Action.RIGHT_CLICK_BLOCK){
				return PlayerAction.INTERACT_BLOCK;
			}
			
			if(((PlayerInteractEvent) event).getAction() == Action.LEFT_CLICK_BLOCK){
				return PlayerAction.HIT_BLOCK;
			}
			
			if(((PlayerInteractEvent) event).getAction() == Action.LEFT_CLICK_AIR){
				return PlayerAction.HIT_AIR;
			}

			if(((PlayerInteractEvent) event).getAction() == Action.RIGHT_CLICK_AIR){
				return PlayerAction.INTERACT_AIR;
			}
		}
		
		if(event instanceof EntityDamageEvent){
			if(((EntityDamageEvent) event).getEntity() == player){
				return PlayerAction.TAKE_DAMAGE;
			}
		}
		
		if(event instanceof EntityDamageByEntityEvent){
			EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;
			if(damageEvent.getDamager() == player){
				return PlayerAction.DO_DAMAGE;
			}
			
			if(damageEvent.getDamager() instanceof Projectile){
				Projectile projectile = (Projectile) damageEvent.getDamager();
				LivingEntity shooter = CompatibilityModifier.Shooter.getShooter(projectile);
				if(shooter instanceof Player){
					return PlayerAction.DO_DAMAGE;
				}
			}
		}
		
		if(event instanceof PlayerMoveEvent){
			return PlayerAction.PLAYER_MOVED;
		}
		
		if(event instanceof EntityTargetEvent){
			if(((EntityTargetEvent) event).getTarget() == player){
				return PlayerAction.PLAYER_TARGETED;				
			}
		}
		
		//No player action involved
		return PlayerAction.NONE;
	}
}
