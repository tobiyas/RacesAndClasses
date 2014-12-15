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

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.commands.AbstractCommand;
import de.tobiyas.racesandclasses.traitcontainer.container.TraitsList;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;

public class CommandExecutor_RaceHelp extends AbstractCommand{
	
	public CommandExecutor_RaceHelp(){
		super("racehelp", new String[]{"rac", "rachelp", "rhelp"});
		RacesAndClasses.getPlugin();

//		String command = "racehelp";
//		if(plugin.getConfigManager().getGeneralConfig().getConfig_general_disable_commands().contains(command)) return;
//		
//		try{
//			plugin.getCommand(command).setExecutor(this);
//		}catch(Exception e){
//			plugin.log("ERROR: Could not register command /" + command + ".");
//		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		String senderName = sender.getName();
		if(sender == Bukkit.getConsoleSender()){
			senderName = "console";
		}
		
		if(args.length == 0){
			String[] messages = HelpPage.getPageContent(senderName, 1).toArray(new String[0]);
			sender.sendMessage(messages);
			return true;
		}
		
		String commandString = args[0];
		
		if(commandString.equalsIgnoreCase("page")){
			int page = 1;
			if(args.length > 1)
				try{
					page = Integer.valueOf(args[1]);
				}catch(NumberFormatException e){
					sender.sendMessage(ChatColor.RED + "The Page-Number must be an Integer value.");
					return true;
				}
			String[] messages = HelpPage.getPageContent(senderName, page).toArray(new String[0]);
			sender.sendMessage(messages);
			return true;
		}
		
		if(commandString.equalsIgnoreCase("trait")){
			if(args.length > 1)
				outTraitHelp(sender, args[1]);
			else
				sender.sendMessage(ChatColor.RED + "The command: " + ChatColor.AQUA + "/racehelp trait <trait-Name>" + ChatColor.RED + " needs an trait-Name as argument!");
			return true;
		}
		
		String[] messages = HelpPage.getCategoryPage(senderName, commandString).toArray(new String[0]);
		sender.sendMessage(messages);
		return true;
	}
	
	private void outTraitHelp(CommandSender sender, String trait){		
		Class<? extends Trait> clazz = TraitsList.getClassOfTrait(trait);
		if(clazz == null){
			sender.sendMessage(ChatColor.RED + "Trait: " + ChatColor.LIGHT_PURPLE + trait + ChatColor.RED + " not found.");
			return;
		}
		
		try{
			sender.sendMessage(ChatColor.YELLOW + "===Trait: " + ChatColor.YELLOW + trait + ChatColor.YELLOW + "===");
			@SuppressWarnings("unchecked")
			List<String> helpList = (List<String>) clazz.getMethod("getHelpForTrait").invoke(clazz);
			
			for(String line : helpList){
				sender.sendMessage(line);
			}
		}catch(Exception e){
			sender.sendMessage(ChatColor.RED + "Trait: " + ChatColor.LIGHT_PURPLE + trait + ChatColor.RED + " has no Help");
			return;
		}
		
		return;
	}

}
