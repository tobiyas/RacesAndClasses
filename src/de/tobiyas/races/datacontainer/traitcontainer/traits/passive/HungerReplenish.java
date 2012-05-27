package de.tobiyas.races.datacontainer.traitcontainer.traits.passive;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import de.tobiyas.races.datacontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.race.RaceManager;
import de.tobiyas.races.datacontainer.traitcontainer.Trait;
import de.tobiyas.races.datacontainer.traitcontainer.TraitEventManager;

public class HungerReplenish implements Trait {
	
	private double value;
	private RaceContainer raceContainer;
	
	public HungerReplenish(RaceContainer raceContainer){
		this.raceContainer = raceContainer;
		TraitEventManager.getTraitEventManager().registerTrait(this);
	}

	@Override
	public String getName() {
		return "HungerReplenishTrait";
	}
	
	@Override
	public RaceContainer getRace(){
		return raceContainer;
	}

	@Override
	public Object getValue() {
		return value;
	}
	
	@Override
	public String getValueString(){
		return String.valueOf(value);
	}

	@Override
	public void setValue(Object obj) {
		value = (Double) obj;
	}

	@Override
	public boolean modify(Event event) {
		if(!(event instanceof FoodLevelChangeEvent)) return false;
		FoodLevelChangeEvent Eevent = (FoodLevelChangeEvent) event;
		if(!(Eevent.getEntity() instanceof Player)) return false;
		
		Player player = (Player) Eevent.getEntity();
		if(RaceManager.getManager().getRaceOfPlayer(player.getName()).equals(raceContainer)){
			int orgValue = player.getFoodLevel();
			int newValue = Eevent.getFoodLevel();
			
			if(newValue > orgValue){
				int newCalcValue = (int) Math.ceil(((newValue - orgValue) * value) + orgValue);
				Eevent.setFoodLevel(newCalcValue);
				return true;
			}
		}
		
		return false;
	}
	
	public static void paistHelpForTrait(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "Nothing to see yet.");
		return;
	}

}
