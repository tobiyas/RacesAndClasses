package de.tobiyas.racesandclasses.traitcontainer.interfaces;

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
	 * This is a String Array consisting of the String values
	 * wanted for the Configuration.
	 * 
	 * The Values are passed after construction with a
	 * Map<String, String>. ConfigField -> value (as String).
	 * @return
	 */
	String[] neededFields() default {};
	String[] optionalFields() default {};
}
