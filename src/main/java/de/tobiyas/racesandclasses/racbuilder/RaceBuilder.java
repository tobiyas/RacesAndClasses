package de.tobiyas.racesandclasses.racbuilder;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.racbuilder.container.BuilderRaceContainer;
import de.tobiyas.racesandclasses.util.persistence.YAMLPersistenceProvider;
import de.tobiyas.util.config.YAMLConfigExtended;


public class RaceBuilder extends AbstractHolderBuilder{

	/**
	 * Generates a Builder for a Race
	 * 
	 * @param name to build
	 */
	public RaceBuilder(String name) {
		super(name);
		
		health = 20;
	}
	
	
	/**
	 * Generates a builder from a existing race
	 * 
	 * @param raceContainer
	 */
	public RaceBuilder(RaceContainer raceContainer){
		super(raceContainer);
		
		health = raceContainer.getRaceMaxHealth();
	}
	
	
	/**
	 * Builds the {@link RaceContainer} from the given values.
	 * 
	 * @return the build RaceContainer.
	 */
	public RaceContainer build(){
		return new BuilderRaceContainer(this.name, this.holderTag, this.armorPermission, this.traitSet, this.permissionList, this.health);
	}
	
	@Override
	protected YAMLConfigExtended getHolderYAMLFile() {
		return YAMLPersistenceProvider.getLoadedRacesFile(true);
	}


	@Override
	protected void saveFurtherToFile(YAMLConfigExtended config) {
		config.set(name + ".config.raceMaxHealth", health);
		config.set(name + ".config.raceTag", holderTag);
	}
}
