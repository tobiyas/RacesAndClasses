package de.tobiyas.racesandclasses.commands.classes;

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
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.gui.HolderInventory;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.classevent.PreClassChangeEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.classevent.PreClassSelectEvent;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.tutorial.TutorialStepContainer;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;
import de.tobiyas.racesandclasses.util.tutorial.TutorialState;

public class CommandExecutor_Class extends Observable implements CommandExecutor {
	
	/**
	 * The plugin called stuff upon
	 */
	private RacesAndClasses plugin;
	
	
	/**
	 * Registers the Command "class" to the plugin.
	 */
	public CommandExecutor_Class(){
		plugin = RacesAndClasses.getPlugin();
		try{
			plugin.getCommand("class").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /class.");
		}
		
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
		
		//Info on certain class
		if(potentialCommand.equalsIgnoreCase("info")){
			AbstractTraitHolder classHolder = null;
			
			if(args.length < 2){
				classHolder = plugin.getClassManager().getHolderOfPlayer(sender.getName());
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
				this.notifyObservers(new TutorialStepContainer(sender.getName(), TutorialState.infoClass));
				this.setChanged();
			}
			return true;
		}
		
		
		//Select class(only if has no class)
		if(potentialCommand.equalsIgnoreCase("select")){
			if(!checkIsPlayer(sender)) return true;
			
			if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.selectClass)) return true;

			Player player = (Player) sender;
			
			boolean useGUI = plugin.getConfigManager().getGeneralConfig().isConfig_useClassGUIToSelect();
			if(useGUI){
				
				AbstractTraitHolder currentClass = plugin.getClassManager().getHolderOfPlayer(player.getName());
				if(currentClass != plugin.getClassManager().getDefaultHolder()){
					LanguageAPI.sendTranslatedMessage(sender,"already_have_class",
							"clasname", currentClass.getName());
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
				this.notifyObservers(new TutorialStepContainer(player.getName(), TutorialState.selectClass));
				this.setChanged();
			}
			
			return true;
		}
				
		//Change class (only if has already a class)
		if(potentialCommand.equalsIgnoreCase("change")){
			if(!checkIsPlayer(sender)) return true;
			if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.changeClass)) return true;

			Player player = (Player) sender;
			
			boolean useGUI = plugin.getConfigManager().getGeneralConfig().isConfig_useClassGUIToSelect();
			if(useGUI){
				
				AbstractTraitHolder currentClass = plugin.getClassManager().getHolderOfPlayer(player.getName());
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
		player.sendMessage(ChatColor.YELLOW + "Class: " + ChatColor.LIGHT_PURPLE + classContainer.getName());
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
		
		AbstractTraitHolder holder = plugin.getClassManager().getHolderOfPlayer(player.getName());
		String nameOfOwnClass = holder != null ? holder.getName() : "";
		
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
		
		
		AbstractTraitHolder currentHolder = plugin.getClassManager().getHolderOfPlayer(player.getName());
		if(currentHolder == null){	
			if(plugin.getClassManager().addPlayerToHolder(player.getName(), potentialClass, true)){
				LanguageAPI.sendTranslatedMessage(player, "class_changed_to",
						"class", potentialClass);
				return true;
			}
			
			return false;
		}else{
			LanguageAPI.sendTranslatedMessage(player, "class_changed_to",
					"class", classContainer.getName());
			return false;
		}
	}
	
	private void change(Player player, String potentialClass){
		if(!plugin.getPermissionManager().checkPermissions(player, PermissionNode.changeClass)) return;
		ClassContainer oldClassContainer = (ClassContainer) plugin.getClassManager().getHolderOfPlayer(player.getName());
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
			if(potentialClass.equalsIgnoreCase(oldClassContainer.getName())){
				LanguageAPI.sendTranslatedMessage(player, "change_to_same_holder",
						"holder", oldClassContainer.getName());
				return;
			}
			
			
			PreClassChangeEvent event = new PreClassChangeEvent(player, oldClassContainer, newClassContainer);
			plugin.fireEventToBukkit(event);
			if(event.isCancelled()){
				player.sendMessage(ChatColor.RED + event.getCancelMessage());
				return;
			}
			
			if(plugin.getClassManager().changePlayerHolder(player.getName(), potentialClass, true)){
				LanguageAPI.sendTranslatedMessage(player, "class_changed_to",
						"class", potentialClass);
			}else{
				LanguageAPI.sendTranslatedMessage(player, "class_not_exist",
						"class", potentialClass);
			}
		}
		
	}

}
