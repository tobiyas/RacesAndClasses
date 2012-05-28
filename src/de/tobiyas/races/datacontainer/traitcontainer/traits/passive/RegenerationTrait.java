package de.tobiyas.races.datacontainer.traitcontainer.traits.passive;

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
import de.tobiyas.races.datacontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.race.RaceManager;
import de.tobiyas.races.datacontainer.traitcontainer.Trait;
import de.tobiyas.races.datacontainer.traitcontainer.TraitEventManager;

public class RegenerationTrait extends Observable implements Trait {

	private RaceContainer raceContainer;
	private double value;
	
	public RegenerationTrait(RaceContainer raceContainer){
		this.raceContainer = raceContainer;
		TraitEventManager.getTraitEventManager().registerTrait(this);
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
		return String.valueOf(value);
	}

	@Override
	public void setValue(Object obj) {
		value = (Double) obj;
	}

	@Override
	public boolean modify(Event event) {
		if(!(event instanceof EntityRegainHealthEvent)) return false;
		EntityRegainHealthEvent Eevent = (EntityRegainHealthEvent) event;
		
		if(Eevent.getEntityType() != EntityType.PLAYER) return false;
		Player player = (Player) Eevent.getEntity();
		if(RaceManager.getManager().getRaceOfPlayer(player.getName()) != raceContainer) return false;
		
		if(Eevent.getRegainReason() == RegainReason.SATIATED){
			double amount = Eevent.getAmount() * value;
			Eevent.setAmount(0);
			notifyObservers(new HealthModifyContainer(player.getName(), amount, "heal"));
			setChanged();
			return true;
		}
		return false;
	}
	
	public static void paistHelpForTrait(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "Nothing here yet :(");
	}

}
