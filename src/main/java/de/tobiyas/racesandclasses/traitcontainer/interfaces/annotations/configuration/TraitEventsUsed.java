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

import org.bukkit.event.Event;

import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;

/**
 * This Annotation is for registering events wanted
 * for traits.
 * 
 * @author tobiyas
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TraitEventsUsed{
	int traitPriority() default 3;
	
	/**
	 * This are the registered events wanted to be used to call 
	 * {@link Trait#trigger(Event)} with.
	 * @return
	 */
	Class<? extends Event>[] registerdClasses() default {};
	
	/**
	 * This are the registered events wanted to be used to call.
	 * <br>These will bypass all the limitations.
	 * {@link Trait#trigger(Event)} with.
	 * @return
	 */
	Class<? extends Event>[] bypassClasses() default {};
};
