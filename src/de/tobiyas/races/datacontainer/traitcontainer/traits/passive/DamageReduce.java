package de.tobiyas.races.datacontainer.traitcontainer.traits.passive;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;

import de.tobiyas.races.datacontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitcontainer.Trait;
import de.tobiyas.races.datacontainer.traitcontainer.TraitEventManager;

public class DamageReduce implements Trait{
	
	private double value;
	private RaceContainer raceContainer;

	public DamageReduce(RaceContainer raceContainer){
		this.raceContainer = raceContainer;
		TraitEventManager.getTraitEventManager().registerTrait(this);
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
	public Object getValue() {
		return value;
	}
	
	@Override
	public String getValueString(){
		return String.valueOf(value);
	}

	@Override
	public void setValue(Object value) {
		this.value = (Double) value;
	}

	@Override
	public boolean modify(Event event) {
		if(!(event instanceof EntityDamageEvent)) return false;
		EntityDamageEvent Eevent = (EntityDamageEvent) event;
		
		if(Eevent.getEntityType() != EntityType.PLAYER) return false;
		Player target = (Player) Eevent.getEntity();
		
		if(raceContainer.containsPlayer(target.getName())){
			int newValue = (int) Math.ceil(Eevent.getDamage() * value);
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
