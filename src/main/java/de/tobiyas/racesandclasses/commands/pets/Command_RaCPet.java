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
package de.tobiyas.racesandclasses.commands.pets;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.commands.AbstractCommand;
import de.tobiyas.racesandclasses.pets.Pet;
import de.tobiyas.racesandclasses.pets.PetBuilder;
import de.tobiyas.racesandclasses.pets.PlayerPetManager;
import de.tobiyas.racesandclasses.pets.SpawnedPet;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;

public class Command_RaCPet extends AbstractCommand {

	/**
	 * Registers the Command "class" to the plugin.
	 */
	public Command_RaCPet(){
		super("racpet");
	}	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {

		//Filter Only Players.
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "Only as Player.");
			return true;
		}
		
		if(sender.isOp() && args.length == 1 && args[0].equalsIgnoreCase("test")){
			Player player = (Player) sender;
			RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
			
			Pet pet = new PetBuilder().setPetType(EntityType.ZOMBIE).build();
			racPlayer.getPlayerPetManager().registerNewPet(pet);
			player.sendMessage("Done");
			return true;
		}

		if(args.length == 1 && args[0].equalsIgnoreCase("list")){
			Player player = (Player) sender;
			RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
			
			PlayerPetManager petManager = racPlayer.getPlayerPetManager();
			player.sendMessage(ChatColor.GREEN + "Pets:");
			for(SpawnedPet pet : petManager.getSpawnedPets()){
				player.sendMessage(ChatColor.AQUA + pet.getPet().getPetName() + " - Dead: " + pet.getPetEntity().isDead());
			}
			
			if(petManager.getRegisteredPets().isEmpty()) player.sendMessage(ChatColor.RED + "Keine");
			return true;
		}
		
		if(sender.isOp() && args.length == 1 && args[0].equalsIgnoreCase("cleanpet")){
			Player player = (Player) sender;
			RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
			
			racPlayer.getPlayerPetManager().despawnAndClear();
			player.sendMessage("Done");
			return true;
		}
		
		pasteHelp(sender);
		return true;
	}
	
	

	/**
	 * Paste the help for the Level command
	 * 
	 * @param sender to send to
	 */
	private void pasteHelp(CommandSender sender) {
		sender.sendMessage(ChatColor.YELLOW + "=== RAC Pets ===");
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "/racpet list" 
				+ ChatColor.YELLOW + "  Adds / Removes EXP to player.");
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "/raclevel <player> lvl <value>"
				+ ChatColor.YELLOW + "  Adds / Removes LEVELS to player.");
	}

}
