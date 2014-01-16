package de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This Annotation is to remove Annotations of the Super tye.
 * 
 * @author tobiyas
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RemoveSuperConfigField {

	/**
	 * The Field to be removed.
	 * 
	 * @return the removed Name
	 */
	public String name();
}
