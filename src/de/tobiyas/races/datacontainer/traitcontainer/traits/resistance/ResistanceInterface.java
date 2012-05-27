package de.tobiyas.races.datacontainer.traitcontainer.traits.resistance;

import java.util.List;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import de.tobiyas.races.datacontainer.traitcontainer.Trait;

public interface ResistanceInterface extends Trait{
	
	public List<DamageCause> getResistanceTypes();
}
