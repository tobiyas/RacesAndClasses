package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.activate.KickTrait;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.APIs.SilenceAndKickAPI;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.AbstractBasicTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.TraitRestriction;
import de.tobiyas.racesandclasses.translation.languages.Keys;
import de.tobiyas.racesandclasses.util.MCPrettyName;
import de.tobiyas.racesandclasses.util.entitysearch.SearchEntity;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class KickTrait extends AbstractBasicTrait {

	/**
	 * The time to silence after kick.
	 */
	private double duration = 0;
	
	/**
	 * The range to use.
	 */
	private int range = 0;
	
	
	@TraitInfos(category="activate", traitName="KickTrait", visible=true)
	@Override
	public void importTrait() {
	}
	
	
	@TraitEventsUsed()
	@Override
	public void generalInit() {}

	
	@Override
	public String getName() {
		return "KickTrait";
	}

	@Override
	public TraitResults trigger(EventWrapper wrapper) {
		return TraitResults.False();
	}
	
	
	@TraitConfigurationNeeded(fields={
			@TraitConfigurationField(fieldName="duration", classToExpect=Double.class, optional=true),
			@TraitConfigurationField(fieldName="range", classToExpect=Integer.class, optional=true)
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		this.duration = configMap.getAsDouble("duration", 0);
		this.range = configMap.getAsInt("range", 4);
	}
	

	@Override
	public boolean isBetterThan(Trait trait) {
		if(trait instanceof KickTrait) return duration > ((KickTrait) trait).duration;
		return false;
	}
	

	@Override
	public boolean canBeTriggered(EventWrapper wrapper) {
		return false;
	}
	

	@Override
	protected String getPrettyConfigIntern() {
		return "kicks and silences for " + (int)duration + " seconds";
	}

	@Override
	protected TraitRestriction checkForFurtherRestrictions(EventWrapper wrapper) {
		int range = modifyToPlayer(wrapper.getPlayer(), this.range, "range");
		LivingEntity target = SearchEntity.inLineOfSight(range, wrapper.getPlayer().getPlayer());
		if(target == null) return TraitRestriction.NoTarget;
		
		return null;
	}
	
	@Override
	protected TraitResults bindCastIntern(RaCPlayer player) {
		int range = modifyToPlayer(player, this.range, "range");
		LivingEntity target = SearchEntity.inLineOfSight(range, player.getPlayer());
		if(target == null) return TraitResults.False();
		
		double silenceTime = modifyToPlayer(player, this.duration, "duration");
		boolean kickedSomething = SilenceAndKickAPI.kickChanneling(target.getUniqueId(), (long)(silenceTime*1000));
		if(kickedSomething) player.sendTranslatedMessage(Keys.trait_kick_sucess, "name", getNameOfEntity(target));
		else player.sendTranslatedMessage(Keys.trait_kick_failed, "name", getNameOfEntity(target));
		
		return TraitResults.True();
	}
	
	
	/**
	 * Returns the name of the Entity.
	 * @param entity to use.
	 * @return the name.
	 */
	private String getNameOfEntity(LivingEntity entity){
		switch(entity.getType()){
			case PLAYER: return ((Player)entity).getDisplayName();
			default: return entity.getCustomName() == null ? MCPrettyName.getPrettyName(entity.getType()) : entity.getCustomName();
		}
	}
	
	
	@Override
	public boolean isBindable() {
		return true;
	}
	
}
