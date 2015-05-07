package de.tobiyas.racesandclasses.entitystatusmanager.hots;

import java.util.Random;

import org.bukkit.entity.LivingEntity;

import de.tobiyas.racesandclasses.vollotile.Vollotile;
import de.tobiyas.util.vollotile.ParticleEffects;

public class Hot {

	/**
	 * The Random generator to use.
	 */
	private static final Random rand = new Random();
	
	
	/**
	 * The entity to tick on.
	 */
	private final LivingEntity entity;
	
	/**
	 * The amount to heal per tick.
	 */
	private final double tickHeal;
	
	/**
	 * The time between the ticks.
	 * <br>Time is in 0.5 Seconds.
	 * <br>This means 1 = 0.5 Seconds, 2 = 1.0 Seconds, ....
	 */
	private final int tickTime;
	
	/**
	 * The Amount of ticks to tick
	 */
	private final int tickAmounts;
	
	/**
	 * The ID of the HoT.
	 */
	private final String id;
	
	/**
	 * If the spell can stack.
	 * <br>If this is true, more than 1 of these heals
	 * can be present on the target.
	 */
	private final boolean canStack;

	
	/**
	 * The Time this hot ticked already.
	 */
	private int tickedTimes = 0;
	
	
	/**
	 * The Time this hot ticked already.
	 */
	private int currentTick = 0;
	
	/**
	 * The Numeric ID of the Hot.
	 */
	private final int numericID;
	
	
	/**
	 * Creates a Hot.
	 * 
	 * @param tickHeal
	 * @param tickTime
	 * @param tickAmounts
	 * @param id
	 * @param canStack
	 */
	public Hot(LivingEntity entity, double tickHeal, int tickTime, int tickAmounts, String id,
			boolean canStack) {
		
		super();
		
		this.entity = entity;
		this.tickHeal = tickHeal;
		this.tickTime = tickTime;
		this.tickAmounts = tickAmounts;
		this.id = id;
		this.canStack = canStack;
		this.numericID = rand.nextInt(Integer.MAX_VALUE);
	}


	public LivingEntity getEntity() {
		return entity;
	}


	public double getTickHeal() {
		return tickHeal;
	}


	public int getTickTime() {
		return tickTime;
	}


	public int getTickAmounts() {
		return tickAmounts;
	}


	public String getId() {
		return id;
	}
	

	public int getNumericID() {
		return numericID;
	}


	public boolean canStack() {
		return canStack;
	}
	
	
	public int getCurrentTicks() {
		return currentTick;
	}


	/**
	 * Ticks this hot.
	 * <br>This includes the Heal.
	 */
	public void tick(){
		if(!stillValid()) return;
		if(currentTick++ <= tickTime) return;
		
		currentTick = 0;
		de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier.LivingEntity.safeHealEntity(entity, tickHeal);
		tickedTimes ++;
		
		Vollotile.get().sendParticleEffectToAll(ParticleEffects.MOB_SPELL, entity.getLocation().clone().add(0, 1, 0), 0f, 2);
	}
	
	
	/**
	 * If the Entity is still a valid target.
	 * <br>This includes the tick times.
	 * 
	 * @return true if still valid.
	 */
	public boolean stillValid(){
		return entity.isValid() && tickedTimes >= tickAmounts;
	}
}
