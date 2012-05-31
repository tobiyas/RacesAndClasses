package de.tobiyas.races.util.consts;

import java.io.File;

import de.tobiyas.races.Races;

public class Consts {

	//Paths
	public static String membersYML = Races.getPlugin().getDataFolder() + File.separator + "PlayerData" + File.separator + "playerdata.yml";
	public static String racesYML = Races.getPlugin().getDataFolder() + File.separator + "races.yml";
	
	//Health
	public static int healthBarLength = 20;
}
