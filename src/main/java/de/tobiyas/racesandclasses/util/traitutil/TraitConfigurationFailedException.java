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
package de.tobiyas.racesandclasses.util.traitutil;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.exceptions.HolderTraitParseException;

public class TraitConfigurationFailedException extends HolderTraitParseException {	
	private static final long serialVersionUID = -1228403755162849596L;
	
	public TraitConfigurationFailedException(String string, AbstractTraitHolder holder) {
		super(string, holder);
	}

	public TraitConfigurationFailedException(String string) {
		this(string, null);
	}

}
