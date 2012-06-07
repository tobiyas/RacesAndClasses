package de.tobiyas.races.datacontainer.traitcontainer.traits.resistance;

import java.util.LinkedList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;

public class DrainResistanceTrait extends Resistance{
	
	public DrainResistanceTrait(RaceContainer raceContainer){
		this.raceContainer = raceContainer;
		privateInit();
	}
	
	public DrainResistanceTrait(ClassContainer classContainer){
		this.classContainer = classContainer;
		privateInit();
	}
	
	private void privateInit(){
		resistances = new LinkedList<DamageCause>();
		resistances.add(DamageCause.DROWNING);
	}

	@Override
	public String getName() {
		return "DrainResistanceTrait";
	}

	@Override
	public Object getValue() {
		return value;
	}
	
	public static void pasteHelpForTrait(CommandSender sender) {
		sender.sendMessage(ChatColor.YELLOW + "You get less damage from draining."); 
	}
	
	@Override
	public boolean isVisible() {
		return true;
	}

}
