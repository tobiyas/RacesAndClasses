package de.tobiyas.racesandclasses.pets.target;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import de.tobiyas.racesandclasses.pets.SpawnedPet;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.util.vollotile.ParticleEffects;
import de.tobiyas.util.vollotile.VollotileCodeManager;

public class LocationTarget implements Target {

	/**
	 * The Location to use.
	 */
	private final Location location;
	
	
	public LocationTarget(Location location) {
		this.location = location;
	}
	
	
	
	@Override
	public boolean valid(SpawnedPet pet) {
		if(!pet.isSpawned()) return false;
		
		Location petLocation = pet.getPetEntity().getLocation();
		return petLocation.distanceSquared(location) > 1;
	}
	
	
	@Override
	public int getPriority() {
		return 10;
	}
	
	
	@Override
	public TargetType getType() {
		return TargetType.Location;
	}
	
	
	@Override
	public void setTarget(LivingEntity pet) {
		VollotileCodeManager.getVollotileCode().entityWalkToLocation(pet, location, 1);
	}
	
	
	@Override
	public void showToOwner(RaCPlayer owner) {
		VollotileCodeManager.getVollotileCode().sendParticleEffect(ParticleEffects.HEART, location, new Vector(0,0.3,0), 1, 3, owner.getPlayer());
	}
	
}
