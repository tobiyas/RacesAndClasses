package de.tobiyas.races.datacontainer.traitcontainer.traits.resistance;

import java.util.List;
import java.util.Observable;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import de.tobiyas.races.datacontainer.health.HealthModifyContainer;
import de.tobiyas.races.datacontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.race.RaceManager;

public abstract class Resistance extends Observable implements ResistanceInterface {

	protected List<DamageCause> resistances;
	protected RaceContainer raceContainer;
	protected double value;
	protected String Operation = "";
	
	@Override
	public abstract String getName();

	@Override
	public RaceContainer getRace() {
		return raceContainer;
	}

	@Override
	public abstract Object getValue();

	@Override
	public String getValueString(){
		return Operation + " " + value;
	}

	@Override
	public void setValue(Object obj) {
		String opAndVal = String.valueOf(obj);
		value = evaluateValue(opAndVal);
	}
	
	private double evaluateValue(String val){
		char firstChar = val.charAt(0);
		
		Operation = "";
		
		if(firstChar == '+')
			Operation = "+";
		
		if(firstChar == '*')
			Operation = "*";
		
		if(firstChar == '-')
			Operation = "-";
		
		if(Operation == "")
			Operation = "*";
		else
			val = val.substring(1, val.length());
		
		return Double.valueOf(val);
	}

	@Override
	public boolean modify(Event event) {
		if(!(event instanceof EntityDamageEvent)) return false;
		EntityDamageEvent Eevent = (EntityDamageEvent) event;
		
		Entity entity = Eevent.getEntity();
		if(!(entity instanceof Player)) return false;
		Player player = (Player) entity;
		if(RaceManager.getManager().getRaceOfPlayer(player.getName()) == raceContainer){
			if(getResistanceTypes().contains(Eevent.getCause())){
				notifyObservers(new HealthModifyContainer(player.getName(), getNewValue(Eevent.getDamage()), "damage"));
				setChanged();
				Eevent.setDamage(0);
				return true;
			}
		}
		
		return false;
	}
	
	private double getNewValue(int oldDmg){
		double newDmg = 0;
		switch(Operation){
			case "": newDmg = oldDmg * value; break;
			case "+": newDmg = oldDmg + value; break;
			case "-" : newDmg = oldDmg - value; break;
			case "*": newDmg = oldDmg * value; break;
			default:  newDmg = oldDmg * value; break;
		}
		
		if(newDmg < 0) newDmg = 0;
		return newDmg;
	}
	
	@Override
	public List<DamageCause> getResistanceTypes() {
		return resistances;
	}
}
