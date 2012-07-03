package de.tobiyas.races.datacontainer.traitcontainer.traits.passive;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.events.EntityDamageByEntityDoubleEvent;
import de.tobiyas.races.datacontainer.traitcontainer.traits.Trait;
import de.tobiyas.races.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;

public class ArrowDamageIncrease implements Trait {

	private double value;
	private String operation;
	
	private RaceContainer raceContainer = null;
	private ClassContainer classContainer = null;
	
	public ArrowDamageIncrease(RaceContainer raceContainer){
		this.raceContainer = raceContainer;
	}
	
	public ArrowDamageIncrease(ClassContainer classContainer){
		this.classContainer = classContainer;
	}
	
	@TraitInfo(registerdClasses = {EntityDamageByEntityDoubleEvent.class})
	@Override
	public void generalInit() {
	}

	@Override
	public String getName() {
		return "ArrowDamageIncreaseTrait";
	}

	@Override
	public RaceContainer getRace() {
		return raceContainer;
	}

	@Override
	public ClassContainer getClazz() {
		return classContainer;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public String getValueString() {
		return "damage: " + operation + " " + value;
	}

	@Override
	public void setValue(Object obj) {
		String opAndVal = String.valueOf(obj);
		value = evaluateValue(opAndVal);
	}
	
	private double evaluateValue(String val){
		char firstChar = val.charAt(0);
		operation = "";
		
		if(firstChar == '+')
			operation = "+";
		
		if(firstChar == '*')
			operation = "*";
		
		if(firstChar == '-')
			operation = "-";
		
		if(operation == "")
			operation = "*";
		else
			val = val.substring(1, val.length());
		
		double newVal = Double.valueOf(val);
		return newVal;
	}

	@Override
	public boolean modify(Event event) {
		if(!(event instanceof EntityDamageByEntityDoubleEvent)) return false;
		EntityDamageByEntityDoubleEvent Eevent = (EntityDamageByEntityDoubleEvent) event;
		if(Eevent.getDamager().getType() != EntityType.ARROW) return false;
		Arrow arrow = (Arrow) Eevent.getDamager();
		if(arrow.getShooter() == null || arrow.getShooter().getType() != EntityType.PLAYER) return false;
		Player shooter = (Player) arrow.getShooter();
		
		if(TraitHolderCombinder.checkContainer(shooter.getName(), this)){
			double newValue = getNewValue(Eevent.getDoubleValueDamage());
			Eevent.setDoubleValueDamage(newValue);
			return true;
		}
		return false;
	}
	
	private double getNewValue(double oldDmg){
		double newDmg = 0;
		switch(operation){
			case "+": newDmg = oldDmg + value; break;
			case "-" : newDmg = oldDmg - value; break;
			case "*": newDmg = oldDmg * value; break;
			default:  newDmg = oldDmg * value; break;
		}
		
		if(newDmg < 0) newDmg = 0;
		return newDmg;
	}

	@Override
	public boolean isVisible() {
		return true;
	}

	@Override
	public boolean isBetterThan(Trait trait) {
		
		return false;
	}

}
