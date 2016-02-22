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
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper.RegainResource;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.resolvers.EventActionResolver;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.resolvers.EventDamageHealResolver;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.resolvers.EventPlayerResolver;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.resolvers.EventTargetResolver;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;

public class EventWrapperFactory {

	
	/**
	 * Builds an EventWrapper from the passed event
	 * 
	 * @param event to build from
	 * 
	 * @return the parsed {@link EventWrapper}
	 */
	public static EventWrapper buildFromEvent(Event event){
		Player player = EventPlayerResolver.resolvePlayer(event);
		if(player == null) return null; //we have no interesting event involving a player

		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
		
		World world = player.getWorld();
		PlayerAction action = EventActionResolver.resolveAction(event, player);

		//target parts
		Entity targetEntity = EventTargetResolver.getTargetEntityFromEvent(event); if(targetEntity == null) targetEntity = player;
		Block targetBlock = EventTargetResolver.getTargetBlockFromEvent(event);
		boolean arrowInvolved = EventPlayerResolver.isArrowInvolved(event);
		
		//damage parts
		double damageHealValue = EventDamageHealResolver.getDamageHealFromEvent(event);
		DamageCause damageCause = EventDamageHealResolver.getDamageCauseFromEvent(event);
		RegainReason regainReason = EventDamageHealResolver.getRegainReasonFromEvent(event);
		RegainResource regainResource = EventDamageHealResolver.getRegainResource(event);
		
		
		return new EventWrapper(
				racPlayer, 
				world, 
				action, 
				targetEntity, 
				targetBlock, 
				damageHealValue, 
				damageCause, 
				arrowInvolved,
				regainReason, 
				regainResource,
				event
			);
	}
	
	
	/**
	 * Builds an Fake event with ONLY a player!
	 * <br>CAUTION this may break some traits if used incorrectly.
	 * 
	 * @param player to generate to
	 * 
	 * @return the almost empty wrapper
	 */
	public static EventWrapper buildOnlyWithplayer(Player player){
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
		if(!racPlayer.isOnline()) return null;
		return buildOnlyWithplayer(racPlayer);
	}

	/**
	 * Builds an Fake event with ONLY a player!
	 * <br>CAUTION this may break some traits if used incorrectly.
	 * 
	 * @param player to generate to
	 * 
	 * @return the almost empty wrapper
	 */
	public static EventWrapper buildOnlyWithplayer(RaCPlayer player){
		return new EventWrapper(player, player.getWorld(), PlayerAction.NONE, null, null, -1, null, false, null, null, null);
	}
}
