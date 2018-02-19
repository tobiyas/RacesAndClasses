package de.tobiyas.racesandclasses.playermanagement.health;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.resolvers.WorldResolver;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier.BukkitPlayer;
import de.tobiyas.util.formating.StringFormatUtils;
import de.tobiyas.util.math.Math2;

public class HealthManager {

	/**
	 * The player to use.
	 */
	private final RaCPlayer player;
	
	
	/**
	 * The Life Modification map.
	 */
	protected final Map<String,Double> healthMap = new HashMap<String,Double>();
	
	
	
	public HealthManager(RaCPlayer player) {
		this.player = player;
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
	 * Gets the Healthboost by name.
	 * Retursn 0 if not registered.
	 * 
	 * @param name to search
	 * @return the health boost number.
	 */
	public double getHealthBoostByName( String name ){
		Double entry = healthMap.get( name );
		return entry == null ? 0 : entry;
	}
	
	
	/**
	 * Rescans Race + class + maxhealth.
	 */
	public void rescanPlayer(){
		int level = player.getLevelManager().getCurrentLevel();
		
		RaceContainer race = player.getRace();
		if(race != null) addMaxHealthBonus("race", race.getMaxHealthMod(level));

		AbstractTraitHolder clazz = player.getclass();
		if(clazz != null) addMaxHealthBonus("class", clazz.getMaxHealthMod(level));
	}
	
	
	/**
	 * Checks if the MaxHealth is correct. If not, sets it.
	 */
	public void checkMaxHealth(){		
		//if the owner does not want to have a health Mod, we don't use it.
		if(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_disableHealthMods()) return;
		
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
	 * Does a tick for the Manager.
	 */
	public void tick() {
		updateHPBar();
	}
	
	
	/**
	 * Update the HP bar if needed.
	 */
	private void updateHPBar() {
		double currentHP = getCurrentHealth();
		double maxHP = getMaxHealth();
		
		double percent = Math2.clamp(0d, (double)currentHP / (double)maxHP, 1d);
		String healthBarString = StringFormatUtils.formatToPercent(percent, 10, '\u220E', ChatColor.RED, ChatColor.BLACK);
		healthBarString = ChatColor.RED + "{" + healthBarString + ChatColor.RED + "}";
		
		player.getActionbarDisplay().setSegment( "health", ""+(int)currentHP );
		player.getActionbarDisplay().setSegment( "maxhealth", ""+(int)maxHP );
		player.getActionbarDisplay().setSegment( "healthbar", healthBarString );
	}
	
	
	
	/**
	 * Forces to produce an HP message.
	 */
	public void forceHPOut(){
		if(player == null || !player.isOnline()) return;
		
		checkMaxHealth();
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
	
	/**
	 * Gets the health-boosts.
	 * @return the boosts.
	 */
	public Map<String,Double> getHealthBoosts(){
		return new HashMap<>( this.healthMap );
	}

}
