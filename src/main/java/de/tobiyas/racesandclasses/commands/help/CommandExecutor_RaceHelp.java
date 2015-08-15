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
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.commands.AbstractCommand;
import de.tobiyas.racesandclasses.traitcontainer.container.TraitsList;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.util.chat.components.ChatMessageObject;
import de.tobiyas.util.chat.components.TellRawChatMessage;
import de.tobiyas.util.vollotile.VollotileCodeManager;

public class CommandExecutor_RaceHelp extends AbstractCommand{
	
	public CommandExecutor_RaceHelp(){
		super("racehelp", new String[]{"rac", "rachelp", "rhelp"});
		RacesAndClasses.getPlugin();
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
		int page = -1;
		try{ page = Integer.parseInt(commandString); }catch(Throwable exp){}
		
		if(commandString.equalsIgnoreCase("page") || page > 0){
			if(args.length > 1 && page <= 0){
				try{
					page = Integer.valueOf(args[1]);
				}catch(NumberFormatException e){
					sender.sendMessage(ChatColor.RED + "The Page-Number must be an Integer value.");
					return true;
				}
			}
			
			//Set to 1 if needed.
			if(page <= 0) page = 1;
			
			String[] messages = HelpPage.getPageContent(senderName, page).toArray(new String[0]);
			sender.sendMessage(messages);
			
			if(VollotileCodeManager.getVollotileCode().getVersion().hasTellRawSupport() 
					&& sender instanceof Player){
				Player player = (Player) sender;
				
				int pageBefore = page - 1;
				int pageAfter = page + 1;
				
				boolean pageBeforeExist = page > 1;
				boolean pageAfterExist = page < HelpPage.getLastPageIndex();
				
				TellRawChatMessage message = new TellRawChatMessage();
				if(pageBeforeExist) message.append(new ChatMessageObject("page " + pageBefore).addCommandClickable("rachelp " + pageBefore).addPopupHover("Go to page " + pageBefore));
				else message.addSimpleText("     ");
				
				message.addSimpleText("                             ");
				
				if(pageAfterExist) message.append(new ChatMessageObject("page " + pageAfter).addCommandClickable("rachelp " + pageAfter).addPopupHover("Go to page " + pageAfter));
				
				message.sendToPlayers(player);
			}
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
