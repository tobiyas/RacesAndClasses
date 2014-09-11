package de.tobiyas.racesandclasses.playermanagement.health;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.resolvers.WorldResolver;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier.BukkitPlayer;

public class HealthManager {

	/**
	 * The player to use.
	 */
	private final RaCPlayer player;
	
	/**
	 * The Display to output the HP
	 */
	private HealthDisplayRunner display;
	
	
	/**
	 * The Life Modification map.
	 */
	protected final Map<String,Double> healthMap = new HashMap<String,Double>();
	
	
	
	public HealthManager(RaCPlayer player) {
		this.player = player;
		this.display = new HealthDisplayRunner(player);
	}
	
	
	/**
	 * Adds a health Bonus 
	 * 
	 * @param key the key to save to
	 * @param value to save.
	 */
	public void addMaxHealthBonus(String key, double value){
		this.healthMap.put(key, value);
		checkMaxHealth();
	}
	
	
	/**
	 * removes a health Bonus 
	 * 
	 * @param key to remove
	 */
	public void removeMaxHealthBonus(String key){
		this.healthMap.remove(key);
		checkMaxHealth();
	}
	
	
	/**
	 * The max Health of the Player
	 * 
	 * @return max health
	 */
	public double getMaxHealth(){
		double health = 20;
		
		for(Entry<String, Double> entry : healthMap.entrySet()) {
			health += entry.getValue();
		}
		
		return health;
	}
	
	
	/**
	 * Rescans Race + class + maxhealth.
	 */
	public void rescanPlayer(){
		RaceContainer race = player.getRace();
		if(race != null) addMaxHealthBonus("race", race.getMaxHealthMod());

		ClassContainer clazz = player.getclass();
		if(clazz != null) addMaxHealthBonus("class", clazz.getMaxHealthMod());
	}
	
	
	/**
	 * Checks if the MaxHealth is correct. If not, sets it.
	 */
	public void checkMaxHealth(){
		if(!player.isOnline()) return;
		double maxHealth = BukkitPlayer.safeGetMaxHealth(player.getPlayer());
		double realMaxHealth = getMaxHealth();
		
		boolean isOnDisabledWorld = WorldResolver.isOnDisabledWorld(player.getPlayer());
		boolean keepMaxHPOnDisabledWorld = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_keep_max_hp_on_disabled_worlds();
		
		if(Math.abs(maxHealth - realMaxHealth) > 0.3) {
			if(isOnDisabledWorld && !keepMaxHPOnDisabledWorld) return;
			 BukkitPlayer.safeSetMaxHealth(realMaxHealth, player.getPlayer());
		}
	}
	
	
	/**
	 * Forces to produce an HP message.
	 */
	public void forceHPOut(){
		if(player == null || !player.isOnline()) return;
		
		checkMaxHealth();
		display.forceHPOut();
	}


	/**
	 * Returns the current Health of the Player
	 * @return
	 */
	public double getCurrentHealth() {
		return BukkitPlayer.safeGetHealth(player.getPlayer());
	}


	/**
	 * The Damage to deal.
	 * 
	 * @param damage to deal.
	 */
	public void damage(double damage) {
		BukkitPlayer.safeDamage(damage, player.getPlayer());
	}
	
	/**
	 * Heals the player by an amount.
	 * 
	 * @param health value to heal.
	 */
	public void heal(double health) {
		BukkitPlayer.safeHeal(health, player.getPlayer());
	}

}
