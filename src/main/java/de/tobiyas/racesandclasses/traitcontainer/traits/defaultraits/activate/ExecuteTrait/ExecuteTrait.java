package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.activate.ExecuteTrait;

import org.bukkit.entity.LivingEntity;

import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.TraitRestriction;
import de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.activate.InstantMeleeDamageTrait.InstantMeleeDamageTrait;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier;
import de.tobiyas.racesandclasses.util.entitysearch.SearchEntity;
import de.tobiyas.racesandclasses.util.friend.EnemyChecker;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;
import de.tobiyas.racesandclasses.vollotile.Vollotile;

public class ExecuteTrait extends InstantMeleeDamageTrait {
	
	/**
	 * Under what percent.
	 */
	private double percent = 20;
	
	
	@TraitInfos(category="activate", traitName="ExecuteTrait", visible=true)
	@Override
	public void importTrait() {}

	
	@Override
	public String getName() {
		return "ExecuteTrait";
	}
	
	
	@TraitConfigurationNeeded(fields={
			@TraitConfigurationField(fieldName="percent", classToExpect=Double.class, optional=true)
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		this.percent = configMap.getAsDouble("percent", 20);
	}
	
	
	@Override
	public TraitResults trigger(EventWrapper wrapper) {
		return TraitResults.False();
	}
	

	@Override
	protected String getPrettyConfigIntern() {
		return "does instant " + damage + " damage when under " + percent + " percent";
	}
	
	
	
	@Override
	protected TraitRestriction checkForFurtherRestrictions(EventWrapper wrapper) {
		int range = modifyToPlayer(wrapper.getPlayer(), this.range, "range");
		LivingEntity target = SearchEntity.inLineOfSight(range, wrapper.getPlayer().getPlayer());
		if(target == null) return TraitRestriction.NoTarget;
		
		double targetPercent = target.getMaxHealth() / target.getHealth();
		if(targetPercent > percent) return TraitRestriction.NoTarget;

		if(EnemyChecker.areAllies(wrapper.getPlayer(), target)) return TraitRestriction.TargetFriendly;
		return null;
	}
	
	
	@Override
	protected TraitResults bindCastIntern(RaCPlayer player) {
		int range = modifyToPlayer(player, this.range, "range");
		LivingEntity target = SearchEntity.inLineOfSight(range, player.getPlayer());
		if(target == null) return TraitResults.False();
		
		double newDamage = modifyToPlayer(player, this.damage, "damage");
		CompatibilityModifier.LivingEntity.safeDamageEntityByEntity(target, player.getPlayer(), newDamage, damageType.getCause());
		
		//Sends a particle effect to be somewhat more cool!
		Vollotile.get().sendOwnParticleEffectToAll(damageType.getParticleContainer(), target.getEyeLocation());
		return TraitResults.True();
	}
	
	@Override
	public boolean isBindable() {
		return true;
	}

}
