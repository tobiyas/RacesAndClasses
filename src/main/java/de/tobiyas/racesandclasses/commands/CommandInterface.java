package de.tobiyas.racesandclasses.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

public interface CommandInterface extends CommandExecutor, TabCompleter {

	
	/**
	 * Returns the Command name.
	 */
	public String getCommandName();
	
	/**
	 * Returns the aliases
	 */
	public String[] getAliases();
	
	/**
	 * if the Command has aliases.
	 * 
	 * @return
	 */
	public boolean hasAliases();
}
