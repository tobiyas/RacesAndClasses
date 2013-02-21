package de.tobiyas.races.datacontainer.traitcontainer.traits.passive;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

import de.tobiyas.races.datacontainer.eventmanagement.events.EntityHealEvent;
import de.tobiyas.races.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitcontainer.traits.Trait;

public class RegenerationTrait implements Trait {

	private RaceContainer raceContainer = null;
	private ClassContainer classContainer = null;
	
	private String Operation;
	private double value;
	
	public RegenerationTrait(){
	}
	
	@Override
	public void setRace(RaceContainer container) {
		this.raceContainer = container;
	}

	@Override
	public void setClazz(ClassContainer container) {
		this.classContainer = container;
	}
	
	@TraitInfo(registerdClasses = {EntityHealEvent.class})
	@Override
	public void generalInit(){
	}
	
	@Override
	public String getName() {
		return "RegenerationTrait";
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
		if(!(event instanceof EntityHealEvent)) return false;
		EntityHealEvent Eevent = (EntityHealEvent) event;
		
		if(Eevent.getEntityType() != EntityType.PLAYER) return false;
		Player player = (Player) Eevent.getEntity();
		if(!TraitHolderCombinder.checkContainer(player.getName(), this)) return false;
		
		if(Eevent.getRegainReason() == RegainReason.SATIATED){
			double amount = getNewValue(Eevent.getDoubleValueAmount());
			Eevent.setDoubleValueAmount(amount);
			return true;
		}
		return false;
	}
	
	private double getNewValue(double oldValue){
		double newValue = 0;
		switch(Operation){
			case "+": newValue = oldValue + value; break;
			case "-" : newValue = oldValue - value; break;
			case "*": newValue = oldValue * value; break;
			default:  newValue = oldValue * value; break;
		}
		
		if(newValue < 0) newValue = 0;
		return newValue;
	}
	
	public static void paistHelpForTrait(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "Nothing here yet :(");
	}
	
	@Override
	public boolean isVisible() {
		return true;
	}
	
	@Override
	public boolean isBetterThan(Trait trait) {
		if(!(trait instanceof RegenerationTrait)) return false;
		
		return value >= (Double) trait.getValue();
	}

}
