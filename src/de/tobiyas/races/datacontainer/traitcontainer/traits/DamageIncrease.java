package de.tobiyas.races.datacontainer.traitcontainer.traits;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import de.tobiyas.races.datacontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitcontainer.ReturnFilter;
import de.tobiyas.races.datacontainer.traitcontainer.Trait;
import de.tobiyas.races.datacontainer.traitcontainer.TraitEventManager;

public class DamageIncrease implements Trait {
	
	private double value;
	private RaceContainer raceContainer;

	public DamageIncrease(RaceContainer raceContainer){
		this.raceContainer = raceContainer;
		TraitEventManager.getTraitEventManager().registerTrait(this);
	}

	@Override
	public String getName() {
		return "DamageIncreaseTrait";
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public void setValue(Object obj) {
		value = (Double) obj;
	}

	@Override
	public ReturnFilter modify(Event event) {
		if(!(event instanceof EntityDamageByEntityEvent)) return null;
		
		EntityDamageByEntityEvent Eevent = (EntityDamageByEntityEvent) event;
		if(!(Eevent.getDamager() instanceof Player)) return null;
		Player causer = (Player) Eevent.getDamager();
 		
		if(raceContainer.containsPlayer(causer.getName())){
			int newValue = (int) Math.ceil(Eevent.getDamage() * value);
			ReturnFilter returnContainer = new ReturnFilter(newValue, 0, false);
			return returnContainer;
		}
		return null;
	}

}
