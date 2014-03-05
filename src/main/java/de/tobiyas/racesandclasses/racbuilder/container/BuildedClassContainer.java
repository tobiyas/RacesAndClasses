/*******************************************************************************
 * Copyright 2014 Tobias Welther
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.tobiyas.racesandclasses.racbuilder.container;

import java.util.List;
import java.util.Set;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;

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
