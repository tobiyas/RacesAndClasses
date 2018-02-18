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

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.commands.AbstractCommand;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.playermanagement.health.HealthManager;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana.ManaManager;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;
import de.tobiyas.racesandclasses.util.items.ItemUtils.ItemQuality;

public class CommandExecutor_PlayerInfo extends AbstractCommand {

private RacesAndClasses plugin;
	
	public CommandExecutor_PlayerInfo(){
		super("playerinfo");
		plugin = RacesAndClasses.getPlugin();
	}
	
	@Override
	public boolean onInternalCommand(CommandSender sender, Command command, String label,
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
			RaCPlayer racPlayer = RaCPlayerManager.get().getPlayerByName(args[0]);
			player = racPlayer == null ? null : racPlayer.getPlayer();
		}
			
		if(player == null){
			sender.sendMessage(ChatColor.RED + LanguageAPI.translateIgnoreError("player_not_exist")
					.replace("player", args[0])
					.build());
			return true;
		}
		
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
		AbstractTraitHolder raceContainer = racPlayer.getRace();
		AbstractTraitHolder classContainer = racPlayer.getclass();
		
		String className = "None";
		String raceName = "None";
		if(classContainer != null){
			className = classContainer.getDisplayName();
		}
		
		if(raceContainer != null){
			raceName = raceContainer.getDisplayName();
		}
		
		boolean hasPermForLocation = plugin.getPermissionManager().checkPermissionsSilent(sender, PermissionNode.playerInfosLocation);
		boolean hasPermForOthers = plugin.getPermissionManager().checkPermissionsSilent(sender, PermissionNode.playerInfosOthers);
		
		
		sender.sendMessage(ChatColor.YELLOW + "=====" + ChatColor.RED + "PLAYER: " + ChatColor.AQUA + player.getName() + ChatColor.YELLOW + "=====");
		sender.sendMessage(ChatColor.YELLOW + "Race: " + ChatColor.RED + raceName);
		sender.sendMessage(ChatColor.YELLOW + "Class: " + ChatColor.RED + className);
		
		if(args.length == 0){
			Set<ItemQuality> armorPerms = new HashSet<ItemQuality>();
			if(racPlayer.getRace() != null) armorPerms.addAll(racPlayer.getRace().getArmorPerms());
			if(racPlayer.getclass() != null) armorPerms.addAll(racPlayer.getclass().getArmorPerms());
			
			String allowed = "";
			for(ItemQuality quality : armorPerms){
				allowed += ", " + quality.getName();
			}
			
			if(!allowed.isEmpty()) allowed = allowed.substring(2);
			if(allowed.isEmpty()) allowed = "None";
			
			sender.sendMessage(ChatColor.YELLOW + "Allowed armor: " + ChatColor.RED + allowed);
		}
		
		if(hasPermForLocation){
			sender.sendMessage(ChatColor.YELLOW + "---L--O--C--A--T--I--O--N---");
			sender.sendMessage(ChatColor.YELLOW + "World: " + ChatColor.AQUA + player.getWorld().getName());
			
			Location loc = player.getLocation();
			sender.sendMessage(ChatColor.YELLOW + "Position - X:" + ChatColor.AQUA + loc.getBlockX() + ChatColor.YELLOW + " Y:" + 
								ChatColor.AQUA + loc.getBlockY() + ChatColor.YELLOW + " Z:" + ChatColor.AQUA + loc.getBlockZ());
		}
		
		if(hasPermForOthers){
			ItemStack inHand = player.getInventory().getItem(player.getInventory().getHeldItemSlot());
			if( inHand == null ) inHand = new ItemStack( Material.AIR );
			
			sender.sendMessage(ChatColor.YELLOW + "---O--T--H--E--R---");
			sender.sendMessage(ChatColor.YELLOW + "Item in Hand: " + ChatColor.AQUA + inHand.getType().toString());

			//Check for Health Boost:
			DecimalFormat format = new DecimalFormat("0.0");
			if( containsKey(args, "-health") ) {
				HealthManager hm = racPlayer.getHealthManager();
				Map<String,Double> boosts = hm.getHealthBoosts();
				sender.sendMessage(ChatColor.YELLOW + "---Health-Boosts: " + boosts.size() + " ---");
				sender.sendMessage(ChatColor.YELLOW + "Current-Health: " + format.format( hm.getCurrentHealth() ) );
				sender.sendMessage(ChatColor.YELLOW + "Max-Health: " + format.format( hm.getMaxHealth() ) );
				for( Map.Entry<String,Double> entry : boosts.entrySet() ) {
					sender.sendMessage(ChatColor.YELLOW + " -" + entry.getKey() + ": " + format.format( entry.getValue() ) );
				}
			}
			
			//Check for Mana Boost:
			if( containsKey(args, "-mana") ) {
				ManaManager mm = racPlayer.getManaManager();
				Map<String,Double> boosts = mm.getAllBonuses();
				sender.sendMessage(ChatColor.YELLOW + "---Mana-Boosts: " + boosts.size() + " ---");
				sender.sendMessage(ChatColor.YELLOW + "Current-Mana: " + format.format( mm.getCurrentMana() ) );
				sender.sendMessage(ChatColor.YELLOW + "Max-Mana: " + format.format( mm.getMaxMana() ) );
				for( Map.Entry<String,Double> entry : boosts.entrySet() ) {
					sender.sendMessage(ChatColor.YELLOW + " -" + entry.getKey() + ": " + format.format( entry.getValue() ) );
				}
			}
		}

		
		return true;
	}
	
	
	
	private boolean containsKey( String[] args, String key ) {
		for( String part : args ) {
			if( part.equalsIgnoreCase( key ) ) return true;
		}
		
		return false;
	}

}
