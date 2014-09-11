package de.tobiyas.racesandclasses.util.traitutil;

import java.lang.reflect.Method;

import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;

public class TraitVisible {


	
	/**
	 * Checks if a Trait is visible by checking it's import Trait Annotation
	 * 
	 * @param trait
	 * @return
	 */
	public static boolean isVisible(Trait trait) {
		try{
			Method importMethod = trait.getClass().getMethod("importTrait");
			TraitInfos infos = importMethod.getAnnotation(TraitInfos.class);
			
			boolean isVisable = infos.visible();
			return isVisable;
		}catch(Exception exp){}
		
		return false;
	}
}
