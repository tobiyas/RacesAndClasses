package de.tobiyas.racesandclasses.traitcontainer.traits.magic;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.util.entitysearch.SearchEntity;
import de.tobiyas.racesandclasses.util.friend.EnemyChecker;
import de.tobiyas.racesandclasses.util.friend.TargetType;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public abstract class AbstractMagicAuraSpellTrait extends
		AbstractContinousCostMagicSpellTrait {


	/**
	 * The Range to use.
	 */
	private double range = 10;
	
	/**
	 * Which targets to choose.
	 */
	private TargetType target = TargetType.ALL;
	
	
	@TraitConfigurationNeeded( 
			fields = {
				@TraitConfigurationField(classToExpect = Double.class, fieldName = "range", optional = true),
				@TraitConfigurationField(classToExpect = String.class, fieldName = "tagetType", optional = true)
			}
		)
	@Override
	public void setConfiguration(TraitConfiguration configMap)
			throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		
		if(configMap.containsKey("range")){
			double range = configMap.getAsDouble("range");
			this.range = range;
		}
		
		if(configMap.containsKey("targetType")){
			String typeName = configMap.getAsString("targetType");
			try{ target = TargetType.valueOf(typeName.toUpperCase()); }catch(IllegalArgumentException exp){}
		}
	}
	

	@Override
	protected boolean activateIntern(RaCPlayer player) {
		return true;
	}

	@Override
	protected boolean deactivateIntern(RaCPlayer player) {
		return true;
	}

	@Override
	protected boolean tickInternal(RaCPlayer player) {
		if(!player.isOnline()) return false;
		List<Entity> nearby = SearchEntity.inCircleAround(player.getPlayer(), range);
		for(Entity entity : nearby){
			if(!(entity instanceof LivingEntity)) continue;
			LivingEntity casted = (LivingEntity) entity;
			if(EnemyChecker.isApplyable(player.getPlayer(), casted, target)){
				tickOnEntity(casted);
			}
		}
		
		return true;
	}
	
	
	/**
	 * The Aura ticks on that entity.
	 * 
	 * @param entity to tick on
	 */
	protected abstract void tickOnEntity(Entity entity);
}
