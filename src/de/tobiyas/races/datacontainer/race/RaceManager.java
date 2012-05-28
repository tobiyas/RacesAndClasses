package de.tobiyas.races.datacontainer.race;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import de.tobiyas.races.Races;
import de.tobiyas.util.economy.defaults.YAMLConfigExtended;

public class RaceManager {
	
	private Races plugin;

	private HashMap<String, RaceContainer> memberList;
	private HashSet<RaceContainer> races;
	
	private YAMLConfigExtended raceConfig;
	private YAMLConfigExtended memberConfig;
	
	private static RaceManager manager;
	
	public RaceManager(){
		manager = this;
		plugin = Races.getPlugin();
	}
	
	public void init(){
		readRaceList();
		readMemberList();
	}
	
	private void readRaceList(){
		races = new HashSet<RaceContainer>();
		
		DefaultRace.createSTDRaces();
		raceConfig = new YAMLConfigExtended(plugin.getDataFolder() + File.separator + "races.yml");
		
		try {
			raceConfig.load(plugin.getDataFolder() + File.separator + "races.yml");
		} catch (Exception e) {
			plugin.log("Error on loading races.yml.");
			return;
		}
		
		for(String race : raceConfig.getConfigurationSection("races").getKeys(false)){
			RaceContainer container = RaceContainer.loadRace(raceConfig, race);
			if(container != null)
				races.add(container);
		}
		
		plugin.log(races.size() + " races loaded.");
		
	}
	
	private void readMemberList(){
		memberList = new HashMap<String, RaceContainer>();
		
		DefaultRace.createSTDMembers();
		memberConfig = new YAMLConfigExtended(plugin.getDataFolder() + File.separator + "members.yml");
		
		memberConfig.load();
		
		for(String member : memberConfig.getConfigurationSection("members").getKeys(false)){
			String raceName = memberConfig.getString("members." + member);
			memberList.put(member, getRaceByName(raceName));
		}
				
	}	
	
	public RaceContainer getRaceOfPlayer(String player){
		return memberList.get(player);
	}
	
	public RaceContainer getRaceByName(String raceName){
		for(RaceContainer container : races){
			if(container.getName().equalsIgnoreCase(raceName))
				return container;
		}
			
		return null;
	}
	
	public ArrayList<String> listAllRaces(){
		ArrayList<String> raceList = new ArrayList<String>();
		
		for(RaceContainer container : races){
			raceList.add(container.getName());
		}
		
		return raceList;
	}
	
	public boolean addPlayerToRace(String player, String potentialRace){
		RaceContainer container = getRaceByName(potentialRace);
		if(container == null) return false;
		memberList.put(player, container);
		memberConfig.set("members." + player, container.getName());
		memberConfig.save();
		
		return true;
	}
	
	public boolean changePlayerRace(String player, String potentialRace){
		if(getRaceByName(potentialRace) == null) return false;
		memberList.remove(player);
		memberConfig.set("members." + player, null);
		memberConfig.save();
		return addPlayerToRace(player, potentialRace);
	}
	
	public static RaceManager getManager(){
		return manager;
	}

	public LinkedList<String> getAllPlayerOfRace(RaceContainer container) {
		LinkedList<String> members = new LinkedList<String>();
		
		for(String member : memberList.keySet()){
			RaceContainer tempContainer = memberList.get(member);
			if(tempContainer.equals(container))
				members.add(member);
		}
				
		return members;
	}
	
}
