package de.tobiyas.races.datacontainer.traitcontainer.traits.passive;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.races.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.events.EntityDamageByEntityDoubleEvent;
import de.tobiyas.races.datacontainer.traitcontainer.traits.Trait;

public class SwordDamageIncrease implements Trait {
	
	private double value;
	private String operation;
	
	private RaceContainer raceContainer = null;
	private ClassContainer classContainer = null;
	
	public SwordDamageIncrease(RaceContainer raceContainer){
		this.raceContainer = raceContainer;
	}
	
	public SwordDamageIncrease(ClassContainer classContainer){
		this.classContainer = classContainer;
	}
	
	@TraitInfo(registerdClasses = {EntityDamageByEntityDoubleEvent.class})
	@Override
	public void generalInit(){
	}

	@Override
	public String getName() {
		return "SwordDamageIncreaseTrait";
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
		if(!(event instanceof EntityDamageByEntityDoubleEvent)) return false;
		
		EntityDamageByEntityDoubleEvent Eevent = (EntityDamageByEntityDoubleEvent) event;
		if(!(Eevent.getDamager() instanceof Player)) return false;
		Player causer = (Player) Eevent.getDamager();
 		
		if(TraitHolderCombinder.checkContainer(causer.getName(), this)){
			if(!checkItemIsSword(causer.getItemInHand())) return false;
			double newValue = getNewValue(Eevent.getDoubleValueDamage());
			Eevent.setDoubleValueDamage(newValue);
			return true;
		}
		return false;
	}
	
	private boolean checkItemIsSword(ItemStack stack){
		Material item = stack.getType();
		if(item == Material.WOOD_SWORD)
			return true;
		
		if(item == Material.STONE_SWORD)
			return true;
		
		if(item == Material.GOLD_SWORD)
			return true;
		
		if(item == Material.IRON_SWORD)
			return true;
		
		if(item == Material.DIAMOND_SWORD)
			return true;
			
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
	
	public static void pasteHelpForTrait(CommandSender sender) {
		sender.sendMessage(ChatColor.YELLOW + "Your Damage will be increased by a value or times an value.");
		return;
	}
	
	@Override
	public boolean isVisible() {
		return true;
	}
	
	@Override
	public boolean isBetterThan(Trait trait) {
		if(!(trait instanceof SwordDamageIncrease)) return false;
		
		return value >= (double) trait.getValue();
	}

}
