package de.tobiyas.races.datacontainer.traitcontainer.traits.resistance;

import java.util.LinkedList;

import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;

public class FallResistanceTrait extends Resistance {
	
	public FallResistanceTrait(RaceContainer raceContainer){
		this.raceContainer = raceContainer;
		privateInit();
	}
	
	public FallResistanceTrait(ClassContainer classContainer){
		this.classContainer = classContainer;
		privateInit();
	}
	
	private void privateInit(){
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
