package de.tobiyas.racesandclasses.addins;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.addins.food.FoodManager;
import de.tobiyas.racesandclasses.addins.kits.RaCKitManager;
import de.tobiyas.racesandclasses.addins.manaflask.ManaPotionManager;
import de.tobiyas.racesandclasses.addins.placeholderapisupport.PlaceholderAPISupportManager;
import de.tobiyas.racesandclasses.addins.spawning.RaceSpawnManager;

public class AddinManager {

	
	/**
	 * The spawn-Manager to use.
	 */
	private final RaceSpawnManager raceSpawnManager;
	
	/**
	 * The foodListener to use.
	 */
	private final FoodManager foodManager;
	
	/**
	 * the Manager for Mana-Potions.
	 */
	private final ManaPotionManager manaPotionManager;
	
	/**
	 * the manager for kits.
	 */
	private final RaCKitManager kitManager;
	
	/**
	 * the manager for the placeholder api.
	 */
	private final PlaceholderAPISupportManager placeholderAPISupportManager;
	
	
	/**
	 * Creates the Addin Manager.
	 * Do not forget to call reload after creation.
	 * 
	 * @param plugin to use for creation.
	 */
	public AddinManager(RacesAndClasses plugin) {
		this.raceSpawnManager = new RaceSpawnManager(plugin);
		this.foodManager = new FoodManager(plugin);
		this.manaPotionManager = new ManaPotionManager(plugin);
		this.kitManager = new RaCKitManager(plugin);
		this.placeholderAPISupportManager = new PlaceholderAPISupportManager(plugin);
	}
	
	
	/**
	 * Reloads the Addin manager.
	 */
	public void reload(){
		raceSpawnManager.load();
		foodManager.reload();
		manaPotionManager.reload();
		kitManager.reload();
		placeholderAPISupportManager.reload();
	}
	
	
	/**
	 * Calls a shutdown on all addins.
	 */
	public void shutdown() {
		raceSpawnManager.save(false);
		//foodManager.shutdown();
		//manaPotionManager.shutdown();
		//kitManager.shutdown();
	}


	public RaceSpawnManager getRaceSpawnManager() {
		return raceSpawnManager;
	}


	public FoodManager getFoodManager() {
		return foodManager;
	}


	public ManaPotionManager getManaPotionManager() {
		return manaPotionManager;
	}


	public RaCKitManager getKitManager() {
		return kitManager;
	}
	
	public PlaceholderAPISupportManager getPlaceholderAPISupportManager() {
		return placeholderAPISupportManager;
	}
	
}
