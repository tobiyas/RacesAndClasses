/*******************************************************************************
 * Copyright 2014 Tobias Welther
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
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
		raceConfig.set("Orc.traits.BerserkerRageTrait.cooldown", 30);

		
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
		raceConfig.set("Elv.traits.SprintTrait.cooldown", 60);
		
		
		try {
			raceConfig.save(raceFile);
		} catch (IOException e) {
			plugin.log("Saving STD races.yml failed.");
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
		classesConfig.set("archer.traits.PoisonArrowTrait.cooldown", 30);
		
		classesConfig.createSection("archer.traits.FireArrowTrait");
		classesConfig.set("archer.traits.FireArrowTrait.duration", 10);
		classesConfig.set("archer.traits.FireArrowTrait.totalDamage", 10);
		classesConfig.set("archer.traits.FireArrowTrait.cooldown", 30);

		classesConfig.createSection("archer.traits.TeleportArrowTrait");
		classesConfig.set("archer.traits.TeleportArrowTrait.cooldown", 60);
		
		//Magician:
		classesConfig.createSection("magician");
		classesConfig.createSection("magician.config");
		classesConfig.createSection("magician.traits");
		
		classesConfig.set("magician.config.classtag", "[Magician]");
		classesConfig.set("magician.config.health", "-1");
		classesConfig.set("magician.config.manabonus", 20);
		
		classesConfig.createSection("magician.traits.ManaRegenerationTrait");
		classesConfig.set("magician.traits.ManaRegenerationTrait.time", 10);
		classesConfig.set("magician.traits.ManaRegenerationTrait.value", 2);
		
		classesConfig.createSection("magician.traits.FireballTrait");
		classesConfig.set("magician.traits.FireballTrait.cost", 6);
		classesConfig.set("magician.traits.FireballTrait.cooldown", 10);

		classesConfig.createSection("magician.traits.ItemForManaConsumeTrait");
		classesConfig.set("magician.traits.ItemForManaConsumeTrait.cooldown", 10);
		classesConfig.set("magician.traits.ItemForManaConsumeTrait.item", "WHEAT");
		classesConfig.set("magician.traits.ItemForManaConsumeTrait.cost", 1);
		classesConfig.set("magician.traits.ItemForManaConsumeTrait.value", 10);

		classesConfig.createSection("magician.traits.WallTrait");
		classesConfig.set("magician.traits.WallTrait.cooldown", 10);
		classesConfig.set("magician.traits.WallTrait.cost", 5);
		classesConfig.set("magician.traits.WallTrait.duration", 5);
		
		classesConfig.createSection("magician.traits.ExplosionTrait");
		classesConfig.set("magician.traits.ExplosionTrait.cooldown", 10);
		classesConfig.set("magician.traits.ExplosionTrait.cost", 10);
		classesConfig.set("magician.traits.ExplosionTrait.range", 5);
		classesConfig.set("magician.traits.ExplosionTrait.damage", 4);
		
		classesConfig.createSection("magician.traits.SlowFallTrait");
		classesConfig.set("magician.traits.SlowFallTrait.cooldown", 10);
		classesConfig.set("magician.traits.SlowFallTrait.cost", 5);
		classesConfig.set("magician.traits.SlowFallTrait.duration", 10);
		
		classesConfig.createSection("magician.traits.ColdFeetTrait");
		classesConfig.set("magician.traits.ColdFeetTrait.cost", 5);
		classesConfig.set("magician.traits.ColdFeetTrait.duration", 10);
		
		try {
			classesConfig.save(classFile);
		} catch (IOException e) {
			plugin.log("Saving STD classes.yml failed.");
			return;
		}
		
		
	}

}
