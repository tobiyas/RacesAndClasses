package de.tobiyas.racesandclasses.entitystatusmanager.debuff;

import java.util.UUID;

public abstract class Debuff {

	/**
	 * The Total duration in ticks.
	 */
	protected final int totalTicks;
	
	/**
	 * The ticks already done.
	 */
	protected int ticksDone = 0;
	
	
	/**
	 * The amount of ticks till it hits.
	 */
	protected final int tickPeriod;
	
	/**
	 * The Current tick. When ticked and tickPeriod
	 * is overtaken, a real tick will be done.
	 */
	protected int currentTick = 0;
	
	/**
	 * The ID this is ticking on.
	 */
	protected final UUID entityID;
	
	/**
	 * The Display Name to use.
	 */
	protected final String displayName;
	
	
	public Debuff(String displayName, int totalTicks, int tickPeriod, UUID entityID) {
		this.displayName = displayName;
		this.totalTicks = totalTicks;
		this.tickPeriod = tickPeriod;
		this.entityID = entityID;
	}
	
	/**
	 * Returns the Entity ID this belongs to.
	 * 
	 * @return the Entity ID this belongs to.
	 */
	public UUID getEntityID() {
		return entityID;
	}
	

	/**
	 * Returns the Display Name.
	 * 
	 * @return The Display Name.
	 */
	public String getDisplayName() {
		return displayName;
	}
	
	
	/**
	 * Returns The Seconds left for this debuff.
	 * 
	 * @return the seconds left for the Debuff.
	 */
	public int getsecondsLeft(){
		double left = totalTicks - ticksDone;
		left *= (double)tickPeriod;
		
		return (int) Math.round(left / 20d);
	}
	
	

	/**
	 * Ticks the Debuff.
	 */
	public void tick(){
		currentTick++;
		if(currentTick > tickPeriod){
			currentTick = 0;
			realTick();
			
			ticksDone++;
		}
	}
	
	/**
	 * if the Debuff is done.
	 * 
	 * @return true if done.
	 */
	public boolean done(){
		return ticksDone > totalTicks;
	}
	
	
	/**
	 * The real tick to do.
	 */
	protected abstract void realTick();
	
	
	/**
	 * Returns the Modified incoming Damage.
	 * 
	 * @return the modified incoming Damage
	 */
	public double modifyIncomingDamage(double value){
		return value;
	}
	
	
	/**
	 * returns the Outgoing Damage.
	 * 
	 * @param value the value to modify.
	 * 
	 * @return  the modified outgoing damage.
	 */
	public double modifyOutgoingDamage(double value){
		return value;
	}
	
}
