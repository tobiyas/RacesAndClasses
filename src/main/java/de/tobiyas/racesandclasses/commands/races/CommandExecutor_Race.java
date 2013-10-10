/*
 * Races - by tobiyas
 * http://
 *
 * powered by Kickstarter
 */

package de.tobiyas.racesandclasses.commands.races;


import java.util.List;
import java.util.Observable;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.gui.HolderInventory;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.raceevent.PreRaceChangeEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.raceevent.PreRaceSelectEvent;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.Trait;
import de.tobiyas.racesandclasses.tutorial.TutorialStepContainer;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;
import de.tobiyas.racesandclasses.util.tutorial.TutorialState;


public class CommandExecutor_Race extends Observable implements CommandExecutor {
	private RacesAndClasses plugin;

	public CommandExecutor_Race(){
		plugin = RacesAndClasses.getPlugin();
		try{
			plugin.getCommand("race").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /race.");
		}
		
		plugin.getTutorialManager().registerObserver(this);
		this.setChanged();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,String label, String[] args) {		
		if(args.length == 0){
			postHelp(sender, false);
			return true;
		}
		
		String raceCommand = args[0];
			
		//Select race(only if has no race)
		if(raceCommand.equalsIgnoreCase("select")){
			if(!checkPlayer(sender)) return true;
			Player player = (Player) sender;
			
			if(!plugin.getPermissionManager().checkPermissions(player, PermissionNode.selectRace)) return true;
			
			boolean useGUI = plugin.getConfigManager().getGeneralConfig().isConfig_useRaceGUIToSelect();
			if(useGUI){
				AbstractTraitHolder currentRace = plugin.getRaceManager().getHolderOfPlayer(player.getName());
				if(currentRace != plugin.getRaceManager().getDefaultHolder()){
					player.sendMessage(ChatColor.RED + "You already have a race: " + ChatColor.AQUA + currentRace.getName() 
							+ ChatColor.RED + ". Use " + ChatColor.LIGHT_PURPLE + "/race change" + ChatColor.RED + " to change your race.");
					return true;
				}
				
				PreRaceSelectEvent ccEvent = new PreRaceSelectEvent(player, (RaceContainer) plugin.getRaceManager().getDefaultHolder());
				plugin.getServer().getPluginManager().callEvent(ccEvent);
				
				if(ccEvent.isCancelled()){
					player.sendMessage(ChatColor.RED + "[RaC] + " + ccEvent.getCancelMessage());
					return true;
				}
				
				HolderInventory holderInventory = new HolderInventory(player, plugin.getRaceManager());
				if(holderInventory.getNumberOfHolder() <= 0){
					player.sendMessage(ChatColor.RED + "[RaC] You don't have any Race to select.");
					return true;
				}
				
				player.openInventory(holderInventory);
				player.sendMessage(ChatColor.GREEN + "Opening Race Selection...");
				return true;
			}
			
			if(args.length != 2){
				player.sendMessage(ChatColor.RED + "This command needs 1 argument: /race select <racename>");
				return true;
			}
			
			String potentialRace = args[1];
			selectRace(player, potentialRace);
			return true;
		}
			
		//Change races (only if has already a race)
		if(raceCommand.equalsIgnoreCase("change")){
			if(!checkPlayer(sender)) return true;
			Player player = (Player) sender;
			
			if(!plugin.getPermissionManager().checkPermissions(player, PermissionNode.changeRace)) return true;
			
			boolean useGUI = plugin.getConfigManager().getGeneralConfig().isConfig_useRaceGUIToSelect();
			if(useGUI){
				AbstractTraitHolder currentRace = plugin.getRaceManager().getHolderOfPlayer(player.getName());
				if(currentRace == plugin.getRaceManager().getDefaultHolder()){
					player.sendMessage(ChatColor.RED + "You don't have a race. Use " + ChatColor.LIGHT_PURPLE + "/race select" 
							+ ChatColor.RED + " to select a race.");
					return true;
				}
				
				PreRaceSelectEvent ccEvent = new PreRaceSelectEvent(player, (RaceContainer) plugin.getRaceManager().getDefaultHolder());
				plugin.getServer().getPluginManager().callEvent(ccEvent);
				
				if(ccEvent.isCancelled()){
					player.sendMessage(ChatColor.RED + "[RaC] + " + ccEvent.getCancelMessage());
					return true;
				}
				
				HolderInventory holderInventory = new HolderInventory(player, plugin.getRaceManager());
				if(holderInventory.getNumberOfHolder() <= 0){
					player.sendMessage(ChatColor.RED + "[RaC] You don't have any Race to select.");
					return true;
				}
				
				player.openInventory(holderInventory);
				player.sendMessage(ChatColor.GREEN + "Opening Race Selection...");
				return true;
			}
			
			if(args.length != 2){
				player.sendMessage(ChatColor.RED + "This command needs 1 argument: /race change <racename>");
				return true;
			}
			
			String potentialRace = args[1];
			changeRace(player, potentialRace);
			return true;
		}
		
		//Info to a race
		if(raceCommand.equalsIgnoreCase("info")){
			String inspectedRace = plugin.getRaceManager().getHolderOfPlayer(sender.getName()).getName();
			
			if(args.length > 1){
				inspectedRace = args[1];
			}
			
			raceInfo(sender, inspectedRace);
			return true;
		}
		
		//lists all races
		if(raceCommand.equalsIgnoreCase("list")){
			raceList(sender);
			this.notifyObservers(new TutorialStepContainer(sender.getName(), TutorialState.infoRace));
			this.setChanged();
			return true;
		}
			
		postHelp(sender, true);
		return true;
	}
	
	
	private boolean checkPlayer(CommandSender sender) {
		if(sender instanceof Player){
			return true;
		}
		
		sender.sendMessage(ChatColor.RED + "[RAC] Only players can use this command." );
		return false;
	}

	
	private void postHelp(CommandSender sender, boolean wrongUsage){
		if(wrongUsage)
			sender.sendMessage(ChatColor.RED + "Wrong usage. The correct usage is one of the following:");
		else
			sender.sendMessage(ChatColor.RED + "Use one of the following commands:");
		
		sender.sendMessage(ChatColor.RED + "/race " + ChatColor.LIGHT_PURPLE + "info");
		sender.sendMessage(ChatColor.RED + "/race " + ChatColor.LIGHT_PURPLE + "list");
		if(plugin.getPermissionManager().checkPermissionsSilent(sender, PermissionNode.changeRace))
			sender.sendMessage(ChatColor.RED + "/race " + ChatColor.LIGHT_PURPLE + "select " + ChatColor.YELLOW + "<racename>");
		
		if(plugin.getPermissionManager().checkPermissionsSilent(sender, PermissionNode.selectRace))
			sender.sendMessage(ChatColor.RED + "/race " + ChatColor.LIGHT_PURPLE + "change " + ChatColor.YELLOW + "<racename>");
	}
	
	private void selectRace(Player player, String newRaceName){
		RaceContainer container = (RaceContainer) plugin.getRaceManager().getHolderOfPlayer(player.getName());
		RaceContainer stdContainer = (RaceContainer) plugin.getRaceManager().getDefaultHolder();
		if(container == null || container == stdContainer){
			RaceContainer raceContainer = (RaceContainer) plugin.getRaceManager().getHolderByName(newRaceName);
			
			if(raceContainer == null){
				player.sendMessage(ChatColor.RED + "The race " + ChatColor.LIGHT_PURPLE + newRaceName + ChatColor.RED + " was not found.");
				return;
			}
			
			if(raceContainer != null && raceContainer == stdContainer){
				player.sendMessage(ChatColor.RED + "You can't select the default race.");
				return;
			}
			
			PreRaceSelectEvent selectEvent = new PreRaceSelectEvent(player, raceContainer);
			plugin.fireEventToBukkit(selectEvent);
			
			if(selectEvent.isCancelled()){
				player.sendMessage(ChatColor.RED + selectEvent.getCancelMessage());
				return;
			}
			
			if(plugin.getRaceManager().addPlayerToHolder(player.getName(), newRaceName, true)){
				player.sendMessage(ChatColor.GREEN + "You are now a " + ChatColor.LIGHT_PURPLE + newRaceName);
			}
				
		}else{
			player.sendMessage(ChatColor.RED + "You already have a race: " + ChatColor.LIGHT_PURPLE + container.getName());
		}
	}
	
	private void changeRace(Player player, String newRace){
		AbstractTraitHolder oldContainer = plugin.getRaceManager().getHolderOfPlayer(player.getName());
		AbstractTraitHolder stdContainer = plugin.getRaceManager().getDefaultHolder();
		
		if(oldContainer != null && oldContainer != plugin.getRaceManager().getDefaultHolder()){
			if(newRace.equalsIgnoreCase(oldContainer.getName())){
				player.sendMessage(ChatColor.RED + "You are already a " + ChatColor.LIGHT_PURPLE + oldContainer.getName());
				return;
			}
			
			if(plugin.getRaceManager().getHolderByName(newRace) != null && plugin.getRaceManager().getHolderByName(newRace) == stdContainer){
				player.sendMessage(ChatColor.RED + "You can't select the default race.");
				return;
			}
			
			AbstractTraitHolder newContainer = plugin.getRaceManager().getHolderByName(newRace);
			if(newContainer == null){
				player.sendMessage(ChatColor.RED + "The race " + ChatColor.LIGHT_PURPLE + newRace + ChatColor.RED + " was not found.");
				return;
			}
			
			PreRaceChangeEvent selectEvent = new PreRaceChangeEvent(player, (RaceContainer) oldContainer, (RaceContainer) newContainer);
			plugin.fireEventToBukkit(selectEvent);
			
			if(selectEvent.isCancelled()){
				player.sendMessage(ChatColor.RED + selectEvent.getCancelMessage());
				return;
			}
			
			if(plugin.getRaceManager().changePlayerHolder(player.getName(), newRace, true)){
				player.sendMessage(ChatColor.GREEN + "You are now a " + ChatColor.LIGHT_PURPLE + newRace);
			}else{
				player.sendMessage(ChatColor.RED + "The race " + ChatColor.LIGHT_PURPLE + newRace + ChatColor.RED + " was not found.");
			}
		}else{
			player.sendMessage(ChatColor.RED + "You have no Race you could change.");
		}
	}
	
	private void raceInfo(CommandSender sender, String inspectedRace){
		RaceContainer container = (RaceContainer) plugin.getRaceManager().getHolderByName(inspectedRace);
		RaceContainer containerOfPlayer = (RaceContainer) plugin.getRaceManager().getHolderOfPlayer(sender.getName());
		if(container == null){
			sender.sendMessage(ChatColor.RED + "No Race named: " + ChatColor.LIGHT_PURPLE + inspectedRace + ChatColor.RED + " found.");
			return;
		}
		
		if(container.equals(containerOfPlayer)){
			sender.sendMessage(ChatColor.YELLOW + "=========" + ChatColor.RED + "RACE INFO" + ChatColor.YELLOW + "=========");
		
			if(containerOfPlayer == null){
				sender.sendMessage(ChatColor.YELLOW + "You have no race selected.");
				return;
			}

		}else{
			sender.sendMessage(ChatColor.YELLOW + "=========" + ChatColor.RED + "RACE INFO OF: " + inspectedRace + ChatColor.YELLOW + "=========");
		}
			
		
		sender.sendMessage(ChatColor.YELLOW + "Race health: " + ChatColor.LIGHT_PURPLE + container.getRaceMaxHealth());
		sender.sendMessage(ChatColor.YELLOW + "Race name: " + ChatColor.LIGHT_PURPLE + container.getName());
		sender.sendMessage(ChatColor.YELLOW + "Race tag: " + ChatColor.LIGHT_PURPLE + container.getTag());
		sender.sendMessage(ChatColor.YELLOW + "Allowed armor: " + ChatColor.LIGHT_PURPLE + container.getArmorString());
		
		sender.sendMessage(ChatColor.YELLOW + "=========" + ChatColor.RED + "Traits" + ChatColor.YELLOW + "=========");
		for(Trait trait : container.getVisibleTraits()){
			sender.sendMessage(ChatColor.BLUE + trait.getName() + " : " + trait.getPrettyConfiguration());
		}
	}
	
	private void raceList(CommandSender sender){
		List<String> races = plugin.getRaceManager().listAllVisibleHolders();
		AbstractTraitHolder senderRace = plugin.getRaceManager().getHolderOfPlayer(sender.getName());
		
		if(senderRace == plugin.getRaceManager().getDefaultHolder()){
			races.add(plugin.getRaceManager().getDefaultHolder().getName());
		}
		
		sender.sendMessage(ChatColor.YELLOW + "======LIST OF RACES======");
		
		for(String race : races){
			if(senderRace != null && race.equals(senderRace.getName())){
				sender.sendMessage(ChatColor.RED + race + ChatColor.YELLOW + "  <-- Your Race!");
			}else{	
				sender.sendMessage(ChatColor.BLUE + race);
			}
		}
	}
}
