package de.tobiyas.races.datacontainer.traitholdercontainer.race;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.tobiyas.races.Races;
import de.tobiyas.races.configuration.global.YAMLConfigExtended;
import de.tobiyas.races.datacontainer.health.HealthManager;
import de.tobiyas.races.datacontainer.traitholdercontainer.DefaultContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.reminder.RaceReminder;
import de.tobiyas.races.util.consts.Consts;

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
		races = new HashSet<RaceContainer>();
		raceConfig = new YAMLConfigExtended(Consts.racesYML);
		
		DefaultContainer.createSTDRaces();
	}
	
	public void init(){
		readRaceList();
		readMemberList();
		initSTDRace();
		
		checkForOnlineWithoutRace();
		new RaceReminder();
	}
	
	private void readRaceList(){
		races.clear();
		raceConfig.load();
		
		if(!raceConfig.getValidLoad()){
			plugin.log("races.yml could not be loaded correctly.");
			return;
		}
		
		for(String race : raceConfig.getYAMLChildren("races")){
			RaceContainer container = RaceContainer.loadRace(raceConfig, race);
			if(container != null)
				races.add(container);
		}
	}
	
	private void readMemberList(){
		memberList = new HashMap<String, RaceContainer>();
		
		DefaultContainer.createSTDMembers();
		
		memberConfig = new YAMLConfigExtended(Consts.playerDataYML).load();
		
		for(String member : memberConfig.getYAMLChildren("playerdata")){
			String raceName = memberConfig.getString("playerdata." + member + ".race");
			memberList.put(member, getRaceByName(raceName));
		}
				
	}
	
	private void initSTDRace(){
		races.add(new RaceContainer());
	}
	
	private void checkForOnlineWithoutRace(){
		Player[] players = Bukkit.getOnlinePlayers();
		
		for(Player player : players){
			String playerName = player.getName();
			RaceContainer container = getRaceOfPlayer(playerName);
			if(container == null){
				addPlayerToRace(playerName, "DefaultRace");
				container = getRaceOfPlayer(playerName);
			}
			
			container.setListEntry(player);
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
	
	public RaceContainer getDefaultContainer(){
		return getRaceByName("DefaultRace");
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
		memberConfig.load();
		
		memberList.put(player, container);
		memberConfig.set("playerdata." + player + ".race", container.getName());
		HealthManager.getHealthManager().checkPlayer(player);
		memberConfig.save();
		
		Player tempPlayer = Bukkit.getPlayer(player);
		container.setListEntry(tempPlayer);
		
		return true;
	}
	
	public boolean changePlayerRace(String player, String potentialRace){
		if(getRaceByName(potentialRace) == null) return false;
		memberList.remove(player);
		memberConfig.load();
		memberConfig.set("playerdata." + player + ".race", null);
		memberConfig.save();
		return addPlayerToRace(player, potentialRace);
	}
	
	public static RaceManager getManager(){
		return manager;
	}

	public LinkedList<String> getAllPlayersOfRace(RaceContainer container) {
		LinkedList<String> members = new LinkedList<String>();
		
		for(String member : memberList.keySet()){
			RaceContainer tempContainer = memberList.get(member);
			if(tempContainer != null && tempContainer.equals(container))
				members.add(member);
		}
				
		return members;
	}
	
}
