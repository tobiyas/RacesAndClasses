package de.tobiyas.racesandclasses.commands;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import de.tobiyas.racesandclasses.RacesAndClasses;

public abstract class AbstractCommand implements CommandInterface {

	
	/**
	 * The Command names to check.
	 */
	protected final Collection<String> commandNames = new HashSet<String>();
	
	/**
	 * The Aliases to use for the Command	
	 */
	protected final Collection<String> aliases = new HashSet<String>();
	
	/**
	 * THe plugin to use for post stuff.
	 */
	protected final RacesAndClasses plugin = RacesAndClasses.getPlugin();
	
	
	public AbstractCommand(String commandName) {
		this.commandNames.add(commandName);
	}
	
	
	public AbstractCommand(String commandName, String... aliases) {
		this.commandNames.add(commandName);
		this.aliases.addAll(Arrays.asList(aliases));
	}

	
	/**
	 * Returns the CommandName
	 */
	@Override
	public Collection<String> getCommandNames(){
		return commandNames;
	}
	
	/**
	 * Returns the aliases
	 */
	@Override
	public String[] getAliases(){
		return aliases.toArray(new String[aliases.size()]);
	}
	
	/**
	 * if the Command has aliases.
	 * 
	 * @return
	 */
	@Override
	public boolean hasAliases(){
		return aliases != null && aliases.size() > 0;
	}


	@Override
	public List<String> onTabComplete(CommandSender sender, Command command,
			String alias, String[] args) {
		//stub method to not having to override in every impl.
		return new LinkedList<String>();
	}
	
	
	@Override
	public void filterToDisabledCommands(Collection<String> disabled) {
		for(String name : disabled){
			//filter aliases:
			Iterator<String> it = aliases.iterator();
			while(it.hasNext()) {
				if(name.equalsIgnoreCase(it.next()))  {
					it.remove();
				}
			}
			
			//filter commands:
			it = commandNames.iterator();
			while(it.hasNext()){
				if(name.equalsIgnoreCase(it.next())){
					it.remove();
				}
			}
		}
	}
	
	@Override
	public boolean hasAnyCommand() {
		return !commandNames.isEmpty();
	}
	
	
}
