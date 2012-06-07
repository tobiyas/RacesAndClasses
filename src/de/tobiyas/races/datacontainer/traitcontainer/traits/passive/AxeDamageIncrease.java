package de.tobiyas.races.datacontainer.traitcontainer.traits.passive;

import java.util.HashSet;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassManager;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceManager;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.TraitEventManager;
import de.tobiyas.races.datacontainer.traitcontainer.traits.Trait;

public class AxeDamageIncrease implements Trait {
	
	private double value;
	private String operation;
	
	private RaceContainer raceContainer = null;
	private ClassContainer classContainer = null;
	
	public AxeDamageIncrease(RaceContainer raceContainer){
		this.raceContainer = raceContainer;
	}
	
	public AxeDamageIncrease(ClassContainer classContainer){
		this.classContainer = classContainer;
	}
	
	@Override
	public void generalInit(){
		HashSet<Class<?>> listenedEvents = new HashSet<Class<?>>();
		listenedEvents.add(EntityDamageByEntityEvent.class);
		TraitEventManager.getInstance().registerTrait(this, listenedEvents);
	}

	@Override
	public String getName() {
		return "AxeDamageIncreaseTrait";
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
		return operation + " " +  String.valueOf(value);
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
		
		return Double.valueOf(val);
	}

	@Override
	public boolean modify(Event event) {
		if(!(event instanceof EntityDamageByEntityEvent)) return false;
		
		EntityDamageByEntityEvent Eevent = (EntityDamageByEntityEvent) event;
		if(!(Eevent.getDamager() instanceof Player)) return false;
		Player causer = (Player) Eevent.getDamager();
 		
		if(checkContainer(causer.getName())){
			if(!checkItemIsSword(causer.getItemInHand())) return false;
			int newValue = (int) Math.ceil(getNewValue(Eevent.getDamage()));
			Eevent.setDamage(newValue);
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
	
	private boolean checkItemIsSword(ItemStack stack){
		Material item = stack.getType();
		if(item == Material.WOOD_AXE)
			return true;
		
		if(item == Material.STONE_AXE)
			return true;
		
		if(item == Material.GOLD_AXE)
			return true;
		
		if(item == Material.IRON_AXE)
			return true;
		
		if(item == Material.DIAMOND_AXE)
			return true;
			
		return false;
	}
	
	private double getNewValue(int oldDmg){
		double newDmg = 0;
		switch(operation){
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
	public boolean isVisible() {
		return true;
	}

}
