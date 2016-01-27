package de.tobiyas.racesandclasses.pets;

import java.util.Collection;
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
			double distToOwner = pet.getDistanceToOwner();
			boolean revive = !pet.isSpawned() && pet.getPet().isAutoRevive();
			
			//Spawn when not present.
			if(revive || distToOwner > 50) {
				pet.despawn();
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
		for(SpawnedPet pet : spawnedPets) pet.despawn();
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
	 * <br>Mainly for showing stuff.
	 * <br>If you need more infos as location and so on, use {@link #getSpawnedPets()}
	 * 
	 * @param pet to register
	 */
	public void registerNewPet(Pet pet) {
		SpawnedPet newPet = new SpawnedPet(pet, owner);
		spawnedPets.add(newPet);
	}
	
	/**
	 * Registerd a new Pet.
	 * <br>Mainly for showing stuff.
	 * <br>If you need more infos as location and so on, use {@link #getSpawnedPets()}
	 * 
	 * @param pet to register
	 */
	public void removePet(Pet pet) {
		for(SpawnedPet spawned : spawnedPets){
			if(spawned.getPet() == pet) {
				spawnedPets.remove(pet);
				return;
			}
		}
	}
	
	/**
	 * Revives the Pet at the given Location.
	 * @param pet to revive.
	 */
	public void revivePet(Pet pet, Location location) {
		for(SpawnedPet spawned : spawnedPets){
			if(spawned.getPet() == pet) spawned.spawnAt(location);
		}
	}
	
	/**
	 * Revives the Pet at the given Location.
	 * @param pet to revive.
	 */
	public SpawnedPet getSpawnedPet(Pet pet) {
		for(SpawnedPet spawned : spawnedPets){
			if(spawned.getPet() == pet) return spawned;
		}
		return null;
	}

	/**
	 * Returns the Registered Pets.
	 * @return the pets.
	 */
	public Collection<Pet> getRegisteredPets(){
		Collection<Pet> pets = new HashSet<Pet>();
		for(SpawnedPet pet : this.spawnedPets) pets.add(pet.getPet());
		return pets;
	}
	
	/**
	 * Returns the Pets present.
	 * @return the pets.
	 */
	public Collection<SpawnedPet> getSpawnedPets(){
		return new HashSet<SpawnedPet>(spawnedPets);
	}
}
