package de.tobiyas.racesandclasses.playermanagement.leveling.manager;

import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.playermanagement.leveling.PlayerLevelManager;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.saving.PlayerSavingData;

public abstract class AbstractPlayerLevelingSystem implements PlayerLevelManager {

	/**
	 * the player to use.
	 */
	protected final RaCPlayer player;
	
	/**
	 * The Plugin to use.
	 */
	protected final RacesAndClasses plugin;
	
	/**
	 * The Data for saving.
	 */
	protected final PlayerSavingData data;
	
	
	public AbstractPlayerLevelingSystem(RaCPlayer player, PlayerSavingData data) {
		this.player = player;
		this.data = data;
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

}
