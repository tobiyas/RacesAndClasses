package de.tobiyas.racesandclasses.configuration.armory;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.util.config.YAMLConfigExtended;


public class ItemStatsConfig {

	/**
	 * The Set of weapons
	 */
	private final Set<Material> weapons = new HashSet<Material>();
	
	/**
	 * The Set of Leather cloth
	 */
	private final Set<Material> leather = new HashSet<Material>();
	
	/**
	 * The Set of Iron cloth
	 */
	private final Set<Material> iron = new HashSet<Material>();
	
	/**
	 * The Set of chain cloth
	 */
	private final Set<Material> chain = new HashSet<Material>();
	
	/**
	 * The Set of gold cloth
	 */
	private final Set<Material> gold = new HashSet<Material>();
	
	/**
	 * The Set of diamond cloth
	 */
	private final Set<Material> diamond = new HashSet<Material>();
	
	/**
	 * The File for the Default File.
	 */
	private final File defaultFile = new File(RacesAndClasses.getPlugin().getDataFolder(), "ArmorAndWeapons.yml");
	
	/**
	 * The Config to use.
	 */
	private final YAMLConfigExtended config = new YAMLConfigExtended(defaultFile);
	
	
	
	public ItemStatsConfig() {
		fillSets();
		fillDefaultConfig();
		
		reload();
	}

	
	/**
	 * Fills all Sets with the default stuff.
	 */
	private void fillSets(){
		//leather
		leather.clear();
		leather.add(Material.LEATHER_BOOTS);
		leather.add(Material.LEATHER_LEGGINGS);
		leather.add(Material.LEATHER_CHESTPLATE);
		leather.add(Material.LEATHER_HELMET);
		
		//iron
		iron.clear();
		iron.add(Material.IRON_BOOTS);
		iron.add(Material.IRON_LEGGINGS);
		iron.add(Material.IRON_CHESTPLATE);
		iron.add(Material.IRON_HELMET);
		
		//chain
		chain.clear();
		chain.add(Material.CHAINMAIL_BOOTS);
		chain.add(Material.CHAINMAIL_LEGGINGS);
		chain.add(Material.CHAINMAIL_CHESTPLATE);
		chain.add(Material.CHAINMAIL_HELMET);
		
		//chain
		gold.clear();
		gold.add(Material.GOLD_BOOTS);
		gold.add(Material.GOLD_LEGGINGS);
		gold.add(Material.GOLD_CHESTPLATE);
		gold.add(Material.GOLD_HELMET);
		
		//diamond
		diamond.clear();
		diamond.add(Material.DIAMOND_BOOTS);
		diamond.add(Material.DIAMOND_LEGGINGS);
		diamond.add(Material.DIAMOND_CHESTPLATE);
		diamond.add(Material.DIAMOND_HELMET);
		
		//now to the Weapon list.
		weapons.clear();
		weapons.add(Material.WOOD_AXE);
		weapons.add(Material.WOOD_SWORD);
		weapons.add(Material.STONE_AXE);
		weapons.add(Material.STONE_SWORD);
		weapons.add(Material.IRON_AXE);
		weapons.add(Material.IRON_SWORD);
		weapons.add(Material.GOLD_AXE);
		weapons.add(Material.GOLD_SWORD);
		weapons.add(Material.DIAMOND_AXE);
		weapons.add(Material.DIAMOND_SWORD);
		weapons.add(Material.BOW);
	}
	
	/**
	 * Fills the Default Config.
	 */
	private void fillDefaultConfig() {
		config.addDefault("chain", toStringList(chain));
		config.addDefault("diamond", toStringList(diamond));
		config.addDefault("gold", toStringList(gold));
		config.addDefault("iron", toStringList(iron));
		config.addDefault("leather", toStringList(leather));
		config.addDefault("weapons", toStringList(weapons));
		
		config.options().copyDefaults(true);
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
		
		parseMaterials(chain, config.getStringList("chain"));
		parseMaterials(diamond, config.getStringList("diamond"));
		parseMaterials(gold, config.getStringList("gold"));
		parseMaterials(iron, config.getStringList("iron"));
		parseMaterials(leather, config.getStringList("leather"));
		parseMaterials(weapons, config.getStringList("weapons"));
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

	

	public Set<Material> getWeapons() {
		return weapons;
	}


	public Set<Material> getLeather() {
		return leather;
	}


	public Set<Material> getIron() {
		return iron;
	}


	public Set<Material> getChain() {
		return chain;
	}


	public Set<Material> getGold() {
		return gold;
	}


	public Set<Material> getDiamond() {
		return diamond;
	}
	
}
