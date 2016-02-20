package de.tobiyas.racesandclasses.entitystatusmanager.dot;

import org.bukkit.util.Vector;

import de.tobiyas.racesandclasses.vollotile.ParticleContainer;
import de.tobiyas.racesandclasses.vollotile.ParticleEffects;

public enum DotType {

	Poison(new ParticleContainer(ParticleEffects.STEP_SOUND, new Vector(0,0.1,0), 10, 133)),
	Fire(new ParticleContainer(ParticleEffects.FLAME, new Vector(0,0.1,0), 10, 0)),
	Magic(new ParticleContainer(ParticleEffects.WITCH_MAGIC, new Vector(0,0.1,0), 10, 0)),
	Bleading(new ParticleContainer(ParticleEffects.STEP_SOUND, new Vector(0,0.1,0), 10, 152));
	
	
	
	/**
	 * The particle Effect to show.
	 */
	private final ParticleContainer container;
	
	/**
	 * The Container to use for effects.
	 * @param container to use.
	 */
	private DotType(ParticleContainer container) {
		this.container = container;
	}

	
	/**
	 * Returns the ParticleContainer to use for The Dot-Type.
	 */
	public ParticleContainer getParticleContainer() {
		return container;
	}
	
}
