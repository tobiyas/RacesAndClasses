package de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana.impl;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.resolvers.WorldResolver;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana.ManaManager;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.TraitWithCost;
import de.tobiyas.util.formating.StringFormatUtils;
import de.tobiyas.util.math.Math2;

public abstract class AbstractManaManager implements ManaManager {

	/**
	 * The Player to manage.
	 */
	protected final RaCPlayer racPlayer;
	
	/**
	 * The Plugin to call Stuff on
	 */
	protected RacesAndClasses plugin = RacesAndClasses.getPlugin();
	
	/**
	 * The map of max mana bonuses.
	 */
	protected final Map<String,Double> maxManaBonusses = new HashMap<String, Double>();
	
	
	public AbstractManaManager(RaCPlayer racPlayer) {
		this.racPlayer = racPlayer;
	}

	@Override
	public void rescanPlayer() {
		if(racPlayer == null || !racPlayer.isOnline() || WorldResolver.isOnDisabledWorld(racPlayer)){
			return;
		}
		
		int level = racPlayer.getLevelManager().getCurrentLevel();
		RaceContainer race = racPlayer.getRace();
		if(race != null && race.getManaBonus(level) > 0){
			addMaxManaBonus("race", race.getManaBonus(level));
		}
		
		ClassContainer clazz = racPlayer.getclass();
		if(clazz != null && clazz.getManaBonus(level) > 0){
			addMaxManaBonus("class", clazz.getManaBonus(level));
		}
	}
	


	@Override
	public boolean playerCastSpell(TraitWithCost spellToCast) {
		if(!hasEnoughMana(spellToCast)) return false;
		
		drownMana(spellToCast.getCost(racPlayer));
		return true;
	}

	@Override
	public boolean hasEnoughMana(TraitWithCost spell) {
		return hasEnoughMana(spell.getCost(racPlayer));
	}




	@Override
	public boolean hasEnoughMana(double manaNeeded) {
		return getCurrentMana() >= manaNeeded;
	}


	@Override
	public void addMaxManaBonus(String key, double value) {
		maxManaBonusses.put(key, value);
		
		double bonus = 0;
		for(Double val : maxManaBonusses.values()) bonus += val;
		
		applyMaxManaBonus(bonus);
	}
	

	@Override
	public void removeMaxManaBonus(String key) {
		maxManaBonusses.remove(key);
		
		double bonus = 0;
		for(Double value : maxManaBonusses.values()) bonus += value;
		
		applyMaxManaBonus(bonus);
	}
	
	
	
	@Override
	public void tick(){
		double maxMana = getMaxMana();
		if(maxMana <= 0) maxMana = 0.1;
		
		double percent = Math2.clamp(0, getCurrentMana() / getMaxMana(), 1);
		String line = StringFormatUtils.formatToPercent(percent, 10, '\u220E', ChatColor.BLUE, ChatColor.WHITE);
		line = ChatColor.DARK_BLUE.toString() + '\u2739' + " {" + line + ChatColor.DARK_BLUE + "}";
		
		racPlayer.getActionbarDisplay().setSegment("manabar", line);
		racPlayer.getActionbarDisplay().setSegment("mana", ""+getCurrentMana());
		racPlayer.getActionbarDisplay().setSegment("maxmana", ""+getMaxMana());
	}	
	
	
	
	/**
	 * Recalculate the Max-Mana bonus.
	 */
	protected abstract void applyMaxManaBonus(double bonus);
	

	@Override
	public Map<String, Double> getAllBonuses() {
		return new HashMap<String,Double>(maxManaBonusses);
	}

	@Override
	public RaCPlayer getPlayer() {
		return racPlayer;
	}

	@Override
	public boolean isManaFull() {
		return getCurrentMana() >= getMaxMana();
	}

}
