package de.tobiyas.racesandclasses.cooldown;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.APIs.CooldownApi;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;

public class PlayerCooldownDragonBarManager {
	
	/**
	 * The player to observe.
	 */
	private final RaCPlayer player;
	
	/**
	 * The bars, Skill -> Bar.
	 */
	private final Map<String, BossBar> map = new HashMap<>();

	
	public PlayerCooldownDragonBarManager(RaCPlayer player) {
		super();
		this.player = player;
	}
	
	
	
	/**
	 * This should be called every 10 ticks.
	 */
	public void tick(){
		Player player = this.player.getPlayer();
		
		//Do a cleanup if player is NOT online any more:
		if(player == null || !player.isOnline()){
			if(!map.isEmpty()){
				for(BossBar bar : map.values()){
					bar.removeAll();
					bar.setVisible(false);
				}
				
				map.clear();
			}
			
			return;
		}
		
		String playerName = player.getName();
		Map<String,Integer> cooldowns = CooldownApi.getAllCooldownsForPlayer(playerName);
		
		for(Map.Entry<String, Integer> entry : cooldowns.entrySet()){
			String trait = entry.getKey();
			double remaining = entry.getValue();
			double max = CooldownApi.getMaxCooldownForTrait(playerName, trait);
			BossBar bar = map.get( trait );
			
			//No bar, no cooldown -> nothing to do!
			if(bar == null && remaining <= 0) continue;

			//Setup bar, since we have a new Cooldown:
			if(bar == null && remaining > 0) {
				bar = Bukkit.createBossBar(trait, BarColor.RED, BarStyle.SEGMENTED_20);
				bar.addPlayer(player);
				continue;
			}
			
			//Remove bar:
			if(bar != null && remaining <= 0){
				bar.setVisible(false);
				continue;
			}
			
			//Update bar to new value:
			if(bar != null && remaining > 0){
				double value = remaining / max;
				value = Math.min(Math.max(0, value), 1);
				bar.setProgress(value);
				if(!bar.isVisible()) bar.setVisible(true);
				continue;
			}
		}
	}
	
	

}
