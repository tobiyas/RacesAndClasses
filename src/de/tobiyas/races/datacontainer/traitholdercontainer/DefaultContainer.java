package de.tobiyas.races.datacontainer.traitholdercontainer;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

import de.tobiyas.races.Races;
import de.tobiyas.races.util.consts.Consts;

public class DefaultContainer {
	
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
	
	public static void createSTDClasses(){
		Races plugin = Races.getPlugin();
		File classFile = new File(Consts.classesYML);
		if(classFile.exists()) return;
		
		try {
			classFile.createNewFile();
		} catch (IOException e) {
			plugin.log("Could not create classes.yml");
			return;
		}
		
		plugin.log("No Class file found. Creating new.");
		
		YamlConfiguration classesConfig = new YamlConfiguration();
		classesConfig.createSection("classes");
		
		classesConfig.createSection("classes.warrior");
		classesConfig.createSection("classes.warrior.config");
		classesConfig.createSection("classes.warrior.traits");
		
		classesConfig.set("classes.warrior.config.classtag", "[Warrior]");
		classesConfig.set("classes.warrior.config.health", "+5");
		classesConfig.set("classes.warrior.traits.SwordDamageIncreaseTrait", "+0.5");
		classesConfig.set("classes.warrior.traits.AxeDamageIncreaseTrait", "+0.5");
		
		classesConfig.createSection("classes.archer");
		classesConfig.createSection("classes.archer.config");
		classesConfig.createSection("classes.archer.traits");
		
		classesConfig.set("classes.archer.config.classtag", "[Archer]");
		classesConfig.set("classes.archer.config.health", "+1");
		classesConfig.set("classes.archer.traits.PoisonArrowTrait", "10#10");
		classesConfig.set("classes.archer.traits.FireArrowTrait", "10#20");
		classesConfig.set("classes.archer.traits.TeleportArrowTrait", "");
		
		try {
			classesConfig.save(classFile);
		} catch (IOException e) {
			plugin.log("Saving STD classes.yml failed.");
			return;
		}
		
		
	}

}
