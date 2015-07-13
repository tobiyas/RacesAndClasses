package de.tobiyas.racesandclasses.configuration.armory;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.util.config.YAMLConfigExtended;
import de.tobiyas.racesandclasses.util.items.ItemUtils.ItemQuality;


public class ArmorConfig {

	
	/**
	 * The File for the Default File.
	 */
	private final File defaultFile = new File(RacesAndClasses.getPlugin().getDataFolder(), "ArmorAndWeapons.yml");
	
	/**
	 * The Config to use.
	 */
	private final YAMLConfigExtended config = new YAMLConfigExtended(defaultFile);
	
	
	
	public ArmorConfig() {
		if(!defaultFile.exists()){
			createDefaultFile();
		}
		
		reload();
	}

	
	/**
	 * Creates the default File.
	 */
	private void createDefaultFile() {
		config.clearConfig();
		
		Set<Material> materials = new HashSet<Material>();
		materials.clear();
		materials.add(Material.LEATHER_BOOTS);
		materials.add(Material.LEATHER_LEGGINGS);
		materials.add(Material.LEATHER_CHESTPLATE);
		materials.add(Material.LEATHER_HELMET);
		config.addDefault("leather", toStringList(materials));
		
		materials.clear();
		materials.add(Material.IRON_BOOTS);
		materials.add(Material.IRON_LEGGINGS);
		materials.add(Material.IRON_CHESTPLATE);
		materials.add(Material.IRON_HELMET);
		config.addDefault("iron", toStringList(materials));
		
		materials.clear();
		materials.add(Material.CHAINMAIL_BOOTS);
		materials.add(Material.CHAINMAIL_LEGGINGS);
		materials.add(Material.CHAINMAIL_CHESTPLATE);
		materials.add(Material.CHAINMAIL_HELMET);
		config.addDefault("chain", toStringList(materials));
		
		materials.clear();
		materials.add(Material.GOLD_BOOTS);
		materials.add(Material.GOLD_LEGGINGS);
		materials.add(Material.GOLD_CHESTPLATE);
		materials.add(Material.GOLD_HELMET);
		config.addDefault("gold", toStringList(materials));
		
		materials.clear();
		materials.add(Material.DIAMOND_BOOTS);
		materials.add(Material.DIAMOND_LEGGINGS);
		materials.add(Material.DIAMOND_CHESTPLATE);
		materials.add(Material.DIAMOND_HELMET);
		config.addDefault("diamond", toStringList(materials));
		
		config.options().copyDefaults(true);
		config.save();
	}

	
	/**
	 * Returns a new List with String from the Collection.
	 * 
	 * @param mats to parse.
	 * @return a list of Strings.
	 */
	private List<String> toStringList(Collection<Material> mats){
		List<String> names = new LinkedList<String>();
		for(Material mat : mats) names.add(mat.name());
		
		return names;
	}
	
	
	/**
	 * Reloads the Config.
	 */
	public void reload(){
		config.load();
		
		Map<String,Set<Material>> toSet = new HashMap<String, Set<Material>>();
		for(String node : config.getRootChildren()){
			Set<Material> parsed = new HashSet<Material>();
			parseMaterials(parsed, config.getStringList(node));
			
			if(!parsed.isEmpty()) toSet.put(node, parsed);
		}
		
		//Load default!
		ItemQuality.loadDefault();
		
		//now Set!
		if(!toSet.isEmpty()) ItemQuality.set(toSet);
	}



	/**
	 * Parses and adds the Materials to the Collections List.
	 * <br>This also clears the Collection before.
	 * 
	 * @param collection to add to 
	 * @param toAdd to add
	 */
	private void parseMaterials(Collection<Material> collection, Collection<String> toAdd){
		collection.clear();
		for(String key : toAdd){
			addMaterial(collection, key);
		}
	}
	
	
	/**
	 * Adds a Mat to the List passed.
	 * 
	 * @param collection to add to
	 * @param mat to add
	 */
	private void addMaterial(Collection<Material> collection, String mat){
		try{
			Material realMat = Material.matchMaterial(mat);
			if(realMat != null) collection.add(realMat);
		}catch(Throwable exp){}
	}

}
