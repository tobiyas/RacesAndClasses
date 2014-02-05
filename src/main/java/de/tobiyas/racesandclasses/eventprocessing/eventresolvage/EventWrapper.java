package de.tobiyas.racesandclasses.eventprocessing.eventresolvage;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

public class EventWrapper {

	/**
	 * the player using the event
	 */
	private final Player player;
	
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
	 * The regain Reason to the Event
	 */
	private final RegainReason regainReason;
	
	
	/**
	 * The Event that was triggered
	 * <br>May disapear in future versions.
	 */
	@Deprecated
	private final Event event;

	
	public EventWrapper(Player player, World world, PlayerAction playerAction,
			Entity entityTarget, Block blockTarget, double damageHealValue,
			DamageCause damageCause, RegainReason regainReason, Event event) {
		super();
	
		this.player = player;
		this.world = world;
		this.playerAction = playerAction;
		this.entityTarget = entityTarget;
		this.blockTarget = blockTarget;
		this.damageHealValue = damageHealValue;
		this.damageCause = damageCause;
		this.regainReason = regainReason;
		
		this.event = event;
	}

	public Player getPlayer() {
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

	public RegainReason getRegainReason() {
		return regainReason;
	}

	
}
