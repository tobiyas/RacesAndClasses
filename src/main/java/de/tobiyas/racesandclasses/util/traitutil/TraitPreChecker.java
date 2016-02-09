package de.tobiyas.racesandclasses.util.traitutil;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.bypasses.NeedMC1_7;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.bypasses.NeedMC1_8;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.bypasses.NeedsOtherPlugins;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;

public class TraitPreChecker {

	
	public static boolean hasNeeds1_6(Class<? extends Trait> traitClass){
		return traitClass.isAnnotationPresent(NeedMC1_8.class);
	}
	
	public static boolean hasNeeds1_7(Class<? extends Trait> traitClass){
		return traitClass.isAnnotationPresent(NeedMC1_7.class);
	}
	
	public static boolean hasNeeds1_8(Class<? extends Trait> traitClass){
		return traitClass.isAnnotationPresent(NeedMC1_8.class);
	}
	
	
	public static Collection<String> getRequiredPlugins(Class<? extends Trait> traitClass){
		Set<String> needed = new HashSet<>();
		if(!traitClass.isAnnotationPresent(NeedsOtherPlugins.class)) return needed;
		String[] need = traitClass.getAnnotation(NeedsOtherPlugins.class).neededPlugins();
		if(need == null || need.length == 0) return needed;
		
		for(String n : need) needed.add(n);
		return needed;
	}
	
	
}
