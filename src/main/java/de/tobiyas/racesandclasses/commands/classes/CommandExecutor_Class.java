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
package de.tobiyas.racesandclasses.commands.classes;

import static de.tobiyas.racesandclasses.translation.languages.Keys.already_have_class;
import static de.tobiyas.racesandclasses.translation.languages.Keys.change_to_same_holder;
import static de.tobiyas.racesandclasses.translation.languages.Keys.class_changed_to;
import static de.tobiyas.racesandclasses.translation.languages.Keys.class_not_exist;
import static de.tobiyas.racesandclasses.translation.languages.Keys.no_class_in_list;
import static de.tobiyas.racesandclasses.translation.languages.Keys.no_class_on_change;
import static de.tobiyas.racesandclasses.translation.languages.Keys.no_class_selected;
import static de.tobiyas.racesandclasses.translation.languages.Keys.no_class_selected_use_info;
import static de.tobiyas.racesandclasses.translation.languages.Keys.no_class_to_select;
import static de.tobiyas.racesandclasses.translation.languages.Keys.only_players;
import static de.tobiyas.racesandclasses.translation.languages.Keys.open_holder;
import static de.tobiyas.racesandclasses.translation.languages.Keys.wrong_command_use;
import static de.tobiyas.racesandclasses.translation.languages.Keys.your_class;

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

import de.tobiyas.racesandclasses.APIs.ClassAPI;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.commands.AbstractCommand;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.gui.HolderInventory;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.HolderPreSelectEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.classevent.PreClassChangeEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.classevent.PreClassSelectEvent;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.TraitWithRestrictions;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;
import de.tobiyas.util.chat.components.ChatMessageObject;
import de.tobiyas.util.chat.components.TellRawChatMessage;
import de.tobiyas.util.vollotile.VollotileCode.MCVersion;
import de.tobiyas.util.vollotile.VollotileCodeManager;

public class CommandExecutor_Class extends AbstractCommand {
	
	
	/**
	 * Registers the Command "class" to the plugin.
	 */
	public CommandExecutor_Class(){
		super("class");
	}

	@Override
	public boolean onInternalCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_classes_enable()){
			LanguageAPI.sendTranslatedMessage(sender, "something_disabled",
					"value", "Classes");
			return true;
		}
		
		if(args.length == 0){
			postHelp(sender);
			return true;
		}
		
		String potentialCommand = args[0];
		boolean isPlayer = sender instanceof Player;
		
		//Info on certain class
		if(potentialCommand.equalsIgnoreCase("info")){
			AbstractTraitHolder classHolder = null;
			RaCPlayer racPlayer = isPlayer ? RaCPlayerManager.get().getPlayer((Player) sender) : null;
			
			if(args.length < 2){
				classHolder = isPlayer ? racPlayer.getclass() : null;
				if(classHolder == null){
					LanguageAPI.sendTranslatedMessage(sender, no_class_selected_use_info);
					return true;
				}
			}else{
				String className = args[1];
				classHolder = plugin.getClassManager().getHolderByName(className);
				if(classHolder == null){
					LanguageAPI.sendTranslatedMessage(sender, class_not_exist,
							"class", className);
					return true;
				}
			}	
			
			info(sender, classHolder);
			return true;
		}
		
		
		//listing of all classes
		if(potentialCommand.equalsIgnoreCase("list")){
			list(sender);
			return true;
		}
		
		
		//Select class(only if has no class)
		if(potentialCommand.equalsIgnoreCase("select")){
			if(!checkIsPlayer(sender)) return true;
			
			if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.selectClass)) return true;

			Player player = (Player) sender;
			RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
			
			boolean useGUI = plugin.getConfigManager().getGeneralConfig().isConfig_useClassGUIToSelect();
			if(useGUI){
				
				AbstractTraitHolder currentClass = racPlayer.getclass();
				if(currentClass != plugin.getClassManager().getDefaultHolder()){
					LanguageAPI.sendTranslatedMessage(sender, already_have_class,
							"class", currentClass.getDisplayName());
					return true;
				}
				
				PreClassSelectEvent ccEvent = new PreClassSelectEvent(player, (ClassContainer) plugin.getClassManager().getDefaultHolder());
				plugin.getServer().getPluginManager().callEvent(ccEvent);
				
				if(ccEvent.isCancelled()){
					player.sendMessage(ChatColor.RED + "[RaC] " + ccEvent.getCancelMessage());
					return true;
				}
				
				HolderInventory holderInventory = new HolderInventory(player, plugin.getClassManager());
				if(holderInventory.getNumberOfHolder() <= 0){
					LanguageAPI.sendTranslatedMessage(sender, no_class_to_select);
					return true;
				}
				
				player.openInventory(holderInventory);
				LanguageAPI.sendTranslatedMessage(sender, open_holder,
						"holder", "Class");
				return true;
			}
			
			if(args.length == 1 ){
				postHelp(sender);
				return true;
			}
			
			String potentialClass = args[1];
			if(select(player, potentialClass)){}
			
			return true;
		}
		
		
		//Generate a Book!
		if(potentialCommand.equalsIgnoreCase("book")){
			if(!VollotileCodeManager.getVollotileCode().getVersion().isVersionGreaterOrEqual(MCVersion.v1_8_R1)){
				sender.sendMessage(ChatColor.RED + "Only in MC 1.8+.");
				return true;
			}
			
			if(sender instanceof Player){
				Player pl = (Player) sender;
				ItemStack book = generateClassesBook(RaCPlayerManager.get().getPlayer(pl));
				pl.getInventory().addItem(book);
			}
			
			sender.sendMessage("Done");	
			return true;
		}
		
				
		//Change class (only if has already a class)
		if(potentialCommand.equalsIgnoreCase("change")){
			if(!checkIsPlayer(sender)) return true;
			if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.changeClass)) return true;

			Player player = (Player) sender;
			RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer((Player) sender);
			
			boolean useGUI = plugin.getConfigManager().getGeneralConfig().isConfig_useClassGUIToSelect();
			if(useGUI){
				AbstractTraitHolder currentClass = racPlayer.getclass();
				if(currentClass == plugin.getClassManager().getDefaultHolder()){
					LanguageAPI.sendTranslatedMessage(sender, no_class_on_change);
					return true;
				}
				
				PreClassSelectEvent ccEvent = new PreClassSelectEvent(player, (ClassContainer) plugin.getClassManager().getDefaultHolder());
				plugin.getServer().getPluginManager().callEvent(ccEvent);
				
				if(ccEvent.isCancelled()){
					player.sendMessage(ChatColor.RED + "[RaC] " + ccEvent.getCancelMessage());
					return true;
				}
				
				HolderInventory holderInventory = new HolderInventory(player, plugin.getClassManager());
				if(holderInventory.getNumberOfHolder() <= 0){
					LanguageAPI.sendTranslatedMessage(sender, no_class_to_select);
					return true;
				}
				
				player.openInventory(holderInventory);
				LanguageAPI.sendTranslatedMessage(sender, open_holder,
						"holder", "Class");
				return true;
			}			
			
			if(args.length == 1){
				postHelp(player);
				return true;
			}
			
			String potentialClass = args[1];
			
			change(player, potentialClass);
			return true;
		}
		
		postHelp(sender);
		return true;
	}
	
	
	private boolean checkIsPlayer(CommandSender sender) {
		if(sender instanceof Player){
			return true;
		}
		
		sender.sendMessage(LanguageAPI.translateIgnoreError(only_players).build());
		return false;
	}
	
	
	private void postHelp(CommandSender player){
		LanguageAPI.sendTranslatedMessage(player, wrong_command_use,
				"command", "");
		player.sendMessage(ChatColor.RED + "/class " + ChatColor.LIGHT_PURPLE + "info");
		player.sendMessage(ChatColor.RED + "/class " + ChatColor.LIGHT_PURPLE + "list");
		if(plugin.getPermissionManager().checkPermissionsSilent(player, PermissionNode.changeClass)){
			player.sendMessage(ChatColor.RED + "/class " + ChatColor.LIGHT_PURPLE + "select " + ChatColor.YELLOW + "<classname>");
		}
			
		if(plugin.getPermissionManager().checkPermissionsSilent(player, PermissionNode.selectClass)){
			player.sendMessage(ChatColor.RED + "/class " + ChatColor.LIGHT_PURPLE + "change " + ChatColor.YELLOW + "<classname>");
		}
	}
	
	private void info(CommandSender player, AbstractTraitHolder holder){
		ClassContainer classContainer = (ClassContainer) holder;
		
		player.sendMessage(ChatColor.YELLOW + "===== " + ChatColor.RED + "ClassInfo" + ChatColor.YELLOW + " =====");
		
		if(classContainer == null){
			LanguageAPI.sendTranslatedMessage(player, no_class_selected);
			return;
		}

		
		player.sendMessage(ChatColor.YELLOW + "Class HealthBonus: " 
				+ ChatColor.LIGHT_PURPLE + classContainer.getMaxHealthMod(1));
		player.sendMessage(ChatColor.YELLOW + "Class: " + ChatColor.LIGHT_PURPLE + classContainer.getDisplayName());
		player.sendMessage(ChatColor.YELLOW + "ClassTag: " + ChatColor.LIGHT_PURPLE + classContainer.getTag());
		
		double mana = classContainer.getManaBonus(1);
		if(mana > 0){
			player.sendMessage(ChatColor.YELLOW + "+ Mana: " + ChatColor.AQUA + mana);
		}
		
		String armorString = classContainer.getArmorString();
		if(!armorString.isEmpty()){
			player.sendMessage(ChatColor.YELLOW + "Armor: " + ChatColor.AQUA + armorString);
		}
		
		player.sendMessage(ChatColor.YELLOW + "==== " + ChatColor.RED + "Class Traits" + ChatColor.YELLOW +" =====");
		for(Trait trait : classContainer.getVisibleTraits()){
			player.sendMessage(ChatColor.BLUE + trait.getDisplayName() + " : " + trait.getPrettyConfiguration());
		}
		
		if(classContainer.getVisibleTraits().size() == 0){
			player.sendMessage(ChatColor.BLUE + "No Traits.");			
		}
	}
	
	private void list(CommandSender player){
		player.sendMessage(ChatColor.YELLOW + "===== " + ChatColor.RED + "Classes" + ChatColor.YELLOW + " =====");
		
		List<String> classes = plugin.getClassManager().getAllHolderNames();
		if(classes.size() == 0){
			player.sendMessage(LanguageAPI.translateIgnoreError(no_class_in_list)
					.build()
					);
			return;
		}
		
		String nameOfOwnClass = "";
		
		if(player instanceof Player){
			RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer((Player) player);
			AbstractTraitHolder holder = racPlayer.getclass();
			nameOfOwnClass = holder != null ? holder.getDisplayName() : "";
		}
		
		for(String classe : classes ){
			if(player instanceof Player){
				Player pl = (Player) player;
				if(!hasPermission(pl, ClassAPI.getClassByName(classe))) continue;
			}
			
			if(classe.equalsIgnoreCase(nameOfOwnClass)){
				String yourClass = LanguageAPI.translateIgnoreError(your_class)
						.build();
				
				player.sendMessage(ChatColor.RED + classe + ChatColor.YELLOW + "  <-- " + yourClass);
			}else{
				player.sendMessage(ChatColor.BLUE + classe);
			}
		}
	}
	
	/**
	 * Checks if a player has the Permission for a holders
	 * 
	 * @param holders to check
	 * @param manager the manager of the holders to check
	 * @return true if the player has access, false otherwise.
	 */
	private boolean hasPermission(Player player, ClassContainer holder) {		
		HolderPreSelectEvent event = null;
		event = new PreClassSelectEvent(player, holder, true, false);
		
		plugin.getServer().getPluginManager().callEvent(event);
		return !event.isCancelled();
	}
	
	private boolean select(Player player, String potentialClass){
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
		potentialClass = potentialClass.toLowerCase();
		
		ClassContainer classContainer = (ClassContainer) plugin.getClassManager().getHolderByName(potentialClass);
		boolean classExists = classContainer != null;
		if(!classExists){
			LanguageAPI.sendTranslatedMessage(player, class_not_exist,
					"class", potentialClass);
			
			return false;
		}
		
		PreClassSelectEvent event = new PreClassSelectEvent(player, classContainer);
		plugin.fireEventToBukkit(event);
		if(event.isCancelled()){
			player.sendMessage(ChatColor.RED + event.getCancelMessage());
			return false;
		}
		
		
		AbstractTraitHolder currentHolder = racPlayer.getclass();
		AbstractTraitHolder defaultHolder = plugin.getClassManager().getDefaultHolder();
		if(currentHolder == defaultHolder){
			if(plugin.getClassManager().addPlayerToHolder(racPlayer, potentialClass, true)){
				LanguageAPI.sendTranslatedMessage(player, class_changed_to,
						"class", classContainer.getDisplayName());
				return true;
			}
			
			return false;
		}else{
			LanguageAPI.sendTranslatedMessage(player, already_have_class,
					"class", classContainer.getDisplayName());
			return false;
		}
	}
	
	private void change(Player player, String potentialClass){
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
		if(!plugin.getPermissionManager().checkPermissions(player, PermissionNode.changeClass)) return;
		ClassContainer oldClassContainer = (ClassContainer) plugin.getClassManager().getHolderOfPlayer(racPlayer);
		ClassContainer newClassContainer = (ClassContainer) plugin.getClassManager().getHolderByName(potentialClass);
		
		if(oldClassContainer == null){
			LanguageAPI.sendTranslatedMessage(player, no_class_on_change);
			return;
		}
		
		if(newClassContainer == null){
			LanguageAPI.sendTranslatedMessage(player, class_not_exist,
					"class", potentialClass);
			return;
		}
		
		if(oldClassContainer != null){
			if(potentialClass.equalsIgnoreCase(oldClassContainer.getDisplayName())){
				LanguageAPI.sendTranslatedMessage(player, change_to_same_holder,
						"holder", oldClassContainer.getDisplayName());
				return;
			}
			
			
			PreClassChangeEvent event = new PreClassChangeEvent(player, oldClassContainer, newClassContainer);
			plugin.fireEventToBukkit(event);
			if(event.isCancelled()){
				player.sendMessage(ChatColor.RED + event.getCancelMessage());
				return;
			}
			
			if(plugin.getClassManager().changePlayerHolder(racPlayer, potentialClass, true)){
				LanguageAPI.sendTranslatedMessage(player, class_changed_to,
						"class", potentialClass);
			}else{
				LanguageAPI.sendTranslatedMessage(player, class_not_exist,
						"class", potentialClass);
			}
		}
		
	}
	
	
	
	/**
	 * Generates a Book with all Races.
	 * 
	 * @return a book
	 */
	private ItemStack generateClassesBook(RaCPlayer player){
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta meta = (BookMeta) book.getItemMeta();
		meta.setDisplayName(ChatColor.AQUA + "" + ChatColor.UNDERLINE + "Classes");
		meta.setLore(Arrays.asList( new String[]{ChatColor.AQUA + "This book contains", ChatColor.AQUA + "all Classes."} ));
		meta.setAuthor("RaC-Plugin");
		meta.setTitle(ChatColor.AQUA + "" + ChatColor.UNDERLINE + "Classes");
		book.setItemMeta(meta);
		
		
		List<TellRawChatMessage> pages = new LinkedList<>();
		//Generate 1st Page
		TellRawChatMessage firstPage = new TellRawChatMessage();
		pages.add(firstPage);

		firstPage.addSimpleText("Classes:", false, true, false, true, false, ChatColor.GRAY)
		.addNewLine()
		.addNewLine();

		
		
		//Generate the Rest of the Pages:
		int nr = 2;
		for(String raceName : ClassAPI.getAllClassNames()){
			if(raceName.equals(ClassAPI.getDefaultClassName())) continue;
			
			ClassContainer container = ClassAPI.getClassByName(raceName);
			if(container != null) pages.add(generateBookPageForClass(container, player));
			
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
	 * @param clazz to generate for
	 * @return the Tell Raw.
	 */
	private TellRawChatMessage generateBookPageForClass(ClassContainer clazz, RaCPlayer player){
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
		
		page.append(new ChatMessageObject("Class: " + clazz.getDisplayName()).addChatColor(ChatColor.GRAY).addBold().addUnderlined())
		.addNewLine()
		.addNewLine()
		
		.append(new ChatMessageObject("Armor").addChatColor(ChatColor.DARK_BLUE).addBold().removeUnderlined()
				.addPopupHover( ChatColor.RED + (clazz.getArmorString() )) )
		.addSimpleText("/", false, false, false, false, false, ChatColor.GRAY)
		
		.append(new ChatMessageObject("HealthBonus").addChatColor(ChatColor.BLUE).addBold().removeUnderlined()
				.addPopupHover(ChatColor.RED + f.format(clazz.getMaxHealthMod(player.getLevel()))))
		.addSimpleText("/", false, false, false, false, false, ChatColor.GRAY)
		
		.append(new ChatMessageObject("Mana").addChatColor(ChatColor.AQUA).addBold().removeUnderlined()
				.addPopupHover(ChatColor.RED + f.format(clazz.getManaBonus(player.getLevel()))))
		.addNewLine()
		.addNewLine()
		
		.append(new ChatMessageObject("Traits:").addChatColor(ChatColor.GRAY).addBold().addUnderlined())
		.addNewLine();
		
		List<Trait> traits = new LinkedList<>();
		traits.addAll(clazz.getVisibleTraits());
		
		Collections.sort(traits);
		for(Trait trait : clazz.getVisibleTraits()){
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
