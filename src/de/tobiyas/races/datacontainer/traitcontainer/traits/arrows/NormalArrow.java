package de.tobiyas.races.datacontainer.traitcontainer.traits.arrows;

import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import de.tobiyas.races.datacontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitcontainer.TraitEventManager;

public class NormalArrow extends AbstractArrow {
	
	public NormalArrow(RaceContainer container){
		this.raceContainer = container;
		TraitEventManager.getTraitEventManager().registerTrait(this);
	}
	
	@Override
	public String getName() {
		return "Normal Arrow";
	}

	@Override
	public String getValueString() {
		return null;
	}

	@Override
	public void setValue(Object obj) {
	}

	@Override
	protected boolean onShoot(EntityShootBowEvent event) {
		return false;
	}

	@Override
	protected boolean onHitEntity(EntityDamageByEntityEvent event) {
		return false;
	}

	@Override
	protected String getArrowName() {
		return "Normal Arrow";
	}

	@Override
	public boolean isVisible() {
		return false;
	}

	@Override
	protected boolean onHitLocation(ProjectileHitEvent event) {
		//Not needed
		return false;
	}
}
