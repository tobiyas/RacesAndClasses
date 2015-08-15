package de.tobiyas.racesandclasses.pets;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.util.location.LocationOffsetUtils;

public class PlayerPetManager {

	/**
	 * The Set of spawned Pets.
	 */
	private final Set<SpawnedPet> spawnedPets = new HashSet<SpawnedPet>();
	
	/**
	 * The Owner of the Pets.
	 */
	private final RaCPlayer owner;
	
	
	public PlayerPetManager(RaCPlayer player) {
		this.owner = player;
	}
	
	
	/**
	 * Ticks the Pets.
	 */
	public void tick(){
		if(!owner.isOnline()) {
			despawnAndClear();
			return;
		}
		
		Location ownerLocation = owner.getLocation();
		for(SpawnedPet pet : spawnedPets){
			//Spawn when not present.
			if(!pet.isSpawned()) {
				pet.spawnAt(LocationOffsetUtils.getRandomAround(ownerLocation));
				continue;
			}
			
			//Revalidate the Target.
			pet.revalidateTarget();
			
			//Show the current target.
			pet.showCurrentTargetToOwner();
		}
	}
	
	
	/**
	 * Despawns all Pets and clears them.
	 */
	public void despawnAndClear(){
		for(SpawnedPet pet : spawnedPets){
			pet.despawn();
		}
		
		spawnedPets.clear();
	}


	/**
	 * The Player got targeted.
	 * 
	 * @param entity that targeted the player.
	 */
	public void playerGotTargeted(LivingEntity entity) {
		for(SpawnedPet pet : spawnedPets){
			pet.setCurrentTarget(entity);
		}
	}
	
	/**
	 * The Player got damaged.
	 * 
	 * @param entity that damaged the Player.
	 */
	public void playerGotDamaged(LivingEntity entity) {
		for(SpawnedPet pet : spawnedPets){
			pet.setCurrentTarget(entity);
		}
	}
	
	/**
	 * The Player attacks this entity.
	 * 
	 * @param damagee that is attacked.
	 */
	public void playerAttacks(LivingEntity entity) {
		for(SpawnedPet pet : spawnedPets){
			pet.setCurrentTarget(entity);
		}
	}


	/**
	 * Registerd a new Pet.
	 * 
	 * @param pet to register
	 */
	public void registerNewPet(Pet pet) {
		SpawnedPet newPet = new SpawnedPet(pet, owner);
		spawnedPets.add(newPet);
	}

	
}
