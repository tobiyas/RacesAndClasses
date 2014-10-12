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
package de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.exceptions;

import java.util.regex.Pattern;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;

public class HolderTraitParseException extends HolderParsingException {
	private static final long serialVersionUID = 6315527299856915167L;

	/**
	 * The Holder this error was produces by.
	 */
	private final AbstractTraitHolder holder;
	
	
	public HolderTraitParseException(String message, AbstractTraitHolder abstractTraitHolder) {
		super(message);
		
		this.holder = abstractTraitHolder;
	}
	
	
	@Override
	public String getLocalizedMessage() {
		String message = super.getLocalizedMessage();
		
		message.replaceAll(Pattern.quote("%HOLDER%"), holder == null ? "UNKNOWN" : holder.getDisplayName());
		return message;
	}
	
}
