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
	 * 
	 * @return true if optional, false if mandatory
	 */
	boolean optional() default true;
}
