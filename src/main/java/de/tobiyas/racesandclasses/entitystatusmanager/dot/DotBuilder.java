package de.tobiyas.racesandclasses.entitystatusmanager.dot;

import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;

public class DotBuilder {

	/**
	 * the Name for the Dot.
	 */
	private final String name;
	
	/**
	 * The person who damaged.
	 */
	private RaCPlayer damager;
	
	/**
	 * The total ticks to use.
	 */
	private int totalTimeInTicks = 0;
	
	/**
	 * the total damage to deal.
	 */
	private double totalDamage = 0;
	
	/**
	 * Every how many ticks to damage.
	 */
	private int damageEveryTicks = 10000;
	
	/**
	 * The Type of the Dot.
	 */
	private DamageType dotType = DamageType.MAGIC;
	
	
	public DotBuilder(String name, RaCPlayer damager) {
		this(name);
		this.damager = damager;
	}
	
	public DotBuilder(String name) {
		this.name = name;
	}


	public DotBuilder setTotalTimeInTicks(int totalTimeInTicks) {
		this.totalTimeInTicks = totalTimeInTicks;
		return this;
	}
	
	public DotBuilder setTotalTimeInSeconds(int totalTimeInSeconds) {
		return setTotalTimeInTicks(totalTimeInSeconds * 20);
	}


	public DotBuilder setTotalDamage(double totalDamage) {
		this.totalDamage = totalDamage;
		return this;
	}
	
	
	public DotBuilder setDamager(RaCPlayer damager) {
		this.damager = damager;
		return this;
	}


	public DotBuilder setDamageEveryTicks(int damageEveryTicks) {
		this.damageEveryTicks = damageEveryTicks;
		return this;
	}

	public DotBuilder setDamageEverySecond() {
		return setDamageEverySeconds(1);
	}
	
	public DotBuilder setDamageEverySeconds(int seconds) {
		return setDamageEveryTicks(seconds * 20);
	}


	public DotBuilder setDamageType(DamageType dotType) {
		this.dotType = dotType;
		return this;
	}
	
	
	/**
	 * Checks if the Builder is valid.
	 * @return true if is valid.
	 */
	public boolean valid(){
		if(name == null || name.isEmpty()) return false;
		if(damageEveryTicks <= 0) return false;
		if(dotType == null) return false;
		if(totalDamage <= 0) return false;
		if(totalTimeInTicks <= 0) return false;
		
		return true;
	}
	
	
	/**
	 * This builds a dotcontainer that is ment to be ticked every 5 ticks.
	 * @return the build container or null if not valid.
	 */
	public DotContainer build(){
		if(!valid()) return null;
		
		//Do some optimization to only tick every 5 ticks:
		int realTicks = totalTimeInTicks / 5;
		int realdamageEveryTicks = damageEveryTicks / 5;
		double damageOnTick = (totalDamage / totalTimeInTicks) * damageEveryTicks;
		
		return new DotContainer(name, damager, dotType, realTicks, damageOnTick, realdamageEveryTicks);
	}
	
}
