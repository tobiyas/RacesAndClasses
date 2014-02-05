package de.tobiyas.racesandclasses.eventprocessing.eventresolvage;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.resolvers.EventActionResolver;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.resolvers.EventDamageHealResolver;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.resolvers.EventPlayerResolver;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.resolvers.EventTargetResolver;

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
		
		World world = player.getWorld();
		PlayerAction action = EventActionResolver.resolveAction(event, player);

		//target parts
		Entity targetEntity = EventTargetResolver.getTargetEntityFromEvent(event);
		Block targetBlock = EventTargetResolver.getTargetBlockFromEvent(event);
		
		//damage parts
		double damageHealValue = EventDamageHealResolver.getDamageHealFromEvent(event);
		DamageCause damageCause = EventDamageHealResolver.getDamageCauseFromEvent(event);
		RegainReason regainReason = EventDamageHealResolver.getRegainReasonFromEvent(event);
		
		
		return new EventWrapper(
				player, 
				world, 
				action, 
				targetEntity, 
				targetBlock, 
				damageHealValue, 
				damageCause, 
				regainReason, 
				event
			);
	}
}
