package de.tobiyas.racesandclasses.commands.classes;

import java.util.List;
import java.util.Observable;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.gui.HolderInventory;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.classevent.PreClassChangeEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.classevent.PreClassSelectEvent;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.Trait;
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
			sender.sendMessage(ChatColor.RED + "Classes are not activated.");
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
					sender.sendMessage(ChatColor.RED + "You have no class selected. Use " + ChatColor.LIGHT_PURPLE + "/class info <class name>" 
							+ ChatColor.RED + "  to inspect a class.");
					return true;
				}
			}else{
				String className = args[1];
				classHolder = plugin.getClassManager().getHolderByName(className);
				if(classHolder == null){
					sender.sendMessage(ChatColor.RED + "The class " + ChatColor.LIGHT_PURPLE + className 
							+ ChatColor.RED + " does not exist.");
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
					player.sendMessage(ChatColor.RED + "You already have a class: " + ChatColor.AQUA + currentClass.getName()
							+ ChatColor.RED + ". Use " + ChatColor.LIGHT_PURPLE + "/class change" + ChatColor.RED + " to change your class.");
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
					player.sendMessage(ChatColor.RED + "[RaC] You don't have any Classes to select.");
					return true;
				}
				
				player.openInventory(holderInventory);
				player.sendMessage(ChatColor.GREEN + "Opening Class Selection...");
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
					player.sendMessage(ChatColor.RED + "You don't have any class. Use " + ChatColor.LIGHT_PURPLE + "/class select" 
							+ ChatColor.RED + " to select a class.");
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
					player.sendMessage(ChatColor.RED + "[RaC] You don't have any Classes to select.");
					return true;
				}
				
				player.openInventory(holderInventory);
				player.sendMessage(ChatColor.GREEN + "Opening Class Selection...");
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
		
		sender.sendMessage(ChatColor.RED + "[RAC] Only players can use this command." );
		return false;
	}
	
	
	private void postHelp(CommandSender player){
		player.sendMessage(ChatColor.RED + "Wrong usage. The correct usage is one of the following:");
		player.sendMessage(ChatColor.RED + "/class " + ChatColor.LIGHT_PURPLE + "info");
		player.sendMessage(ChatColor.RED + "/class " + ChatColor.LIGHT_PURPLE + "list");
		if(plugin.getPermissionManager().checkPermissionsSilent(player, PermissionNode.changeClass))
			player.sendMessage(ChatColor.RED + "/class " + ChatColor.LIGHT_PURPLE + "select " + ChatColor.YELLOW + "<classname>");
		
		if(plugin.getPermissionManager().checkPermissionsSilent(player, PermissionNode.selectClass))
			player.sendMessage(ChatColor.RED + "/class " + ChatColor.LIGHT_PURPLE + "change " + ChatColor.YELLOW + "<classname>");
	}
	
	private void info(CommandSender player, AbstractTraitHolder holder){
		ClassContainer classContainer = (ClassContainer) holder;
		
		player.sendMessage(ChatColor.YELLOW + "===== " + ChatColor.RED + "ClassInfo" + ChatColor.YELLOW + " =====");
		
		if(classContainer == null){
			player.sendMessage(ChatColor.RED + "You have no class selected.");
			return;
		}
		
		player.sendMessage(ChatColor.YELLOW + "ClassHealthMod: " + ChatColor.LIGHT_PURPLE + classContainer.getClassHealthModify());
		player.sendMessage(ChatColor.YELLOW + "Class: " + ChatColor.LIGHT_PURPLE + classContainer.getName());
		player.sendMessage(ChatColor.YELLOW + "ClassTag: " + ChatColor.LIGHT_PURPLE + classContainer.getTag());
		player.sendMessage(ChatColor.YELLOW + "==== " + ChatColor.RED + "Class Traits" + ChatColor.YELLOW +" =====");
		
		for(Trait trait : classContainer.getVisibleTraits()){
			player.sendMessage(ChatColor.BLUE + trait.getName() + " : " + trait.getPrettyConfiguration());
		}
	}
	
	private void list(CommandSender player){
		player.sendMessage(ChatColor.YELLOW + "===== " + ChatColor.RED + "Classes" + ChatColor.YELLOW + " =====");
		
		List<String> classes = plugin.getClassManager().getAllHolderNames();
		if(classes.size() == 0){
			player.sendMessage(ChatColor.RED + "No Classes in the list.");
			return;
		}
		
		AbstractTraitHolder holder = plugin.getClassManager().getHolderOfPlayer(player.getName());
		String nameOfOwnClass = holder != null ? holder.getName() : "";
		
		for(String classe : classes ){
			if(classe.equalsIgnoreCase(nameOfOwnClass)){
				player.sendMessage(ChatColor.RED + classe + ChatColor.YELLOW + "  <-- Your Class!");
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
			player.sendMessage(ChatColor.RED + "The class " + ChatColor.LIGHT_PURPLE + potentialClass + ChatColor.RED + " was not found.");
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
				player.sendMessage(ChatColor.GREEN + "You are now a " + ChatColor.LIGHT_PURPLE + potentialClass);
				return true;
			}
			
			return false;
		}else{
			player.sendMessage(ChatColor.RED + "You already have a class: " + ChatColor.LIGHT_PURPLE + classContainer.getName());
			return false;
		}
	}
	
	private void change(Player player, String potentialClass){
		if(!plugin.getPermissionManager().checkPermissions(player, PermissionNode.changeClass)) return;
		ClassContainer oldClassContainer = (ClassContainer) plugin.getClassManager().getHolderOfPlayer(player.getName());
		ClassContainer newClassContainer = (ClassContainer) plugin.getClassManager().getHolderByName(potentialClass);
		
		if(oldClassContainer == null){
			player.sendMessage(ChatColor.RED + "You have no class you could change");
			return;
		}
		
		if(newClassContainer == null){
			player.sendMessage(ChatColor.RED + "The class " + ChatColor.LIGHT_PURPLE + potentialClass + ChatColor.RED + " was not found.");
			return;
		}
		
		if(oldClassContainer != null){
			if(potentialClass.equalsIgnoreCase(oldClassContainer.getName())){
				player.sendMessage(ChatColor.RED + "You are already a " + ChatColor.LIGHT_PURPLE + oldClassContainer.getName());
				return;
			}
			
			
			PreClassChangeEvent event = new PreClassChangeEvent(player, oldClassContainer, newClassContainer);
			plugin.fireEventToBukkit(event);
			if(event.isCancelled()){
				player.sendMessage(ChatColor.RED + event.getCancelMessage());
				return;
			}
			
			if(plugin.getClassManager().changePlayerHolder(player.getName(), potentialClass, true)){
				player.sendMessage(ChatColor.GREEN + "You are now a " + ChatColor.LIGHT_PURPLE + potentialClass);
			}else{
				player.sendMessage(ChatColor.RED + "The class " + ChatColor.LIGHT_PURPLE + potentialClass + ChatColor.RED + " was not found.");
			}
		}
		
	}

}
