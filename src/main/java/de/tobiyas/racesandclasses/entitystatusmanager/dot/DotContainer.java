package de.tobiyas.racesandclasses.entitystatusmanager.dot;

import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;

public class DotContainer {
	
	/**
	 * A readable name for the Dot.
	 */
	private final String name;
	
	/**
	 * the player damaged.
	 */
	private final RaCPlayer damager;
	
	/**
	 * The Effect to trigger
	 */
	private final DamageCause damageType;
	
	/**
	 * The Effect to trigger
	 */
	private final DotType dotType;
	
	/**
	 * ticks left to trigger
	 */
	private int ticks;
	
	/**
	 * the DamagePer Tick
	 */
	private final double damageOnTick;

	/**
	 * The timings the Effect ticks.
	 */
	private final int damageEveryTicks;

	

	public DotContainer(String name, RaCPlayer damager, 
			DamageCause damageType, DotType dotType, 
			int ticks, double damageOnTick, int damageEveryTicks) {
		
		this.name = name;
		this.damager = damager;
		this.damageType = damageType;
		this.dotType = dotType;
		this.ticks = ticks;
		this.damageOnTick = damageOnTick;
		this.damageEveryTicks = damageEveryTicks;
	}
	
	
	public String getName() {
		return name;
	}

	public int getTicks() {
		return ticks;
	}

	public void setTicks(int ticks) {
		this.ticks = ticks;
	}

	public double getDamageOnTick() {
		return damageOnTick;
	}

	public int getDamageEveryTicks() {
		return damageEveryTicks;
	}
	
	public RaCPlayer getDamager() {
		return damager;
	}
	
	public DamageCause getDamageType() {
		return damageType;
	}
	
	public DotType getDotType() {
		return dotType;
	}
	
}
