/*******************************************************************************
 * Copyright 2014 Tobias Welther
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.tobiyas.racesandclasses.listeners.commandcomplete;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;

import de.tobiyas.racesandclasses.RacesAndClasses;

public class CommandCompleteListener implements Listener {

	/**
	 * Plugin to register to
	 */
	private final RacesAndClasses plugin;
	
	public CommandCompleteListener() {
		plugin = RacesAndClasses.getPlugin();
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	
	@EventHandler
	public void onAutoComplete(PlayerChatTabCompleteEvent event){
		checkCommandAutocomplete(event);
	}


	private void checkCommandAutocomplete(PlayerChatTabCompleteEvent event) {
		String message = event.getChatMessage();
		if(message.contains(" ")){
			checkForFurtherComplete(event);		
		}else{
			checkAutoCompleteFirstCommand(event.getTabCompletions(), message);
		}
	}


	/**
	 * Checks for further completion of the Event.
	 * <br>NEEDED: event.getChatMessage contains " " (space).
	 * 
	 * @param event to check
	 */
	private void checkForFurtherComplete(PlayerChatTabCompleteEvent event) {
		String commandToFind = event.getChatMessage().split(" ")[0];
		
		String foundCommand = null;
		for(Entry<String, Map<String, Object>> possible : plugin.getDescription().getCommands().entrySet()){
			String commandName = possible.getKey();			
			if(commandToFind.equalsIgnoreCase(commandName)){
				foundCommand = commandName;
				break;
			}
			
			if(possible.getValue().containsKey("aliases")){
				Object aliases = possible.getValue().get("aliases");
				if(aliases instanceof String){
					if(commandToFind.equalsIgnoreCase((String)aliases)){
						foundCommand = commandName;
						break;
					}
				}
				
				if(aliases instanceof List){
					@SuppressWarnings("unchecked")
					List<String> aliasList = (List<String>) aliases;
					for(String aliasName : aliasList){
						if(commandToFind.equalsIgnoreCase(aliasName)){
							foundCommand = commandName;
							break;
						}
					}
				}
			}
		}
		
		if(foundCommand != null){
			commandFound(foundCommand, event);
			return;
		}
		
		
	}


	/**
	 * Checks the command for even more auto complete.
	 * 
	 * @param foundCommand the command found
	 * @param event to work with
	 */
	private void commandFound(String foundCommand,
			PlayerChatTabCompleteEvent event) {
		//TODO do autocomplete complete!
		
	}


	/**
	 * Checks if any command comes near message.
	 * 
	 * @param tabCompletions to add to
	 * @param message to check against
	 */
	private void checkAutoCompleteFirstCommand(
			Collection<String> tabCompletions, String message) {
		for(String command : plugin.getDescription().getCommands().keySet()){
			if(command.startsWith(message)){
				tabCompletions.add(command);
			}
		}
	}
	

}
