package de.tobiyas.racesandclasses.racbuilder;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.racbuilder.container.BuilderRaceContainer;


public class RaceBuilder extends AbstractHolderBuilder{

	/**
	 * The max Health of the Race
	 */
	protected double raceMaxHealth;
	
	
	/**
	 * Generates a Builder for a Race
	 * 
	 * @param name to build
	 */
	public RaceBuilder(String name) {
		super(name);
		
		raceMaxHealth = 20;
	}
	
	
	/**
	 * Sets the max health of the Race.
	 * 
	 * @param raceMaxHealth of the race.
	 */
	public void setRaceMaxHealth(double raceMaxHealth){
		this.raceMaxHealth = raceMaxHealth;
	}

	
	/**
	 * Builds the {@link RaceContainer} from the given values.
	 * 
	 * @return the build RaceContainer.
	 */
	public RaceContainer build(){
		return new BuilderRaceContainer(this.name, this.holderTag, this.armorPermission, this.traitSet, this.permissionList, this.raceMaxHealth);
	}
}
