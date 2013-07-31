package de.tobiyas.racesandclasses.traitcontainer.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD})
public @interface TraitInfos{
	public String traitName();
	public String category() default "None";
	public boolean visible() default true;
}
