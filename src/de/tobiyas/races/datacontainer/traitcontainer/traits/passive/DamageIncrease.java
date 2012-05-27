package de.tobiyas.races.datacontainer.traitcontainer.traits.passive;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import de.tobiyas.races.datacontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitcontainer.Trait;
import de.tobiyas.races.datacontainer.traitcontainer.TraitEventManager;

public class DamageIncrease implements Trait {
	
	private double value;
	private RaceContainer raceContainer;

	public DamageIncrease(RaceContainer raceContainer){
		this.raceContainer = raceContainer;
		TraitEventManager.getTraitEventManager().registerTrait(this);
	}

	@Override
	public String getName() {
		return "DamageIncreaseTrait";
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
		if(!(event instanceof EntityDamageByEntityEvent)) return false;
		
		EntityDamageByEntityEvent Eevent = (EntityDamageByEntityEvent) event;
		if(!(Eevent.getDamager() instanceof Player)) return false;
		Player causer = (Player) Eevent.getDamager();
 		
		if(raceContainer.containsPlayer(causer.getName())){
			int newValue = (int) Math.ceil(Eevent.getDamage() * value);
			System.out.println("DMG increase: oldValue: " + Eevent.getDamage() + " new: " + newValue + " inner" + "From: " + ((Player) Eevent.getDamager()).getName() + " to: " + Eevent.getEntityType().getName() + " EventID: " + Eevent.toString());
			Eevent.setDamage(newValue);
			return true;
		}
		return false;
	}
	
	public static void paistHelpForTrait(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "Nothing to see yet.");
		return;
	}

}
