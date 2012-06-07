package de.tobiyas.races.commands.classes;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.races.Races;
import de.tobiyas.races.datacontainer.traitcontainer.traits.Trait;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassManager;

public class CommandExecutor_ClassInfo implements CommandExecutor {

	private Races plugin;
	
	public CommandExecutor_ClassInfo(){
		plugin = Races.getPlugin();
		try{
			plugin.getCommand("classinfo").setExecutor(this);
		}catch(Exception e){
			plugin.log("ERROR: Could not register command /classinfo.");
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "Only players can use this command.");
			return true;
		}
		Player player = (Player) sender;
		player.sendMessage(ChatColor.YELLOW + "===== " + ChatColor.RED + "ClassInfo" + ChatColor.YELLOW + " =====");
		
		ClassContainer container = ClassManager.getInstance().getClassOfPlayer(player.getName());
		if(container == null){
			player.sendMessage(ChatColor.RED + "You have no class selected.");
			return true;
		}
		
		player.sendMessage(ChatColor.YELLOW + "Your Class: " + ChatColor.LIGHT_PURPLE + container.getName());
		player.sendMessage(ChatColor.YELLOW + "Your ClassTag: " + ChatColor.LIGHT_PURPLE + container.getTag());
		player.sendMessage(ChatColor.YELLOW + "==== " + ChatColor.RED + "Class Traits" + ChatColor.YELLOW +" =====");
		
		for(Trait trait : container.getVisibleTraits()){
			player.sendMessage(ChatColor.BLUE + trait.getName() + " : " + trait.getValueString());
		}
		
		return true;
	}

}
