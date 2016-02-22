package de.tobiyas.racesandclasses.infight;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.scheduler.BukkitTask;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.InFightAPI;
import de.tobiyas.racesandclasses.eventprocessing.events.entitydamage.EntityHealEvent;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier.EntityRegainHealth;
import de.tobiyas.racesandclasses.util.traitutil.TraitRegionChecker;
import de.tobiyas.racesandclasses.vollotile.ParticleContainer;
import de.tobiyas.racesandclasses.vollotile.ParticleEffects;
import de.tobiyas.racesandclasses.vollotile.Vollotile;
import de.tobiyas.util.formating.Pair;
import de.tobiyas.util.player.PlayerUtils;
import de.tobiyas.util.schedule.DebugBukkitRunnable;

public class OutOfFightHealer extends DebugBukkitRunnable {

	/**
	 * The Instance Task to use.
	 */
	private static BukkitTask instance;
	
	/**
	 * The Last healed ID.
	 */
	private final Map<UUID,Long> lastHealed = new HashMap<UUID, Long>();
	
	
	public OutOfFightHealer() {
		super("OutOfFightHealer");
	}
	
	
	@Override
	protected void runIntern() {
		Pair<Double,Double> toHeal = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_magic_outOfFightRegeneration();
		if(toHeal == null) return;
		
		double healValue = toHeal.first;
		double interval = toHeal.second * 1000;
		
		if(healValue <= 0) return;
		if(interval <= 1000) interval = 1000;
		
		//just as cache.
		long now = System.currentTimeMillis();
		
		for(Player player : PlayerUtils.getOnlinePlayers()){
			//first check: needs heal?
			if(player.getHealth() >= (player.getMaxHealth() - 0.05)) continue;
			
			RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
			//second check: in fight
			if(InFightAPI.isInFight(racPlayer)) continue;
			
			//third check: disabled World.
			if(TraitRegionChecker.isInDisabledLocation(racPlayer.getLocation())) continue;
			
			//fourth check: last healed!
			UUID id = player.getUniqueId();
			long lastHealed = this.lastHealed.containsKey(id) ? this.lastHealed.get(id) : 0;
			
			if(now - lastHealed > interval){
				//we got to heal him!
				try{
					EntityRegainHealthEvent event = new EntityHealEvent(player, healValue, RegainReason.MAGIC_REGEN);
					Bukkit.getPluginManager().callEvent(event);
					
					double newValue = EntityRegainHealth.safeGetAmount(event);
					if(newValue > 0){
						racPlayer.heal(newValue);
						this.lastHealed.put(id, now);
						
						//send him some cool particles.
						Vollotile.get().sendOwnParticleEffectToAll(
									new ParticleContainer(
											ParticleEffects.SPELL, 
											20, 
											0), 
								player.getEyeLocation());
					}
				}catch(Throwable exp){
					exp.printStackTrace();
				}
			}
		}
	}

	
	
	/**
	 * Launches a new Instance, if non present.
	 */
	public static void launch(){
		if(instance == null) instance = new OutOfFightHealer().runTaskTimer(RacesAndClasses.getPlugin(), 20 * 2, 20 * 2);
	}


	/**
	 * Kills the Old instance if present.
	 */
	public static void kill() {
		if(instance != null) instance.cancel();
	}
	
}
