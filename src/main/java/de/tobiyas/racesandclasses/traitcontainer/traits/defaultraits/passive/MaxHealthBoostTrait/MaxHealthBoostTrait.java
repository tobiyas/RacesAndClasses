package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.MaxHealthBoostTrait;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapperFactory;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.classevent.AfterClassChangedEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.classevent.AfterClassSelectedEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.raceevent.AfterRaceChangedEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.raceevent.AfterRaceSelectedEvent;
import de.tobiyas.racesandclasses.playermanagement.health.HealthManager;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.RemoveSuperConfigField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.TraitRestriction;
import de.tobiyas.racesandclasses.traitcontainer.traits.pattern.TickEverySecondsTrait;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class MaxHealthBoostTrait extends TickEverySecondsTrait {

	
	/**
	 * The amount of health to add.
	 */
	private double healthAmount;
	
	/**
	 * The ID to use for the Boost.
	 */
	private final String boostID = UUID.randomUUID().toString();
	
	
	@TraitInfos(category="passive", traitName="MaxHealthBoostTrait", visible=true)
	@Override
	public void importTrait() {}
	

	@TraitEventsUsed
	@Override
	public void generalInit() {
		super.generalInit();
		
		plugin.registerEvents( this );
	}

	@Override
	public String getName() {
		return "MaxHealthBoostTrait";
	}
	

	@Override
	public TraitResults trigger( EventWrapper wrapper ) {
		return TraitResults.True();
	}
	
	@TraitConfigurationNeeded( fields = {
			@TraitConfigurationField(fieldName = "amount", classToExpect = Double.class)
		}, removedFields = { 
			@RemoveSuperConfigField(name = "seconds") 
		} )
	@Override
	public void setConfiguration( TraitConfiguration config ) throws TraitConfigurationFailedException {
		if( !config.containsKey( "seconds" ) ) config.put( "seconds", 3 );
		super.setConfiguration(config);
		
		this.healthAmount = config.getAsDouble("amount", 0);
	}
	

	@Override
	public boolean isBetterThan(Trait trait) {
		if( trait instanceof MaxHealthBoostTrait ) return healthAmount > ( ( MaxHealthBoostTrait ) trait ).healthAmount;
		return false;
	}


	@Override
	protected String getPrettyConfigIntern() {
		return "Add " + healthAmount + " Health";
	}
	
	
	@EventHandler
	public void playerLogout(PlayerQuitEvent event){
		RaCPlayer player = RaCPlayerManager.get().getPlayer(event.getPlayer());
		checkHealthBoost( player );
	}
	
	@EventHandler
	public void playerChangeHolder( AfterClassSelectedEvent event ){
		RaCPlayer player = RaCPlayerManager.get().getPlayer(event.getPlayer());
		checkHealthBoost(player);
	}

	@EventHandler
	public void playerChangeHolder( AfterRaceSelectedEvent event ){
		RaCPlayer player = RaCPlayerManager.get().getPlayer(event.getPlayer());
		checkHealthBoost(player);
	}
	
	
	@EventHandler
	public void playerChangeHolder( AfterClassChangedEvent event ){
		RaCPlayer player = RaCPlayerManager.get().getPlayer(event.getPlayer());
		checkHealthBoost(player);
	}

	@EventHandler
	public void playerChangeHolder( AfterRaceChangedEvent event ){
		RaCPlayer player = RaCPlayerManager.get().getPlayer(event.getPlayer());
		checkHealthBoost(player);
	}
	
	
	@Override
	public void skillLevelChanged(RaCPlayer player) {
		super.skillLevelChanged(player);
		
		checkHealthBoost( player );
	}
	
	/**
	 * Checks the Health boost here.
	 * @param player to check for.
	 */
	private void checkHealthBoost( RaCPlayer player ){
		boolean isInTrait = player.getTraits().contains( this );
		if( !isInTrait ) {
			removeBoost( player );
			return;
		}
		
		if( restrictionsMet( player ) ) addOrUpdateBoost( player );
		else removeBoost( player );
	}
	
	
	private boolean restrictionsMet( RaCPlayer player ) {
		return checkRestrictions( EventWrapperFactory.buildOnlyWithplayer( player ) ) == TraitRestriction.None;
	}
	

	private void removeBoost( RaCPlayer player ){
		HealthManager hm = player.getHealthManager();
		hm.removeMaxHealthBonus( boostID );
	}
	
	/**
	 * Updates the Max-Health boost.
	 * @param player to add for.
	 */
	private void addOrUpdateBoost( RaCPlayer player ){
		double shouldBe = getModValue( player, "amount", healthAmount );
		HealthManager hm = player.getHealthManager();
		if( Math.abs( hm.getHealthBoostByName( boostID ) - shouldBe ) > 0.01 ){
			hm.addMaxHealthBonus( boostID, shouldBe);
		}
	}


	@Override
	protected boolean tickDoneForPlayer(RaCPlayer player) {
		addOrUpdateBoost( player );
		return true;
	}
	
	@Override
	protected void restrictionsFailed(RaCPlayer player) {
		super.restrictionsFailed(player);
		
		removeBoost(player);
	}
	


	@Override
	protected String getPrettyConfigurationPre() {
		return "Add " + healthAmount + " Max-Health";
	}

}
