package de.tobiyas.racesandclasses.racbuilder.container;

import java.util.List;
import java.util.Set;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.Trait;

public class BuildedClassContainer extends ClassContainer {

	/**
	 * Generates a ClassContainer.
	 * 
	 * @param name
	 */
	public BuildedClassContainer(String name, String classTag, boolean[] armorPermissions, Set<Trait> traits, 
			List<String> permissionList, String operation, double healthValue) {
		
		super(name);
		
		this.traits = traits;
		this.holderTag = classTag;
		this.holderPermissions.add(permissionList);
		this.armorUsage = armorPermissions;
		
		this.classHealthModify = operation + healthValue;
	}

	
	@Override
	public AbstractTraitHolder load(){
		return this;
	}
}
