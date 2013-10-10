package de.tobiyas.racesandclasses.traitcontainer.interfaces;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.bukkit.event.Event;

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
};
