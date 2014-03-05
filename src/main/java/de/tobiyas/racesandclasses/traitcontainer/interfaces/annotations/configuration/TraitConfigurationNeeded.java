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
package de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This Annotation is needed to check which configuration fields
 * are needed in the Race/Class configuration.
 * 
 * @author tobiyas
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TraitConfigurationNeeded{
	/**
	 * This is a {@link TraitConfigurationField} Array consisting of the String values and the association classes
	 * wanted for the Configuration.
	 * 
	 * The Values are passed after construction with a
	 * Map<String, String>. ConfigField -> value (as String).
	 * 
	 * @return the Fields the Trait wants
	 */
	TraitConfigurationField[] fields() default {};

	/**
	 * This is a list of Fields that are removed from super class
	 */
	RemoveSuperConfigField[] removedFields() default {};
}
