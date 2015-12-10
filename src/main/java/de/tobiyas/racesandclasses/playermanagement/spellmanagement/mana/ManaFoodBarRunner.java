package de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.util.Vector;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.resolvers.WorldResolver;
import de.tobiyas.racesandclasses.eventprocessing.events.mana.ManaRegenerationEvent;
import de.tobiyas.racesandclasses.util.traitutil.TraitRegionChecker;
import de.tobiyas.util.schedule.DebugBukkitRunnable;
import de.tobiyas.util.vollotile.ParticleEffects;
import de.tobiyas.util.vollotile.VollotileCodeManager;

public class ManaFoodBarRunner extends DebugBukkitRunnable implements Listener {

	private final ManaManager manaManager;
	
	/**
	 * The plugin to use.
	 */
	private final RacesAndClasses plugin;
	
	/**
	 * The Ticks for the Sprint.
	 */
	private int sprintTick = 0;
	
	/**
	 * IF the Player is sprinting.
	 */
	private boolean isSprinting = false;
	
	
	
	public ManaFoodBarRunner(ManaManager manaManager) {
		super("ManaFoodbarRunner");
		this.manaManager = manaManager;
		this.plugin = RacesAndClasses.getPlugin();
	}
	
	
	/**
	 * Starts this runnable.
	 */
	public void start(){
		this.runTaskTimer(RacesAndClasses.getPlugin(), 10, 10);
		Bukkit.getPluginManager().registerEvents(this, RacesAndClasses.getPlugin());
	}

	@Override
	protected void runIntern() {
		//sprinting is always active!
		if(isSprinting){
			if(manaManager.getPlayer().getGameMode() != GameMode.SURVIVAL) isSprinting = false;
			else sprintTick++;
			
			int eventyTicksMana = plugin.getConfigManager().getGeneralConfig().getConfig_magic_sprintingManaDrainInterval() * 2;
			if(sprintTick >= eventyTicksMana){
				double howMany = plugin.getConfigManager().getGeneralConfig().getConfig_magic_sprintingManaCost();
				if(howMany > 0 || manaManager.getPlayer().getGameMode() == GameMode.SURVIVAL) {
					manaManager.drownMana(howMany);
					//also play some fancy particles.
					VollotileCodeManager.getVollotileCode().sendParticleEffect(ParticleEffects.ENCHANTMENT_TABLE, 
							manaManager.getPlayer().getLocation(), new Vector(0.2, 0, 0.2), 1, 4, manaManager.getPlayer().getPlayer());
				}
				
				sprintTick = 0;
			}
		}
		
		if(!isActive()) return;
		Player player = manaManager.getPlayer().getPlayer();
		player.setFoodLevel(getLevel());
		player.setSaturation(20);
		
		
		//just to be sure.
		if(plugin.getConfigManager().getGeneralConfig().getConfig_magic_sprintingManaCost() > 0
				&& player.getFoodLevel() <= 2 
				&& player.isSprinting()){
			
			isSprinting = false;
			player.setSprinting(false);
		}
	}
	
	
	private int getLevel(){
		double current = this.manaManager.getCurrentMana();
		double max = this.manaManager.getMaxMana();
		
		if(max == 0) return 1;
		
		double percent = current / max;
		int calcedLevel = (int)Math.floor(percent * 20d);
		if(current == 0) calcedLevel = 0;
		
		return calcedLevel;
	}
	
	
	/**
	 * If the Runner is active and can be used.
	 * 
	 * @return true if active and can be used.
	 */
	private boolean isActive(){
		return plugin.getConfigManager().getGeneralConfig().isConfig_useFoodManaBar()
				&& manaManager.getPlayer().isOnline();
	}
	
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void foodLevelChange(FoodLevelChangeEvent event){
		if(!isActive()) return;
		
		if(!TraitRegionChecker.isInDisabledLocation(event.getEntity().getLocation())) event.setCancelled(true);
	}
	
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void damageByFoodlevel(EntityDamageEvent event){
		if(event.getCause() != DamageCause.STARVATION) return;
		if(!event.getEntity().getUniqueId().equals(manaManager.getPlayer().getUniqueId())) return;
		if(!isActive()) return;
		
		if(!WorldResolver.isDisabledWorld(event.getEntity().getWorld())) {
			event.setCancelled(true);
		}
	}
	

	@EventHandler(priority = EventPriority.LOWEST)
	public void healByFoodlevel(EntityRegainHealthEvent event){
		if(event.getRegainReason() != RegainReason.SATIATED) return;
		if(!event.getEntity().getUniqueId().equals(manaManager.getPlayer().getUniqueId())) return;
		if(!isActive()) return;
		
		if(!WorldResolver.isDisabledWorld(event.getEntity().getWorld())) {
			event.setCancelled(true);
		}
	}
	
	
	@EventHandler
	public void playerStartsSprinting(PlayerToggleSprintEvent event){
		if(plugin.getConfigManager().getGeneralConfig().getConfig_magic_sprintingManaCost() <= 0) return;
		if(!event.getPlayer().getUniqueId().equals(manaManager.getPlayer().getUniqueId())) return;
		
		isSprinting = event.isSprinting();
		
		if(isSprinting && manaManager.getCurrentMana() <= 2){
			event.setCancelled(true);
			isSprinting = false;
		}
	}
	
	
	@EventHandler
	public void playerGainsMana(ManaRegenerationEvent event){
		if(!event.getPlayer().getUniqueId().equals(manaManager.getPlayer().getUniqueId())) return;
		if(plugin.getConfigManager().getGeneralConfig().getConfig_magic_manaRefillWhileSprinting()) return;
		
		if(event.getPlayer().isSprinting()){
			event.setCancelled(true);
			event.setAmount(0);
		}
	}
	
	
	@EventHandler
	public void playerLeave(PlayerQuitEvent event){
		if(plugin.getConfigManager().getGeneralConfig().getConfig_magic_sprintingManaCost() <= 0) return;
		if(!event.getPlayer().getUniqueId().equals(manaManager.getPlayer().getUniqueId())) return;
		
		isSprinting = false;
	}

	
	
}
