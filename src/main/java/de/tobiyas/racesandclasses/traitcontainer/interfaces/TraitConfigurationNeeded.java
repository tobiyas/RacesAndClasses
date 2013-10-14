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
	 * This is a {@link TraitConfigurationField} Array consisting of the String values and the association classes
	 * wanted for the Configuration.
	 * 
	 * The Values are passed after construction with a
	 * Map<String, String>. ConfigField -> value (as String).
	 * 
	 * @return the Fields the Trait wants
	 */
	TraitConfigurationField[] fields() default {};
}
