package de.tobiyas.racesandclasses.configuration.raceteams;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.util.config.YAMLConfigExtended;

public class RaceTeamManager {

	
	/**
	 * The Teams to use
	 */
	private final Set<RaceTeam> teams = new HashSet<RaceTeam>();
	
	
	
	/**
	 * Reloads the Whole manager.
	 */
	public void reaload(){
		teams.clear();
		
		File file = new File(RacesAndClasses.getPlugin().getDataFolder(), "raceTeams.yml");
		YAMLConfigExtended config = new YAMLConfigExtended(file);
		
		if(!file.exists()){
			String text = "When 2 players are in the same Team, they can not harm each other. Synthax:   team1: [Elf,Orc]";
			config.options().header(text);
			
			config.options().copyDefaults(true);
			config.options().copyHeader(true);
			
			config.forceSave();
		}
		
		config.load();
		for(String teamName : config.getRootChildren()){
			if(!config.isList(teamName)) continue;
			
			List<String> teamList = config.getStringList(teamName);
			if(!teamList.isEmpty()) teams.add(new RaceTeam(teamName, teamList));
		}
	}
	
	
	/**
	 * Checks if the Players are in the Same team.
	 * 
	 * @param player1 to check
	 * @param player2 to check
	 * 
	 * @return true if they are in the same team.
	 */
	public boolean sameTeam(RaCPlayer player1, RaCPlayer player2){
		if(player1 == null || player2 == null) return false;
		
		for(RaceTeam team : teams){
			if(team.isInTeam(player1, player2)) return true;
		}
		
		return false;
	}
	
	
}
