package de.tobiyas.races.datacontainer.traitcontainer.traits;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;

import de.tobiyas.races.datacontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitcontainer.ReturnFilter;
import de.tobiyas.races.datacontainer.traitcontainer.Trait;
import de.tobiyas.races.datacontainer.traitcontainer.TraitEventManager;

public class DamageReduce implements Trait{
	
	private double value;
	private RaceContainer raceContainer;

	public DamageReduce(RaceContainer raceContainer){
		this.raceContainer = raceContainer;
		TraitEventManager.getTraitEventManager().registerTrait(this);
	}

	@Override
	public String getName() {
		return "DamageReduceTrait";
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public void setValue(Object value) {
		this.value = (Double) value;
	}

	@Override
	public ReturnFilter modify(Event event) {
		if(!(event instanceof EntityDamageEvent)) return null;
		EntityDamageEvent Eevent = (EntityDamageEvent) event;
		
		if(Eevent.getEntityType() != EntityType.PLAYER) return null;
		Player target = (Player) Eevent.getEntity();
		
		if(raceContainer.containsPlayer(target.getName())){
			int newValue = (int) Math.ceil(Eevent.getDamage() * value);
			ReturnFilter returnContainer = new ReturnFilter(newValue, 0, false);
			
			return returnContainer;
		}
		return null;
	}
}
