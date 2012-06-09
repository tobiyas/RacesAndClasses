package de.tobiyas.races.util.consts;

import java.io.File;

import de.tobiyas.races.Races;

public class Consts {
	
	private static final String racesPath = Races.getPlugin().getDataFolder() + File.separator;

	//Paths
	public static final String membersYML = racesPath + "playerdata.yml";
	public static final String racesYML = racesPath + "races.yml";
	public static final String classesYML = racesPath +"classes.yml";
	public static final String channelsYML = racesPath + "channels.yml";
	
	public static final String traitConfigDir = Races.getPlugin().getDataFolder() + File.separator + "TraitConfig" + File.separator;
	
	//Health
	public static final int healthBarLength = 20;
}
