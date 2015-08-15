package de.tobiyas.racesandclasses.pets;

import org.bukkit.entity.EntityType;

public class PetBuilder {

	

	/**
	 * The Type of the Pet.
	 */
	private EntityType petType = EntityType.WOLF;
	
	/**
	 * The Name of the Pet.
	 */
	private String petName = "Hans";
	
	/**
	 * The Max-Health of the pet.
	 */
	private double petMaxHealth = 10;
	
	/**
	 * The Damage of the pet.
	 */
	private double petDamage = 1;
	
	/**
	 * if the pet is passive
	 */
	private  boolean passive = false;
	
	/**
	 * If the pet is invincible
	 */
	private  boolean invincible = false;

	
	
	
	
	
	
	
	public EntityType getPetType() {
		return petType;
	}

	public PetBuilder setPetType(EntityType petType) {
		this.petType = petType;
		return this;
	}

	public String getPetName() {
		return petName;
	}

	public PetBuilder setPetName(String petName) {
		this.petName = petName;
		return this;
	}

	public double getPetMaxHealth() {
		return petMaxHealth;
	}

	public PetBuilder setPetMaxHealth(double petMaxHealth) {
		this.petMaxHealth = petMaxHealth;
		return this;
	}

	public double getPetDamage() {
		return petDamage;
	}

	public PetBuilder setPetDamage(double petDamage) {
		this.petDamage = petDamage;
		return this;
	}

	public boolean isPassive() {
		return passive;
	}

	public PetBuilder setPassive(boolean passive) {
		this.passive = passive;
		return this;
	}

	public boolean isInvincible() {
		return invincible;
	}

	public PetBuilder setInvincible(boolean invincible) {
		this.invincible = invincible;
		return this;
	}
	
	
	/**
	 * Builds the Pet.
	 * 
	 * @return build pet.
	 */
	public Pet build(){
		return new Pet(petType, petName, petMaxHealth, petDamage, passive, invincible);
	}
	
	
	
	
}
