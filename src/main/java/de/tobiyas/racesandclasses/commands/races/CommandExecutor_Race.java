/*
 * Races - by tobiyas
 * http://
 *
 * powered by Kickstarter
 */

package de.tobiyas.racesandclasses.commands.races;


import static de.tobiyas.racesandclasses.translation.languages.Keys.already_are;
import static de.tobiyas.racesandclasses.translation.languages.Keys.already_have_race;
import static de.tobiyas.racesandclasses.translation.languages.Keys.no_race_selected;
import static de.tobiyas.racesandclasses.translation.languages.Keys.no_traits;
import static de.tobiyas.racesandclasses.translation.languages.Keys.race_changed_to;
import static de.tobiyas.racesandclasses.translation.languages.Keys.race_not_exist;
import static de.tobiyas.racesandclasses.translation.languages.Keys.your_race;

import java.util.List;
import java.util.Observable;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.gui.HolderInventory;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.raceevent.PreRaceChangeEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.raceevent.PreRaceSelectEvent;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
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
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_enableRaces()){
			sender.sendMessage(LanguageAPI.translateIgnoreError("something_disabled")
					.replace("value", "Race")
					.build());
			return true;
		}
		
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
					sender.sendMessage(LanguageAPI.translateIgnoreError("already_have_race")
							.replace("race", currentRace.getName())
							.build());
					
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
					sender.sendMessage(LanguageAPI.translateIgnoreError("no_race_to_select")
							.build());
					return true;
				}
				
				player.openInventory(holderInventory);
				sender.sendMessage(LanguageAPI.translateIgnoreError("open_holder")
						.replace("holder", "Race")
						.build());
				return true;
			}
			
			if(args.length != 2){
				sender.sendMessage(LanguageAPI.translateIgnoreError("needs_1_arg")
						.replace("command", "/race select <racename>")
						.build());
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
					sender.sendMessage(LanguageAPI.translateIgnoreError("no_race_selected")
							.build());
					
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
					sender.sendMessage(LanguageAPI.translateIgnoreError("no_race_to_select")
							.build());
					return true;
				}
				
				player.openInventory(holderInventory);
				sender.sendMessage(LanguageAPI.translateIgnoreError("open_holder")
						.replace("holder", "Race")
						.build());
				return true;
			}
			
			if(args.length != 2){
				sender.sendMessage(LanguageAPI.translateIgnoreError("needs_1_arg")
						.replace("command", "/race change <racename>")
						.build());
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
		
		sender.sendMessage(LanguageAPI.translateIgnoreError("only_players")
				.build());

		return false;
	}

	
	private void postHelp(CommandSender sender, boolean wrongUsage){
		if(wrongUsage){
			sender.sendMessage(ChatColor.RED + "Wrong usage. The correct usage is one of the following:");
		}else{
			sender.sendMessage(ChatColor.RED + "Use one of the following commands:");
		}
			
		sender.sendMessage(ChatColor.RED + "/race " + ChatColor.LIGHT_PURPLE + "info");
		sender.sendMessage(ChatColor.RED + "/race " + ChatColor.LIGHT_PURPLE + "list");
		if(plugin.getPermissionManager().checkPermissionsSilent(sender, PermissionNode.changeRace)){
			sender.sendMessage(ChatColor.RED + "/race " + ChatColor.LIGHT_PURPLE + "select " + ChatColor.YELLOW + "<racename>");
		}
			
		if(plugin.getPermissionManager().checkPermissionsSilent(sender, PermissionNode.selectRace)){
			sender.sendMessage(ChatColor.RED + "/race " + ChatColor.LIGHT_PURPLE + "change " + ChatColor.YELLOW + "<racename>");
		}
	}
	
	private void selectRace(Player player, String newRaceName){
		RaceContainer container = (RaceContainer) plugin.getRaceManager().getHolderOfPlayer(player.getName());
		RaceContainer stdContainer = (RaceContainer) plugin.getRaceManager().getDefaultHolder();
		if(container == null || container == stdContainer){
			RaceContainer raceContainer = (RaceContainer) plugin.getRaceManager().getHolderByName(newRaceName);
			
			if(raceContainer == null){
				player.sendMessage(LanguageAPI.translateIgnoreError(race_not_exist)
						.replace("race", newRaceName)
						.build());
				return;
			}
			
			if(raceContainer != null && raceContainer == stdContainer){
				player.sendMessage(LanguageAPI.translateIgnoreError(race_not_exist)
						.replace("race", "default race")
						.build());
				return;
			}
			
			PreRaceSelectEvent selectEvent = new PreRaceSelectEvent(player, raceContainer);
			plugin.fireEventToBukkit(selectEvent);
			
			if(selectEvent.isCancelled()){
				player.sendMessage(ChatColor.RED + selectEvent.getCancelMessage());
				return;
			}
			
			if(plugin.getRaceManager().addPlayerToHolder(player.getName(), newRaceName, true)){
				player.sendMessage(LanguageAPI.translateIgnoreError(race_changed_to)
						.replace("race", newRaceName)
						.build());
			}
				
		}else{
			player.sendMessage(LanguageAPI.translateIgnoreError(already_have_race)
					.replace("race", container.getName())
					.build());
		}
	}
	
	private void changeRace(Player player, String newRace){
		AbstractTraitHolder oldContainer = plugin.getRaceManager().getHolderOfPlayer(player.getName());
		AbstractTraitHolder stdContainer = plugin.getRaceManager().getDefaultHolder();
		
		if(oldContainer != null && oldContainer != plugin.getRaceManager().getDefaultHolder()){
			if(newRace.equalsIgnoreCase(oldContainer.getName())){
				player.sendMessage(LanguageAPI.translateIgnoreError(already_are)
						.replace("holder", oldContainer.getName())
						.build());
				return;
			}
			
			if(plugin.getRaceManager().getHolderByName(newRace) != null && plugin.getRaceManager().getHolderByName(newRace) == stdContainer){
				player.sendMessage(LanguageAPI.translateIgnoreError(race_not_exist)
						.replace("race", plugin.getRaceManager().getDefaultHolder().getName())
						.build());
				return;
			}
			
			AbstractTraitHolder newContainer = plugin.getRaceManager().getHolderByName(newRace);
			if(newContainer == null){
				player.sendMessage(LanguageAPI.translateIgnoreError(race_not_exist)
						.replace("race", newRace)
						.build());
				return;
			}
			
			PreRaceChangeEvent selectEvent = new PreRaceChangeEvent(player, (RaceContainer) oldContainer, (RaceContainer) newContainer);
			plugin.fireEventToBukkit(selectEvent);
			
			if(selectEvent.isCancelled()){
				player.sendMessage(ChatColor.RED + selectEvent.getCancelMessage());
				return;
			}
			
			if(plugin.getRaceManager().changePlayerHolder(player.getName(), newRace, true)){
				player.sendMessage(LanguageAPI.translateIgnoreError(race_changed_to)
						.replace("race", newRace)
						.build());
			}else{
				player.sendMessage(LanguageAPI.translateIgnoreError(race_not_exist)
						.replace("race", newRace)
						.build());
			}
		}else{
			player.sendMessage(LanguageAPI.translateIgnoreError(no_race_selected)
					.build());
		}
	}
	
	private void raceInfo(CommandSender sender, String inspectedRace){
		RaceContainer container = (RaceContainer) plugin.getRaceManager().getHolderByName(inspectedRace);
		RaceContainer containerOfPlayer = (RaceContainer) plugin.getRaceManager().getHolderOfPlayer(sender.getName());
		if(container == null){
			sender.sendMessage(LanguageAPI.translateIgnoreError(race_not_exist)
					.replace("race", inspectedRace)
					.build());
			return;
		}
		
		if(container.equals(containerOfPlayer)){
			sender.sendMessage(ChatColor.YELLOW + "=========" + ChatColor.RED + "RACE INFO" + ChatColor.YELLOW + "=========");
		
			if(containerOfPlayer == null){
				sender.sendMessage(LanguageAPI.translateIgnoreError(no_race_selected)
						.build());
				return;
			}

		}else{
			sender.sendMessage(ChatColor.YELLOW + "=========" + ChatColor.RED + "RACE INFO OF: " + inspectedRace + ChatColor.YELLOW + "=========");
		}
			
		
		sender.sendMessage(ChatColor.YELLOW + "Race health: " + ChatColor.LIGHT_PURPLE + container.getRaceMaxHealth());
		sender.sendMessage(ChatColor.YELLOW + "Race name: " + ChatColor.LIGHT_PURPLE + container.getName());
		sender.sendMessage(ChatColor.YELLOW + "Race tag: " + ChatColor.LIGHT_PURPLE + container.getTag());
		sender.sendMessage(ChatColor.YELLOW + "Allowed armor: " + ChatColor.LIGHT_PURPLE + container.getArmorString());
		
		double mana = container.getManaBonus();
		if(mana > 0){
			sender.sendMessage(ChatColor.YELLOW + "+ Mana: " + ChatColor.AQUA + mana);
		}
		
		sender.sendMessage(ChatColor.YELLOW + "=========" + ChatColor.RED + "Traits" + ChatColor.YELLOW + "=========");
		for(Trait trait : container.getVisibleTraits()){
			sender.sendMessage(ChatColor.BLUE + trait.getDisplayName() + " : " + trait.getPrettyConfiguration());
		}
		
		if(container.getVisibleTraits().size() == 0){
			sender.sendMessage(LanguageAPI.translateIgnoreError(no_traits).build());			
		}
	}
	
	private void raceList(CommandSender sender){
		List<String> races = plugin.getRaceManager().listAllVisibleHolders();
		AbstractTraitHolder senderRace = plugin.getRaceManager().getHolderOfPlayer(sender.getName());
		
		if(senderRace == plugin.getRaceManager().getDefaultHolder()){
			races.add(plugin.getRaceManager().getDefaultHolder().getName());
		}
		
		sender.sendMessage(ChatColor.YELLOW + "======LIST OF RACES======");
		
		String yourString = LanguageAPI.translateIgnoreError(your_race).build();
		for(String race : races){
			if(senderRace != null && race.equals(senderRace.getName())){
				sender.sendMessage(ChatColor.RED + race + ChatColor.YELLOW + "  <-- " + yourString);
			}else{	
				sender.sendMessage(ChatColor.BLUE + race);
			}
		}
	}
}
