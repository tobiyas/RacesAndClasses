package de.tobiyas.racesandclasses.util.consts;

import java.io.File;

import de.tobiyas.racesandclasses.RacesAndClasses;

public class Consts {
	
	private static final String racesPath = RacesAndClasses.getPlugin().getDataFolder() + File.separator;
	
	//Paths
	public static final String channelConfigPathYML = racesPath + "channels" + File.separator;
	public static final String traitConfigDir = RacesAndClasses.getPlugin().getDataFolder() + File.separator + "TraitConfig" + File.separator;
	public static final String tutorialPath = RacesAndClasses.getPlugin().getDataFolder() + File.separator + "Tutorials" + File.separator;
	public static String playerDataPath = racesPath + File.separator + "PlayerData" + File.separator; //Not final because of testing!
	
	
	//Files
	public static final String racesYML = racesPath + "races.yml";
	public static final String classesYML = racesPath +"classes.yml";
	public static final String channelsYML = channelConfigPathYML + "channels.yml";
	public static final String channelConfigYML = channelConfigPathYML + "config.yml";
	public static final String tutorialYML = tutorialPath + "states.yml";
	
	//Health
	public static final int displayBarLength = 20;
	
	//Debugging
	public static final int timingLength = 10;
	
	//Version
	public static final String currentDevStage = "B T";
	public static final String detailedVersionString = "1.0.3 RC 2";
	public static final String configVersion = "1.3"; //<- for 1.0.3 RC 2 Version: 1.3 (Changed FOR RC 2)
	
	//Races
	public static String defaultRace = "DefaultRace";
	
	
	//Versioning
	public static final String minimalBukkitVersionString = "1.6.2";
	public static final int minimalBukkitMainVersion = 1;
	public static final int minimalBukkitSubVersion = 6;
	public static final int minimalBukkitRevVersion = 2;
	
	public static final int CURSE_ID_FOR_PLUGIN = 58896;
}