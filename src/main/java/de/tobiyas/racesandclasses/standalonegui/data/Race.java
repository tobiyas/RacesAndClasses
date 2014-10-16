package de.tobiyas.racesandclasses.standalonegui.data;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class Race {

	/**
	 * The Races
	 */
	private static final HashSet<Race> races = new HashSet<Race>();
	
	
	/**
	 * THe Traits of the Race
	 */
	private final Set<Trait> traits = new HashSet<Trait>();
	
	/**
	 * The Name of the Race
	 */
	private String raceName = "NONE";

	
	
	public String getRaceName() {
		return raceName;
	}
	
	public void setRaceName(String raceName) {
		this.raceName = raceName;
	}
	
	

	public Set<Trait> getTraits() {
		return traits;
	}
	
	
	
	
	//Controll of the Races//
	
	
	public static void addNewRace(Race race){
		races.add(race);
	}
	
	
	public static void removeRace(Race race){
		races.remove(race);
	}
	
	
	/**
	 * Returns the Set of races.
	 * 
	 * @return
	 */
	public static Set<Race> getRaces(){
		return races;
	}
	
	
	/**
	 * Load the Races from a dir.
	 * 
	 * @param dir to load from.
	 */
	public static void loadRaces(File dir){
		
	}
	
	/**
	 * Saves the Races to a dir.
	 * 
	 * @param dir to save to.
	 */
	public static void saveRaces(File dir){
		
	}
	
}
