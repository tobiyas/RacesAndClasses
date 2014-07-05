package de.tobiyas.racesandclasses.commands;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public abstract class AbstractCommand implements CommandInterface {

	
	/**
	 * The Command name to check.
	 */
	private final String commandName;
	
	/**
	 * The Aliases to use for the Command	
	 */
	private final String[] aliases;
	
	
	public AbstractCommand(String commandName) {
		this.commandName = commandName;
		this.aliases = new String[0];
	}

	
	public AbstractCommand(String commandName, String[] aliases) {
		this.commandName = commandName;
		this.aliases = aliases;
	}

	
	/**
	 * Returns the CommandName
	 */
	public String getCommandName(){
		return commandName;
	}
	
	/**
	 * Returns the aliases
	 */
	public String[] getAliases(){
		return aliases;
	}
	
	/**
	 * if the Command has aliases.
	 * 
	 * @return
	 */
	public boolean hasAliases(){
		return aliases != null && aliases.length > 0;
	}


	@Override
	public List<String> onTabComplete(CommandSender sender, Command command,
			String alias, String[] args) {
		//stub method to not having to override in every impl.
		return new LinkedList<String>();
	}
	
	
	
}
