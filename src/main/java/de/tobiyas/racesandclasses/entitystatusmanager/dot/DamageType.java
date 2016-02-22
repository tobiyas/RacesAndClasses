package de.tobiyas.racesandclasses.entitystatusmanager.dot;

import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.Vector;

import de.tobiyas.racesandclasses.translation.languages.Keys;
import de.tobiyas.racesandclasses.vollotile.ParticleContainer;
import de.tobiyas.racesandclasses.vollotile.ParticleEffects;
import de.tobiyas.util.math.Levenshtein;

public enum DamageType {

	POISON(new ParticleContainer(ParticleEffects.STEP_SOUND, new Vector(0,0.1,0), 10, 133), DamageCause.POISON, Keys.damage_poison),
	FIRE(new ParticleContainer(ParticleEffects.FLAME, new Vector(0,0.1,0), 10, 0), DamageCause.FIRE, Keys.damage_fire),
	MAGIC(new ParticleContainer(ParticleEffects.WITCH_MAGIC, new Vector(0,0.1,0), 10, 0), DamageCause.MAGIC, Keys.damage_magic),
	BLEEDING(new ParticleContainer(ParticleEffects.STEP_SOUND, new Vector(0,0.1,0), 10, 152), DamageCause.SUFFOCATION, Keys.damage_suffocation),
	CONTACT(new ParticleContainer(ParticleEffects.STEP_SOUND, new Vector(0,0.1,0), 10, 152), DamageCause.CONTACT, Keys.damage_contact),
    ENTITY_ATTACK(new ParticleContainer(ParticleEffects.STEP_SOUND, new Vector(0,0.1,0), 10, 152), DamageCause.ENTITY_ATTACK, Keys.damage_entity_attack),
    PROJECTILE(new ParticleContainer(ParticleEffects.STEP_SOUND, new Vector(0,0.1,0), 10, 152), DamageCause.PROJECTILE, Keys.damage_projectile),
    SUFFOCATION(new ParticleContainer(ParticleEffects.STEP_SOUND, new Vector(0,0.1,0), 10, 152), DamageCause.SUFFOCATION, Keys.damage_suffocation),
    FALL(new ParticleContainer(ParticleEffects.STEP_SOUND, new Vector(0,0.1,0), 10, 152), DamageCause.FALL, Keys.damage_fall),
    FIRE_TICK(new ParticleContainer(ParticleEffects.STEP_SOUND, new Vector(0,0.1,0), 10, 152), DamageCause.FIRE_TICK, Keys.damage_fire_tick),
    MELTING(new ParticleContainer(ParticleEffects.STEP_SOUND, new Vector(0,0.1,0), 10, 152), DamageCause.MELTING, Keys.damage_melting),
    LAVA(new ParticleContainer(ParticleEffects.STEP_SOUND, new Vector(0,0.1,0), 10, 152), DamageCause.LAVA, Keys.damage_lava),
    DROWNING(new ParticleContainer(ParticleEffects.STEP_SOUND, new Vector(0,0.1,0), 10, 152), DamageCause.DROWNING, Keys.damage_drowning),
    BLOCK_EXPLOSION(new ParticleContainer(ParticleEffects.STEP_SOUND, new Vector(0,0.1,0), 10, 152), DamageCause.BLOCK_EXPLOSION, Keys.damage_block_explosion),
    ENTITY_EXPLOSION(new ParticleContainer(ParticleEffects.STEP_SOUND, new Vector(0,0.1,0), 10, 152), DamageCause.ENTITY_EXPLOSION, Keys.damage_entity_explosion),
    VOID(new ParticleContainer(ParticleEffects.STEP_SOUND, new Vector(0,0.1,0), 10, 152), DamageCause.VOID, Keys.damage_void),
    LIGHTNING(new ParticleContainer(ParticleEffects.STEP_SOUND, new Vector(0,0.1,0), 10, 152), DamageCause.LIGHTNING, Keys.damage_lightning),
    SUICIDE(new ParticleContainer(ParticleEffects.STEP_SOUND, new Vector(0,0.1,0), 10, 152), DamageCause.SUICIDE, Keys.damage_suicide),
    STARVATION(new ParticleContainer(ParticleEffects.STEP_SOUND, new Vector(0,0.1,0), 10, 152), DamageCause.STARVATION, Keys.damage_starvation),
    WITHER(new ParticleContainer(ParticleEffects.STEP_SOUND, new Vector(0,0.1,0), 10, 152), DamageCause.WITHER, Keys.damage_wither),
    FALLING_BLOCK(new ParticleContainer(ParticleEffects.STEP_SOUND, new Vector(0,0.1,0), 10, 152), DamageCause.FALLING_BLOCK, Keys.damage_falling_block),
    THORNS(new ParticleContainer(ParticleEffects.STEP_SOUND, new Vector(0,0.1,0), 10, 152), DamageCause.THORNS, Keys.damage_thorns),
    CUSTOM(new ParticleContainer(ParticleEffects.STEP_SOUND, new Vector(0,0.1,0), 10, 152), DamageCause.CUSTOM, Keys.damage_custom);
	
	
	/**
	 * The particle Effect to show.
	 */
	private final ParticleContainer container;
	
	/**
	 * the Cause to use.
	 */
	private final DamageCause cause;
	
	/**
	 * The name to use.
	 */
	private final String nameKey;
	
	/**
	 * The Container to use for effects.
	 * @param container to use.
	 */
	private DamageType(ParticleContainer container, DamageCause cause, String nameKey) {
		this.container = container;
		this.cause = cause;
		this.nameKey = nameKey;
	}

	
	/**
	 * Returns the ParticleContainer to use for The Dot-Type.
	 */
	public ParticleContainer getParticleContainer() {
		return container;
	}
	
	/**
	 * Returns the Cause to use.
	 * @return the cause.
	 */
	public DamageCause getCause() {
		return cause;
	}
	
	/**
	 * The DisplayName Key to use.
	 * @return the display name Key to use..
	 */
	public String getNameKey() {
		return nameKey;
	}


	/**
	 * Parses the Damage type:
	 * @param type to parse.
	 * @return
	 */
	public static DamageType parse(String name) {
		for(DamageType type : values()){
			if(name.equalsIgnoreCase(type.name())) return type;
			if(name.equalsIgnoreCase(type.getCause().name())) return type;
		}
		
		DamageType nearest = Levenshtein.getNearestIgnoreCase(name, values());
		return nearest == null ? FIRE : nearest;
	}
}
