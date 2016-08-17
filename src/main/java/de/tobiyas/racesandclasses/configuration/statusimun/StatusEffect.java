package de.tobiyas.racesandclasses.configuration.statusimun;

public enum StatusEffect {

	SLOW,
	STUN,
	POISON,
	SILENCE,
	FIRE;
	
	
	
	/**
	 * Resolves the effect to the name.
	 * @param name to resolve.
	 * @return the resolved or null.
	 */
	public static StatusEffect resolve(String name){
		name = name.toUpperCase();
		for(StatusEffect effect : values()){
			if(effect.name().equals(name)) return effect;
		}
		
		return null;
	}
	
}
