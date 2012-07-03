package de.tobiyas.races.datacontainer.traitcontainer.traits.resistance;

import java.util.List;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import de.tobiyas.races.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.events.EntityDamageDoubleEvent;
import de.tobiyas.races.datacontainer.traitcontainer.traits.Trait;

public abstract class Resistance implements ResistanceInterface {

	protected List<DamageCause> resistances;
	protected RaceContainer raceContainer = null;
	protected ClassContainer classContainer = null;
	
	protected double value;
	protected String Operation = "";
	
	
	@TraitInfo(registerdClasses = {EntityDamageDoubleEvent.class})
	@Override
	public void generalInit(){
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
				
				Eevent.setDoubleValueDamage(newDmg);
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
	
	@Override
	public boolean isBetterThan(Trait trait){
		if(trait.getClass() != this.getClass()) return false;
		
		return value >= (double) trait.getValue();
	}
}
