package de.tobiyas.races.datacontainer.traitcontainer.traits.passive;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import de.tobiyas.races.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.events.EntityDamageByEntityDoubleEvent;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.events.EntityDamageDoubleEvent;
import de.tobiyas.races.datacontainer.traitcontainer.traits.Trait;

public class DamageReduce implements Trait{
	
	private double value;
	private String Operation;
	
	private RaceContainer raceContainer = null;
	private ClassContainer classContainer = null;

	public DamageReduce(RaceContainer raceContainer){
		this.raceContainer = raceContainer;
	}
	
	public DamageReduce(ClassContainer classContainer){
		this.classContainer = classContainer;
	}
	
	@TraitInfo(registerdClasses = {EntityDamageByEntityDoubleEvent.class})
	@Override
	public void generalInit(){
	}

	@Override
	public String getName() {
		return "DamageReduceTrait";
	}
	
	@Override
	public RaceContainer getRace(){
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
	public String getValueString(){
		return Operation + " " + String.valueOf(value);
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
		
		if(Eevent.getEntityType() != EntityType.PLAYER) return false;
		Player target = (Player) Eevent.getEntity();
		
		if(TraitHolderCombinder.checkContainer(target.getName(), this)){
			double newValue = getNewValue(Eevent.getDoubleValueDamage());
			Eevent.setDoubleValueDamage(newValue);
			return true;
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
	
	public static void pasteHelpForTrait(CommandSender sender) {
		sender.sendMessage(ChatColor.YELLOW + "Your taken damage will be decreased or divided by an value.");
		return;
	}
	
	@Override
	public boolean isVisible() {
		return true;
	}
	
	@Override
	public boolean isBetterThan(Trait trait) {
		if(!(trait instanceof DamageReduce)) return false;
		
		return value >= (double) trait.getValue();
	}
}
