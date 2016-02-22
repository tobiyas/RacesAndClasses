package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.activate.MeleeDotTrait;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;

import de.tobiyas.racesandclasses.APIs.DotAPI;
import de.tobiyas.racesandclasses.entitystatusmanager.dot.DamageType;
import de.tobiyas.racesandclasses.entitystatusmanager.dot.DotBuilder;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.AbstractBasicTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.util.entitysearch.SearchEntity;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class MeleeDotTrait extends AbstractBasicTrait {

	
	/**
	 * The Dot to apply.
	 */
	private DotBuilder dotBuilderToApply = null;
	
	
	@TraitEventsUsed(registerdClasses = {})
	@Override
	public void generalInit(){
		plugin.registerEvents(this);
	}

	@Override
	public String getName() {
		return "MeleeDotTrait";
	}
	
	
	@Override
	protected String getPrettyConfigIntern() {
		return "fire a Grappling hook.";
	}

	@TraitConfigurationNeeded(fields = {
				@TraitConfigurationField(fieldName = "damage", classToExpect = Double.class, optional = true),
				@TraitConfigurationField(fieldName = "duration", classToExpect = Integer.class, optional = true),
				@TraitConfigurationField(fieldName = "damageEvery", classToExpect = Integer.class, optional = true),
				@TraitConfigurationField(fieldName = "damageType", classToExpect = String.class, optional = true)
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		double damage = configMap.getAsDouble("damage", 2);
		int duration = configMap.getAsInt("duration", 4)*20;
		int damageEvery = configMap.getAsInt("damageEvery", 20);
		String type = configMap.getAsString("damageType", DamageType.BLEEDING.name()).toUpperCase();
		
		this.dotBuilderToApply = new DotBuilder(getName())
			.setTotalDamage(damage)
			.setTotalTimeInSeconds(duration)
			.setDamageEveryTicks(damageEvery)
			.setDamageType(DamageType.parse(type));
		
		if(!this.dotBuilderToApply.valid()) throw new TraitConfigurationFailedException(getName() + " Dot-Properties are not valid!", holders.iterator().next());
	}
	

	@Override
	public TraitResults trigger(EventWrapper eventWrapper) {   
		return TraitResults.False();
	}
	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "Put a DoT on your enemy.");
		return helpList;
	}
	
	@Override
	public boolean isBetterThan(Trait trait) {
		return false;
	}
	
	@TraitInfos(category="activate", traitName="MeleeDotTrait", visible=true)
	@Override
	public void importTrait() {
	}

	@Override
	public boolean triggerButHasUplink(EventWrapper wrapper) {
		//Not needed
		return true;
	}
	
	@Override
	public boolean notifyTriggeredUplinkTime(EventWrapper wrapper) {
		return true;
	}
	
	@Override
	public boolean isBindable() {
		return true;
	}
	
	
	@Override
	protected TraitResults bindCastIntern(RaCPlayer player) {
		LivingEntity target = SearchEntity.inLineOfSight(5, player.getPlayer());
		if(target == null) return TraitResults.False();
		
		//Add the Dot:
		dotBuilderToApply.setDamager(player);
		DotAPI.addDot(target, dotBuilderToApply);
		return TraitResults.True();
	}

	@Override
	public boolean canBeTriggered(EventWrapper wrapper) {
		return false;
	}
	
}
