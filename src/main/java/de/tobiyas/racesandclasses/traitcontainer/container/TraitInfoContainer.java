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
package de.tobiyas.racesandclasses.traitcontainer.container;

import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;

public class TraitInfoContainer {

	private String name;
	private Class<? extends Trait> clazz;
	private String category;
	private boolean visible;
	
	public TraitInfoContainer(String name, Class<? extends Trait> clazz, String category, boolean visible){
		this.name = name;
		this.clazz = clazz;
		this.category = category;
		this.visible = visible;
	}
	
	public String getName(){
		return name;
	}
	
	public Class<? extends Trait> getClazz(){
		return clazz;
	}
	
	public String getCategory(){
		return category;
	}
	
	public boolean isVisible(){
		return visible;
	}
}
