package de.tobiyas.racesandclasses.traitcontainer.interfaces;

import java.io.Serializable;

import org.bukkit.event.Event;

import de.tobiyas.racesandclasses.APIs.CooldownApi;


/**
 * This Class is an indicator to that the Trait has uplink
 * or uses Cooldowns.
 * <br>
 * <br>
 * This has no direct effect on the code. It is just an indicator as {@link Serializable}
 * 
 * @author tobiyas
 */
public interface TraitWithUplink {
	
	
	/**
	 * Returns the Indicator name of a Uplink.
	 * <br>This is the name that the {@link CooldownApi} is fed with.
	 * 
	 * @return the Indicator of the Uplink name
	 */
	public String getUplinkIndicatorName();
	
	/**
	 * Returns the total uplink time in seconds
	 * 
	 * @return the time in seconds
	 */
	public int getMaxUplinkTime();
	
	
	/**
	 * This is triggered when the User has uplink on the Skill,
	 * But would be triggered.
	 * <br> Returning TRUE does NOT paste the Uplink message!
	 * 
	 * @param event the event that would be triggered
	 * @return true to NOT display uplink message!
	 */
	public boolean triggerButHasUplink(Event event);
}
