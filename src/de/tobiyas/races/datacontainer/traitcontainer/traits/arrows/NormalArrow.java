package de.tobiyas.races.datacontainer.traitcontainer.traits.arrows;

import java.util.HashSet;

import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.TraitEventManager;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.events.EntityDamageByEntityDoubleEvent;

public class NormalArrow extends AbstractArrow {
	
	public NormalArrow(RaceContainer container){
		this.raceContainer = container;
	}
	
	@Override
	public void generalInit(){
		HashSet<Class<?>> listenedEvents = new HashSet<Class<?>>();
		listenedEvents.add(PlayerInteractEvent.class);
		listenedEvents.add(EntityDamageByEntityDoubleEvent.class);
		TraitEventManager.getInstance().registerTrait(this, listenedEvents);
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
