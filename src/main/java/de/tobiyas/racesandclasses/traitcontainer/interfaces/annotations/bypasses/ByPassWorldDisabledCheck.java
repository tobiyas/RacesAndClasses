package de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.bypasses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This indicates that the World check for this trait should be bypassed.
 * 
 * @author tobiyas
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE})
public @interface ByPassWorldDisabledCheck {

}
