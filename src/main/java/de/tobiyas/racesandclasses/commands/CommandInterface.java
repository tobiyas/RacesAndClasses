package de.tobiyas.racesandclasses.commands;

import java.util.Collection;
import java.util.Map;

import org.bukkit.command.TabExecutor;

public interface CommandInterface extends TabExecutor {

	
	/**
	 * Returns the Command name.
	 */
	public Collection<String> getCommandNames();
	
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
	
	
	/**
	 * Removes everything disabled.
	 * 
	 * @param disabled to remove.
	 */
	public void filterToDisabledCommands(Collection<String> disabled);
	
	
	/**
	 * Does a Remapping to the Command.
	 * 
	 * @param remaps to apply.
	 */
	public void applyRemapping(Map<String,String> remaps);
	
	
	/**
	 * if there is any command to activate.
	 * 
	 * @return true if there is any command.
	 */
	public boolean hasAnyCommand();

	/**
	 * Returns the Description.
	 * 
	 * @return the Description
	 */
	public String getDescription();
	
}
