package de.tobiyas.races.datacontainer.traitcontainer.imports;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD})
public @interface ImportTrait{
	public String traitName();
	public String category() default "None";
	public boolean visible() default true;
}
