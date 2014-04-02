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
package de.tobiyas.racesandclasses.commands.tutorial;

import static de.tobiyas.racesandclasses.translation.languages.Keys.tutorial_already_running;
import static de.tobiyas.racesandclasses.translation.languages.Keys.tutorial_error;
import static de.tobiyas.racesandclasses.translation.languages.Keys.tutorial_no_set_at_this_state;
import static de.tobiyas.racesandclasses.translation.languages.Keys.tutorial_not_running;
import static de.tobiyas.racesandclasses.translation.languages.Keys.tutorial_stopped;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.util.tutorial.TutorialState;

public class CommandExecutor_RacesTutorial implements CommandExecutor{

	private RacesAndClasses plugin;
	
	public CommandExecutor_RacesTutorial(){
		plugin = RacesAndClasses.getPlugin();

		String command = "racestutorial";
		if(plugin.getConfigManager().getGeneralConfig().getConfig_general_disable_commands().contains(command)) return;
		
		try{
			plugin.getCommand(command).setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /" + command + ".");
		}

	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_tutorials_enable()){
			sender.sendMessage(ChatColor.RED + "Tutorials not enabled.");
			return true;
		}
		
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "Only Players can use this command.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if(args.length == 0){
			postHelp(player);
			return true;
		}
		
		String commandString = args[0];
		
		if(commandString.equalsIgnoreCase("set")){
			if(args.length != 2 || TutorialState.getState(args[1]) == TutorialState.none){
				player.sendMessage(ChatColor.RED + "The command needs a valid new State! Valid Stats are: ");
				String stateString = "";
				for(TutorialState state : TutorialState.values()){
					if(state != TutorialState.none){
						stateString += ChatColor.AQUA + state.name() + ChatColor.RED + ", ";
					}
				}
				
				stateString = stateString.substring(0, stateString.length() - 2);
				player.sendMessage(stateString);
				return true;
			}
			
			setState(player, args[1]);
			return true;
		}
		
		if(commandString.equalsIgnoreCase("start")){
			
			if(args.length != 1){
				postHelp(player);
				return true;
			}

			tutorialStart(player);
			return true;
		}
		
		if(commandString.equalsIgnoreCase("stop")){
			if(args.length != 1){
				postHelp(player);
				return true;
			}
			
			tutorialStop(player);
			return true;
		}
		
		if(commandString.equalsIgnoreCase("skip")){
			if(args.length != 1){
				postHelp(player);
				return true;
			}
			
			tutorialSkip(player);
			return true;
		}
		
		if(commandString.equalsIgnoreCase("reset")){
			if(args.length != 1){
				postHelp(player);
				return true;
			}
			
			tutorialReset(player);
			return true;
		}
		
		if(commandString.equalsIgnoreCase("repost")){
			if(args.length != 1){
				postHelp(player);
				return true;
			}
			
			tutorialRepost(player);
			return true;
		}
		
		postHelp(player);
		return true;
	}
	
	private void postHelp(Player player){
		player.sendMessage(ChatColor.RED + "Tutorial Commands are:");
		player.sendMessage(ChatColor.AQUA + "/racestutorial " + ChatColor.LIGHT_PURPLE + "start" + ChatColor.YELLOW + " : starts a new Tutorial.");
		player.sendMessage(ChatColor.AQUA + "/racestutorial " + ChatColor.LIGHT_PURPLE + "stop" + ChatColor.YELLOW + " : stops the running Tutorial.");
		player.sendMessage(ChatColor.AQUA + "/racestutorial " + ChatColor.LIGHT_PURPLE + "skip" + ChatColor.YELLOW + " : skips the current step.");
		player.sendMessage(ChatColor.AQUA + "/racestutorial " + ChatColor.LIGHT_PURPLE + "reset" + ChatColor.YELLOW + " : resets the Tutorial.");
		player.sendMessage(ChatColor.AQUA + "/racestutorial " + ChatColor.LIGHT_PURPLE + "repost" + ChatColor.YELLOW + " : reposts the current State.");
		player.sendMessage(ChatColor.AQUA + "/racestutorial " + ChatColor.LIGHT_PURPLE + "set " + ChatColor.DARK_PURPLE + "<new State>" + ChatColor.YELLOW + 
							" : sets the Tutorial to the given state.");
	}
	
	private void tutorialStart(Player player){
		if(plugin.getTutorialManager().getCurrentState(player.getName()) != null){
			LanguageAPI.sendTranslatedMessage(player, tutorial_already_running);
			return;
		}
		
		if(!plugin.getTutorialManager().start(player.getUniqueId())){
			LanguageAPI.sendTranslatedMessage(player, tutorial_error);
		}
	}
	
	private void tutorialStop(Player player){
		if(!checkHasTutorial(player)) return;
		
		if(plugin.getTutorialManager().stop(player.getUniqueId())){
			LanguageAPI.sendTranslatedMessage(player, tutorial_stopped);
		}else{
			LanguageAPI.sendTranslatedMessage(player, tutorial_error);
		}
	}
	
	private void tutorialSkip(Player player){
		if(!checkHasTutorial(player)) return;
		
		if(!plugin.getTutorialManager().skip(player.getUniqueId())){
			LanguageAPI.sendTranslatedMessage(player, tutorial_error);
		}
	}
	
	private void tutorialReset(Player player){
		if(!checkHasTutorial(player)) return;
		
		if(!plugin.getTutorialManager().reset(player.getUniqueId())){
			LanguageAPI.sendTranslatedMessage(player, tutorial_error);
		}
	}
	
	private void tutorialRepost(Player player){
		if(!checkHasTutorial(player)) return;
		
		if(!plugin.getTutorialManager().repost(player.getUniqueId())){
			LanguageAPI.sendTranslatedMessage(player, tutorial_error);
		}
	}
	
	private void setState(Player player, String state){
		if(!checkHasTutorial(player)) return;
		
		if(!plugin.getTutorialManager().setState(player.getUniqueId(), state))
			LanguageAPI.sendTranslatedMessage(player, tutorial_no_set_at_this_state);
	}
	
	private boolean checkHasTutorial(Player player){
		if(plugin.getTutorialManager().getCurrentState(player.getName()) == null){
			LanguageAPI.sendTranslatedMessage(player, tutorial_not_running);
			return false;
		}
		
		return true;
	}
}
