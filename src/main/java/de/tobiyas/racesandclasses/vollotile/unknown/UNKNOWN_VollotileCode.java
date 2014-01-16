package de.tobiyas.racesandclasses.vollotile.unknown;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.vollotile.VollotileCode;

/**
 * This is a dummy implementation for some dummy
 * 
 * @author tobiyas
 */
public class UNKNOWN_VollotileCode extends VollotileCode {

	public UNKNOWN_VollotileCode() {
		super("UNKNOWN"); //We have no clue.
	}
	
	@Override
	public void playCriticalHitEffect(Player toSendTo, Entity toPlayEffect) {
	}

	@Override
	public void removeParticleEffect(LivingEntity entity) {
	}
	
}
