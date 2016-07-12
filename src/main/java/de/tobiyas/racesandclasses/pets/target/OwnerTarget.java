package de.tobiyas.racesandclasses.pets.target;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import de.tobiyas.racesandclasses.pets.SpawnedPet;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.util.location.LocationOffsetUtils;
import de.tobiyas.util.vollotile.ParticleEffects;
import de.tobiyas.util.vollotile.VollotileCodeManager;

public class OwnerTarget implements Target {

	/**
	 * The owner to follow
	 */
	private final RaCPlayer owner;
	
	
	public OwnerTarget(RaCPlayer owner) {
		this.owner = owner;
	}
	
	
	@Override
	public boolean valid(SpawnedPet pet) {
		return true;
	}
	
	
	@Override
	public int getPriority() {
		return 1;
	}
	
	
	@Override
	public TargetType getType() {
		return TargetType.Owner;
	}
	
	
	@Override
	public void setTarget(LivingEntity pet) {
		VollotileCodeManager.getVollotileCode().entityWalkToLocation(pet, LocationOffsetUtils.getRandomAround(owner.getLocation()), 1);
	}
	
	
	@Override
	public void showToOwner(RaCPlayer owner) {
		if(!owner.isValid()) return;
		Location target = owner.getLocation();
		VollotileCodeManager.getVollotileCode().sendParticleEffect(ParticleEffects.CRIT, target, new Vector(0,0.3,0), 1, 3, owner.getPlayer());
	}
	
}
