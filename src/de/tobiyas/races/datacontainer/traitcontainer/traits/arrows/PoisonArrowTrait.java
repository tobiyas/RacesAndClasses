package de.tobiyas.races.datacontainer.traitcontainer.traits.arrows;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectTypeWrapper;

import de.tobiyas.races.datacontainer.health.damagetickers.DamageTicker;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.events.EntityDamageByEntityDoubleEvent;

public class PoisonArrowTrait extends AbstractArrow {

	public PoisonArrowTrait(RaceContainer raceContainer){
		this.raceContainer = raceContainer;
	}
	
	public PoisonArrowTrait(ClassContainer classContainer){
		this.classContainer = classContainer;
	}
	
	@TraitInfo(registerdClasses = {EntityDamageByEntityDoubleEvent.class, ProjectileHitEvent.class, PlayerInteractEvent.class})
	@Override
	public void generalInit(){
	}
	
	@Override
	public boolean isVisible() {
		return true;
	}

	@Override
	public String getName() {
		return "PoisonArrowTrait";
	}

	@Override
	public String getValueString() {
		return "duration: " + duration + " damage: " + totalDamage;
	}

	@Override
	public void setValue(Object obj) {
		String preValue = (String) obj;
		
		String[] split = preValue.split("#");
		if(split.length != 2)
			split = new String[]{"10", "10"};
		
		duration = Integer.valueOf(split[0]);
		this.totalDamage = Double.valueOf(split[0]);
	}

	@Override
	protected boolean onShoot(EntityShootBowEvent event) {
		//Not needed
		return false;
	}

	@Override
	protected boolean onHitEntity(EntityDamageByEntityDoubleEvent event) {
		Entity hitTarget = event.getEntity();
		if(!(hitTarget instanceof LivingEntity)) return false;
		
		double damagePerTick = totalDamage / duration;
		DamageTicker ticker = new DamageTicker((LivingEntity) hitTarget, duration, damagePerTick, DamageCause.POISON);
		ticker.linkPotionEffect(PotionEffectTypeWrapper.POISON.createEffect(duration, 0));
		return true;
	}

	@Override
	protected boolean onHitLocation(ProjectileHitEvent event) {
		//Not needed
		return false;
	}

	@Override
	protected String getArrowName() {
		return "Poison Arrow";
	}

}
