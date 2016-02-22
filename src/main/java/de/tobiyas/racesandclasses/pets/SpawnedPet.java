package de.tobiyas.racesandclasses.pets;

import java.util.EnumMap;
import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Tameable;
import org.bukkit.metadata.FixedMetadataValue;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.pets.target.EntityAttackTarget;
import de.tobiyas.racesandclasses.pets.target.LocationTarget;
import de.tobiyas.racesandclasses.pets.target.OwnerTarget;
import de.tobiyas.racesandclasses.pets.target.Target;
import de.tobiyas.racesandclasses.pets.target.Target.TargetType;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;

public class SpawnedPet {

	/**
	 * The pet that should be spawned.
	 */
	private final Pet pet;
	
	/**
	 * The Owner of the Pet.
	 */
	private final RaCPlayer owner;
	
	/**
	 * The Pet.
	 */
	private Entity petEntity;
	
	/**
	 * The Enum Map for Targets.
	 */
	private final EnumMap<Target.TargetType, Target> targets = new EnumMap<>(Target.TargetType.class);
	
	
	public SpawnedPet(Pet pet, RaCPlayer owner) {
		this.pet = pet;
		this.owner = owner;
		
		targets.put(TargetType.Owner, new OwnerTarget(owner));
	}
	
	
	/**
	 * Despawns the Pet.
	 */
	public void despawn(){
		if(petEntity != null) {
			petEntity.remove();
			petEntity.removeMetadata("pet", RacesAndClasses.getPlugin());
		}
		
		petEntity = null;
	}
	
	
	/**
	 * Spawns the Pet.
	 */
	public boolean spawnAt(Location location){
		if(petEntity != null) despawn();
		
		petEntity = location.getWorld().spawnEntity(location, pet.getPetType());
		if(petEntity != null){
			petEntity.setMetadata("pet", new FixedMetadataValue(RacesAndClasses.getPlugin(), this));

			//Living Entity.
			if(petEntity instanceof LivingEntity){
				LivingEntity livingPet = (LivingEntity) petEntity;
				livingPet.setMaxHealth(pet.getPetMaxHealth());
			}
			
			//Tameable.
			if(petEntity instanceof Tameable){
				Tameable tameable = (Tameable) petEntity;
				tameable.setOwner(owner.getRealPlayer());
			}
			
			//Set baby or not.
			if(petEntity instanceof Ageable){
				Ageable ageable = (Ageable) petEntity;
				if(pet.isBaby()) ageable.setBaby(); else ageable.setAdult();
			}
			
			//Name stuff
			petEntity.setCustomName(pet.getPetName());
			petEntity.setCustomNameVisible(true);
			
			
			//Targeting
			revalidateTarget();
		}
		
		return petEntity != null && petEntity.isValid();
	}


	
	
	public Pet getPet() {
		return pet;
	}


	public Entity getPetEntity() {
		return petEntity;
	}


	public RaCPlayer getOwner() {
		return owner;
	}


	/**
	 * Sets the new Target.
	 * 
	 * @param currentTarget to set.
	 */
	public void setCurrentTarget(LivingEntity currentTarget) {
		if(currentTarget == null && isSpawned() && (petEntity instanceof Creature)) {
			((Creature)petEntity).setTarget(null);
			return;
		}
		
		targets.put(TargetType.Entity, new EntityAttackTarget(currentTarget));
		revalidateTarget();
	}

	
	/**
	 * If the Pet is spawned.
	 * 
	 * @return true if spawned.
	 */
	public boolean isSpawned() {
		return petEntity != null && petEntity.isValid();
	}


	/**
	 * Sets the new Current target.
	 * 
	 * @param newLocation to set.
	 */
	public void setNewCurrentTargetLocation(Location newLocation) {
		this.targets.put(TargetType.Location, new LocationTarget(newLocation));
		revalidateTarget();
	}
	
	
	/**
	 * Shows the current Target to the Owner.
	 */
	public void showCurrentTargetToOwner(){
		Target target = getTarget();
		if(target != null) target.showToOwner(owner);
	}
	
	
	/**
	 * Revalidates the current Target.
	 */
	public void revalidateTarget(){
		if(!isSpawned()) return;
		
		Target current = getTarget();
		if(current != null) current.setTarget((LivingEntity) getPetEntity());
	}
	
	
	/**
	 * Returns the current target.
	 * 
	 * @return the current Target or null if entity == null
	 */
	public Target getTarget(){
		if(!isSpawned()) return null;
		
		for(Target target : new HashSet<Target>(targets.values())){
			if(!target.valid(this)) {
				targets.remove(target.getType());
			}
		}
		
		int prio = 0;
		Target current = null;
		for(Target target : new HashSet<Target>(targets.values())){
			if(target.getPriority() > prio) { current = target; prio = target.getPriority(); }
		}
		
		return current;
	}


	/**
	 * Returns the Distance to the Owner.
	 * @return the distance to the Owner.
	 */
	public double getDistanceToOwner() {
		if(!owner.isOnline()) return Double.MAX_VALUE;
		if(!isSpawned()) return Double.MAX_VALUE;
		if(owner.getWorld() != petEntity.getWorld()) return Double.MAX_VALUE;
		
		return owner.getLocation().distance(petEntity.getLocation());
	}
	
	
}
