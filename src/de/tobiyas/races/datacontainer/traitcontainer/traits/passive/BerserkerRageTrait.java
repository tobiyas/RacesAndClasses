package de.tobiyas.races.datacontainer.traitcontainer.traits.passive;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import de.tobiyas.races.Races;
import de.tobiyas.races.configuration.traits.TraitConfig;
import de.tobiyas.races.configuration.traits.TraitConfigManager;
import de.tobiyas.races.datacontainer.health.HealthManager;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.events.EntityDamageByEntityDoubleEvent;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.events.EntityDamageDoubleEvent;
import de.tobiyas.races.datacontainer.traitcontainer.traits.Trait;
import de.tobiyas.races.datacontainer.traitcontainer.traits.TraitsWithUplink;
import de.tobiyas.races.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;

public class BerserkerRageTrait implements TraitsWithUplink {

	private double value;
	private String Operation;
	
	private RaceContainer raceContainer = null;
	private ClassContainer classContainer = null;
	
	private HashMap<String, Integer> uplinkMap = new HashMap<String, Integer>();
	
	private static int uplinkTime = 60*20;
	private static int duration = 10*20;
	private static double activationLimit = 30;
	

	public BerserkerRageTrait(RaceContainer raceContainer){
		this.raceContainer = raceContainer;
	}
	
	public BerserkerRageTrait(ClassContainer classContainer){
		this.classContainer = classContainer;
	}
	
	@TraitInfo(registerdClasses = {EntityDamageDoubleEvent.class}) //TODO check if need EntityDamageEvent needed.
	@Override
	public void generalInit() {		
		TraitConfig config = TraitConfigManager.getInstance().getConfigOfTrait(getName());
		if(config != null){
			uplinkTime = (int) config.getValue("trait.uplink", 60) * 20;
			duration = (int) config.getValue("trait.duration", 10) * 20;
			activationLimit = config.getDouble("trait.activationLimit", 30);
		}
	}

	@Override
	public String getName() {
		return "BerserkerRageTrait";
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
		return "damage increase: " + Operation + value;
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
		//Handle activation
		if(event instanceof EntityDamageDoubleEvent){
			Entity entity = null;
			
			if(event instanceof EntityDamageDoubleEvent)
				entity = ((EntityDamageDoubleEvent) event).getEntity();
			
			if(entity.getType() != EntityType.PLAYER) return false;
			Player player = (Player) entity;
			
			if(TraitHolderCombinder.checkContainer(player.getName(), this)){
				double maxHealth = HealthManager.getHealthManager().getMaxHealthOfPlayer(player.getName());
				double currentHealth =  HealthManager.getHealthManager().getHealthOfPlayer(player.getName());
				double healthPercent = 100 * currentHealth / maxHealth;
				if(healthPercent > activationLimit) return false;
				
				checkUplinkAndActive(player, false);
				return false;
			}
		}
		
		if(event instanceof EntityDamageByEntityDoubleEvent){			
			EntityDamageByEntityDoubleEvent Eevent = (EntityDamageByEntityDoubleEvent) event;
			Entity entity = Eevent.getDamager();
			if(entity instanceof Arrow) 
				entity = ((Arrow) entity).getShooter();
			
			if(entity == null || entity.getType() != EntityType.PLAYER) return false;
			Player player = (Player) entity;
			
			if(TraitHolderCombinder.checkContainer(player.getName(), this)){
				if(!checkIfActive(player)) return false;
				double newValue = getNewValue(Eevent.getDoubleValueDamage());
				Eevent.setDoubleValueDamage(newValue);
				return true;
			}
		}
		
		return false;
	}
	
	private boolean checkUplinkAndActive(Player player, boolean notify){
		if(uplinkMap.containsKey(player.getName())){
			return false;
		}
		
		uplinkMap.put(player.getName(), uplinkTime + duration);
		player.sendMessage(ChatColor.LIGHT_PURPLE + getName() + ChatColor.GREEN + " toggled.");
		return false;
	}
	
	private boolean checkIfActive(Player player){
		String playerName = player.getName();
		
		if(!uplinkMap.containsKey(playerName)) return false;
		int remainingTime = uplinkMap.get(playerName);
		return (remainingTime - uplinkTime) > 0;
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
	public boolean isVisible() {
		return true;
	}

	@Override
	public void tickReduceUplink() {
		for(String player : uplinkMap.keySet()){
			int remainingTime = uplinkMap.get(player);
			remainingTime -= Races.getPlugin().interactConfig().getconfig_globalUplinkTickPresition();
			
			if(remainingTime == uplinkTime){
				Player tempPlayer = Bukkit.getPlayer(player);
				if(tempPlayer != null)
					tempPlayer.sendMessage(ChatColor.LIGHT_PURPLE + getName() + ChatColor.RED + " has faded.");
			}
				
			if(remainingTime <= 0)
				uplinkMap.remove(player);
			else
				uplinkMap.put(player, remainingTime);
		}
	}

	@Override
	public boolean isBetterThan(Trait trait) {
		if(!(trait instanceof BerserkerRageTrait)) return false;
		
		return value >= (double) trait.getValue();
	}

}
