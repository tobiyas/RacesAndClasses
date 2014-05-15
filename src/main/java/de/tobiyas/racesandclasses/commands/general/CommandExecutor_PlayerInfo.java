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
package de.tobiyas.racesandclasses.commands.general;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.commands.AbstractCommand;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;

public class CommandExecutor_PlayerInfo extends AbstractCommand {

private RacesAndClasses plugin;
	
	public CommandExecutor_PlayerInfo(){
		super("playerinfo");
		plugin = RacesAndClasses.getPlugin();

//		String command = "playerinfo";
//		if(plugin.getConfigManager().getGeneralConfig().getConfig_general_disable_commands().contains(command)) return;
//		
//		try{
//			plugin.getCommand(command).setExecutor(this);
//		}catch(Exception e){
//			plugin.log("ERROR: Could not register command /" + command + ".");
//		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {

		if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.playerInfos)) return true;
		
		Player player = null;
		if(args.length == 0){
			if(sender instanceof Player){
				player = (Player) sender;
			}else{
				sender.sendMessage(ChatColor.RED + LanguageAPI.translateIgnoreError("needs_1_arg")
						.replace("command", "/playerinfo <playername>")
						.build());
				return true;
			}
				
		}else{
			player = Bukkit.getPlayer(args[0]);
		}
			
		if(player == null){
			sender.sendMessage(ChatColor.RED + LanguageAPI.translateIgnoreError("player_not_exist")
					.replace("player", args[0])
					.build());
			return true;
		}
		
		AbstractTraitHolder raceContainer = plugin.getRaceManager().getHolderOfPlayer(player.getUniqueId());
		AbstractTraitHolder classContainer = plugin.getClassManager().getHolderOfPlayer(player.getUniqueId());
		String className = "None";
		String raceName = "None";
		if(classContainer != null){
			className = classContainer.getName();
		}
		
		if(raceContainer != null){
			raceName = raceContainer.getName();
		}
		
		
		sender.sendMessage(ChatColor.YELLOW + "=====" + ChatColor.RED + "PLAYER: " + ChatColor.AQUA + player.getName() + ChatColor.YELLOW + "=====");
		sender.sendMessage(ChatColor.YELLOW + "Race: " + ChatColor.RED + raceName);
		sender.sendMessage(ChatColor.YELLOW + "Class: " + ChatColor.RED + className);
		sender.sendMessage(ChatColor.YELLOW + "---L--O--C--A--T--I--O--N---");
		sender.sendMessage(ChatColor.YELLOW + "World: " + ChatColor.AQUA + player.getWorld().getName());
		
		Location loc = player.getLocation();
		sender.sendMessage(ChatColor.YELLOW + "Position - X:" + ChatColor.AQUA + loc.getBlockX() + ChatColor.YELLOW + " Y:" + 
							ChatColor.AQUA + loc.getBlockY() + ChatColor.YELLOW + " Z:" + ChatColor.AQUA + loc.getBlockZ());
		
		sender.sendMessage(ChatColor.YELLOW + "---O--T--H--E--R---");
		sender.sendMessage(ChatColor.YELLOW + "Item in Hand: " + ChatColor.AQUA + player.getItemInHand().getType().toString());
		return true;
	}

}
