package de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import de.tobiyas.util.player.PlayerUtils;
import de.tobiyas.util.schedule.DebugBukkitRunnable;

public class ManaXPBarRunner extends DebugBukkitRunnable implements Listener {
	
	
	
	public ManaXPBarRunner() {
		super("ManaXPBarRunner");
	}
	
	
	/**
	 * Starts this runnable.
	 */
	public void start(){
		this.runTaskTimer(RacesAndClasses.getPlugin(), 20, 20);
	}
	

	@Override
	protected void runIntern() {
		boolean use = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_magic_manaShowPlace().toLowerCase().startsWith("x");
		if(!use) return;
		
		for(Player player : PlayerUtils.getOnlinePlayers()){
			RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
			ManaManager manaManager = racPlayer.getManaManager();
			
			double current = manaManager.getCurrentMana();
			double max = manaManager.getMaxMana();
			max = Math.max(1, max);
			
			double mana = current / max;
			player.setExp((float) mana);
		}
	}
	
}
