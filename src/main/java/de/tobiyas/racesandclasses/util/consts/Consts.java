package de.tobiyas.racesandclasses.util.consts;

import java.io.File;

import de.tobiyas.racesandclasses.RacesAndClasses;

public class Consts {
	
	private static final String racesPath = RacesAndClasses.getPlugin().getDataFolder() + File.separator;
	
	//Paths
	public static final String channelConfigPathYML = racesPath + "channels" + File.separator;
	public static final String traitConfigDir = RacesAndClasses.getPlugin().getDataFolder() + File.separator + "TraitConfig" + File.separator;
	public static final String tutorialPath = RacesAndClasses.getPlugin().getDataFolder() + File.separator + "Tutorials" + File.separator;
	
	
	//Files
	public static final String playerDataYML = racesPath + File.separator + "PlayerData" + File.separator + "playerdata.yml";
	public static final String racesYML = racesPath + "races.yml";
	public static final String classesYML = racesPath +"classes.yml";
	public static final String channelsYML = channelConfigPathYML + "channels.yml";
	public static final String channelConfigYML = channelConfigPathYML + "config.yml";
	public static final String tutorialYML = tutorialPath + "states.yml";
	
	//Health
	public static final int healthBarLength = 20;
	
	//Debugging
	public static final int timingLength = 10;
	
	//Knockback
	public static double knockBackMulti = 0.7;
	
	//DeathManager
	public static int secondsPlayerHit = 5;
	
	//Version
	public static String currentDevStage = "B T";
	public static String detailedVersionString = "1.0.2 BETA 5 - RC 2";
	
	
	//Enchants
	public static int maxLevel = 5;
	
	//Races
	public static String defaultRace = "DefaultRace";
	
	
	//Versioning
	public static String minimalBukkitVersionString = "1.6.2";
	public static final int minimalBukkitMainVersion = 1;
	public static final int minimalBukkitSubVersion = 6;
	public static final int minimalBukkitRevVersion = 2;
}