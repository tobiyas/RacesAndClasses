package de.tobiyas.races.commands.classes;

import java.util.LinkedList;
import java.util.Observable;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.races.Races;
import de.tobiyas.races.datacontainer.traitcontainer.traits.Trait;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassManager;
import de.tobiyas.races.tutorial.TutorialManager;
import de.tobiyas.races.tutorial.TutorialStepContainer;
import de.tobiyas.races.util.consts.PermissionNode;
import de.tobiyas.races.util.tutorial.TutorialState;

public class CommandExecutor_Class extends Observable implements CommandExecutor {
	
	private Races plugin;
	
	public CommandExecutor_Class(){
		plugin = Races.getPlugin();
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
		if(!plugin.getGeneralConfig().getConfig_classes_enable()){
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
		if(args.length == 1){
			if(potentialCommand.equalsIgnoreCase("info")){
				info(player);
				return true;
			}
			
			if(potentialCommand.equalsIgnoreCase("list")){
				list(player);
				this.notifyObservers(new TutorialStepContainer(player.getName(), TutorialState.infoClass));
				this.setChanged();
				return true;
			}
		}
			
		if(args.length == 2){
			String potentialClass = args[1];
			//Select class(only if has no class)
			if(potentialCommand.equalsIgnoreCase("select")){
				select(player, potentialClass);
				this.notifyObservers(new TutorialStepContainer(player.getName(), TutorialState.selectClass));
				this.setChanged();
				return true;
			}
				
			//Change races (only if has already a class)
			if(potentialCommand.equalsIgnoreCase("change")){
				change(player, potentialClass);
				return true;
			}
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
	
	private void info(Player player){
		player.sendMessage(ChatColor.YELLOW + "===== " + ChatColor.RED + "ClassInfo" + ChatColor.YELLOW + " =====");
		
		ClassContainer container = ClassManager.getInstance().getClassOfPlayer(player.getName());
		if(container == null){
			player.sendMessage(ChatColor.RED + "You have no class selected.");
			return;
		}
		
		player.sendMessage(ChatColor.YELLOW + "Your Class: " + ChatColor.LIGHT_PURPLE + container.getName());
		player.sendMessage(ChatColor.YELLOW + "Your ClassTag: " + ChatColor.LIGHT_PURPLE + container.getTag());
		player.sendMessage(ChatColor.YELLOW + "==== " + ChatColor.RED + "Class Traits" + ChatColor.YELLOW +" =====");
		
		for(Trait trait : container.getVisibleTraits()){
			player.sendMessage(ChatColor.BLUE + trait.getName() + " : " + trait.getValueString());
		}
	}
	
	private void list(Player player){
		player.sendMessage(ChatColor.YELLOW + "===== " + ChatColor.RED + "Classes" + ChatColor.YELLOW + " =====");
		
		LinkedList<String> classes = ClassManager.getInstance().getClassNames();
		if(classes.size() == 0){
			player.sendMessage(ChatColor.RED + "No Classes in the list.");
			return;
		}
		
		for(String classe : classes )
			player.sendMessage(ChatColor.BLUE + classe);
	}
	
	private void select(Player player, String potentialClass){
		if(!plugin.getPermissionManager().checkPermissions(player, PermissionNode.selectClass)) return;
		ClassContainer container = ClassManager.getInstance().getClassOfPlayer(player.getName());
		if(container == null)
			if(ClassManager.getInstance().addPlayerToClass(player.getName(), potentialClass))
				player.sendMessage(ChatColor.GREEN + "You are now a " + ChatColor.LIGHT_PURPLE + potentialClass);
			else
				player.sendMessage(ChatColor.RED + "The class " + ChatColor.LIGHT_PURPLE + potentialClass + ChatColor.RED + " was not found.");
		else
			player.sendMessage(ChatColor.RED + "You already have a class: " + ChatColor.LIGHT_PURPLE + container.getName());
	}
	
	private void change(Player player, String potentialClass){
		if(!plugin.getPermissionManager().checkPermissions(player, PermissionNode.changeClass)) return;
		ClassContainer container = ClassManager.getInstance().getClassOfPlayer(player.getName());
			
		if(container != null){
			if(potentialClass.equalsIgnoreCase(container.getName())){
				player.sendMessage(ChatColor.RED + "You are already a " + ChatColor.LIGHT_PURPLE + container.getName());
				return;
			}
				
			if(ClassManager.getInstance().changePlayerClass(player.getName(), potentialClass))
				player.sendMessage(ChatColor.GREEN + "You are now a " + ChatColor.LIGHT_PURPLE + potentialClass);
			else
				player.sendMessage(ChatColor.RED + "The class " + ChatColor.LIGHT_PURPLE + potentialClass + ChatColor.RED + " was not found.");
		}else
			player.sendMessage(ChatColor.RED + "You have no class you could change");
	}

}
