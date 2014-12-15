package de.tobiyas.racesandclasses.standalonegui.data;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class GuiRace implements Comparable<GuiRace> {

	/**
	 * The Races
	 */
	private static final HashSet<GuiRace> races = new HashSet<GuiRace>();
	
	
	/**
	 * THe Traits of the Race
	 */
	private final Set<GuiTrait> traits = new HashSet<GuiTrait>();
	
	/**
	 * The Name of the Race
	 */
	private String raceName = "NONE";
	
	
	public GuiRace(String raceName, Set<GuiTrait> traits) {
		this.traits.addAll(traits);
		this.raceName = raceName;
	}

	
	
	public String getRaceName() {
		return raceName;
	}
	
	public void setRaceName(String raceName) {
		this.raceName = raceName;
	}
	
	

	public Set<GuiTrait> getTraits() {
		return traits;
	}
	
	
	@Override
	public String toString() {
		return raceName;
	}
	
	
	
	
	//Controll of the Races//
	
	
	public static void addNewRace(GuiRace race){
		races.add(race);
	}
	
	
	public static void removeRace(GuiRace race){
		races.remove(race);
	}
	
	
	/**
	 * Returns the Set of races.
	 * 
	 * @return
	 */
	public static Set<GuiRace> getRaces(){
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



	@Override
	public int compareTo(GuiRace o) {
		return raceName.compareTo(o.raceName);
	}
	
}
