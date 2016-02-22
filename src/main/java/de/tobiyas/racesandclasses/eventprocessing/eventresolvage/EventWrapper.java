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
package de.tobiyas.racesandclasses.eventprocessing.eventresolvage;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;

public class EventWrapper {

	/**
	 * the player using the event
	 */
	private final RaCPlayer player;
	
	/**
	 * The world this event is on
	 */
	private final World world;
	
	/**
	 * The action the player is doing
	 */
	private final PlayerAction playerAction;
	
	/**
	 * The target the player has
	 */
	private final Entity entityTarget;
	
	/**
	 * The BlockTarget the player has
	 */
	private final Block blockTarget;
	
	/**
	 * The value of Damage / Heal
	 */
	private final double damageHealValue;
	
	/**
	 * The DamageCause of the Event
	 */
	private final DamageCause damageCause;
	
	/**
	 * If an arrow is involved in the whole eventening.
	 */
	private final boolean arrowInvolved;
	
	/**
	 * The regain Reason to the Event
	 */
	private final RegainReason regainReason;
	
	/**
	 * The Resource regained.
	 */
	private final RegainResource regainResource;
	
	
	/**
	 * The Event that was triggered
	 * <br>This may be needed by some Traits to modify it.
	 * <br>For example adjusting Health Regeneration
	 */
	private final Event event;

	
	public EventWrapper(RaCPlayer player, World world, PlayerAction playerAction,
			Entity entityTarget, Block blockTarget, double damageHealValue,
			DamageCause damageCause, boolean arrowInvolved, RegainReason regainReason, 
			RegainResource regainResource, Event event) {
	
		this.player = player;
		this.world = world;
		this.playerAction = playerAction;
		this.entityTarget = entityTarget;
		this.blockTarget = blockTarget;
		this.damageHealValue = damageHealValue;
		this.damageCause = damageCause;
		this.arrowInvolved = arrowInvolved;
		this.regainReason = regainReason;
		this.regainResource = regainResource;
		
		this.event = event;
	}

	public RaCPlayer getPlayer() {
		return player;
	}

	public World getWorld() {
		return world;
	}

	public PlayerAction getPlayerAction() {
		return playerAction;
	}

	public Entity getEntityTarget() {
		return entityTarget;
	}

	public Block getBlockTarget() {
		return blockTarget;
	}

	public double getDamageHealValue() {
		return damageHealValue;
	}

	public Event getEvent() {
		return event;
	}

	public DamageCause getDamageCause() {
		return damageCause;
	}

	public boolean isArrowInvolved() {
		return arrowInvolved;
	}

	public RegainReason getRegainReason() {
		return regainReason;
	}
	

	
	public RegainResource getRegainResource() {
		return regainResource;
	}



	/**
	 * This is the Ragain resource
	 * 
	 * @author Tobiyas
	 */
	public static enum RegainResource{
		HEALTH,
		HUNGER,
		MANA
	}
	
}
