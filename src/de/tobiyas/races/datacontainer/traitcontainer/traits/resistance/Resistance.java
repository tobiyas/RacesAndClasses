package de.tobiyas.races.datacontainer.traitcontainer.traits.resistance;

import java.util.List;
import java.util.Observable;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import de.tobiyas.races.datacontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.race.RaceManager;
import de.tobiyas.races.datacontainer.traitcontainer.traits.health.HealthModifyContainer;

public abstract class Resistance extends Observable implements ResistanceInterface {

	protected List<DamageCause> resistances;
	protected RaceContainer raceContainer;
	protected double value;
	
	@Override
	public abstract String getName();

	@Override
	public RaceContainer getRace() {
		return raceContainer;
	}

	@Override
	public abstract Object getValue();

	@Override
	public abstract String getValueString();

	@Override
	public abstract void setValue(Object obj);

	@Override
	public boolean modify(Event event) {
		if(!(event instanceof EntityDamageEvent)) return false;
		EntityDamageEvent Eevent = (EntityDamageEvent) event;
		
		Entity entity = Eevent.getEntity();
		if(!(entity instanceof Player)) return false;
		Player player = (Player) entity;
		if(RaceManager.getManager().getRaceOfPlayer(player.getName()) == raceContainer){
			if(getResistanceTypes().contains(Eevent.getCause())){
				notifyObservers(new HealthModifyContainer(player.getName(), Eevent.getDamage() * value, "damage"));
				Eevent.setDamage(0);
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public List<DamageCause> getResistanceTypes() {
		return resistances;
	}
}
