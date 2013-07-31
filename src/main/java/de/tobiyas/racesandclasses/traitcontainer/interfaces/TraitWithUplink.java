package de.tobiyas.racesandclasses.traitcontainer.interfaces;

import java.io.Serializable;

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
public interface TraitWithUplink extends Trait{
	
	
	/**
	 * Returns the Indicator name of a Uplink.
	 * <br>This is the name that the {@link CooldownApi} is fed with.
	 * 
	 * @return the Indicator of the Uplink name
	 */
	public String getUplinkIndicatorName();
}
