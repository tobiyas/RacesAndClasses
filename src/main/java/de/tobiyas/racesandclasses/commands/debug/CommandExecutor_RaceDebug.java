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
package de.tobiyas.racesandclasses.commands.debug;

import java.util.Properties;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.commands.AbstractCommand;
import de.tobiyas.racesandclasses.util.consts.Consts;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;

public class CommandExecutor_RaceDebug extends AbstractCommand {

	private RacesAndClasses plugin;
	
	public CommandExecutor_RaceDebug(){
		super("racedebug", new String[]{"rdebug"});
		plugin = RacesAndClasses.getPlugin();
	}
	
	@Override
	public boolean onCommand(final CommandSender sender, Command command, String label,
			String[] args) {
		
		if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.debug)) return true;
		
		if(args.length == 1){
			String commandString = args[0];
			if(commandString.equalsIgnoreCase("scan")){
				long timeTook = runDebugScan();
				sender.sendMessage(ChatColor.GREEN + "Full Scan finished (" + timeTook + "ms) and logged in Debug file.");
				return true;
			}
			
			if(commandString.equals("time")){
				//We need to add a 6 hour offset, since MC seems to be starting at 6am.
				int hour = ((int) (Bukkit.getWorld("world").getTime() / 1000l) + 6) % 24;
				boolean isNight = hour > 18 || hour < 6;
				boolean isDay = hour > 6 && hour < 18;
				
				sender.sendMessage(ChatColor.GREEN + "It is: " + (isNight ? ChatColor.RED + "Night" : "") + "" + (isDay ? ChatColor.YELLOW +  "Day": ""));
				return true;
			}
			
			if(commandString.equalsIgnoreCase("timing")){
				sender.sendMessage(ChatColor.GREEN + "Profiling Event-Timings. Results in " + ChatColor.LIGHT_PURPLE + 
									Consts.timingLength + ChatColor.GREEN + " seconds.");
				new DebugTimingEvents(sender);
				return true;
			}
			
			if(commandString.equalsIgnoreCase("clearcd")){
				sender.sendMessage(ChatColor.GREEN + "CDs cleared");
				plugin.getCooldownManager().clearAllCooldowns();
				return true;
			}
			
			if(commandString.equalsIgnoreCase("testerror")){ //Produces Error! Only for testing!
				if(sender instanceof Player && !((Player)sender).isOp()){
					sender.sendMessage(ChatColor.RED + "You may not use this command!");
					return true;
				}
				
				sender.sendMessage(ChatColor.GREEN + "Error is beeing fired!");
				try{
					throw new NullPointerException("useless generated exeption.");
				}catch(NullPointerException e){
					String message = "Checking Error writing";
					plugin.logStackTrace(message, e);
					sender.sendMessage(ChatColor.GREEN + "Worked!");
					return true;
				}
			}
			
			sender.sendMessage(ChatColor.RED + "No debug command found for: " + ChatColor.LIGHT_PURPLE + commandString);
			return true;
		}
		
		postHelp(sender);
		return true;
	}
	
	private void postHelp(CommandSender sender){
		sender.sendMessage(ChatColor.RED + "Wrong usage. The correct usage is one of the following:");
		sender.sendMessage(ChatColor.RED + "/racedebug " + ChatColor.LIGHT_PURPLE + "scan");
		sender.sendMessage(ChatColor.RED + "/racedebug " + ChatColor.LIGHT_PURPLE + "timing");
		sender.sendMessage(ChatColor.RED + "/racedebug " + ChatColor.LIGHT_PURPLE + "testerror");
		sender.sendMessage(ChatColor.RED + "Others coming soon.");
	}
	
	private long runDebugScan(){
		long startTime = System.currentTimeMillis();
		plugin.getDebugLogger().log("------------------------------------------------------------------");
		plugin.getDebugLogger().log("Running Full debug Scan");
		
		Properties props = System.getProperties();
		plugin.getDebugLogger().log("============System Properties============");
		for(Object objProp : props.keySet()){
			String prop = (String) objProp;
			String value = props.getProperty(prop);
			plugin.getDebugLogger().log("Property: " + prop + " value: " + value);
		}
		
		long timeTook = System.currentTimeMillis() - startTime;
		plugin.getDebugLogger().log("Full debug scan finished. It took: " + timeTook + "ms.");
		plugin.getDebugLogger().log("------------------------------------------------------------------");
		
		return timeTook;
	}

}
