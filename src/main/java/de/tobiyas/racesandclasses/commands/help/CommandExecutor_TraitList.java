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
package de.tobiyas.racesandclasses.commands.help;

import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.commands.AbstractCommand;
import de.tobiyas.racesandclasses.traitcontainer.container.TraitsList;

public class CommandExecutor_TraitList extends AbstractCommand {

	public CommandExecutor_TraitList(){
		super("traitlist", new String[]{"tlist"});
		RacesAndClasses.getPlugin();
	}
	
	@Override
	public boolean onInternalCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		if(args.length == 0){
			postAllCategorys(sender);
			return true;
		}
		
		String page = "0";
		if(args.length == 1)
			page = args[0];
		
		HashSet<String> category = TraitsList.getCategory(page);
		
		sender.sendMessage(ChatColor.YELLOW + "======" + ChatColor.LIGHT_PURPLE + "Trait-List PAGE: " + page + ChatColor.YELLOW + "======");
		if(category == null)
			return true;
		for(String trait : category)
			sender.sendMessage(ChatColor.LIGHT_PURPLE + trait);
		
		return true;
	}
	
	private void postAllCategorys(CommandSender sender){
		sender.sendMessage(ChatColor.YELLOW + "======" + ChatColor.LIGHT_PURPLE + "Trait-Categories:" + ChatColor.YELLOW + "======");
		
		HashSet<String> categories = TraitsList.getAllCategories();
		for(String category : categories){
			sender.sendMessage(ChatColor.AQUA + category);
		}
	}

}
