package de.tobiyas.races.configuration.traits;

import java.io.File;
import java.io.IOException;

import de.tobiyas.races.Races;
import de.tobiyas.util.config.YAMLConfigExtended;
import de.tobiyas.races.util.consts.Consts;
import de.tobiyas.races.util.items.CreateDropContainer;

public class DefaultTraitConfig {

	public static void createDefaultTraitConfig(){
		createTraitConfig("TrollbloodTrait", "trait.uplink", 60, false);
		createTraitConfig("TrollbloodTrait", "trait.iteminhand", 260, true);
		
		createTraitConfig("SprintTrait", "trait.uplink", 40, false);
		createTraitConfig("SprintTrait", "trait.iteminhand", 260, true);
		
		createTraitConfig("TeleportArrowTrait", "trait.uplink", 60, false);
		
		createTraitConfig("STDAxeDamageTrait", "trait.damage.wood", 4, true);
		createTraitConfig("STDAxeDamageTrait", "trait.damage.stone", 5, true);
		createTraitConfig("STDAxeDamageTrait", "trait.damage.gold", 4, true);
		createTraitConfig("STDAxeDamageTrait", "trait.damage.iron", 6, true);
		createTraitConfig("STDAxeDamageTrait", "trait.damage.diamond", 7, true);
		
		createTraitConfig("HealOthersTrait", "trait.uplink", 60, true);
		createTraitConfig("HealOthersTrait", "trait.iteminhand", 287, true);
		
		createTraitConfig("BerserkerRageTrait", "trait.uplink", 60, true);
		createTraitConfig("BerserkerRageTrait", "trait.duration", 10, true);
		createTraitConfig("BerserkerRageTrait", "trait.activationLimit", 30, true);
		
		createTraitConfig("DwarfSkinTrait", "trait.uplink", 60, true);
		createTraitConfig("DwarfSkinTrait", "trait.duration", 10, true);
		createTraitConfig("DwarfSkinTrait", "trait.activationLimit", 30, true);
		
		createTraitConfig("LastStandTrait", "trait.uplink", 60, true);
		createTraitConfig("LastStandTrait", "trait.activationLimit", 30, true);
		
		CreateDropContainer.createAllContainers();		
	}
	
	private static void createTraitConfig(String traitName, String config, Object value, boolean force){
		YAMLConfigExtended yamlConfig = createYAMLIfNotExist(traitName, force);
		if(yamlConfig == null)
			return;
		
		if(!yamlConfig.isInt(config))
			yamlConfig.set(config, value);
		
		yamlConfig.save();
	}
	
	private static YAMLConfigExtended createYAMLIfNotExist(String traitName, boolean force){
		File file = new File(Consts.traitConfigDir, traitName + ".yml");
		if(file.exists()){
			if(!force)
				return null;
		}else
			try {
				file.createNewFile();
			} catch (IOException e) {
				Races.getPlugin().log("Could not create file: " + file.toString());
				return null;
			}
		
		YAMLConfigExtended config = new YAMLConfigExtended(file.toString()).load();
		if(config.getValidLoad())
			return config;
		
		return null;
	}
}
