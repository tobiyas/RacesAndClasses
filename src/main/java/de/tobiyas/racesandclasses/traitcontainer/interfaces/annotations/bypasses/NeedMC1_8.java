package de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.bypasses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker Interface to show that it needs MC 1.8 or above.
 * 
 * @author tobiyas
 */
@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NeedMC1_8 {

}
