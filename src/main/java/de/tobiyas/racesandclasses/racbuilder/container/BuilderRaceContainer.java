package de.tobiyas.racesandclasses.racbuilder.container;

import java.util.List;
import java.util.Set;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;

public class BuilderRaceContainer extends RaceContainer {

	
	/**
	 * Creates a new RaceContainer for the passed values.
	 * 
	 * @param name the name to generate to.
	 */
	public BuilderRaceContainer(String name, String raceTag, boolean[] armorPermissions, Set<Trait> traits, 
			List<String> permissionList, double raceMaxHealth) {
		super(name);
		
		this.holderTag = raceTag;
		this.armorUsage = armorPermissions;
		
		this.raceMaxHealth = raceMaxHealth;
		this.traits = traits;
		
		this.raceChatColor = RacesAndClasses.getPlugin().getConfigManager().getChannelConfig().getConfig_racechat_default_color();
		this.raceChatFormat = RacesAndClasses.getPlugin().getConfigManager().getChannelConfig().getConfig_racechat_default_format();
	}
	
	
	@Override
	public AbstractTraitHolder load(){
		return this;
	}

}
