package de.tobiyas.racesandclasses.playermanagement.leveling.manager;

import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.playermanagement.PlayerSavingContainer;
import de.tobiyas.racesandclasses.playermanagement.leveling.PlayerLevelManager;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;

public abstract class AbstractPlayerLevelingSystem implements PlayerLevelManager {

	/**
	 * the player to use.
	 */
	protected final RaCPlayer player;
	
	/**
	 * The Plugin to use.
	 */
	protected final RacesAndClasses plugin;
	
	
	public AbstractPlayerLevelingSystem(RaCPlayer player) {
		this.player = player;
		this.plugin = RacesAndClasses.getPlugin();
	}
	
	
	@Override
	public RaCPlayer getPlayer() {
		return player;
	}


	@Override
	public void tick() {
		redrawMCLevelBar();
	}
	
	
	/**
	 * Redras the Level bar if wanted.
	 */
	private void redrawMCLevelBar(){
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_gui_level_useMCLevelBar()) return;
		if(!player.isOnline()) return;
		
		Player realPlayer = player.getPlayer();
		float percent = (float)getCurrentExpOfLevel() / (float)getMaxEXPToNextLevel();
		
		realPlayer.setExp(percent);
		realPlayer.setLevel(getCurrentLevel());
	}
	
	

	@Override
	public boolean addExp(int exp) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeExp(int exp) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void save() {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveTo(PlayerSavingContainer container) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reloadFromPlayerSavingContaienr(PlayerSavingContainer container) {
		// TODO Auto-generated method stub

	}

	@Override
	public void checkLevelChanged() {
		// TODO Auto-generated method stub

	}

	@Override
	public void reloadFromYaml() {
		// TODO Auto-generated method stub

	}

	@Override
	public void forceDisplay() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean canRemove(int toRemove) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addLevel(int value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeLevel(int value) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getMaxEXPToNextLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

}
