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

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.commands.CommandInterface;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.gui.HolderInventory;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.classevent.PreClassChangeEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.classevent.PreClassSelectEvent;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.translation.languages.Keys;
import de.tobiyas.racesandclasses.tutorial.TutorialStepContainer;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;
import de.tobiyas.racesandclasses.util.tutorial.TutorialState;

public class CommandExecutor_Class extends Observable implements CommandInterface {
	
	/**
	 * The plugin called stuff upon
	 */
	private RacesAndClasses plugin;
	
	
	/**
	 * Registers the Command "class" to the plugin.
	 */
	public CommandExecutor_Class(){
		plugin = RacesAndClasses.getPlugin();

//		String command = "class";
//		if(plugin.getConfigManager().getGeneralConfig().getConfig_general_disable_commands().contains(command)) return;
//		
//		try{
//			plugin.getCommand(command).setExecutor(this);
//		}catch(Exception e){
//			plugin.log("ERROR: Could not register command /" + command + ".");
//		}
		
		plugin.getTutorialManager().registerObserver(this);
		this.setChanged();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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
					LanguageAPI.sendTranslatedMessage(sender,"no_class_selected_use_info");
					return true;
				}
			}else{
				String className = args[1];
				classHolder = plugin.getClassManager().getHolderByName(className);
				if(classHolder == null){
					LanguageAPI.sendTranslatedMessage(sender,"class_not_exist",
							"class", className);
				}
			}	
			
			info(sender, classHolder);
			return true;
		}
		
		
		//listing of all classes
		if(potentialCommand.equalsIgnoreCase("list")){
			list(sender);
			
			if(sender instanceof Player){
				RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer((Player) sender);
				this.notifyObservers(new TutorialStepContainer(racPlayer, TutorialState.infoClass));
				this.setChanged();
			}
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
					LanguageAPI.sendTranslatedMessage(sender,"already_have_class",
							"clasname", currentClass.getDisplayName());
					return true;
				}
				
				PreClassSelectEvent ccEvent = new PreClassSelectEvent(player, (ClassContainer) plugin.getClassManager().getDefaultHolder());
				plugin.getServer().getPluginManager().callEvent(ccEvent);
				
				if(ccEvent.isCancelled()){
					player.sendMessage(ChatColor.RED + "[RaC] + " + ccEvent.getCancelMessage());
					return true;
				}
				
				HolderInventory holderInventory = new HolderInventory(player, plugin.getClassManager());
				if(holderInventory.getNumberOfHolder() <= 0){
					LanguageAPI.sendTranslatedMessage(sender, "no_class_to_select");
					return true;
				}
				
				player.openInventory(holderInventory);
				LanguageAPI.sendTranslatedMessage(sender, "open_holder",
						"holder", "Class");
				return true;
			}
			
			if(args.length == 1 ){
				postHelp(sender);
				return true;
			}
			
			String potentialClass = args[1];
			if(select(player, potentialClass)){
				this.notifyObservers(new TutorialStepContainer(racPlayer, TutorialState.selectClass));
				this.setChanged();
			}
			
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
					LanguageAPI.sendTranslatedMessage(sender, "no_class_on_change");
					return true;
				}
				
				PreClassSelectEvent ccEvent = new PreClassSelectEvent(player, (ClassContainer) plugin.getClassManager().getDefaultHolder());
				plugin.getServer().getPluginManager().callEvent(ccEvent);
				
				if(ccEvent.isCancelled()){
					player.sendMessage(ChatColor.RED + "[RaC] + " + ccEvent.getCancelMessage());
					return true;
				}
				
				HolderInventory holderInventory = new HolderInventory(player, plugin.getClassManager());
				if(holderInventory.getNumberOfHolder() <= 0){
					LanguageAPI.sendTranslatedMessage(sender,"no_class_to_select");
					return true;
				}
				
				player.openInventory(holderInventory);
				LanguageAPI.sendTranslatedMessage(sender, "open_holder",
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
		
		sender.sendMessage(LanguageAPI.translateIgnoreError("only_players").build());
		return false;
	}
	
	
	private void postHelp(CommandSender player){
		LanguageAPI.sendTranslatedMessage(player, "wrong_command_use",
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
			LanguageAPI.sendTranslatedMessage(player, "no_class_selected");
			return;
		}

		
		player.sendMessage(ChatColor.YELLOW + "ClassHealth: " 
				+ ChatColor.LIGHT_PURPLE + classContainer.getClassHealthModify()
				+ classContainer.getClassHealthModValue());
		player.sendMessage(ChatColor.YELLOW + "Class: " + ChatColor.LIGHT_PURPLE + classContainer.getDisplayName());
		player.sendMessage(ChatColor.YELLOW + "ClassTag: " + ChatColor.LIGHT_PURPLE + classContainer.getTag());
		
		double mana = classContainer.getManaBonus();
		if(mana > 0){
			player.sendMessage(ChatColor.YELLOW + "+ Mana: " + ChatColor.AQUA + mana);
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
			player.sendMessage(LanguageAPI.translateIgnoreError("no_class_in_list")
					.build()
					);
			return;
		}
		
		RaCPlayer racPlayer = null;
		String nameOfOwnClass = "";
		
		if(player instanceof Player){
			racPlayer = RaCPlayerManager.get().getPlayer((Player) player);
			AbstractTraitHolder holder = racPlayer.getclass();
			nameOfOwnClass = holder != null ? holder.getDisplayName() : "";
		}
		
		for(String classe : classes ){
			if(classe.equalsIgnoreCase(nameOfOwnClass)){
				String yourClass = LanguageAPI.translateIgnoreError("your_class")
						.build();
				
				player.sendMessage(ChatColor.RED + classe + ChatColor.YELLOW + "  <-- " + yourClass);
			}else{
				player.sendMessage(ChatColor.BLUE + classe);
			}
		}
	}
	
	private boolean select(Player player, String potentialClass){
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
		potentialClass = potentialClass.toLowerCase();
		
		ClassContainer classContainer = (ClassContainer) plugin.getClassManager().getHolderByName(potentialClass);
		boolean classExists = classContainer != null;
		if(!classExists){
			LanguageAPI.sendTranslatedMessage(player, "class_not_exist",
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
				LanguageAPI.sendTranslatedMessage(player, "class_changed_to",
						"class", classContainer.getDisplayName());
				return true;
			}
			
			return false;
		}else{
			LanguageAPI.sendTranslatedMessage(player, Keys.already_have_class,
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
			LanguageAPI.sendTranslatedMessage(player, "no_class_on_change");
			return;
		}
		
		if(newClassContainer == null){
			LanguageAPI.sendTranslatedMessage(player, "class_not_exist",
					"class", potentialClass);
			return;
		}
		
		if(oldClassContainer != null){
			if(potentialClass.equalsIgnoreCase(oldClassContainer.getDisplayName())){
				LanguageAPI.sendTranslatedMessage(player, "change_to_same_holder",
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
				LanguageAPI.sendTranslatedMessage(player, "class_changed_to",
						"class", potentialClass);
			}else{
				LanguageAPI.sendTranslatedMessage(player, "class_not_exist",
						"class", potentialClass);
			}
		}
		
	}
	
	
	/**
	 * Returns the CommandName
	 */
	@Override
	public String getCommandName(){
		return "class";
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command,
			String alias, String[] args) {
		return new LinkedList<String>();
	}

	@Override
	public String[] getAliases() {
		return new String[]{};
	}
	
	@Override
	public boolean hasAliases() {
		return false;
	}
	
}
