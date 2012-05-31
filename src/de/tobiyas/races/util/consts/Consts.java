package de.tobiyas.races.util.consts;

import java.io.File;

import de.tobiyas.races.Races;

public class Consts {

	//Paths
	public static final String membersYML = Races.getPlugin().getDataFolder() + File.separator + "PlayerData" + File.separator + "playerdata.yml";
	public static final String racesYML = Races.getPlugin().getDataFolder() + File.separator + "races.yml";
	
	public static final String traitConfigDir = Races.getPlugin().getDataFolder() + File.separator + "TraitConfig" + File.separator;
	
	//Health
	public static int healthBarLength = 20;
}
