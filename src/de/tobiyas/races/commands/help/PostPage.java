package de.tobiyas.races.commands.help;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import de.tobiyas.races.Races;
import de.tobiyas.races.util.consts.PermissionNode;

public class PostPage {
	
	private final static int pages = 7; 
	
	public static void postPage(CommandSender sender, int page){
		switch(page){
			case 1: page1(sender); break;
			case 2: page2(sender); break;
			case 3: page3(sender); break;
			case 4: page4(sender); break;
			case 5: page5(sender); break;
			case 6: page6(sender); break;
			case 7: page7(sender); break;
			default: page1(sender);
		}
	}
	
	public static void postPage(CommandSender sender, String category){
		if(category.equalsIgnoreCase("help")){
			page1(sender);
			return;
		}
		
		if(category.equalsIgnoreCase("races")){
			page2(sender);
			return;
		}
		
		if(category.equalsIgnoreCase("classes")){
			page3(sender);
			return;
		}
		
		if(category.equalsIgnoreCase("chat")){
			page4(sender);
			return;
		}
		
		if(category.equalsIgnoreCase("channel")){
			page5(sender);
			return;
		}
		
		if(category.equalsIgnoreCase("config")){
			page6(sender);
			return;
		}
		
		if(category.equalsIgnoreCase("general")){
			page7(sender);
			return;
		}
		
		page1(sender);
	}
	
	//Help commands
	private static void page1(CommandSender sender){
		postPageHead(sender, 1, "HELP");
		sender.sendMessage(ChatColor.GREEN + "Pages are: " + ChatColor.RED + "1:help, 2:races, 3:classes, 4:chat, 5:channel, 6:config, 7:general");
		sender.sendMessage(ChatColor.GREEN + "/racehelp " + ChatColor.YELLOW + "displays this page.");
		sender.sendMessage(ChatColor.GREEN + "/racehelp page <number> " + ChatColor.YELLOW + "displays the page with the number.");
		sender.sendMessage(ChatColor.GREEN + "/racehelp tutorial " + ChatColor.YELLOW + "gives you a step by step tutorial.");
		sender.sendMessage(ChatColor.GREEN + "/racehelp trait <traitname> " + ChatColor.YELLOW + "displays the help to a trait.");
	}
	
	//Race commands
	private static void page2(CommandSender sender){
		postPageHead(sender, 2, "RACES");
		sender.sendMessage(ChatColor.GREEN + "/race " + ChatColor.YELLOW + "lets you handle your Race.");
		sender.sendMessage(ChatColor.GREEN + "/race select <RaceName> " + ChatColor.YELLOW + "lets you select a race if you haven't got any.");
		sender.sendMessage(ChatColor.GREEN + "/race change <RaceName> " + ChatColor.YELLOW + "lets you change your race.");
		sender.sendMessage(ChatColor.GREEN + "/race info " + ChatColor.YELLOW + " displays infos for your race.");
		sender.sendMessage(ChatColor.GREEN + "/race list " + ChatColor.YELLOW + " displays all races available.");
	}

	//Class commands
	private static void page3(CommandSender sender){
		postPageHead(sender, 3, "CLASSES");
		sender.sendMessage(ChatColor.GREEN + "/class " + ChatColor.YELLOW + "lets you handle your Class.");
		sender.sendMessage(ChatColor.GREEN + "/class select <ClassName> " + ChatColor.YELLOW + "lets you select a class if you haven't got any.");
		sender.sendMessage(ChatColor.GREEN + "/class change <ClassName> " + ChatColor.YELLOW + "lets you change your class.");
		sender.sendMessage(ChatColor.GREEN + "/class info " + ChatColor.YELLOW + " displays infos for your race.");
		sender.sendMessage(ChatColor.GREEN + "/class list " + ChatColor.YELLOW + " displays all races available.");
	}
	
	//Chat commands
	private static void page4(CommandSender sender){
		postPageHead(sender, 4, "CHAT");
		sender.sendMessage(ChatColor.GREEN + "/whisper <playername> <message> " + ChatColor.YELLOW + "whispers the target with a message.");
		sender.sendMessage(ChatColor.GREEN + "/racechat <message> " + ChatColor.YELLOW + "sends a message to your race channel");
		
		int range = Races.getPlugin().interactConfig().getConfig_localchat_range();
		sender.sendMessage(ChatColor.GREEN + "/localchat <message> " + ChatColor.YELLOW + "sends a message in a range of " + range);
		sender.sendMessage(ChatColor.YELLOW + "More information to Channels can be displayed by the command '/racehelp page 5'");
	}
	
	//Channel commands
	private static void page5(CommandSender sender){
		postPageHead(sender, 5, "CHANNEL");
		sender.sendMessage(ChatColor.GREEN + "/channel " + ChatColor.YELLOW + "breaf overview over channel commands.");
		sender.sendMessage(ChatColor.GREEN + "/channel info [channelname] " + ChatColor.YELLOW + "gives you infos to the given channel.");
		sender.sendMessage(ChatColor.GREEN + "/channel list " + ChatColor.YELLOW + "lists all public channels.");
		sender.sendMessage(ChatColor.GREEN + "/channel post <channelname> " + ChatColor.YELLOW + "changes the channel you write to to another one.");
		sender.sendMessage(ChatColor.GREEN + "/channel join <channelname> [password] " + ChatColor.YELLOW + "joins a channel. If it has a password, it is checked.");
		sender.sendMessage(ChatColor.GREEN + "/channel leave <channelname> " + ChatColor.YELLOW + "leaves the given channel.");
		sender.sendMessage(ChatColor.GREEN + "/channel create <channelname> [channeltype] [password] " + ChatColor.YELLOW + "creates a new channel.");
		sender.sendMessage(ChatColor.GREEN + "/channel edit <channelname> <property> <newValue> " + ChatColor.YELLOW + "changes a property of a channel.");
	}
	
	//Config commands
	private static void page6(CommandSender sender){
		postPageHead(sender, 6, "CONFIG");
		sender.sendMessage(ChatColor.GREEN + "/raceconfig " + ChatColor.YELLOW + "displays your current MemberConfig.");
		sender.sendMessage(ChatColor.GREEN + "/raceconfig <attribute> <value> " + ChatColor.YELLOW + "Changes a config option for you.");
	}
	
	//General commands
	private static void page7(CommandSender sender){
		postPageHead(sender, 7, "GENERAL");
		sender.sendMessage(ChatColor.GREEN + "/hp " + ChatColor.YELLOW + "displays your current HP.");
		if(Races.getPlugin().getPermissionManager().checkPermissionsSilent(sender, PermissionNode.debug)){
			sender.sendMessage(ChatColor.GREEN + "/racedebug scan " + ChatColor.YELLOW + "Does a system scan. (debug purpose)");
			sender.sendMessage(ChatColor.GREEN + "/racedebug timing " + ChatColor.YELLOW + "Does a event timing scan. (debug purpose)");
		}
		
		if(Races.getPlugin().getPermissionManager().checkPermissionsSilent(sender, PermissionNode.god))
			sender.sendMessage(ChatColor.GREEN + "/racegod [playername] " + "gives a player godmode.");
		
		if(Races.getPlugin().getPermissionManager().checkPermissionsSilent(sender, PermissionNode.healSelf))
			sender.sendMessage(ChatColor.GREEN + "/raceheal " + ChatColor.YELLOW + "heals yourself fully.");
		
		if(Races.getPlugin().getPermissionManager().checkPermissionsSilent(sender, PermissionNode.healSelf))
			sender.sendMessage(ChatColor.GREEN + "/raceheal [playername]" + ChatColor.YELLOW + "heals the given player fully.");
		
		if(Races.getPlugin().getPermissionManager().checkPermissionsSilent(sender, PermissionNode.reload))
			sender.sendMessage(ChatColor.GREEN + "/racereload [gc] " + ChatColor.YELLOW + "Fully reloads the plugin. (gc means with garbage collection)");
	}
	
	private static void postPageHead(CommandSender sender, int page, String category){
		sender.sendMessage(ChatColor.YELLOW + "========== " + ChatColor.RED + "RACES HELP: " + ChatColor.LIGHT_PURPLE  + category + ChatColor.AQUA +
							" Page<" + ChatColor.RED + page + ChatColor.AQUA +  "/" + ChatColor.RED + pages + ChatColor.AQUA+ ">" +
							ChatColor.YELLOW + " ==========");
	}

}
