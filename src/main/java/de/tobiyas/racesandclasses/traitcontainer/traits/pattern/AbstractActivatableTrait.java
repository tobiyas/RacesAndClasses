package de.tobiyas.racesandclasses.traitcontainer.traits.pattern;

import static de.tobiyas.racesandclasses.translation.languages.Keys.cooldown_is_ready_again;
import static de.tobiyas.racesandclasses.translation.languages.Keys.disabled_region;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.APIs.MessageScheduleApi;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.AbstractBasicTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.util.traitutil.TraitRegionChecker;

public abstract class AbstractActivatableTrait extends AbstractBasicTrait {

	
	@Override
	public boolean isBindable() {
		return true;
	}
	
	
	@Override
	protected final TraitResults bindCastIntern(RaCPlayer player) {
		if(TraitRegionChecker.isInDisabledLocation(player.getLocation())){
			player.sendTranslatedMessage(disabled_region);
			return TraitResults.False();
		}
		
		TraitResults result = trigger(player);
		evaluateTraitResult(player, result);
		return result;
	}
	
	
	
	/**
	 * Triggers the Activateable Trait.
	 * 
	 * @param player to trigger to
	 * 
	 * @return
	 */
	public abstract TraitResults trigger(RaCPlayer player);

	
	@Override
	public TraitResults trigger(EventWrapper wrapper) {
		if(TraitRegionChecker.isInDisabledLocation(wrapper.getPlayer().getLocation())){
			wrapper.getPlayer().sendTranslatedMessage(disabled_region);
			return TraitResults.False();
		}
		
		TraitResults result = trigger(wrapper.getPlayer());
		
		//not sure about EVAL here... :/
		evaluateIntern(wrapper.getPlayer(), result);
		return result;
	}
	
	
	/**
	 * This evaluates the Trait Result.
	 * 
	 * @param player to evaluate
	 * @param result to evaluate.
	 */
	protected void evaluateTraitResult(RaCPlayer player, TraitResults result){
		if(cooldownTime > 0
				&& result.isTriggered()
				&& result.isSetCooldownOnPositiveTrigger()){
			
			setCooldownIfNeeded(player);
		}
		
		evaluateIntern(player, result);
	}
	
	/**
	 * Sets the cooldown of a player
	 * 
	 * @param player to set
	 */
	protected void setCooldownIfNeeded(RaCPlayer player) {
		String playerName = player.getName();
		String cooldownName = "trait." + getDisplayName();
		
		int uplinkTraitTime = getMaxUplinkTime();
		if(uplinkTraitTime > 0){
			plugin.getCooldownManager().setCooldown(playerName, cooldownName, uplinkTraitTime);
			
			String cooldownDownMessage = LanguageAPI.translateIgnoreError(cooldown_is_ready_again)
					.replace("trait_name", getDisplayName())
					.build();
			
			MessageScheduleApi.scheduleMessageToPlayer(player.getName(), uplinkTraitTime, cooldownDownMessage);
		}
	}
	
	
	/**
	 * This is called AFTER the Abstract Trait Results are Applied.
	 * This is for instance the Cooldown.
	 * <br>This is even called when the Trait is NOT triggered!
	 * 
	 * @param player to check
	 * @param result to check.
	 */
	protected void evaluateIntern(RaCPlayer player, TraitResults result){}
}
