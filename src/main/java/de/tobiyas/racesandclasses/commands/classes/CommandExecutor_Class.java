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
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.gui.HolderInventory;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.classevent.ClassChangeEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.classevent.ClassSelectEvent;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.Trait;
import de.tobiyas.racesandclasses.tutorial.TutorialManager;
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
		
		TutorialManager.registerObserver(this);
		this.setChanged();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_classes_enable()){
			sender.sendMessage(ChatColor.RED + "Classes are not activated.");
			return true;
		}
		
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only Players can use this command");
			return true;
		}
		
		Player player = (Player) sender;
		
		if(args.length == 0){
			postHelp(player);
			return true;
		}
		
		String potentialCommand = args[0];
		
		//Info on certain class
		if(potentialCommand.equalsIgnoreCase("info")){
			AbstractTraitHolder classHolder = null;
			
			if(args.length < 2){
				classHolder = ClassManager.getInstance().getHolderOfPlayer(player.getName());
				if(classHolder == null){
					player.sendMessage(ChatColor.RED + "You have no class selected. Use " + ChatColor.LIGHT_PURPLE + "/class info <class name>" 
							+ ChatColor.RED + "  to inspect a class.");
					return true;
				}				
			}else{
				String className = args[1];
				classHolder = ClassManager.getInstance().getHolderByName(className);
				if(classHolder == null){
					player.sendMessage(ChatColor.RED + "The class " + ChatColor.LIGHT_PURPLE + className 
							+ ChatColor.RED + " does not exist.");
				}
			}	
			
			info(player, classHolder);
			return true;
		}
		
		
		//listing of all classes
		if(potentialCommand.equalsIgnoreCase("list")){
			list(player);
			this.notifyObservers(new TutorialStepContainer(player.getName(), TutorialState.infoClass));
			this.setChanged();
			return true;
		}
		
		
		//Select class(only if has no class)
		if(potentialCommand.equalsIgnoreCase("select")){
			boolean useGUI = plugin.getConfigManager().getGeneralConfig().isConfig_useClassGUIToSelect();
			if(useGUI){
				player.openInventory(new HolderInventory(player, ClassManager.getInstance()));
				player.sendMessage(ChatColor.GREEN + "Opening Class Selection...");
				return true;
			}
			
			String potentialClass = args[1];
			if(select(player, potentialClass)){
				this.notifyObservers(new TutorialStepContainer(player.getName(), TutorialState.selectClass));
				this.setChanged();
			}
			
			return true;
		}
				
		//Change races (only if has already a class)
		if(potentialCommand.equalsIgnoreCase("change")){
			boolean useGUI = plugin.getConfigManager().getGeneralConfig().isConfig_useClassGUIToSelect();
			if(useGUI){
				player.openInventory(new HolderInventory(player, ClassManager.getInstance()));
				player.sendMessage(ChatColor.GREEN + "Opening Class Selection...");
				return true;
			}
			
			if(args.length != 2){
				postHelp(player);
				return true;
			}
			
			String potentialClass = args[1];
			
			change(player, potentialClass);
			return true;
		}
			
		postHelp(player);
		return true;
	}
	
	private void postHelp(Player player){
		player.sendMessage(ChatColor.RED + "Wrong usage. The correct usage is one of the following:");
		player.sendMessage(ChatColor.RED + "/class " + ChatColor.LIGHT_PURPLE + "info");
		player.sendMessage(ChatColor.RED + "/class " + ChatColor.LIGHT_PURPLE + "list");
		if(plugin.getPermissionManager().checkPermissionsSilent(player, PermissionNode.changeClass))
			player.sendMessage(ChatColor.RED + "/class " + ChatColor.LIGHT_PURPLE + "select " + ChatColor.YELLOW + "<classname>");
		
		if(plugin.getPermissionManager().checkPermissionsSilent(player, PermissionNode.selectClass))
			player.sendMessage(ChatColor.RED + "/class " + ChatColor.LIGHT_PURPLE + "change " + ChatColor.YELLOW + "<classname>");
	}
	
	private void info(Player player, AbstractTraitHolder holder){
		player.sendMessage(ChatColor.YELLOW + "===== " + ChatColor.RED + "ClassInfo" + ChatColor.YELLOW + " =====");
		
		if(holder == null){
			player.sendMessage(ChatColor.RED + "You have no class selected.");
			return;
		}
		
		player.sendMessage(ChatColor.YELLOW + "Class: " + ChatColor.LIGHT_PURPLE + holder.getName());
		player.sendMessage(ChatColor.YELLOW + "ClassTag: " + ChatColor.LIGHT_PURPLE + holder.getTag());
		player.sendMessage(ChatColor.YELLOW + "==== " + ChatColor.RED + "Class Traits" + ChatColor.YELLOW +" =====");
		
		for(Trait trait : holder.getVisibleTraits()){
			player.sendMessage(ChatColor.BLUE + trait.getName() + " : " + trait.getPrettyConfiguration());
		}
	}
	
	private void list(Player player){
		player.sendMessage(ChatColor.YELLOW + "===== " + ChatColor.RED + "Classes" + ChatColor.YELLOW + " =====");
		
		List<String> classes = ClassManager.getInstance().getAllHolderNames();
		if(classes.size() == 0){
			player.sendMessage(ChatColor.RED + "No Classes in the list.");
			return;
		}
		
		for(String classe : classes )
			player.sendMessage(ChatColor.BLUE + classe);
	}
	
	private boolean select(Player player, String potentialClass){
		potentialClass = potentialClass.toLowerCase();
		
		if(!plugin.getPermissionManager().checkPermissions(player, PermissionNode.selectClass)) return false;
		
		ClassContainer classContainer = (ClassContainer) ClassManager.getInstance().getHolderByName(potentialClass);
		boolean classExists = classContainer != null;
		if(!classExists){
			player.sendMessage(ChatColor.RED + "The class " + ChatColor.LIGHT_PURPLE + potentialClass + ChatColor.RED + " was not found.");
			return false;
		}
		
		ClassSelectEvent event = new ClassSelectEvent(player, classContainer);
		plugin.fireEventToBukkit(event);
		if(event.isCancelled()){
			player.sendMessage(ChatColor.RED + event.getCancelMessage());
			return false;
		}
		
		
		if(classContainer == null){	
			if(ClassManager.getInstance().addPlayerToHolder(player.getName(), potentialClass)){
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
		ClassContainer oldClassContainer = (ClassContainer) ClassManager.getInstance().getHolderOfPlayer(player.getName());
		ClassContainer newClassContainer = (ClassContainer) ClassManager.getInstance().getHolderByName(potentialClass);
		
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
			
			
			ClassChangeEvent event = new ClassChangeEvent(player, oldClassContainer, newClassContainer);
			plugin.fireEventToBukkit(event);
			if(event.isCancelled()){
				player.sendMessage(ChatColor.RED + event.getCancelMessage());
				return;
			}
			
			if(ClassManager.getInstance().changePlayerHolder(player.getName(), potentialClass))
				player.sendMessage(ChatColor.GREEN + "You are now a " + ChatColor.LIGHT_PURPLE + potentialClass);
			else
				player.sendMessage(ChatColor.RED + "The class " + ChatColor.LIGHT_PURPLE + potentialClass + ChatColor.RED + " was not found.");
		}
		
	}

}
