package de.tobiyas.races.datacontainer.race;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

import de.tobiyas.races.Races;
import de.tobiyas.races.util.consts.Consts;

public class DefaultRace {
	
	public static void createSTDRaces(){
		Races plugin = Races.getPlugin();
		File raceFile = new File(Consts.racesYML);
		if(raceFile.exists()) return;
		
		try {
			raceFile.createNewFile();
		} catch (IOException e) {
			plugin.log("Could not create races.yml");
			return;
		}
		
		plugin.log("No Race file found. Creating new.");
		
		YamlConfiguration raceConfig = new YamlConfiguration();
		raceConfig.createSection("races");
		
		raceConfig.createSection("races.orc");
		raceConfig.createSection("races.orc.config");
		raceConfig.createSection("races.orc.traits");
		
		raceConfig.set("races.orc.config.racetag", "[Orc]");
		raceConfig.set("races.orc.traits.DamageReduceTrait", 0.5);
		
		try {
			raceConfig.save(raceFile);
		} catch (IOException e) {
			plugin.log("Saving STD races.yml failed.");
			return;
		}
		
		
	}
	
	public static void createSTDMembers(){
		Races plugin = Races.getPlugin();
		File membersFile = new File(Consts.membersYML);
		if(membersFile.exists()) return;
		
		try {
			membersFile.createNewFile();
		} catch (IOException e) {
			plugin.log("Could not create members.yml");
			return;
		}
		
		plugin.log("No Member file found. Creating new.");
		
		YamlConfiguration membersConfig = new YamlConfiguration();
		membersConfig.createSection("playerdata");
		
		try {
			membersConfig.save(membersFile);
		} catch (IOException e) {
			plugin.log("Saving STD member.yml failed.");
			return;
		}
	}

}
