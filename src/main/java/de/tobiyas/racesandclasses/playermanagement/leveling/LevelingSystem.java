package de.tobiyas.racesandclasses.playermanagement.leveling;

public enum LevelingSystem {
	/**
	 * The Inbuild Level System
	 */
	RacesAndClasses,
	
	/**
	 * The MC level
	 */
	VanillaMC,
	
	/**
	 * SkillAPI is used.
	 */
	SkillAPI;

	
	/**
	 * Parses an String to the corresponding LevelingSystem.
	 * 
	 * @param toParse the String to parse.
	 * 
	 * @return the correct leveling system.
	 */
	public static LevelingSystem parse(String toParse) {
		if(toParse == null) return RacesAndClasses;
		
		toParse = toParse.toLowerCase();
		if(toParse.startsWith("r")){
			return RacesAndClasses;
		}
		
		if(toParse.startsWith("m")){
			return VanillaMC;
		}
		
		if(toParse.startsWith("s")){
			return SkillAPI;
		}
		
		return RacesAndClasses;
	}
}
