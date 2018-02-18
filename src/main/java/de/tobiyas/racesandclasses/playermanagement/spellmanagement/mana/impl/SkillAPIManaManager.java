package de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana.impl;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;

import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;

public class SkillAPIManaManager extends AbstractManaManager {

	

	
	public SkillAPIManaManager(RaCPlayer racPlayer) {
		super(racPlayer);
	}

	
	
	private PlayerData getData(){
		return SkillAPI.getPlayerData(racPlayer.getPlayer());
	}
	
	

	@Override
	public double fillMana(double value) {
		getData().giveMana(value);
		return getCurrentMana();
	}

	@Override
	public double drownMana(double value) {
		getData().useMana(value);
		return getCurrentMana();
	}

	@Override
	public double getMaxMana() {
		return getData().getMaxMana();
	}

	@Override
	public double getCurrentMana() {
		return getData().getMana();
	}

	
	@Override
	public void addMaxManaBonus(String key, double value) {
		Double old = maxManaBonusses.put(key, value);
		if(old != null) value = value - old;
		
		getData().addMaxMana(value);
	}

	
	@Override
	public void removeMaxManaBonus(String key) {
		Double toRemove = maxManaBonusses.remove(key);
		if(toRemove != null) getData().addMaxMana(-toRemove);
	}

	
	@Override
	protected void applyMaxManaBonus(double bonus) {}
	
	
	@Override
	public double getManaBoostByName(String boostID) {
		return 0;
	}

}
