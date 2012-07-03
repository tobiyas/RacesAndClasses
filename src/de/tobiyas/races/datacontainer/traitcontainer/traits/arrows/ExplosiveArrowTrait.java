package de.tobiyas.races.datacontainer.traitcontainer.traits.arrows;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.TraitEventManager;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.events.EntityDamageByEntityDoubleEvent;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.events.EntityDamageDoubleEvent;

public class ExplosiveArrowTrait extends AbstractArrow{
	
	private HashMap<Arrow, Player> arrowMap;
	private boolean destroyBlocks = false;

	public ExplosiveArrowTrait(RaceContainer raceContainer){
		this.raceContainer = raceContainer;
	}
	
	public ExplosiveArrowTrait(ClassContainer classContainer){
		this.classContainer = classContainer;
	}
	
	@TraitInfo(registerdClasses = {EntityDamageByEntityDoubleEvent.class, ProjectileHitEvent.class, PlayerInteractEvent.class, EntityShootBowEvent.class})
	@Override
	public void generalInit(){
		arrowMap = new HashMap<Arrow, Player>();
	}
	
	@Override
	public boolean isVisible() {
		return true;
	}

	@Override
	public String getName() {
		return "ExplosiveArrowTrait";
	}

	@Override
	public String getValueString() {
		return "radius: " + duration + " damage: " + totalDamage;
	}

	@Override
	public void setValue(Object obj) {
		String preValue = (String) obj;
		
		String[] split = preValue.split("#");
		if(split.length == 3){
			destroyBlocks = Boolean.valueOf(split[2]);
		}else
			if(split.length != 2)
				split = new String[]{"10", "5"};
		
		duration = Integer.valueOf(split[0]);
		this.totalDamage = Double.valueOf(split[0]);
	}

	@Override
	protected boolean onShoot(EntityShootBowEvent event) {
		if(!(event.getProjectile() instanceof Arrow)) return false;
		if(!(event.getEntity() instanceof Player)) return false;
		Arrow arrow = (Arrow) event.getProjectile();
		Player player = (Player) event.getEntity();
		
		arrowMap.put(arrow, player);
		return false;
	}

	@Override
	protected boolean onHitEntity(EntityDamageByEntityDoubleEvent event) {
		if(!(event.getEntity() instanceof Arrow)) return false;
		if(!arrowMap.containsKey(event.getEntity())) return false;
		Arrow arrow = (Arrow) event.getEntity();
		
		Location loc = arrow.getLocation();
		if(destroyBlocks){
			loc.getWorld().createExplosion(loc, duration);
			arrowMap.remove(arrow);
			return true;
		}else
			loc.getWorld().createExplosion(loc, 0);
		
		HashSet<LivingEntity> damageTo = getEntitiesNear(loc, duration);
		
		for(LivingEntity entity : damageTo){
			EntityDamageEvent newEvent = new EntityDamageDoubleEvent(entity, DamageCause.BLOCK_EXPLOSION, totalDamage, true);
			TraitEventManager.fireEvent(newEvent);
		}
		
		arrowMap.remove(arrow);
		return false;
	}
	
	private HashSet<LivingEntity> getEntitiesNear(Location loc, int radius){
		Location locToCheck = loc.getBlock().getLocation();
		HashSet<LivingEntity> entitySet = new HashSet<LivingEntity>();
		
		for(Entity entity : loc.getWorld().getEntities()){
			if(!(entity instanceof LivingEntity)) continue;
			if(entity.getLocation().getBlock().getLocation().distance(locToCheck) < radius)
				entitySet.add((LivingEntity)entity);
		}
		
		return entitySet;
	}

	@Override
	protected boolean onHitLocation(ProjectileHitEvent event) {
		if(!(event.getEntity() instanceof Arrow)) return false;
		if(!arrowMap.containsKey(event.getEntity())) return false;
		Arrow arrow = (Arrow) event.getEntity();
		
		Location loc = arrow.getLocation();
		if(destroyBlocks){
			loc.getWorld().createExplosion(loc, duration);
			arrowMap.remove(arrow);
			return true;
		}else
			loc.getWorld().createExplosion(loc, 0);
		
		HashSet<LivingEntity> damageTo = getEntitiesNear(loc, duration);
		arrowMap.remove(arrow);
		
		for(LivingEntity entity : damageTo){
			EntityDamageDoubleEvent newEvent = new EntityDamageDoubleEvent(entity, DamageCause.BLOCK_EXPLOSION, totalDamage, true);
			TraitEventManager.fireEvent(newEvent);
		}
		
		return false;
	}

	@Override
	protected String getArrowName() {
		return "Explosive Arrow";
	}

}
