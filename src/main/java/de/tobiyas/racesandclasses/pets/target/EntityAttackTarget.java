package de.tobiyas.racesandclasses.pets.target;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.Vector;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.pets.SpawnedPet;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.util.vollotile.ParticleEffects;
import de.tobiyas.util.vollotile.VollotileCodeManager;

public class EntityAttackTarget implements Target {
	
	/**
	 * The Set of Entities the default {@link Creature#setTarget(LivingEntity)} is working.
	 */
	private static Set<EntityType> targetWorkingEntities = new HashSet<EntityType>(Arrays.asList(new EntityType[] {
			EntityType.WOLF, EntityType.OCELOT
	}));
	
	/**
	 * The Set of Entities the default {@link Creature#setTarget(LivingEntity)} is working.
	 */
	private static Set<EntityType> shootingEntities = new HashSet<EntityType>(Arrays.asList(new EntityType[] {
			EntityType.SKELETON, EntityType.BLAZE
	}));

	
	/**
	 * The last time the entity attacked!
	 */
	private long lastAttack = 0;
	
	
	/**
	 * The Entity Target to set.
	 */
	private final LivingEntity entity;
	
	
	
	public EntityAttackTarget(LivingEntity entity) {
		this.entity = entity;
	}
	
	
	@Override
	public boolean valid(SpawnedPet pet) {
		if(entity == null) return false;
		
		return entity.isValid();
	}
	
	
	@Override
	public int getPriority() {
		return 100;
	}
	
	
	@Override
	public TargetType getType() {
		return TargetType.Entity;
	}
	
	
	@Override
	public void setTarget(LivingEntity pet) {
		if(pet instanceof Creature) {
			Creature petCreature = (Creature) pet;
			//Normal set target.
			
			EntityType petType = pet.getType();
			if(targetWorkingEntities.contains(petType)) {
				petCreature.setTarget(entity);
				return;
			}
			
			//Own damage Function.
			if(shootingEntities.contains(petType)) petShootAttack(petCreature);
			else petMeleeAttack(petCreature);
		}
	}
	
	/**
	 * Pet melee Attack! GO!
	 * 
	 * @param pet to use.
	 */
	private void petMeleeAttack(Creature pet) {
		double distance = pet.getLocation().distanceSquared(entity.getLocation());
		
		//If near, damage!
		if(distance < 2) {
			long timeSinceLastAttack = System.currentTimeMillis() - lastAttack;
			if(timeSinceLastAttack  > 2000){
				//damage entity!
				@SuppressWarnings("deprecation")
				EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(pet, entity, DamageCause.ENTITY_ATTACK, 1);
				RacesAndClasses.getPlugin().fireEventToBukkit(event);
				
				if(!event.isCancelled()) {
					entity.damage(event.getDamage(), pet);
					lastAttack = System.currentTimeMillis();
				}
			}
		}
		
		
		//If not near, walk near!
		if(distance > 2) {
			VollotileCodeManager.getVollotileCode().entityWalkToLocation(pet, entity.getLocation(), 1);
		}
	}

	/**
	 * Shoot an Arrow if near enough.
	 * 
	 * @param pet to use.
	 */
	private void petShootAttack(Creature pet) {
		double distance = pet.getLocation().distanceSquared(entity.getLocation());

		if(distance > 10) VollotileCodeManager.getVollotileCode().entityWalkToLocation(pet, entity.getLocation(), 1);
		
		//If near, Attack!
		if(distance < 10){
			VollotileCodeManager.getVollotileCode().entityWalkToLocation(pet, pet.getLocation(), 1);
			
			Arrow arrow = pet.launchProjectile(Arrow.class);
			arrow.setVelocity(pet.getLocation().toVector().subtract(entity.getLocation().toVector()).normalize().multiply(4));
		}
		
	}
	
	
	@Override
	public void showToOwner(RaCPlayer owner) {
		if(!entity.isValid()) return;
		Location target = entity.getLocation();
		VollotileCodeManager.getVollotileCode().sendParticleEffect(ParticleEffects.HAPPY_VILLAGER, target, new Vector(0,0.3,0), 1, 3, owner.getPlayer());
	}
	
}
