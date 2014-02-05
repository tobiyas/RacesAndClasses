package de.tobiyas.racesandclasses.traitcontainer.traits.resistance;

import java.util.List;
import java.util.Map;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.AbstractBasicTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.ResistanceInterface;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;
import de.tobiyas.racesandclasses.util.traitutil.TraitStringUtils;

public abstract class AbstractResistance extends AbstractBasicTrait implements ResistanceInterface {

	protected List<DamageCause> resistances;
	protected AbstractTraitHolder traitHolder;
	
	protected double value;
	protected String operation = "";
	
	
	@Override
	public abstract String getName();

	@Override
	public void setTraitHolder(AbstractTraitHolder abstractTraitHolder){
		this.traitHolder = abstractTraitHolder;
	}
	
	@Override
	public AbstractTraitHolder getTraitHolder(){
		return traitHolder;
	}

	@Override
	protected String getPrettyConfigIntern(){
		return operation + " " + value;
	}

	@TraitConfigurationNeeded(fields = {
		@TraitConfigurationField(fieldName = "operation", classToExpect = String.class), 
		@TraitConfigurationField(fieldName = "value", classToExpect = Double.class)
	})
	@Override
	public void setConfiguration(Map<String, Object> configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		operation = (String) configMap.get("operation");
		value = (Double) configMap.get("value");
	}

	@Override
	public boolean trigger(Event event) {
		if(!(event instanceof EntityDamageEvent)) return false;
		EntityDamageEvent Eevent = (EntityDamageEvent) event;
		
		Entity entity = Eevent.getEntity();
		if(!(entity instanceof Player)) return false;
		Player player = (Player) entity;
		if(TraitHolderCombinder.checkContainer(player.getName(), this)){
			if(getResistanceTypes().contains(Eevent.getCause())){
				
				//If there is damage * 0, cancel the Event to show no damage effect.
				if(instantCancle()){
					CompatibilityModifier.EntityDamage.safeSetDamage(0, Eevent);
					Eevent.setCancelled(true);
					return true;
				}
				
				double oldDmg = CompatibilityModifier.EntityDamage.safeGetDamage(Eevent);
				double newDmg = TraitStringUtils.getNewValue(oldDmg, operation, value);
				
				CompatibilityModifier.EntityDamage.safeSetDamage(newDmg, Eevent);
				
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if the event should get an instant cancle.
	 * <br>This is when we have '*' as operator and 0 as value.
	 */
	private boolean instantCancle(){
		return operation.equals("*") && value == 0;
	}
	
	@Override
	public List<DamageCause> getResistanceTypes() {
		return resistances;
	}
	
	@Override
	public boolean isBetterThan(Trait trait){
		if(trait.getClass() != this.getClass()) return false;
		AbstractResistance otherTrait = (AbstractResistance) trait;
		
		return value >= otherTrait.value;
	}

	@Override
	public boolean canBeTriggered(Event event) {
		if(!(event instanceof EntityDamageEvent)) return false;
		EntityDamageEvent Eevent = (EntityDamageEvent) event;
		
		Entity entity = Eevent.getEntity();
		if(!(entity instanceof Player)) return false;
		Player player = (Player) entity;
		if(TraitHolderCombinder.checkContainer(player.getName(), this)){
			if(getResistanceTypes().contains(Eevent.getCause())){
				return true;
			}
		}
		
		return false;
	}
	
	
}
