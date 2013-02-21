package de.tobiyas.races.datacontainer.traitholdercontainer.race;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Observable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.tobiyas.races.Races;
import de.tobiyas.util.config.YAMLConfigExtended;
import de.tobiyas.races.datacontainer.health.HealthManager;
import de.tobiyas.races.datacontainer.traitholdercontainer.DefaultContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.reminder.RaceReminder;
import de.tobiyas.races.tutorial.TutorialManager;
import de.tobiyas.races.tutorial.TutorialStepContainer;
import de.tobiyas.races.util.consts.Consts;
import de.tobiyas.races.util.tutorial.TutorialState;

public class RaceManager extends Observable{
	
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
		
		checkForPossiblyWrongInitialized();
		new RaceReminder();
		
		TutorialManager.registerObserver(this);
		this.setChanged();
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
			String raceName = memberConfig.getString("playerdata." + member + ".race", Consts.defaultRace);
			memberList.put(member, getRaceByName(raceName));
		}
				
	}
	
	private void initSTDRace(){
		races.add(new RaceContainer());
	}
	
	private void checkForPossiblyWrongInitialized(){
		Player[] players = Bukkit.getOnlinePlayers();
		
		for(Player player : players){
			String playerName = player.getName();
			RaceContainer container = getRaceOfPlayer(playerName);
			if(container == null){
				addPlayerToRace(playerName, Consts.defaultRace);
				container = getRaceOfPlayer(playerName);
			}
			
			container.setListEntry(player);
		}
		
		for(String playerName : memberList.keySet())
			if(memberList.get(playerName) == null)
				addPlayerToRace(playerName, Consts.defaultRace);
	}
	
	public RaceContainer getRaceOfPlayer(String player){
		RaceContainer container = memberList.get(player);
		if(container == null)
			addPlayerToRace(player, Consts.defaultRace);
		
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
		return getRaceByName(Consts.defaultRace);
	}
	
	public ArrayList<String> listAllRaces(){
		ArrayList<String> raceList = new ArrayList<String>();
		
		for(RaceContainer container : races){
			raceList.add(container.getName());
		}
		
		return raceList;
	}
	
	public ArrayList<String> listAllVisibleRaces(){
		ArrayList<String> raceList = new ArrayList<String>();
		
		for(RaceContainer container : races){
			if(container != getDefaultContainer())
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
		
		this.notifyObservers(new TutorialStepContainer(player, TutorialState.selectRace));
		this.setChanged();
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
