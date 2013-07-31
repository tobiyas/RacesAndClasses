package de.tobiyas.racesandclasses.datacontainer.traitholdercontainer;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.util.consts.Consts;

public class DefaultContainer {
	
	public static void createSTDRaces(){
		RacesAndClasses plugin = RacesAndClasses.getPlugin();
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
		raceConfig.createSection("Orc");
		raceConfig.createSection("Orc.config");
		raceConfig.createSection("Orc.traits");
		
		raceConfig.set("Orc.config.racetag", "[Orc]");
		raceConfig.set("Orc.config.raceMaxHealth", 30);
		raceConfig.set("Orc.config.armor", "iron,diamond,chain");
		
		raceConfig.createSection("Orc.traits.DamageReduceTrait");
		raceConfig.set("Orc.traits.DamageReduceTrait.operation", '*');
		raceConfig.set("Orc.traits.DamageReduceTrait.value", 0.5);
		
		raceConfig.createSection("Orc.traits.BerserkerRageTrait");
		raceConfig.set("Orc.traits.BerserkerRageTrait.operation", '+');
		raceConfig.set("Orc.traits.BerserkerRageTrait.value", 1);

		
		raceConfig.createSection("Elv.config");
		raceConfig.createSection("Elv.traits");
		
		raceConfig.set("Elv.config.racetag", "[Elv]");
		raceConfig.set("Elv.config.raceMaxHealth", 20);
		raceConfig.set("Elv.config.armor", "leather,gold,chain");
		
		raceConfig.createSection("Elv.traits.FallResistanceTrait");
		raceConfig.set("Elv.traits.FallResistanceTrait.operation", '-');
		raceConfig.set("Elv.traits.FallResistanceTrait.value", 2);

		raceConfig.createSection("Elv.traits.SprintTrait");
		raceConfig.set("Elv.traits.SprintTrait.duration", 10);
		raceConfig.set("Elv.traits.SprintTrait.value", 3);
		
		
		try {
			raceConfig.save(raceFile);
		} catch (IOException e) {
			plugin.log("Saving STD races.yml failed.");
			return;
		}	
	}
	
	public static void createSTDMembers(){
		RacesAndClasses plugin = RacesAndClasses.getPlugin();
		File membersFile = new File(Consts.playerDataYML);
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
		RacesAndClasses plugin = RacesAndClasses.getPlugin();
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
		
		//Warrior:
		classesConfig.createSection("warrior");
		classesConfig.createSection("warrior.config");
		classesConfig.createSection("warrior.traits");
		
		classesConfig.set("warrior.config.classtag", "[Warrior]");
		classesConfig.set("warrior.config.health", "+5");
		
		
		classesConfig.createSection("warrior.traits.SwordDamageIncreaseTrait");
		classesConfig.set("warrior.traits.SwordDamageIncreaseTrait.operation", '+');
		classesConfig.set("warrior.traits.SwordDamageIncreaseTrait.value", 1);

		classesConfig.createSection("warrior.traits.AxeDamageIncreaseTrait");
		classesConfig.set("warrior.traits.AxeDamageIncreaseTrait.operation", '+');
		classesConfig.set("warrior.traits.AxeDamageIncreaseTrait.value", 1);
		
		//Archer:
		classesConfig.createSection("archer");
		classesConfig.createSection("archer.config");
		classesConfig.createSection("archer.traits");
		
		classesConfig.set("archer.config.classtag", "[Archer]");
		classesConfig.set("archer.config.health", "+1");
		
		classesConfig.createSection("archer.traits.PoisonArrowTrait");
		classesConfig.set("archer.traits.PoisonArrowTrait.duration", 10);
		classesConfig.set("archer.traits.PoisonArrowTrait.totalDamage", 10);
		
		classesConfig.createSection("archer.traits.FireArrowTrait");
		classesConfig.set("archer.traits.FireArrowTrait.duration", 10);
		classesConfig.set("archer.traits.FireArrowTrait.totalDamage", 10);

		classesConfig.createSection("archer.traits.TeleportArrowTrait");
		
		try {
			classesConfig.save(classFile);
		} catch (IOException e) {
			plugin.log("Saving STD classes.yml failed.");
			return;
		}
		
		
	}

}
