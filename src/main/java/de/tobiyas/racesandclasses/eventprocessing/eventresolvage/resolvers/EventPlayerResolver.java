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

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

import de.tobiyas.racesandclasses.eventprocessing.events.chatevent.PlayerSendChannelChatMessageEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.HolderSelectedEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.leveling.LevelEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.traittrigger.TraitTriggerEvent;
import de.tobiyas.racesandclasses.util.bukkit.versioning.CertainVersionChecker;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier;

public class EventPlayerResolver {

	/**
	 * Resolves a player to the Player wanted
	 * 
	 * @param event to resolve
	 * @return
	 */
	public static Player resolvePlayer(Event event){
		//block events like place / break
		if(event instanceof BlockEvent){
			if(event instanceof BlockPlaceEvent){
				return ((BlockPlaceEvent) event).getPlayer();
			}
			
			if(event instanceof BlockBreakEvent){
				return ((BlockBreakEvent) event).getPlayer();
			}
			
			if(event instanceof BlockDamageEvent){
				return ((BlockDamageEvent) event).getPlayer();
			}
		}
		
		//Projectile events. 
		//We need to get the shooter.
		if(event instanceof ProjectileHitEvent){
			ProjectileHitEvent launchEvent = (ProjectileHitEvent) event;
			LivingEntity shooter = CompatibilityModifier.Shooter.getShooter(launchEvent.getEntity());
			if(shooter instanceof Player) return (Player) shooter;
		}
		
		if(event instanceof ProjectileLaunchEvent){
			ProjectileLaunchEvent launchEvent = (ProjectileLaunchEvent) event;
			LivingEntity shooter = CompatibilityModifier.Shooter.getShooter(launchEvent.getEntity());
			if(shooter instanceof Player) return (Player) shooter;
		}
		
		//check if any projectile
		if(event instanceof EntityDamageByEntityEvent){
			EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;
			Entity damager = damageEvent.getDamager();
			if(damager instanceof Player) return (Player) damager;
			
			if(damager instanceof Projectile){
				LivingEntity shooter = CompatibilityModifier.Shooter.getShooter((Projectile) damager);
				if(shooter != null && shooter instanceof Player){
					return (Player) shooter;
				}
			}
		}
		
		
		if(event instanceof EntityEvent){			
			EntityEvent entityEvent = (EntityEvent) event;
			if(entityEvent.getEntityType() == EntityType.PLAYER){
				return (Player) entityEvent.getEntity();
			}
		}
		
		if(event instanceof InventoryEvent){
			InventoryEvent inventoryEvent = (InventoryEvent) event;
			if(inventoryEvent.getInventory().getHolder() instanceof Player){
				return (Player) inventoryEvent.getInventory().getHolder();
			}
		}
		
		if(event instanceof InventoryMoveItemEvent){
			InventoryMoveItemEvent inventoryMoveItemEvent = (InventoryMoveItemEvent) event;
			if(inventoryMoveItemEvent.getSource().getHolder() instanceof Player){
				return (Player) inventoryMoveItemEvent.getSource().getHolder();
			}
		}
		
		if(event instanceof InventoryPickupItemEvent){
			InventoryPickupItemEvent pickupItemEvent = (InventoryPickupItemEvent) event;
			if(pickupItemEvent.getInventory().getHolder() instanceof Player){
				return (Player) pickupItemEvent.getInventory().getHolder();
			}
		}
		
		if(event instanceof PlayerEvent){
			return ((PlayerEvent) event).getPlayer();
		}
		
		if(CertainVersionChecker.isAbove1_6()){
			if(event instanceof PlayerLeashEntityEvent){
				return ((PlayerLeashEntityEvent) event).getPlayer();
			}
		}
		
		
		if(event instanceof PlayerSendChannelChatMessageEvent){
			return ((PlayerSendChannelChatMessageEvent) event).getPlayer();
		}
		
		if(event instanceof VehicleEvent){
			if(event instanceof VehicleEntityCollisionEvent){
				VehicleEntityCollisionEvent vecevent = (VehicleEntityCollisionEvent) event;
				if(vecevent.getEntity() instanceof Player){
					return (Player) vecevent.getEntity();
				}
			}
			
			if(event instanceof VehicleEnterEvent){
				VehicleEnterEvent vehicleEnterEvent = (VehicleEnterEvent) event;
				if(vehicleEnterEvent.getEntered() instanceof Player){
					return (Player) vehicleEnterEvent.getEntered();
				}
			}
			
			if(event instanceof VehicleExitEvent){
				VehicleExitEvent vehicleExitEvent = (VehicleExitEvent) event;
				if(vehicleExitEvent.getExited() instanceof Player){
					return (Player) vehicleExitEvent.getExited();
				}
			}
		}
		

		//RaC-Plugin Events:		
		if(event instanceof LevelEvent){
			return Bukkit.getPlayer(((LevelEvent) event).getPlayerName());
		}
		
		if(event instanceof HolderSelectedEvent){
			return ((HolderSelectedEvent) event).getPlayer();
		}
		
		if(event instanceof PlayerSendChannelChatMessageEvent){
			return ((PlayerSendChannelChatMessageEvent)event).getPlayer();
		}
		
		if(event instanceof TraitTriggerEvent){
			return null; //This can not be interesting for a Trait.
		}
		
		return null;
	}
	
	public static boolean isArrowInvolved(Event event) {
		//check if any projectile
		if(event instanceof EntityDamageByEntityEvent){
			EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;
			Entity damager = damageEvent.getDamager();
			if(damager instanceof Projectile){
				LivingEntity shooter = CompatibilityModifier.Shooter.getShooter((Projectile) damager);
				if(shooter != null && shooter instanceof Player){
					return true;
				}
			}
		}
		
		return false;
	}
}
