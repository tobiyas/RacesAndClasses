package de.tobiyas.racesandclasses.traitcontainer.traits.pattern;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.scheduler.BukkitTask;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.BuffAPI;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.traits.magic.AbstractMagicSpellTrait;
import de.tobiyas.racesandclasses.translation.languages.Keys;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;
import de.tobiyas.racesandclasses.vollotile.ParticleContainer;
import de.tobiyas.racesandclasses.vollotile.Vollotile;
import de.tobiyas.util.schedule.DebugBukkitRunnable;


public abstract class AbstractBuffTrait extends AbstractMagicSpellTrait {

	/**
	 * The Time the Buff is active before it's down.
	 */
	protected int timeActive = 5;
	
	/**
	 * The Map of the Tasks running.
	 */
	protected final Map<RaCPlayer,BukkitTask> taskMap = new HashMap<RaCPlayer, BukkitTask>();
	
	/**
	 * This will avaid the Buff beeing removed after activated.
	 */
	protected boolean stayActiveTillTimeout = false;

	/**
	 * The Particle Effect to show while active.
	 */
	protected ParticleContainer particlesWhileActive = null;
	
	/**
	 * The Particle Effect to show when the buff is consumed.
	 */
	protected ParticleContainer particlesWhenUsed = null;
	
	
	@TraitConfigurationNeeded(fields = {
			@TraitConfigurationField(fieldName = "timeActive", classToExpect = Integer.class, optional = true),
			@TraitConfigurationField(fieldName = "stayActiveTillTimeout", classToExpect = Boolean.class, optional = true),
			@TraitConfigurationField(fieldName = "particlesWhileActive", classToExpect = String.class, optional = true),
			@TraitConfigurationField(fieldName = "particlesWhenUsed", classToExpect = String.class, optional = true),
	})
	@Override
	public void setConfiguration(TraitConfiguration configMap)
			throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		if(configMap.containsKey("timeActive")){
			timeActive = configMap.getAsInt("timeActive");
		}
		
		if(configMap.containsKey("stayActiveTillTimeout")){
			stayActiveTillTimeout = configMap.getAsBool("stayActiveTillTimeout");
		}
		
		if(configMap.containsKey("particlesWhileActive")){
			particlesWhileActive = configMap.getAsParticleContainer("particlesWhileActive");
		}
		
		if(configMap.containsKey("particlesWhenUsed")){
			particlesWhenUsed = configMap.getAsParticleContainer("particlesWhenUsed");
		}
	}

	
	@Override
	public final void magicSpellTriggered(final RaCPlayer player, TraitResults result) {
		if(taskMap.containsKey(player)) {
			result.copyFrom(TraitResults.False());
			return;
		}
		
		player.sendTranslatedMessage(Keys.buff_activated, "buff", this.getDisplayName());
		buffActivated(player);
		
		BukkitTask task = new DebugBukkitRunnable("BuffTrait"+getDisplayName()){
			int i = 1;
			@Override
			protected void runIntern() {
				boolean stillActive = taskMap.containsKey(player);
				if(particlesWhileActive != null && stillActive) Vollotile.get().sendOwnParticleEffectToAll(particlesWhileActive, 
						player.getLocation());
				
				if(i >= timeActive){
					cancel();
					
					if(stillActive) {
						taskMap.remove(player);						
						buffTimeouted(player);
						BuffAPI.removeBuff(player.getUniqueId(), getDisplayName());
						
						player.sendTranslatedMessage(Keys.buff_timeout, "buff", AbstractBuffTrait.this.getDisplayName());
					}
				}
				
				i++;
			}
		}.runTaskTimer(RacesAndClasses.getPlugin(), 1, 20);
		
		taskMap.put(player, task);
		BuffAPI.addBuff(player.getUniqueId(), getDisplayName(), System.currentTimeMillis() + (timeActive * 1000));
		result.copyFrom(TraitResults.True());
	}
	
	
	
	/**
	 * The Buff got activated.
	 * 
	 * @param player to activate to.
	 */
	protected abstract void buffActivated(RaCPlayer player);
	
	
	/**
	 * Returns true if the Buff is active on a player.
	 * 
	 * @param player to check
	 * @return true if active, false if not.
	 */
	protected boolean isActive(RaCPlayer player){
		return taskMap.containsKey(player);
	}
	
	
	/**
	 * The Buff got used.
	 * Call this on use. It will cancel the Timeout.
	 * 
	 * @param player that used the buff.
	 */
	protected void buffUsed(RaCPlayer player){
		if(particlesWhenUsed != null) Vollotile.get().sendOwnParticleEffectToAll(particlesWhenUsed, 
				player.getLocation());
		
		if( stayActiveTillTimeout ) return;
		
		player.sendTranslatedMessage(Keys.buff_used, "buff", this.getDisplayName());
		BukkitTask task = taskMap.remove(player);
		if(task != null) task.cancel();
		
		BuffAPI.removeBuff(player.getUniqueId(), getDisplayName());
	}
	
	
	/**
	 * The Buff got a timeout.
	 * 
	 * @param player that timeouted..
	 */
	protected abstract void buffTimeouted(RaCPlayer player);
	
	
}
