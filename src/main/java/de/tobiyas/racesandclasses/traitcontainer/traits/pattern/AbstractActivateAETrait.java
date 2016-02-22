package de.tobiyas.racesandclasses.traitcontainer.traits.pattern;

import org.bukkit.entity.Entity;

import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.magic.AbstractMagicSpellTrait;
import de.tobiyas.racesandclasses.util.entitysearch.SearchEntity;
import de.tobiyas.racesandclasses.util.friend.EnemyChecker;
import de.tobiyas.racesandclasses.util.friend.TargetType;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public abstract class AbstractActivateAETrait extends AbstractMagicSpellTrait {

	protected final static String RANGE_PATH = "range";
	protected final static String TARGET_PATH = "target";

	/**
	 * The Range the AE should go.
	 */
	protected double range = 5;
	
	/**
	 * Targets to apply on.
	 */
	protected TargetType target = TargetType.ALL;
	
	
	@TraitConfigurationNeeded(fields = {
			@TraitConfigurationField(classToExpect = Double.class, fieldName = "range", optional = true),
			@TraitConfigurationField(classToExpect = String.class, fieldName = "target", optional = true)	
		}
	)
	@Override
	public void setConfiguration(TraitConfiguration configMap)
			throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		if(configMap.containsKey(RANGE_PATH)){
			range = configMap.getAsDouble(RANGE_PATH);
		}
		
		if(configMap.containsKey("target")){
			String target = configMap.getAsString("target").toLowerCase();
			
			if(target.startsWith("all")) this.target = TargetType.ALL;
			if(target.startsWith("fr") || target.startsWith("ally")) this.target = TargetType.FRIEND;
			if(target.startsWith("e") || target.startsWith("fe")) this.target = TargetType.ENEMY;
		}
	}
	

	@Override
	public boolean isBetterThan(Trait trait) {
		return true;
	}

	
	
	/**
	 * The Trait triggered on this player.
	 * 
	 * @param caster that it was triggered on.
	 * 
	 * @return true if trigger on this entity worked.
	 */
	protected abstract boolean triggerOnEntity(RaCPlayer caster, Entity otherEntity);
	
	
	@Override
	protected void magicSpellTriggered(RaCPlayer player, TraitResults result) {
		boolean hitSomething = false;
		
		for(Entity otherEntity : SearchEntity.inCircleAround(player.getPlayer(), range)){
			if(EnemyChecker.isApplyable(player.getPlayer(), otherEntity, target)){
				if(triggerOnEntity(player, otherEntity)) hitSomething = true;
			}
		}
		
		
		result.copyFrom(hitSomething ? TraitResults.True() : TraitResults.False());
	}
}
