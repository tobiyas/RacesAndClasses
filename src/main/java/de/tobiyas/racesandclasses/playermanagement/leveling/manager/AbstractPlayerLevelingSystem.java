package de.tobiyas.racesandclasses.playermanagement.leveling.manager;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.playermanagement.leveling.PlayerLevelManager;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.saving.PlayerSavingData;
import de.tobiyas.util.formating.StringFormatUtils;
import de.tobiyas.util.math.Math2;

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
		updateActionbarData();
	}
	

	private void updateActionbarData() {
		int currentEXP = getCurrentExpOfLevel();
		int maxEXP = getMaxEXPToNextLevel();
		
		double percent = Math2.clamp(0d, (double)currentEXP / (double)maxEXP, 1d);
		String expString = StringFormatUtils.formatToPercent(percent, 10, '\u220E', ChatColor.YELLOW, ChatColor.BLACK);
		expString = ChatColor.YELLOW + "{" + expString + ChatColor.YELLOW + "}";
		
		player.getActionbarDisplay().setSegment("level", ""+getCurrentLevel());
		player.getActionbarDisplay().setSegment("maxexp", ""+getCurrentLevel());
		player.getActionbarDisplay().setSegment("exp", ""+currentEXP);
		player.getActionbarDisplay().setSegment("expbar", expString);
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
