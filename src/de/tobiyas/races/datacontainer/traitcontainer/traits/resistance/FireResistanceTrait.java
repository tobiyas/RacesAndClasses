package de.tobiyas.races.datacontainer.traitcontainer.traits.resistance;

import java.util.LinkedList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import de.tobiyas.races.datacontainer.health.HealthManager;
import de.tobiyas.races.datacontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitcontainer.TraitEventManager;

public class FireResistanceTrait extends Resistance {
	
	public FireResistanceTrait(RaceContainer container){
		this.raceContainer = container;
		TraitEventManager.getTraitEventManager().registerTrait(this);
		
		resistances = new LinkedList<DamageCause>();
		resistances.add(DamageCause.FIRE);
		resistances.add(DamageCause.FIRE_TICK);
		
		addObserver(HealthManager.getHealthManager());
	}
	
	@Override
	public String getName() {
		return "FireResistanceTrait";
	}

	@Override
	public Object getValue() {
		return value;
	}
	
	public static void paistHelpForTrait(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "Nothing here yet.");
		//TODO
	}

}
