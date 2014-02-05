package de.tobiyas.racesandclasses.traitcontainer.traits.magic;

import static de.tobiyas.racesandclasses.translation.languages.Keys.magic_spell_activated;
import static de.tobiyas.racesandclasses.translation.languages.Keys.magic_spell_deactivated;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.ContinousCostMagicTrait;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

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
	public final boolean activate(final Player player) {
		if(isActivated(player)) return false;
		if(!activateIntern(player)) return false;
		
		int tickDuration = everyXSeconds <= 0 ? durationInSeconds : everyXSeconds;
		
		int bukkitID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				if(!tick(player, true)){
					deactivate(player);
					return;
				}
			}
		}, tickDuration * 20, tickDuration * 20);			
		
		
		LanguageAPI.sendTranslatedMessage(player, magic_spell_activated, "%TRAIT_NAME%", this.getDisplayName());
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
	protected abstract boolean activateIntern(Player player);
	
	@Override
	public final boolean deactivate(Player player) {
		if(!isActivated(player)) return false;
		if(!deactivateIntern(player)) return false;
		
		int schedulerID = activePlayersSchedulerMap.get(player.getName());
		Bukkit.getScheduler().cancelTask(schedulerID);
		
		activePlayersSchedulerMap.remove(player.getName());
		LanguageAPI.sendTranslatedMessage(player, magic_spell_deactivated, "%TRAIT_NAME%", this.getDisplayName());
		return true;
	}

	
	/**
	 * Deactivates the Spell active.
	 * 
	 * @param player to deactivate to
	 * 
	 * @return true if deactivated.
	 */
	protected abstract boolean deactivateIntern(Player player);
	
	
	@Override
	public final boolean isActivated(Player player) {
		return activePlayersSchedulerMap.containsKey(player.getName());
	}

	@Override
	protected final boolean magicSpellTriggered(Player player) {
		if(everyXSeconds < 1){
			return activate(player);
		}
		
		if(isActivated(player)){
			return deactivate(player);
		}else{
			return activate(player);
		}
	}
	
	/**
	 * Ticks the Player.
	 * <br>CHECKS costs and returns false if ticking should stop!
	 * 
	 * @param player that ticked.
	 * 
	 * @return if worked or not.
	 */
	protected final boolean tick(Player player, boolean checkRemoveCost){
		if(everyXSeconds <= 0) return false;
		
		String playerName = player.getName();
		if(checkRemoveCost && !plugin.getPlayerManager().getSpellManagerOfPlayer(playerName).canCastSpell(this)){
			//player does not have enough cost type to use this spell.
			return false;
		}
		
		boolean worked = tickInternal(player);
		if(checkRemoveCost && worked){
			plugin.getPlayerManager().getSpellManagerOfPlayer(player.getName()).removeCost(this);
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
	protected abstract boolean tickInternal(Player player);

	
	@TraitConfigurationNeeded(fields = {
			@TraitConfigurationField(fieldName = "every", classToExpect = Integer.class, optional = true),
			@TraitConfigurationField(fieldName = "duration", classToExpect = Integer.class, optional = true)
	})
	@Override
	public void setConfiguration(Map<String, Object> configMap)
			throws TraitConfigurationFailedException {
		
		super.setConfiguration(configMap);
		
		if(configMap.containsKey("every")){
			everyXSeconds = (Integer) configMap.get("every");
		}
		
		if(configMap.containsKey("duration")){
			durationInSeconds = (Integer) configMap.get("duration");
		}
		
		if(everyXSeconds <= 0 && durationInSeconds <= 0){
			throw new TraitConfigurationFailedException("Trait: " + getDisplayName() + " needs either 'every' or 'duration'. "
					+ "None of them with a positive value was found. Please fix this.");
		}
	}

	
}
