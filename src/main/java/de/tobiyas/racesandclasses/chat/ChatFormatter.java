package de.tobiyas.racesandclasses.chat;

import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.util.chat.ChannelLevel;


public class ChatFormatter{

	private String format = "";
	private ChannelLevel level = ChannelLevel.NONE;
	private String name = "";
	private String color = "&c";
	
	
	private String prefix = "&f[";
	private String suffix = "&f]";
	
	public ChatFormatter(){
	}
	
	public ChatFormatter(String name, String color, ChannelLevel level, String format){
		this.name = name;
		this.color = color;
		this.level = level;
		this.format = format;
	}
	
	public void setFormat(String format){
		this.format = format;
	}
	
	public String getFormat(){
		return format;
	}
	
	public String format(String playerName, String msg, String forceFormat){
		Player player = Bukkit.getPlayer(playerName);
		RacesAndClasses plugin = RacesAndClasses.getPlugin();
		AbstractTraitHolder container = plugin.getRaceManager().getHolderOfPlayer(playerName);
		
		String displayName;
		String realName;
		String world;
		
		if(playerName.equalsIgnoreCase("CONSOLE")){
			displayName = "CONSOLE";
			realName = "CONSOLE";
			world = "None";
		}else{
			if(player == null){
				displayName = "UNKNOWN";
				realName = "UNKOWN";
				world = "UNKNOWN";
			}else{
				displayName = player.getDisplayName();
				realName = player.getName();
				world = player.getWorld().getName();
			}
		}
		
		String messageFormat = "";
		if(forceFormat == "")
			messageFormat = new String(format);
		else
			messageFormat = new String(forceFormat);
			
		String raceTag = "NONE";
		if(container != null){
			raceTag = container.getTag();
		}
		
		//default: {color}[{nick}] &f{prefix}{sender}{suffix}{color}: {msg}
		messageFormat = messageFormat.replaceAll(Pattern.quote("{color}"), color);
		messageFormat = messageFormat.replaceAll(Pattern.quote("{nick}"), name);
		messageFormat = messageFormat.replaceAll(Pattern.quote("{prefix}"), prefix);
		messageFormat = messageFormat.replaceAll(Pattern.quote("{suffix}"), suffix);
		messageFormat = messageFormat.replaceAll(Pattern.quote("{sender}"), displayName);
		messageFormat = messageFormat.replaceAll(Pattern.quote("{realname}"), realName);
		messageFormat = messageFormat.replaceAll(Pattern.quote("{race}"), raceTag);
		messageFormat = messageFormat.replaceAll(Pattern.quote("{world}"), world);
		messageFormat = messageFormat.replaceAll(Pattern.quote("{channeltype}"), level.name());
		messageFormat = decodeColor(messageFormat);
		messageFormat = messageFormat.replaceAll(Pattern.quote("{msg}"), msg);
		
		return messageFormat;
	}
	
	private String decodeColor(String message){
		return message.replaceAll("(&([a-f0-9]))", "§$2");
	}

	public ChannelLevel getLevel() {
		return level;
	}

	public void setLevel(ChannelLevel level) {
		this.level = level;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

}
