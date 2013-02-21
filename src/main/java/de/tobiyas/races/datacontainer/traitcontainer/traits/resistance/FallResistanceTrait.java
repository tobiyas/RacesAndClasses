package de.tobiyas.races.datacontainer.traitcontainer.traits.resistance;

import java.util.LinkedList;

import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import de.tobiyas.races.datacontainer.eventmanagement.events.EntityDamageDoubleEvent;

public class FallResistanceTrait extends Resistance {
	
	public FallResistanceTrait(){
	}
	
	@TraitInfo(registerdClasses = {EntityDamageDoubleEvent.class})
	@Override
	public void generalInit(){
		resistances = new LinkedList<DamageCause>();
		resistances.add(DamageCause.FALL);
	}

	@Override
	public String getName() {
		return "FallResistanceTrait";
	}

	@Override
	public Object getValue() {
		return value;
	}
	
	@Override
	public boolean isVisible() {
		return true;
	}

}
