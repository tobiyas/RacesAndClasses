package de.tobiyas.races.datacontainer.traitcontainer.traits.resistance;

import java.util.LinkedList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import de.tobiyas.races.datacontainer.eventmanagement.events.EntityDamageDoubleEvent;

public class FireResistanceTrait extends Resistance {
	
	public FireResistanceTrait(){
	}

	
	@TraitInfo(registerdClasses = {EntityDamageDoubleEvent.class})
	@Override
	public void generalInit(){
		resistances = new LinkedList<DamageCause>();
		resistances.add(DamageCause.FIRE);
		resistances.add(DamageCause.FIRE_TICK);
	}
	
	@Override
	public String getName() {
		return "FireResistanceTrait";
	}

	@Override
	public Object getValue() {
		return value;
	}
	
	public static void pasteHelpForTrait(CommandSender sender) {
		sender.sendMessage(ChatColor.YELLOW + "You get less damage from Fire related DamageSources.");
	}
	
	@Override
	public boolean isVisible() {
		return true;
	}

}
