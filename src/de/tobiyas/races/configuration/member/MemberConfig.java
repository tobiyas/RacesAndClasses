package de.tobiyas.races.configuration.member;

import java.util.ArrayList;
import java.util.HashMap;

import de.tobiyas.races.configuration.global.YAMLConfigExtended;
import de.tobiyas.races.util.consts.Consts;

public class MemberConfig {

	private String player;
	private boolean enableLifeDisplay;
	private int lifeDisplayInterval;
	
	private static YAMLConfigExtended config = new YAMLConfigExtended(Consts.membersYML);
	
	private MemberConfig(String player){
		config.load();
		this.player = player;
		enableLifeDisplay = config.getBoolean("playerdata." + player + ".config.lifeDisplay.enable", true);
		lifeDisplayInterval = config.getInt("playerdata." + player + ".config.lifeDisplay.interval", 60);
		
		//Add other vars
		save();
	}
	
	
	public static MemberConfig createMemberConfig(String player){
		return new MemberConfig(player);
	}
	
	public void setEnableLifeDisplay(boolean enable){
		config.load();
		config.set("playerdata." + player + ".config.lifeDisplay.enable", enable);
		config.save();
		this.enableLifeDisplay = enable;
	}
	
	public void setLifeDisplayIntervall(int interval){
		config.load();
		config.set("playerdata." + player + ".config.lifeDisplay.interval", interval);
		config.save();
		this.lifeDisplayInterval = interval;
	}
	
	public boolean getEnableLifeDisplay(){
		return enableLifeDisplay;
	}
	
	public int getLifeDisplayInterval(){
		return lifeDisplayInterval;
	}
	
	public String getName(){
		return player;
	}


	public void save() {
		config.load();
		config.set("playerdata." + player + ".config.lifeDisplay.interval", lifeDisplayInterval);
		config.set("playerdata." + player + ".config.lifeDisplay.enable", enableLifeDisplay);
		config.save();
	}


	public boolean changeAttribute(String attribute, String value) {
		attribute = attribute.toLowerCase();
		
		if(attribute.equalsIgnoreCase("displayenable")){
			if(!isBool(value))
				return false;
			setEnableLifeDisplay(converToBool(value));
			return true;
		}
		
		if(attribute.equalsIgnoreCase("displayinterval")){
			if(getInt(value) == null)
				return false;
			setLifeDisplayIntervall(getInt(value));
			return true;
		}
		
		return false;
	}
	
	private Integer getInt(String inT){
		try{
			int newInt = Integer.valueOf(inT);
			return newInt;
		}catch(NumberFormatException e){
			return null;
		}
	}
	
	private boolean converToBool(String bool){
		bool = bool.toLowerCase();
		if(bool.equalsIgnoreCase("on") || bool.equalsIgnoreCase("true") || bool.equalsIgnoreCase("1") || bool.equalsIgnoreCase("yes"))
			return true;
		if(bool.equalsIgnoreCase("off") || bool.equalsIgnoreCase("false") || bool.equalsIgnoreCase("0") || bool.equalsIgnoreCase("no"))
			return false;
		
		return false;
	}
	
	private boolean isBool(String bool){
		bool = bool.toLowerCase();
		if(bool.equalsIgnoreCase("on") || bool.equalsIgnoreCase("true") || bool.equalsIgnoreCase("1") || bool.equalsIgnoreCase("yes"))
			return true;
		if(bool.equalsIgnoreCase("off") || bool.equalsIgnoreCase("false") || bool.equalsIgnoreCase("0") || bool.equalsIgnoreCase("no"))
			return true;
		return false;
	}
	
	public ArrayList<String> getSupportetAttributes(){
		ArrayList<String> attributes = new ArrayList<String>();
		attributes.add("DisplayInterval");
		attributes.add("DisplayEnable");
		
		return attributes;
	}


	public HashMap<String, Object> getSupportetAttributesAndValues() {
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("DisplayInterval: ", lifeDisplayInterval);
		attributes.put("DisplayEnable: ", enableLifeDisplay);
		
		return attributes;
	}
}
