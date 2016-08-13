package de.tobiyas.racesandclasses.commands.group;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.commands.AbstractCommand;
import de.tobiyas.util.autocomplete.AutoCompleteUtils;

public class Command_Group extends AbstractCommand {

	
	/**
	 * The Plugin to use.
	 */
	@SuppressWarnings("unused")
	private final RacesAndClasses plugin;
	
	
	public Command_Group(RacesAndClasses plugin) {
		super("group", "gr");
		
		this.plugin = plugin;
	}
	
	
	
	@Override
	public boolean onInternalCommand(CommandSender sender, Command command, String label, String[] args) {
		//TODO implement me!
		return false;
	}
	
	
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> subs = new LinkedList<>();
		subs.add("invite");
		subs.add("kick");
		subs.add("leave");
		
		if(args.length == 1) return AutoCompleteUtils.getAllNamesWith(subs, args[0]);
		
		return super.onTabComplete(sender, command, alias, args);
	}

}
