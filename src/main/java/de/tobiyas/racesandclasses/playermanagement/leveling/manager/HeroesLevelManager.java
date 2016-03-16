package de.tobiyas.racesandclasses.playermanagement.leveling.manager;

import org.bukkit.Bukkit;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;

import de.tobiyas.racesandclasses.playermanagement.leveling.PlayerLevelManager;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;

public class HeroesLevelManager implements PlayerLevelManager {

	/**
	 * The Player to use.
	 */
	private final RaCPlayer player;
	
	public HeroesLevelManager(RaCPlayer player) {
		this.player = player;
	}
	
	
	@Override
	public int getCurrentLevel() {
		if(!isHerosActive()) return 1;
		
		return getHero().getLevel();
	}

	@Override
	public int getCurrentExpOfLevel() {
		if(!isHerosActive()) return 0;
		
		return 0;
	}

	@Override
	public RaCPlayer getPlayer() {
		return player;
	}

	@Override
	public void setCurrentLevel(int level) {
		if(!isHerosActive()) return;
		
		//Heroes seems to not support this.
	}

	@Override
	public void setCurrentExpOfLevel(int currentExpOfLevel) {
		if(!isHerosActive()) return;
		
		//Heroes seems to not support this.
	}
	
	@Override
	public void tick() {
		//not needed
	}

	@Override
	public boolean addExp(int exp) {
		if(!isHerosActive()) return false;
		
		getHero().addExp(exp, getHero().getHeroClass(), player.getLocation());
		return true;
	}

	@Override
	public boolean removeExp(int exp) {
		if(!isHerosActive()) return false;

		//Heroes seems to not support this.
		return false;
	}


	@Override
	public void checkLevelChanged() {
		//nothing to do.
	}


	@Override
	public void forceDisplay() {
		//nothing to do.
	}

	@Override
	public boolean canRemove(int toRemove) {
		if(!isHerosActive()) return false;
		
		
		return false;
	}
	
	
	
	private boolean isHerosActive(){
		return Bukkit.getPluginManager().getPlugin("Heroes") != null;
	}
	
	
	private Heroes getHeroes(){
		return Heroes.getInstance();
	}
	
	private Hero getHero(){
		return getHeroes().getCharacterManager().getHero(player.getPlayer());
	}


	@Override
	public void addLevel(int value) {
	}


	@Override
	public void removeLevel(int value) {
	}


	@Override
	public int getMaxEXPToNextLevel() {
		return 1;
	}

}
