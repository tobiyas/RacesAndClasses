package de.tobiyas.racesandclasses.traitcontainer.traits.resistance;

import java.util.List;

import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import de.tobiyas.racesandclasses.traitcontainer.interfaces.Trait;

public interface ResistanceInterface extends Trait{
	
	public List<DamageCause> getResistanceTypes();
}
