package de.tobiyas.races.datacontainer.traitcontainer.traits.arrows;

import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import de.tobiyas.races.datacontainer.eventmanagement.events.EntityDamageByEntityDoubleEvent;

public class NormalArrow extends AbstractArrow {
	
	public NormalArrow(){
	}
	
	@TraitInfo(registerdClasses = {EntityDamageByEntityDoubleEvent.class, PlayerInteractEvent.class})
	@Override
	public void generalInit(){
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
	protected boolean onHitEntity(EntityDamageByEntityDoubleEvent event) {
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
