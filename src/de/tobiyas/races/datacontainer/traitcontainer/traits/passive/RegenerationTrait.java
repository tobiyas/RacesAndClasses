package de.tobiyas.races.datacontainer.traitcontainer.traits.passive;

import java.util.HashSet;
import java.util.Observable;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import de.tobiyas.races.datacontainer.health.HealthManager;
import de.tobiyas.races.datacontainer.health.HealthModifyContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassManager;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceManager;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.TraitEventManager;
import de.tobiyas.races.datacontainer.traitcontainer.traits.Trait;

public class RegenerationTrait extends Observable implements Trait {

	private RaceContainer raceContainer = null;
	private ClassContainer classContainer = null;
	
	private String Operation;
	private double value;
	
	public RegenerationTrait(RaceContainer raceContainer){
		this.raceContainer = raceContainer;
	}
	
	public RegenerationTrait(ClassContainer classContainer){
		this.classContainer = classContainer;
	}
	
	@Override
	public void generalInit(){
		HashSet<Class<?>> listenedEvents = new HashSet<Class<?>>();
		listenedEvents.add(EntityRegainHealthEvent.class);
		TraitEventManager.getInstance().registerTrait(this, listenedEvents);
		
		addObserver(HealthManager.getHealthManager());
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
		if(!(event instanceof EntityRegainHealthEvent)) return false;
		EntityRegainHealthEvent Eevent = (EntityRegainHealthEvent) event;
		
		if(Eevent.getEntityType() != EntityType.PLAYER) return false;
		Player player = (Player) Eevent.getEntity();
		if(!checkContainer(player.getName())) return false;
		
		if(Eevent.getRegainReason() == RegainReason.SATIATED){
			double amount = getNewValue(Eevent.getAmount());
			Eevent.setAmount(0);
			notifyObservers(new HealthModifyContainer(player.getName(), amount, "heal"));
			setChanged();
			return true;
		}
		return false;
	}
	
	private boolean checkContainer(String playerName){
		if(raceContainer != null){
			RaceContainer container = RaceManager.getManager().getRaceOfPlayer(playerName);
			if(container == null) return true;
			return raceContainer == container;
		}
		if(classContainer != null){
			ClassContainer container = ClassManager.getInstance().getClassOfPlayer(playerName);
			if(container == null) return true;
			return classContainer == container;
		}
		
		return false;
	}
	
	private double getNewValue(int oldValue){
		double newValue = 0;
		switch(Operation){
			case "": newValue = oldValue * value; break;
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

}
