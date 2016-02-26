/*******************************************************************************
 * Copyright 2014 Tobias Welther
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.tobiyas.racesandclasses.traitcontainer.traits.magic;

import static de.tobiyas.racesandclasses.translation.languages.Keys.magic_spell_activated;
import static de.tobiyas.racesandclasses.translation.languages.Keys.magic_spell_deactivated;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;

import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.PlayerAction;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.ContinousCostMagicTrait;
import de.tobiyas.racesandclasses.translation.languages.Keys;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;
import de.tobiyas.util.vollotile.ParticleEffects;
import de.tobiyas.util.vollotile.helper.ParticleHelper;

public abstract class AbstractContinousCostMagicSpellTrait extends
		AbstractMagicSpellTrait implements ContinousCostMagicTrait {

	
	/**
	 * Checks every x seconds for the Trait.
	 */
	protected int everyXSeconds = -1;
	
	/**
	 * The Duration for a one-time-use
	 */
	protected int durationInSeconds = -1;

	/**
	 * On which players the Trait is currently active.
	 */
	protected Map<String,Integer> activePlayersSchedulerMap = new HashMap<String,Integer>();
	

	/**
	 * This can also be a negative value!
	 * If negative, it means, this is not ment for continous.
	 * 
	 */
	@Override
	public final int everyXSeconds() {
		return everyXSeconds;
	}

	@Override
	public final boolean activate(final RaCPlayer player) {
		if(player == null) return false;
		if(isActivated(player)) return false;
		if(!activateIntern(player)) return false;
		
		int tickDuration = everyXSeconds <= 0 ? durationInSeconds : everyXSeconds;
		tickDuration = (int) (tickDuration / getModValue(player,"duration"));
		
		tick(player, true, true);
		
		int bukkitID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				if(!tick(player, true, false)){
					deactivate(player);
					return;
				}
			}
		}, tickDuration * 20, tickDuration * 20);
		
		
		LanguageAPI.sendTranslatedMessage(player, magic_spell_activated, "trait_name", this.getDisplayName());
		activePlayersSchedulerMap.put(player.getName(), bukkitID);
		return true;
	}
	
	/**
	 * Activates intern the Magic spell.
	 * 
	 * @param player to activate to
	 * 
	 * @return true if worked, false otherwise.
	 */
	protected abstract boolean activateIntern(RaCPlayer player);
	
	@Override
	public final boolean deactivate(RaCPlayer player) {
		if(player == null) return false;
		if(!isActivated(player)) return false;
		if(!deactivateIntern(player)) return false;
		
		int schedulerID = activePlayersSchedulerMap.get(player.getName());
		Bukkit.getScheduler().cancelTask(schedulerID);
		
		activePlayersSchedulerMap.remove(player.getName());
		LanguageAPI.sendTranslatedMessage(player, magic_spell_deactivated, "trait_name", this.getDisplayName());
		return true;
	}

	
	/**
	 * Deactivates the Spell active.
	 * 
	 * @param player to deactivate to
	 * 
	 * @return true if deactivated.
	 */
	protected abstract boolean deactivateIntern(RaCPlayer player);
	
	
	@Override
	public final boolean isActivated(RaCPlayer player) {
		if(player == null) return false;
		return activePlayersSchedulerMap.containsKey(player.getName());
	}

	@Override
	protected final void magicSpellTriggered(RaCPlayer player, TraitResults result) {
		if(everyXSeconds < 1){
			result.setRemoveCostsAfterTrigger(activate(player));
			return;
		}
		
		if(isActivated(player)){
			deactivate(player);
			result.setRemoveCostsAfterTrigger(false).setTriggered(true).setSetCooldownOnPositiveTrigger(true);
			return;
		}else{
			activate(player);
			result.setRemoveCostsAfterTrigger(true).setTriggered(true).setSetCooldownOnPositiveTrigger(false);
			return;
		}
	}
	
	
	
	
	@Override
	public boolean triggerButHasUplink(EventWrapper wrapper) {
		if(wrapper.getPlayerAction() == PlayerAction.CAST_SPELL && isActivated(wrapper.getPlayer())){
			deactivate(wrapper.getPlayer());
			return true;
		}
		
		return super.triggerButHasUplink(wrapper);
	}

	@Override
	public void triggerButDoesNotHaveEnoghCostType(EventWrapper wrapper) {
		if(wrapper.getPlayerAction() == PlayerAction.CAST_SPELL && isActivated(wrapper.getPlayer())){
			deactivate(wrapper.getPlayer());
			return;
		}
		
		super.triggerButDoesNotHaveEnoghCostType(wrapper);
	}

	/**
	 * Ticks the Player.
	 * <br>CHECKS costs and returns false if ticking should stop!
	 * 
	 * @param player that ticked.
	 * 
	 * @return if worked or not.
	 */
	protected final boolean tick(RaCPlayer player, boolean checkRemoveCost, boolean bypassEverySecondCheck){
		if(!bypassEverySecondCheck && everyXSeconds <= 0){
			setCooldownIfNeeded(player);
			return false;
		}
		
		if(checkRemoveCost && !player.getSpellManager().canCastSpell(this)){
			//player does not have enough cost type to use this spell.
			return false;
		}
		
		boolean worked = tickInternal(player);
		if(checkRemoveCost && worked){
			player.getSpellManager().removeCost(this);
		}
		
		return worked;
	}
	
	
	/**
	 * Ticks the player internally.
	 * 
	 * @param player the player to tick.
	 * 
	 * @return true if tick works.
	 */
	protected abstract boolean tickInternal(RaCPlayer player);

	
	@TraitConfigurationNeeded(fields = {
			@TraitConfigurationField(fieldName = "every", classToExpect = Integer.class, optional = true),
			@TraitConfigurationField(fieldName = "duration", classToExpect = Integer.class, optional = true)
	})
	@Override
	public void setConfiguration(TraitConfiguration configMap)
			throws TraitConfigurationFailedException {
		
		super.setConfiguration(configMap);
		
		if(configMap.containsKey("every")){
			everyXSeconds = configMap.getAsInt("every");
		}
		
		if(configMap.containsKey("duration")){
			durationInSeconds = configMap.getAsInt("duration");
		}
		
		if(everyXSeconds <= 0 && durationInSeconds <= 0){
			throw new TraitConfigurationFailedException("Trait: " + getDisplayName() + " needs either 'every' or 'duration'. "
					+ "None of them with a positive value was found. Please fix this.");
		}
	}

	
	@Override
	public void gotKicked(UUID player) {
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
		if(deactivate(racPlayer)){
			if(racPlayer.isOnline()){
				ParticleHelper.sendXParticleEffectToAllWithRandWidth(ParticleEffects.CRIT, racPlayer.getEyeLocation(), 0, 10);
				racPlayer.sendTranslatedMessage(Keys.trait_kicked, "name", getName());
			}
		}
		
		super.gotKicked(player);
	}
	
}
