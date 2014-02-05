package de.tobiyas.racesandclasses.traitcontainer.traits.statictraits;

import java.util.Map;

import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.traits.arrows.AbstractArrow;

public class NormalArrow extends AbstractArrow {
	
	public NormalArrow(){
	}
	
	@TraitEventsUsed(registerdClasses = {EntityDamageByEntityEvent.class, PlayerInteractEvent.class})
	@Override
	public void generalInit(){
	}
	
	@Override
	public String getName() {
		return "Normal Arrow";
	}

	@Override
	protected String getPrettyConfigIntern() {
		return "Just a normal Arrow";
	}

	@TraitConfigurationNeeded
	@Override
	public void setConfiguration(Map<String, Object> configMap) {
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
	protected boolean onHitLocation(ProjectileHitEvent event) {
		//Not needed
		return false;
	}
	
	@TraitInfos(category="arrow", traitName="NormalArrowTrait", visible=false)
	@Override
	public void importTrait() {
	}

}
