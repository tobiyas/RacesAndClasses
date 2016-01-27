package de.tobiyas.racesandclasses.pets;

import org.bukkit.entity.EntityType;

public class Pet {

	/**
	 * The Type of the Pet.
	 */
	private final EntityType petType;
	
	/**
	 * The Name of the Pet.
	 */
	private final String petName;
	
	/**
	 * The Max-Health of the pet.
	 */
	private final double petMaxHealth;
	
	/**
	 * The Damage of the pet.
	 */
	private final double petDamage;
	
	/**
	 * if the pet is passive
	 */
	private final boolean passive;
	
	/**
	 * If the pet is invincible
	 */
	private final boolean invincible;
	
	/**
	 * If the pet is a baby.
	 */
	private final boolean baby;
	
	/**
	 * If the pet is a baby.
	 */
	private final boolean autoRevive;
	
	
	

	
	
	public Pet(EntityType petType, String petName, double petMaxHealth,
			double petDamage, boolean passive, boolean invincible, boolean baby, boolean autoRevive) {
		this.petType = petType;
		this.petName = petName;
		this.petMaxHealth = petMaxHealth;
		this.petDamage = petDamage;
		this.passive = passive;
		this.invincible = invincible;
		this.baby = baby;
		this.autoRevive = autoRevive;
	}


	public EntityType getPetType() {
		return petType;
	}


	public String getPetName() {
		return petName;
	}


	public double getPetMaxHealth() {
		return petMaxHealth;
	}


	public double getPetDamage() {
		return petDamage;
	}


	public boolean isPassive() {
		return passive;
	}


	public boolean isInvincible() {
		return invincible;
	}
	
	public boolean isBaby() {
		return baby;
	}
	
	public boolean isAutoRevive() {
		return autoRevive;
	}
}
