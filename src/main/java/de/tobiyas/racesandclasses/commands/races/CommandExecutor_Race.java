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
/*
 * Races - by tobiyas
 * http://
 *
 * powered by Kickstarter
 */

package de.tobiyas.racesandclasses.commands.races;


import static de.tobiyas.racesandclasses.translation.languages.Keys.already_are;
import static de.tobiyas.racesandclasses.translation.languages.Keys.already_have_race;
import static de.tobiyas.racesandclasses.translation.languages.Keys.needs_1_arg;
import static de.tobiyas.racesandclasses.translation.languages.Keys.no_race_selected;
import static de.tobiyas.racesandclasses.translation.languages.Keys.no_race_to_select;
import static de.tobiyas.racesandclasses.translation.languages.Keys.no_traits;
import static de.tobiyas.racesandclasses.translation.languages.Keys.open_holder;
import static de.tobiyas.racesandclasses.translation.languages.Keys.race_changed_to;
import static de.tobiyas.racesandclasses.translation.languages.Keys.race_not_exist;
import static de.tobiyas.racesandclasses.translation.languages.Keys.something_disabled;
import static de.tobiyas.racesandclasses.translation.languages.Keys.your_race;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.APIs.RaceAPI;
import de.tobiyas.racesandclasses.commands.AbstractCommand;
import de.tobiyas.racesandclasses.commands.CommandInterface;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.gui.HolderInventory;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.raceevent.PreRaceChangeEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.raceevent.PreRaceSelectEvent;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.TraitWithRestrictions;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;
import de.tobiyas.util.chat.components.ChatMessageObject;
import de.tobiyas.util.chat.components.TellRawChatMessage;
import de.tobiyas.util.vollotile.VollotileCode.MCVersion;
import de.tobiyas.util.vollotile.VollotileCodeManager;


public class CommandExecutor_Race extends AbstractCommand implements CommandInterface {
	

	public CommandExecutor_Race(){
		super("race");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,String label, String[] args) {
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_enableRaces()){
			LanguageAPI.sendTranslatedMessage(sender, something_disabled,
					"value", "Race");
			return true;
		}
		
		if(args.length == 0){
			postHelp(sender, false);
			return true;
		}
		
		String raceCommand = args[0];
		boolean isPlayer = sender instanceof Player;
			
		//Select race(only if has no race)
		if(raceCommand.equalsIgnoreCase("select")){
			if(!checkPlayer(sender)) return true;
			Player player = (Player) sender;
			RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
			
			if(!plugin.getPermissionManager().checkPermissions(player, PermissionNode.selectRace)) return true;
			
			boolean useGUI = plugin.getConfigManager().getGeneralConfig().isConfig_useRaceGUIToSelect();
			if(useGUI){
				AbstractTraitHolder currentRace = racPlayer.getRace();
				if(currentRace != plugin.getRaceManager().getDefaultHolder()){
					LanguageAPI.sendTranslatedMessage(sender, already_have_race,
							"race", currentRace.getDisplayName());
					
					return true;
				}
				
				PreRaceSelectEvent ccEvent = new PreRaceSelectEvent(player, (RaceContainer) plugin.getRaceManager().getDefaultHolder());
				plugin.getServer().getPluginManager().callEvent(ccEvent);
				
				if(ccEvent.isCancelled()){
					player.sendMessage(ChatColor.RED + "[RaC] " + ccEvent.getCancelMessage());
					return true;
				}
				
				HolderInventory holderInventory = new HolderInventory(player, plugin.getRaceManager());
				
				if(holderInventory.getNumberOfHolder() <= 0){
					LanguageAPI.sendTranslatedMessage(sender, no_race_to_select);
					return true;
				}
				
				player.openInventory(holderInventory);
				LanguageAPI.sendTranslatedMessage(sender, open_holder,
						"holder", "Race");
				return true;
			}
			
			if(args.length != 2){
				LanguageAPI.sendTranslatedMessage(sender, needs_1_arg,
						"command", "/race select <racename>");
				return true;
			}
			
			String potentialRace = args[1];
			selectRace(racPlayer, potentialRace);
			return true;
		}
			
		//Change races (only if has already a race)
		if(raceCommand.equalsIgnoreCase("change")){
			if(!checkPlayer(sender)) return true;
			Player player = (Player) sender;
			RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
			
			if(!plugin.getPermissionManager().checkPermissions(player, PermissionNode.changeRace)) return true;
			
			boolean useGUI = plugin.getConfigManager().getGeneralConfig().isConfig_useRaceGUIToSelect();
			if(useGUI){
				AbstractTraitHolder currentRace = racPlayer.getRace();
				if(currentRace == plugin.getRaceManager().getDefaultHolder()){
					LanguageAPI.sendTranslatedMessage(sender, no_race_selected);
					return true;
				}
				
				PreRaceSelectEvent ccEvent = new PreRaceSelectEvent(player, (RaceContainer) plugin.getRaceManager().getDefaultHolder());
				plugin.getServer().getPluginManager().callEvent(ccEvent);
				
				if(ccEvent.isCancelled()){
					player.sendMessage(ChatColor.RED + "[RaC] " + ccEvent.getCancelMessage());
					return true;
				}
				
				HolderInventory holderInventory = new HolderInventory(player, plugin.getRaceManager());
				if(holderInventory.getNumberOfHolder() <= 0){
					LanguageAPI.sendTranslatedMessage(sender, no_race_to_select);
					return true;
				}
				
				player.openInventory(holderInventory);
				LanguageAPI.sendTranslatedMessage(sender, open_holder,
						"holder", "Race");
				return true;
			}
			
			if(args.length != 2){
				LanguageAPI.sendTranslatedMessage(sender, needs_1_arg,
						"command", "/race change <racename>");
				return true;
			}
			
			String potentialRace = args[1];
			changeRace(racPlayer, potentialRace);
			return true;
		}
		
		//Info to a race
		if(raceCommand.equalsIgnoreCase("info")){			
			RaCPlayer racPlayer = isPlayer ? RaCPlayerManager.get().getPlayer((Player)sender) : null;
			String inspectedRace = isPlayer ? racPlayer.getRace().getDisplayName() : "";
			
			if(args.length > 1){
				inspectedRace = args[1];
			}
			
			raceInfo(sender, inspectedRace);
			return true;
		}
		
		//lists all races
		if(raceCommand.equalsIgnoreCase("list")){
			raceList(sender);
			
			if(sender instanceof Player){}
			return true;
		}
		
		//Generate a Book!
		if(raceCommand.equalsIgnoreCase("book")){
			if(!VollotileCodeManager.getVollotileCode().getVersion().isVersionGreaterOrEqual(MCVersion.v1_8_R1)){
				sender.sendMessage(ChatColor.RED + "Only in MC 1.8+.");
				return true;
			}
			
			if(sender instanceof Player){
				Player pl = (Player) sender;
				ItemStack book = generateRacesBook(RaCPlayerManager.get().getPlayer(pl));
				pl.getInventory().addItem(book);
			}
			
			sender.sendMessage("Done");	
			return true;
		}
			
		postHelp(sender, true);
		return true;
	}
	
	
	private boolean checkPlayer(CommandSender sender) {
		if(sender instanceof Player){
			return true;
		}
		
		LanguageAPI.sendTranslatedMessage(sender, "only_players");
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
	
	private void selectRace(RaCPlayer player, String newRaceName){
		RaceContainer container = player.getRace();
		RaceContainer stdContainer = (RaceContainer) plugin.getRaceManager().getDefaultHolder();
		if(container == null || container == stdContainer){
			RaceContainer raceContainer = (RaceContainer) plugin.getRaceManager().getHolderByName(newRaceName);
			
			if(raceContainer == null){
				LanguageAPI.sendTranslatedMessage(player, race_not_exist,
						"race", newRaceName);
				return;
			}
			
			if(raceContainer != null && raceContainer == stdContainer){
				LanguageAPI.sendTranslatedMessage(player, race_not_exist,
						"race", "default race");
				return;
			}
			
			PreRaceSelectEvent selectEvent = new PreRaceSelectEvent(player.getPlayer(), raceContainer);
			plugin.fireEventToBukkit(selectEvent);
			
			if(selectEvent.isCancelled()){
				player.sendMessage(ChatColor.RED + selectEvent.getCancelMessage());
				return;
			}
			
			if(plugin.getRaceManager().addPlayerToHolder(player, newRaceName, true)){
				LanguageAPI.sendTranslatedMessage(player, race_changed_to,
						"race", newRaceName);
			}
				
		}else{
			LanguageAPI.sendTranslatedMessage(player, already_have_race,
					"race", container.getDisplayName());
		}
	}
	
	private void changeRace(RaCPlayer player, String newRace){
		AbstractTraitHolder oldContainer = player.getRace();
		AbstractTraitHolder stdContainer = plugin.getRaceManager().getDefaultHolder();
		
		if(oldContainer != null && oldContainer != plugin.getRaceManager().getDefaultHolder()){
			if(newRace.equalsIgnoreCase(oldContainer.getDisplayName())){
				player.sendMessage(LanguageAPI.translateIgnoreError(already_are)
						.replace("holders", oldContainer.getDisplayName())
						.build());
				return;
			}
			
			if(plugin.getRaceManager().getHolderByName(newRace) != null && plugin.getRaceManager().getHolderByName(newRace) == stdContainer){
				LanguageAPI.sendTranslatedMessage(player, race_not_exist,
						"race", plugin.getRaceManager().getDefaultHolder().getDisplayName());
				return;
			}
			
			AbstractTraitHolder newContainer = plugin.getRaceManager().getHolderByName(newRace);
			if(newContainer == null){
				LanguageAPI.sendTranslatedMessage(player, race_not_exist,
						"race", newRace);
				return;
			}
			
			PreRaceChangeEvent selectEvent = new PreRaceChangeEvent(player.getPlayer(), (RaceContainer) oldContainer, (RaceContainer) newContainer);
			plugin.fireEventToBukkit(selectEvent);
			
			if(selectEvent.isCancelled()){
				player.sendMessage(ChatColor.RED + selectEvent.getCancelMessage());
				return;
			}
			
			if(plugin.getRaceManager().changePlayerHolder(player, newRace, true)){
				LanguageAPI.sendTranslatedMessage(player, race_changed_to,
						"race", newRace);
			}else{
				LanguageAPI.sendTranslatedMessage(player, race_not_exist,
						"race", newRace);
			}
		}else{
			LanguageAPI.sendTranslatedMessage(player, no_race_selected);
		}
	}
	
	private void raceInfo(CommandSender sender, String inspectedRace){
		RaceContainer container = (RaceContainer) plugin.getRaceManager().getHolderByName(inspectedRace);
		
		RaCPlayer player = null;
		RaceContainer containerOfPlayer = null;
		if(sender instanceof Player){
			player = RaCPlayerManager.get().getPlayer((Player)sender);
			containerOfPlayer = player.getRace();
		}
		
		if(container == null){
			LanguageAPI.sendTranslatedMessage(sender, race_not_exist,
					"race", inspectedRace);
			return;
		}
		
		if(container.equals(containerOfPlayer)){
			sender.sendMessage(ChatColor.YELLOW + "=========" + ChatColor.RED + "RACE INFO" + ChatColor.YELLOW + "=========");
		
			if(containerOfPlayer == null){
				LanguageAPI.sendTranslatedMessage(sender, no_race_selected);
				return;
			}

		}else{
			sender.sendMessage(ChatColor.YELLOW + "=========" + ChatColor.RED + "RACE INFO OF: " + inspectedRace + ChatColor.YELLOW + "=========");
		}
			
		
		sender.sendMessage(ChatColor.YELLOW + "Race Bonus-Health: " + ChatColor.LIGHT_PURPLE + container.getMaxHealthMod(1));
		sender.sendMessage(ChatColor.YELLOW + "Race name: " + ChatColor.LIGHT_PURPLE + container.getDisplayName());
		sender.sendMessage(ChatColor.YELLOW + "Race tag: " + ChatColor.LIGHT_PURPLE + container.getTag());
		sender.sendMessage(ChatColor.YELLOW + "Allowed armor: " + ChatColor.LIGHT_PURPLE + container.getArmorString());
		
		double mana = container.getManaBonus(1);
		if(mana > 0){
			sender.sendMessage(ChatColor.YELLOW + "+ Mana: " + ChatColor.AQUA + mana);
		}
		
		sender.sendMessage(ChatColor.YELLOW + "=========" + ChatColor.RED + "Traits" + ChatColor.YELLOW + "=========");
		for(Trait trait : container.getVisibleTraits()){
			sender.sendMessage(ChatColor.BLUE + trait.getDisplayName() + " : " + trait.getPrettyConfiguration());
		}
		
		if(container.getVisibleTraits().size() == 0){
			LanguageAPI.sendTranslatedMessage(sender, no_traits);			
		}
	}
	
	private void raceList(CommandSender sender){
		List<String> races = plugin.getRaceManager().listAllVisibleHolders();
		
		boolean isPlayer = sender instanceof Player;
		RaCPlayer player = isPlayer ? RaCPlayerManager.get().getPlayer((Player)sender) : null;
		
		AbstractTraitHolder senderRace = isPlayer ? player.getRace() : null;
		if(senderRace == plugin.getRaceManager().getDefaultHolder()){
			races.add(plugin.getRaceManager().getDefaultHolder().getDisplayName());
		}
		
		sender.sendMessage(ChatColor.YELLOW + "======LIST OF RACES======");
		
		String yourString = LanguageAPI.translateIgnoreError(your_race).build();
		for(String race : races){
			if(senderRace != null && race.equals(senderRace.getDisplayName())){
				sender.sendMessage(ChatColor.RED + race + ChatColor.YELLOW + "  <-- " + yourString);
			}else{	
				sender.sendMessage(ChatColor.BLUE + race);
			}
		}
	}
	
	
	/**
	 * Generates a Book with all Races.
	 * 
	 * @return a book
	 */
	private ItemStack generateRacesBook(RaCPlayer player){
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta meta = (BookMeta) book.getItemMeta();
		meta.setDisplayName(ChatColor.AQUA + "" + ChatColor.UNDERLINE + "Races");
		meta.setLore(Arrays.asList( new String[]{ChatColor.AQUA + "This book contains", ChatColor.AQUA + "all Races."} ));
		meta.setAuthor("RaC-Plugin");
		meta.setTitle(ChatColor.AQUA + "" + ChatColor.UNDERLINE + "Races");
		book.setItemMeta(meta);
		
		
		List<TellRawChatMessage> pages = new LinkedList<>();
		//Generate 1st Page
		TellRawChatMessage firstPage = new TellRawChatMessage();
		pages.add(firstPage);

		firstPage.addSimpleText("Races:", false, true, false, true, false, ChatColor.GRAY)
		.addNewLine()
		.addNewLine();

		
		
		//Generate the Rest of the Pages:
		int nr = 2;
		for(String raceName : RaceAPI.getAllRaceNames()){
			if(raceName.equals(RaceAPI.getDefaultRaceName())) continue;
			
			RaceContainer container = RaceAPI.getRaceByName(raceName);
			if(container != null) pages.add(generateBookPageForRace(container, player));
			
			//Add the Front page.
			firstPage.addSimpleText(container.getDisplayName(), false, true, false, false, false, ChatColor.AQUA)
			.addSimpleText("  - page " + nr, false, false, false, false, false, ChatColor.GRAY)
			.addNewLine();
			
			nr++;
		}
		
		VollotileCodeManager.getVollotileCode().editBookToPages(book, pages);
		return book;
	}
	
	
	/**
	 * Generates a Book Info page for the Race.
	 * 
	 * @param race to generate for
	 * @param player 
	 * @return the Tell Raw.
	 */
	private TellRawChatMessage generateBookPageForRace(RaceContainer race, RaCPlayer player){
		DecimalFormat f = new DecimalFormat("0.0");
		TellRawChatMessage page = new TellRawChatMessage();
		
		/*
		 * <Race>
		 * 
		 * Armor: <>
		 * Health: <>
		 * Mana: <>
		 * 
		 * Traits:
		 * - Name : Description
		 * ......
		 * 
		 */
		
		page.append(new ChatMessageObject("Race: " + race.getDisplayName()).addChatColor(ChatColor.GRAY).addBold().addUnderlined())
		.addNewLine()
		.addNewLine()
		
		.append(new ChatMessageObject("Armor").addChatColor(ChatColor.DARK_BLUE).addBold().removeUnderlined()
				.addPopupHover( ChatColor.RED + (race.getArmorString() )) )
		.addSimpleText("/", false, false, false, false, false, ChatColor.GRAY)
		
		.append(new ChatMessageObject("HealthBonus").addChatColor(ChatColor.BLUE).addBold().removeUnderlined()
				.addPopupHover(ChatColor.RED + f.format(race.getMaxHealthMod(player.getLevel()))))
		.addSimpleText("/", false, false, false, false, false, ChatColor.GRAY)
		
		.append(new ChatMessageObject("Mana").addChatColor(ChatColor.AQUA).addBold().removeUnderlined()
				.addPopupHover(ChatColor.RED + f.format(race.getManaBonus(player.getLevel()))))
		.addNewLine()
		.addNewLine()
		
		.append(new ChatMessageObject("Traits:").addChatColor(ChatColor.GRAY).addBold().addUnderlined())
		.addNewLine();
		
		List<Trait> traits = new LinkedList<>();
		traits.addAll(race.getVisibleTraits());
		
		Collections.sort(traits);
		for(Trait trait : race.getVisibleTraits()){
			String popup = ChatColor.GRAY + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD
					+ "Trait: " + trait.getDisplayName() + "\n" + ChatColor.RESET;
			
			if(trait instanceof TraitWithRestrictions){
				TraitWithRestrictions twr = (TraitWithRestrictions) trait;
				if(twr.getMinLevel() > 1) popup += ChatColor.GRAY + "MinLvl: " + ChatColor.RED + twr.getMinLevel() + "\n";
				if(twr.getMaxLevel() < 100000) popup += ChatColor.GRAY + "MaxLvl: " + ChatColor.RED + twr.getMaxLevel() + "\n";
			}
			
			popup += ChatColor.GRAY + "Description: " + ChatColor.RED + trait.getPrettyConfiguration();
			
			page.append(new ChatMessageObject("-" + trait.getDisplayName()).addChatColor(ChatColor.AQUA).removeBold().removeUnderlined()
				.addPopupHover(popup))
			.addNewLine();
		}
		
		
		return page;
	}
	
	
}
