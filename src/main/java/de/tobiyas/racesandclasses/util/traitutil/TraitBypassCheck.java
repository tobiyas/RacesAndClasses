package de.tobiyas.racesandclasses.util.traitutil;

import org.bukkit.event.Event;

import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;

public class TraitBypassCheck {


	/**
	 * Checks if the class is on the bypass list.
	 * 
	 * @param traitClass to check.
	 * @param eventClass to check.
	 * 
	 * @return true if it is.
	 */
	public static boolean hasBypass(Class<? extends Trait> traitClass, Class<? extends Event> eventClass){
		try{
			Class<?> toInspect = traitClass;
			while(toInspect != Object.class){
				try{
					TraitEventsUsed used = toInspect.getMethod("generalInit").getAnnotation(TraitEventsUsed.class);
					if(used == null || used.bypassClasses().length == 0) continue;
					
					for(Class<? extends Event> clazz : used.bypassClasses()){
						if(clazz == eventClass) return true;
					}
				}catch(Throwable exp){ 
					continue; 
				}finally{
					if(toInspect.getSuperclass() == Object.class|| toInspect.getSuperclass() == null) return false;
					toInspect = toInspect.getSuperclass();
				}
			}
			
			return false;
		}catch(Throwable exp){ return false; }
	}
}
