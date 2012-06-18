package de.tobiyas.races.datacontainer.traitcontainer.traits.resistance;

import java.util.HashSet;
import java.util.List;
import java.util.Observable;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import de.tobiyas.races.datacontainer.health.HealthManager;
import de.tobiyas.races.datacontainer.health.HealthModifyContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.TraitEventManager;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.events.EntityDamageDoubleEvent;

public abstract class Resistance extends Observable implements ResistanceInterface {

	protected List<DamageCause> resistances;
	protected RaceContainer raceContainer = null;
	protected ClassContainer classContainer = null;
	
	protected double value;
	protected String Operation = "";
	
	
	@Override
	public void generalInit(){
		HashSet<Class<?>> listenedEvents = new HashSet<Class<?>>();
		listenedEvents.add(EntityDamageDoubleEvent.class);
		TraitEventManager.getInstance().registerTrait(this, listenedEvents);
		
		addObserver(HealthManager.getHealthManager());
	}
	
	@Override
	public abstract String getName();

	@Override
	public RaceContainer getRace() {
		return raceContainer;
	}
	
	@Override
	public ClassContainer getClazz() {
		return classContainer;
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
		if(!(event instanceof EntityDamageDoubleEvent)) return false;
		EntityDamageDoubleEvent Eevent = (EntityDamageDoubleEvent) event;
		
		Entity entity = Eevent.getEntity();
		if(!(entity instanceof Player)) return false;
		Player player = (Player) entity;
		if(TraitHolderCombinder.checkContainer(player.getName(), this)){
			if(getResistanceTypes().contains(Eevent.getCause())){
				double oldDmg = Eevent.getDoubleValueDamage();
				double newDmg = getNewValue(oldDmg);
				
				notifyObservers(new HealthModifyContainer(player.getName(), newDmg, "damage"));
				setChanged();
				Eevent.setDamage(0);
				return true;
			}
		}
		
		return false;
	}
	
	private double getNewValue(double oldDmg){
		double newDmg = 0;
		switch(Operation){
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
