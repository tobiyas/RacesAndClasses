/*
 * Races - by tobiyas
 * http://
 *
 * powered by Kickstarter
 */

package de.tobiyas.racesandclasses.commands.races;


import java.util.ArrayList;
import java.util.Observable;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.chat.channels.ChannelManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.gui.HolderInventory;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceManager;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.raceevent.RaceChangeEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.raceevent.RaceSelectEvent;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.Trait;
import de.tobiyas.racesandclasses.tutorial.TutorialManager;
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
		
		TutorialManager.registerObserver(this);
		this.setChanged();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only Players can use this command");
			return true;
		}
		
		Player player = (Player) sender;
		if(args.length == 0){
			postHelp(player, false);
			return true;
		}
		
		String raceCommand = args[0];
			
		//Select race(only if has no race)
		if(raceCommand.equalsIgnoreCase("select")){
			if(!plugin.getPermissionManager().checkPermissions(player, PermissionNode.selectRace)) return true;
			
			boolean useGUI = plugin.getConfigManager().getGeneralConfig().isConfig_useRaceGUIToSelect();
			if(useGUI){
				player.openInventory(new HolderInventory(player, RaceManager.getInstance()));
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
			if(!plugin.getPermissionManager().checkPermissions(player, PermissionNode.changeRace)) return true;
			
			boolean useGUI = plugin.getConfigManager().getGeneralConfig().isConfig_useRaceGUIToSelect();
			if(useGUI){
				player.openInventory(new HolderInventory(player, RaceManager.getInstance()));
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
			if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.raceInfo)) return true;
			String inspectedRace = RaceManager.getInstance().getHolderOfPlayer(player.getName()).getName();
			if(args.length > 1){
				inspectedRace = args[1];
			}
			
			raceInfo(player, inspectedRace);
			return true;
		}
		
		//lists all races
		if(raceCommand.equalsIgnoreCase("list")){
			raceList(player);
			this.notifyObservers(new TutorialStepContainer(player.getName(), TutorialState.infoRace));
			this.setChanged();
			return true;
		}
			
		postHelp(player, true);
		return true;
	}
	
	private void postHelp(Player player, boolean wrongUsage){
		if(wrongUsage)
			player.sendMessage(ChatColor.RED + "Wrong usage. The correct usage is one of the following:");
		else
			player.sendMessage(ChatColor.RED + "Use one of the following commands:");
		
		player.sendMessage(ChatColor.RED + "/race " + ChatColor.LIGHT_PURPLE + "info");
		player.sendMessage(ChatColor.RED + "/race " + ChatColor.LIGHT_PURPLE + "list");
		if(plugin.getPermissionManager().checkPermissionsSilent(player, PermissionNode.changeRace))
			player.sendMessage(ChatColor.RED + "/race " + ChatColor.LIGHT_PURPLE + "select " + ChatColor.YELLOW + "<racename>");
		
		if(plugin.getPermissionManager().checkPermissionsSilent(player, PermissionNode.selectRace))
			player.sendMessage(ChatColor.RED + "/race " + ChatColor.LIGHT_PURPLE + "change " + ChatColor.YELLOW + "<racename>");
	}
	
	private void selectRace(Player player, String newRaceName){
		RaceContainer container = (RaceContainer) RaceManager.getInstance().getHolderOfPlayer(player.getName());
		RaceContainer stdContainer = (RaceContainer) RaceManager.getInstance().getDefaultHolder();
		if(container == null || container == stdContainer){
			RaceContainer raceContainer = (RaceContainer) RaceManager.getInstance().getHolderByName(newRaceName);
			
			if(raceContainer == null){
				player.sendMessage(ChatColor.RED + "The race " + ChatColor.LIGHT_PURPLE + newRaceName + ChatColor.RED + " was not found.");
				return;
			}
			
			if(raceContainer != null && raceContainer == stdContainer){
				player.sendMessage(ChatColor.RED + "You can't select the default race.");
				return;
			}
			
			RaceSelectEvent selectEvent = new RaceSelectEvent(player, raceContainer);
			plugin.fireEventToBukkit(selectEvent);
			
			if(selectEvent.isCancelled()){
				player.sendMessage(ChatColor.RED + selectEvent.getCancelMessage());
				return;
			}
			
			if(RaceManager.getInstance().addPlayerToHolder(player.getName(), newRaceName)){
				player.sendMessage(ChatColor.GREEN + "You are now a " + ChatColor.LIGHT_PURPLE + newRaceName);
				if(plugin.getConfigManager().getGeneralConfig().isConfig_channels_enable()){
					ChannelManager.GetInstance().playerChangeRace("", player);
				}
			}
				
		}else{
			player.sendMessage(ChatColor.RED + "You already have a race: " + ChatColor.LIGHT_PURPLE + container.getName());
		}
	}
	
	private void changeRace(Player player, String newRace){
		AbstractTraitHolder oldContainer = RaceManager.getInstance().getHolderOfPlayer(player.getName());
		AbstractTraitHolder stdContainer = RaceManager.getInstance().getDefaultHolder();
		
		if(oldContainer != null && oldContainer != RaceManager.getInstance().getDefaultHolder()){
			String oldRace = oldContainer.getName();
			if(newRace.equalsIgnoreCase(oldContainer.getName())){
				player.sendMessage(ChatColor.RED + "You are already a " + ChatColor.LIGHT_PURPLE + oldContainer.getName());
				return;
			}
			
			if(RaceManager.getInstance().getHolderByName(newRace) != null && RaceManager.getInstance().getHolderByName(newRace) == stdContainer){
				player.sendMessage(ChatColor.RED + "You can't select the default race.");
				return;
			}
			
			AbstractTraitHolder newContainer = RaceManager.getInstance().getHolderByName(newRace);
			if(newContainer == null){
				player.sendMessage(ChatColor.RED + "The race " + ChatColor.LIGHT_PURPLE + newRace + ChatColor.RED + " was not found.");
				return;
			}
			
			RaceChangeEvent selectEvent = new RaceChangeEvent(player, (RaceContainer) oldContainer, (RaceContainer) newContainer);
			plugin.fireEventToBukkit(selectEvent);
			
			if(selectEvent.isCancelled()){
				player.sendMessage(ChatColor.RED + selectEvent.getCancelMessage());
				return;
			}
			
			if(RaceManager.getInstance().changePlayerHolder(player.getName(), newRace)){
				player.sendMessage(ChatColor.GREEN + "You are now a " + ChatColor.LIGHT_PURPLE + newRace);
				if(plugin.getConfigManager().getGeneralConfig().isConfig_channels_enable()){
					ChannelManager.GetInstance().playerChangeRace(oldRace, player);
				}
			}else{
				player.sendMessage(ChatColor.RED + "The race " + ChatColor.LIGHT_PURPLE + newRace + ChatColor.RED + " was not found.");
			}
		}else{
			player.sendMessage(ChatColor.RED + "You have no Race you could change.");
		}
	}
	
	private void raceInfo(Player player, String inspectedRace){
		AbstractTraitHolder container = RaceManager.getInstance().getHolderByName(inspectedRace);
		AbstractTraitHolder containerOfPlayer =  RaceManager.getInstance().getHolderOfPlayer(player.getName());
		if(container == null){
			player.sendMessage(ChatColor.RED + "No Race named: " + ChatColor.LIGHT_PURPLE + inspectedRace + ChatColor.RED + " found.");
			return;
		}
		
		if(container.equals(containerOfPlayer)){
			player.sendMessage(ChatColor.YELLOW + "=========" + ChatColor.RED + "RACE INFO" + ChatColor.YELLOW + "=========");
		
			if(containerOfPlayer == null){
				player.sendMessage(ChatColor.YELLOW + "You have no race selected.");
				return;
			}

		}else{
			player.sendMessage(ChatColor.YELLOW + "=========" + ChatColor.RED + "RACE INFO OF: " + inspectedRace + ChatColor.YELLOW + "=========");
		}
			
		
		player.sendMessage(ChatColor.YELLOW + "Race name: " + ChatColor.LIGHT_PURPLE + container.getName());
		player.sendMessage(ChatColor.YELLOW + "Race tag: " + ChatColor.LIGHT_PURPLE + container.getTag());
		player.sendMessage(ChatColor.YELLOW + "Allowed armor: " + ChatColor.LIGHT_PURPLE + container.getArmorString());
		
		player.sendMessage(ChatColor.YELLOW + "=========" + ChatColor.RED + "Traits" + ChatColor.YELLOW + "=========");
		for(Trait trait : container.getVisibleTraits()){
			player.sendMessage(ChatColor.BLUE + trait.getName() + " : " + trait.getPrettyConfiguration());
		}
	}
	
	private void raceList(Player player){
		ArrayList<String> races = RaceManager.getInstance().listAllVisibleHolders();
		AbstractTraitHolder playerRace = RaceManager.getInstance().getHolderOfPlayer(player.getName());
		if(playerRace == RaceManager.getInstance().getDefaultHolder()){
			races.add(RaceManager.getInstance().getDefaultHolder().getName());
		}
		
		player.sendMessage(ChatColor.YELLOW + "======LIST OF RACES======");
		
		for(String race : races){
			if(playerRace != null && race.equals(playerRace.getName())){
				player.sendMessage(ChatColor.RED + race + ChatColor.YELLOW + "  <-- Your race");
			}else{	
				player.sendMessage(ChatColor.BLUE + race);
			}
		}
	}
}
