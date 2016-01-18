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

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import de.tobiyas.racesandclasses.RacesAndClasses;

public class DefaultContainer {
	
	public static void createSTDRaces(){
		RacesAndClasses plugin = RacesAndClasses.getPlugin();
		File raceDir = new File(plugin.getDataFolder(), "races");
		if(!raceDir.exists()) raceDir.mkdirs();
		else return;
		
		plugin.log("No Race file found. Creating new.");
		
		//orcs
		YamlConfiguration orcConfig = new YamlConfiguration();
		File orcFile = new File(raceDir, "orc.yml");
		orcConfig.createSection("Orc");
		orcConfig.createSection("Orc.config");
		orcConfig.createSection("Orc.traits");
		
		orcConfig.set("Orc.config.tag", "[Orc]");
		orcConfig.set("Orc.config.healthbonus", 10);
		orcConfig.set("Orc.config.guislot", -1);
		orcConfig.set("Orc.config.armor", "iron,diamond,chain");
		
		orcConfig.createSection("Orc.traits.DamageReduceTrait");
		orcConfig.set("Orc.traits.DamageReduceTrait.operation", '*');
		orcConfig.set("Orc.traits.DamageReduceTrait.value", 0.5);
		
		orcConfig.createSection("Orc.traits.BerserkerRageTrait");
		orcConfig.set("Orc.traits.BerserkerRageTrait.operation", '+');
		orcConfig.set("Orc.traits.BerserkerRageTrait.value", 1);
		orcConfig.set("Orc.traits.BerserkerRageTrait.cooldown", 30);

		
		//Elves
		YamlConfiguration elvesConfig = new YamlConfiguration();
		File elvesFile = new File(raceDir, "elves.yml");
		elvesConfig.createSection("Elv.config");
		elvesConfig.createSection("Elv.traits");
		
		elvesConfig.set("Elv.config.tag", "[Elv]");
		elvesConfig.set("Elv.config.healthbonus", 0);
		elvesConfig.set("Elv.config.guislot", -1);
		elvesConfig.set("Elv.config.armor", "leather,gold,chain");
		
		elvesConfig.createSection("Elv.traits.FallResistanceTrait");
		elvesConfig.set("Elv.traits.FallResistanceTrait.operation", '-');
		elvesConfig.set("Elv.traits.FallResistanceTrait.value", 2);

		elvesConfig.createSection("Elv.traits.SprintTrait");
		elvesConfig.set("Elv.traits.SprintTrait.duration", 10);
		elvesConfig.set("Elv.traits.SprintTrait.value", 3);
		elvesConfig.set("Elv.traits.SprintTrait.cooldown", 60);
		
		
		try {
			orcConfig.save(orcFile);
			elvesConfig.save(elvesFile);
		} catch (IOException e) {
			plugin.log("Saving STD races.yml failed.");
			return;
		}	
	}
	
	
	public static void createSTDClasses(){
		RacesAndClasses plugin = RacesAndClasses.getPlugin();
		File classDir = new File(plugin.getDataFolder(), "classes");
		if(!classDir.exists()) classDir.mkdirs();
		else return;
		
		plugin.log("No Class files found. Creating new.");
		
		//Warrior:
		YamlConfiguration warriorConfig = new YamlConfiguration();
		File warriorFile = new File(classDir, "warrior.yml");
		warriorConfig.createSection("warrior");
		warriorConfig.createSection("warrior.config");
		warriorConfig.createSection("warrior.traits");
		
		warriorConfig.set("warrior.config.tag", "[Warrior]");
		warriorConfig.set("warrior.config.healthbonus", 5);
		warriorConfig.set("warrior.config.guislot", -1);
		
		
		warriorConfig.createSection("warrior.traits.SwordDamageIncreaseTrait");
		warriorConfig.set("warrior.traits.SwordDamageIncreaseTrait.operation", '+');
		warriorConfig.set("warrior.traits.SwordDamageIncreaseTrait.value", 1);

		warriorConfig.createSection("warrior.traits.AxeDamageIncreaseTrait");
		warriorConfig.set("warrior.traits.AxeDamageIncreaseTrait.operation", '+');
		warriorConfig.set("warrior.traits.AxeDamageIncreaseTrait.value", 1);
		
		
		//Archer:
		YamlConfiguration archerConfig = new YamlConfiguration();
		File archerFile = new File(classDir, "archer.yml");
		archerConfig.createSection("archer");
		archerConfig.createSection("archer.config");
		archerConfig.createSection("archer.traits");
		
		archerConfig.set("archer.config.tag", "[Archer]");
		archerConfig.set("archer.config.healthbonus", "+1");
		archerConfig.set("archer.config.guislot", -1);
		
		archerConfig.createSection("archer.traits.PoisonArrowTrait");
		archerConfig.set("archer.traits.PoisonArrowTrait.duration", 10);
		archerConfig.set("archer.traits.PoisonArrowTrait.totalDamage", 10);
		archerConfig.set("archer.traits.PoisonArrowTrait.cooldown", 30);
		
		archerConfig.createSection("archer.traits.FireArrowTrait");
		archerConfig.set("archer.traits.FireArrowTrait.duration", 10);
		archerConfig.set("archer.traits.FireArrowTrait.totalDamage", 10);
		archerConfig.set("archer.traits.FireArrowTrait.cooldown", 30);

		archerConfig.createSection("archer.traits.TeleportArrowTrait");
		archerConfig.set("archer.traits.TeleportArrowTrait.cooldown", 60);
		
		//Magician:
		YamlConfiguration magicianConfig = new YamlConfiguration();
		File magicianFile = new File(classDir, "magician.yml");
		magicianConfig.createSection("magician");
		magicianConfig.createSection("magician.config");
		magicianConfig.createSection("magician.traits");
		
		magicianConfig.set("magician.config.tag", "[Magician]");
		magicianConfig.set("magician.config.healthbonus", -1);
		magicianConfig.set("magician.config.manabonus", 20);
		magicianConfig.set("magician.config.guislot", -1);
		
		magicianConfig.createSection("magician.traits.ManaRegenerationTrait");
		magicianConfig.set("magician.traits.ManaRegenerationTrait.time", 10);
		magicianConfig.set("magician.traits.ManaRegenerationTrait.value", 2);
		
		magicianConfig.createSection("magician.traits.FireballTrait");
		magicianConfig.set("magician.traits.FireballTrait.cost", 6);
		magicianConfig.set("magician.traits.FireballTrait.cooldown", 10);

		magicianConfig.createSection("magician.traits.ItemForManaConsumeTrait");
		magicianConfig.set("magician.traits.ItemForManaConsumeTrait.cooldown", 10);
		magicianConfig.set("magician.traits.ItemForManaConsumeTrait.item", "WHEAT");
		magicianConfig.set("magician.traits.ItemForManaConsumeTrait.cost", 1);
		magicianConfig.set("magician.traits.ItemForManaConsumeTrait.value", 10);

		magicianConfig.createSection("magician.traits.WallTrait");
		magicianConfig.set("magician.traits.WallTrait.cooldown", 10);
		magicianConfig.set("magician.traits.WallTrait.cost", 5);
		magicianConfig.set("magician.traits.WallTrait.duration", 5);
		
		magicianConfig.createSection("magician.traits.ExplosionTrait");
		magicianConfig.set("magician.traits.ExplosionTrait.cooldown", 10);
		magicianConfig.set("magician.traits.ExplosionTrait.cost", 10);
		magicianConfig.set("magician.traits.ExplosionTrait.range", 5);
		magicianConfig.set("magician.traits.ExplosionTrait.damage", 4);
		
		magicianConfig.createSection("magician.traits.SlowFallTrait");
		magicianConfig.set("magician.traits.SlowFallTrait.cooldown", 10);
		magicianConfig.set("magician.traits.SlowFallTrait.cost", 5);
		magicianConfig.set("magician.traits.SlowFallTrait.duration", 10);
		
		magicianConfig.createSection("magician.traits.ColdFeetTrait");
		magicianConfig.set("magician.traits.ColdFeetTrait.cost", 5);
		magicianConfig.set("magician.traits.ColdFeetTrait.duration", 10);
		
		//Shaman:
		YamlConfiguration shamanConfig = new YamlConfiguration();
		File shamanFile = new File(classDir, "shaman.yml");
		shamanConfig.createSection("shaman");
		shamanConfig.createSection("shaman.config");
		shamanConfig.createSection("shaman.traits");
		
		shamanConfig.set("shaman.config.tag", "[Shaman]");
		shamanConfig.set("shaman.config.healthbonus", 1);
		shamanConfig.set("shaman.config.manabonus", 20);
		shamanConfig.set("shaman.config.guislot", -1);
		
		shamanConfig.createSection("shaman.traits.HealTotemTrait");
		shamanConfig.set("shaman.traits.HealTotemTrait.duration", 10);
		shamanConfig.set("shaman.traits.HealTotemTrait.value", 3);
		shamanConfig.set("shaman.traits.HealTotemTrait.every", 2);
		
		shamanConfig.createSection("shaman.traits.PotionTotemTrait");
		shamanConfig.set("shaman.traits.PotionTotemTrait.effect", 6);
		shamanConfig.set("shaman.traits.PotionTotemTrait.duration", 10);
		
		shamanConfig.createSection("shaman.traits.ItemForManaConsumeTrait");
		shamanConfig.set("shaman.traits.ItemForManaConsumeTrait.cooldown", 10);
		shamanConfig.set("shaman.traits.ItemForManaConsumeTrait.item", Material.POTION.name());
		shamanConfig.set("shaman.traits.ItemForManaConsumeTrait.cost", 1);
		shamanConfig.set("shaman.traits.ItemForManaConsumeTrait.value", 10);
		
		shamanConfig.createSection("shaman.traits.ManaRegenerationTrait");
		shamanConfig.set("shaman.traits.ManaRegenerationTrait.value", 1);
		shamanConfig.set("shaman.traits.ManaRegenerationTrait.time", 5);

		
		try {
			shamanConfig.save(shamanFile);
			archerConfig.save(archerFile);
			magicianConfig.save(magicianFile);
			warriorConfig.save(warriorFile);
		} catch (IOException e) {
			plugin.log("Saving STD classes.yml failed.");
			return;
		}
		
		
	}

}
