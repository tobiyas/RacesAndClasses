package de.tobiyas.racesandclasses.entitystatusmanager.dot;

import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;

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
	private final DamageType damageType;
	
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

	

	public DotContainer(String name, RaCPlayer damager, DamageType dotType, 
			int ticks, double damageOnTick, int damageEveryTicks) {
		
		this.name = name;
		this.damager = damager;
		this.damageType = dotType;
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
	
	public DamageType getDamageType() {
		return damageType;
	}
	
}
