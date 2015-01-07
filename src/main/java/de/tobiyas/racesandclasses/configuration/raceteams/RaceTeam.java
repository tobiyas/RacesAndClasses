package de.tobiyas.racesandclasses.configuration.raceteams;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;

public class RaceTeam {

	/**
	 * The Set of Race Names.
	 */
	private final Set<String> raceNames = new HashSet<String>();
	
	/**
	 * The Name of the Team.
	 */
	private final String teamName;
	
	
	/**
	 * Creates a Team for the Set of Race names.
	 * 
	 * @param raceNames to set.
	 */
	public RaceTeam(String teamName, Collection<String> raceNames) {
		this.teamName = teamName;
		
		if(raceNames != null && !raceNames.isEmpty()) this.raceNames.addAll(raceNames);
	}
	
	
	
	/**
	 * Checks if the Player is in this Team present.
	 * 
	 * @param player1 to check
	 * @param player2 to check
	 * 
	 * @return true if and only IF the players are in this team.
	 */
	public boolean isInTeam(RaCPlayer player1, RaCPlayer player2){
		if(raceNames.isEmpty()) return false;
		
		RaceContainer race1 = player1.getRace();
		RaceContainer race2 = player2.getRace();
		
		if(race1 == null || race2 == null) return false;
		return raceNames.contains(race1.getDisplayName()) && raceNames.contains(race2.getDisplayName());
	}
	
	
	/**
	 * Returns the Team name.
	 * 
	 * @return the team name.
	 */
	public String getTeamName() {
		return teamName;
	}
	
}
