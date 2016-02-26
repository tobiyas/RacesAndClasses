package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.activate.InstantMeleeDamageTrait;

import org.bukkit.entity.LivingEntity;

import de.tobiyas.racesandclasses.entitystatusmanager.dot.DamageType;
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
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier;
import de.tobiyas.racesandclasses.util.entitysearch.SearchEntity;
import de.tobiyas.racesandclasses.util.friend.EnemyChecker;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;
import de.tobiyas.racesandclasses.vollotile.Vollotile;

public class InstantMeleeDamageTrait extends AbstractBasicTrait {

	/**
	 * The damage to deal.
	 */
	protected double damage = 1;
	
	/**
	 * The damage to deal.
	 */
	protected DamageType damageType = DamageType.ENTITY_ATTACK;
	
	/**
	 * The range to use.
	 */
	protected int range = 3;
	
	
	
	@TraitInfos(category="activate", traitName="InstantMeleeDamageTrait", visible=true)
	@Override
	public void importTrait() {}

	
	@TraitEventsUsed()
	@Override
	public void generalInit() {}

	
	@Override
	public String getName() {
		return "InstantMeleeDamageTrait";
	}
	
	
	@TraitConfigurationNeeded(fields={
			@TraitConfigurationField(fieldName="range", classToExpect=Integer.class, optional=true),
			@TraitConfigurationField(fieldName="damage", classToExpect=Double.class, optional=true),
			@TraitConfigurationField(fieldName="damageType", classToExpect=String.class, optional=true)
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		this.range = configMap.getAsInt("range", 3);
		this.damage = configMap.getAsDouble("damage", 1);
		this.damageType = DamageType.parse(configMap.getAsString("damageType", DamageType.ENTITY_ATTACK.name()));
	}

	
	@Override
	public TraitResults trigger(EventWrapper wrapper) {
		return TraitResults.False();
	}
	

	@Override
	public boolean isBetterThan(Trait trait) {
		if(trait instanceof InstantMeleeDamageTrait) return damage > ((InstantMeleeDamageTrait)trait).damage;
		return false;
	}
	

	@Override
	public boolean canBeTriggered(EventWrapper wrapper) {
		return false;
	}
	

	@Override
	protected String getPrettyConfigIntern() {
		return "does instant " + damage + " damage";
	}
	
	
	
	@Override
	protected TraitRestriction checkForFurtherRestrictions(EventWrapper wrapper) {
		int range = modifyToPlayer(wrapper.getPlayer(), this.range, "range");
		LivingEntity target = SearchEntity.inLineOfSight(range, wrapper.getPlayer().getPlayer());
		if(target == null) return TraitRestriction.NoTarget;
		if(EnemyChecker.areAllies(wrapper.getPlayer(), target)) return TraitRestriction.TargetFriendly;
		
		return null;
	}
	
	
	@Override
	protected TraitResults bindCastIntern(RaCPlayer player) {
		int range = modifyToPlayer(player, this.range, "range");
		LivingEntity target = SearchEntity.inLineOfSight(range, player.getPlayer());
		if(target == null) return TraitResults.False();
		if(EnemyChecker.areAllies(player.getPlayer(), target)) return TraitResults.False();
		
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
