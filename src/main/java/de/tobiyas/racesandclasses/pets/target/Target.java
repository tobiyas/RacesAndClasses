package de.tobiyas.racesandclasses.pets.target;

import org.bukkit.entity.LivingEntity;

import de.tobiyas.racesandclasses.pets.SpawnedPet;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;

public interface Target {

	
	
	
	/**
	 * Returns the Priority of the Target.
	 */
	public int getPriority();
	
	/**
	 * Returns the Type of the Target.
	 */
	public TargetType getType();
	
	
	/**
	 * If the current target is still valid.
	 * 
	 * @param pet to use for validation.
	 * 
	 * @return true if still valid.
	 */
	public boolean valid(SpawnedPet pet);
	
	
	/**
	 * Sets the target of the Pet to the wanted location.
	 * 
	 * @param pet to use.
	 */
	public void setTarget(LivingEntity pet);
	
	/**
	 * Shows the Target to the owner.
	 * 
	 * @param owner to show to.
	 */
	public void showToOwner(RaCPlayer owner);

	
	
	public enum TargetType{
		
		Entity,
		Location,
		Owner;
		
	}

	
	
}
