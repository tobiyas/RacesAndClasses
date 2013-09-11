package de.tobiyas.racesandclasses.traitcontainer.traits.passive;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.AbstractBasicTrait;
import de.tobiyas.racesandclasses.util.traitutil.TraitStringUtils;

public abstract class AbstractPassiveTrait extends AbstractBasicTrait{

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
