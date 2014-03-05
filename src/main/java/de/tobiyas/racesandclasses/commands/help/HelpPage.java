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
package de.tobiyas.racesandclasses.commands.help;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;

public class HelpPage {
	
	/**
	 * The total amount of pages for the help
	 */
	private final static int pages = 7; 
	
	
	/**
	 * Post directly a page to a Player.
	 * If the page number is > {@link #pages} ({@value #pages}), page 1 is printed.
	 * 
	 * @param page the page number to be sent
	 * @param playerName to check for Permission
	 */
	public static List<String> getPageContent(String playerName, int page){
		switch(page){
			case 1: return page1(); 
			case 2: return page2();
			case 3: return page3();
			case 4: return page4();
			case 5: return page5();
			case 6: return page6();
			case 7: return page7(playerName);
			default: return page1();
		}
	}
	
	
	/**
	 * Posts the page via category to a player.
	 * The categories are found in the code.
	 * If category is not identified, the first page is posted.
	 * 
	 * @param sender
	 * @param category
	 */
	public static List<String>  getCategoryPage(String sender, String category){
		if(category.equalsIgnoreCase("help")){
			return page1();
		}
		
		if(category.equalsIgnoreCase("races")){
			return page2();
		}
		
		if(category.equalsIgnoreCase("classes")){
			return page3();
		}
		
		if(category.equalsIgnoreCase("chat")){
			return page4();
		}
		
		if(category.equalsIgnoreCase("channel")){
			return page5();
		}
		
		if(category.equalsIgnoreCase("config")){
			return page6();
		}
		
		if(category.equalsIgnoreCase("general")){
			return page7(sender);
		}
		
		return page1();
	}

	
	/**
	 * Generates the help for the Help page
	 */
	private static List<String> page1(){
		List<String> returnList = new LinkedList<String>();
		
		returnList.add(postPageHead(1, "HELP"));
		returnList.add(ChatColor.GREEN + "Pages are: " + ChatColor.RED + "1:help, 2:races, 3:classes, 4:chat, 5:channel, 6:config, 7:general");
		returnList.add(ChatColor.GREEN + "/racehelp " + ChatColor.YELLOW + "displays this page.");
		returnList.add(ChatColor.GREEN + "/racehelp page <number> " + ChatColor.YELLOW + "displays the page with the number.");
		returnList.add(ChatColor.GREEN + "/racehelp trait <traitname> " + ChatColor.YELLOW + "displays the help to a trait.");
		returnList.add(ChatColor.GREEN + "/playerinfo [player-name] " + ChatColor.YELLOW + "displays infos to a player.");
		returnList.add(ChatColor.GREEN + "/racestutorial <start/stop/next>" + ChatColor.YELLOW + "gives you a step by step tutorial of the Plugin.");
	
		return returnList;
	}
	

	/**
	 * Generates the help for the Races page
	 */
	private static List<String> page2(){
		List<String> returnList = new LinkedList<String>();
		
		returnList.add(postPageHead(2, "RACES"));
		returnList.add(ChatColor.GREEN + "/race " + ChatColor.YELLOW + "lets you handle your Race.");
		returnList.add(ChatColor.GREEN + "/race select <RaceName> " + ChatColor.YELLOW + "lets you select a race if you haven't got any.");
		returnList.add(ChatColor.GREEN + "/race change <RaceName> " + ChatColor.YELLOW + "lets you change your race.");
		returnList.add(ChatColor.GREEN + "/race info " + ChatColor.YELLOW + " displays infos for your race.");
		returnList.add(ChatColor.GREEN + "/race list " + ChatColor.YELLOW + " displays all races available.");
		
		return returnList;
	}


	/**
	 * Generates the help for the Classes page
	 */
	private static List<String> page3(){
		List<String> returnList = new LinkedList<String>();
		
		returnList.add(postPageHead(3, "CLASSES"));
		returnList.add(ChatColor.GREEN + "/class " + ChatColor.YELLOW + "lets you handle your Class.");
		returnList.add(ChatColor.GREEN + "/class select <ClassName> " + ChatColor.YELLOW + "lets you select a class if you haven't got any.");
		returnList.add(ChatColor.GREEN + "/class change <ClassName> " + ChatColor.YELLOW + "lets you change your class.");
		returnList.add(ChatColor.GREEN + "/class info " + ChatColor.YELLOW + " displays infos for your race.");
		returnList.add(ChatColor.GREEN + "/class list " + ChatColor.YELLOW + " displays all races available.");

		return returnList;
	}
	

	/**
	 * Generates the help for the Chat page
	 */
	private static List<String> page4(){
		List<String> returnList = new LinkedList<String>();
		
		returnList.add(postPageHead(4, "CHAT"));
		returnList.add(ChatColor.GREEN + "/whisper <playername> <message> " + ChatColor.YELLOW + "whispers the target with a message.");
		returnList.add(ChatColor.GREEN + "/racechat <message> " + ChatColor.YELLOW + "sends a message to your race channel");
		
		int range = RacesAndClasses.getPlugin().getConfigManager().getChannelConfig().getConfig_localchat_range();
		returnList.add(ChatColor.GREEN + "/localchat <message> " + ChatColor.YELLOW + "sends a message in a range of " + range);
		returnList.add(ChatColor.YELLOW + "More information to Channels can be displayed by the command '/racehelp page 5'");

		return returnList;
	}
	

	/**
	 * Generates the help for the Channel page
	 */
	private static List<String> page5(){
		List<String> returnList = new LinkedList<String>();
		
		returnList.add(postPageHead(5, "CHANNEL"));
		returnList.add(ChatColor.GREEN + "/channel " + ChatColor.YELLOW + "breaf overview over channel commands.");
		returnList.add(ChatColor.GREEN + "/channel info [channelname] " + ChatColor.YELLOW + "gives you infos to the given channel.");
		returnList.add(ChatColor.GREEN + "/channel list " + ChatColor.YELLOW + "lists all public channels.");
		returnList.add(ChatColor.GREEN + "/channel post <channelname> " + ChatColor.YELLOW + "changes the channel you write to to another one.");
		returnList.add(ChatColor.GREEN + "/channel join <channelname> [password] " + ChatColor.YELLOW + "joins a channel. If it has a password, it is checked.");
		returnList.add(ChatColor.GREEN + "/channel leave <channelname> " + ChatColor.YELLOW + "leaves the given channel.");
		returnList.add(ChatColor.GREEN + "/channel create <channelname> [channeltype] [password] " + ChatColor.YELLOW + "creates a new channel.");
		returnList.add(ChatColor.GREEN + "/channel edit <channelname> <property> <newValue> " + ChatColor.YELLOW + "changes a property of a channel.");

		return returnList;
	}
	

	/**
	 * Generates the help for the Config page
	 */
	private static List<String> page6(){
		List<String> returnList = new LinkedList<String>();
		
		returnList.add(postPageHead(6, "CONFIG"));
		returnList.add(ChatColor.GREEN + "/raceconfig " + ChatColor.YELLOW + "displays your current MemberConfig.");
		returnList.add(ChatColor.GREEN + "/raceconfig <attribute> <value> " + ChatColor.YELLOW + "Changes a config option for you.");

		return returnList;
	}
	
	
	/**
	 * Generates the help for the General page
	 * 
	 * @param playerName this is needed to check against permissions
	 */
	private static List<String> page7(String playerName){
		List<String> returnList = new LinkedList<String>();
		
		returnList.add(postPageHead(7, "GENERAL"));
		returnList.add(ChatColor.GREEN + "/hp " + ChatColor.YELLOW + "displays your current HP.");
		
		
		CommandSender sender = playerName.equals("console") ? Bukkit.getConsoleSender() : Bukkit.getPlayer(playerName);
		if(RacesAndClasses.getPlugin().getPermissionManager().checkPermissionsSilent(sender, PermissionNode.debug)){
			returnList.add(ChatColor.GREEN + "/racedebug scan " + ChatColor.YELLOW + "Does a system scan. (debug purpose)");
			returnList.add(ChatColor.GREEN + "/racedebug timing " + ChatColor.YELLOW + "Does a event timing scan. (debug purpose)");
		}
		
		if(RacesAndClasses.getPlugin().getPermissionManager().checkPermissionsSilent(sender, PermissionNode.god))
			returnList.add(ChatColor.GREEN + "/racegod [playername] " + "gives a player godmode.");
		
		if(RacesAndClasses.getPlugin().getPermissionManager().checkPermissionsSilent(sender, PermissionNode.healSelf))
			returnList.add(ChatColor.GREEN + "/raceheal " + ChatColor.YELLOW + "heals yourself fully.");
		
		if(RacesAndClasses.getPlugin().getPermissionManager().checkPermissionsSilent(sender, PermissionNode.healSelf))
			returnList.add(ChatColor.GREEN + "/raceheal [playername]" + ChatColor.YELLOW + "heals the given player fully.");
		
		if(RacesAndClasses.getPlugin().getPermissionManager().checkPermissionsSilent(sender, PermissionNode.reload))
			returnList.add(ChatColor.GREEN + "/racesreload [gc] " + ChatColor.YELLOW + "Fully reloads the plugin. (gc means with garbage collection)");

		return returnList;
	}
	
	
	/**
	 * Posts the Category header to the player.
	 * The Category with it's page number is passed via argument. 
	 * 
	 * @param page the page number to fill
	 * @param category the category to fill
	 */
	private static String postPageHead(int page, String category){
		return ChatColor.YELLOW + "========== " + ChatColor.RED + "RACES HELP: " + ChatColor.LIGHT_PURPLE  + category + ChatColor.AQUA +
							" Page<" + ChatColor.RED + page + ChatColor.AQUA +  "/" + ChatColor.RED + pages + ChatColor.AQUA+ ">" +
							ChatColor.YELLOW + " ==========";
	}

}
