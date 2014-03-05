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
public @interface TraitConfigurationField {
	
	/**
	 * The Name of the Field wanted
	 * 
	 * @return the name of the wanted Field
	 */
	String fieldName();
	
	/**
	 * The Class the field should be
	 * 
	 * @return the class to be casted
	 */
	Class<?> classToExpect();
	
	/**
	 * If the field is optional or not
	 * <br>This is an optional Field.
	 * Default is: false -> Configuration field is mandatory
	 * 
	 * @return true if optional, false if mandatory
	 */
	boolean optional() default false;

}
