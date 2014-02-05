package de.tobiyas.racesandclasses.eventprocessing.eventresolvage.resolvers;

import org.bukkit.entity.Player;
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
			if(plugin.getPlayerManager().getSpellManagerOfPlayer(player.getName()).isWandItem(itemInHands)){
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
			if(((EntityDamageByEntityEvent) event).getDamager() == player){
				return PlayerAction.DO_DAMAGE;
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
		
		return PlayerAction.UNKNOWN;
	}
}
