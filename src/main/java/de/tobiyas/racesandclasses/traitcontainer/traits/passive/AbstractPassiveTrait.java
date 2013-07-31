package de.tobiyas.racesandclasses.traitcontainer.traits.passive;

import java.util.Map;

import org.bukkit.event.Event;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.Trait;
import de.tobiyas.racesandclasses.util.traitutil.TraitStringUtils;

public abstract class AbstractPassiveTrait implements Trait {

	protected AbstractTraitHolder traitHolder;
	
	protected double value;
	protected String operation;
	
	
	@Override
	public void setTraitHolder(AbstractTraitHolder traitHolder){
		this.traitHolder = traitHolder;
	}
	
	
	@Override
	public AbstractTraitHolder getTraitHolder(){
		return traitHolder;
	}
	
	@Override
	public abstract void generalInit();

	@Override
	public abstract String getName();


	@Override
	public abstract String getPrettyConfiguration();

	
	@Override
	public abstract void setConfiguration(Map<String, String> configMap);
	

	@Override
	public abstract boolean modify(Event event);

	
	@Override
	public abstract boolean isBetterThan(Trait otherTrait);

	
	/**
	 * Calculates the new value by evaluating the operation
	 * to the old damage value
	 * 
	 * @param oldDmg
	 * @return
	 */
	protected double getNewValue(double oldDmg) {
		return TraitStringUtils.getNewValue(oldDmg, operation, value);
	}
	
	
	@Override
	public String toString(){
		return getName();
	}
}
