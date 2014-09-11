package de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.bypasses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NeedsOtherPlugins {

	/**
	 * Returns the Needed plugins to check for.
	 * 
	 * @return an empty list by default.
	 */
	public String[] neededPlugins() default {};
}
