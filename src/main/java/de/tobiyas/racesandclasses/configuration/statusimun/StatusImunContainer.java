package de.tobiyas.racesandclasses.configuration.statusimun;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class StatusImunContainer {

	/**
	 * The Set of imun effects.
	 */
	private final Set<StatusEffect> imun = new HashSet<>();
	
	/**
	 * The name of the monster.
	 */
	private final String name;
	
	
	public StatusImunContainer(String name, Collection<StatusEffect> imun) {
		this.name = name;
		if(imun != null) this.imun.addAll(imun);
	}
	
	/**
	 * If the monster is imun to this effect.
	 * @param effect to check
	 * @return true if is immun.
	 */
	public boolean isImun(StatusEffect effect){
		if(effect == null) return false;
		return imun.contains(effect);
	}
	
	
	/**
	 * Gets against which effects this guy is imun.
	 * @return the collection of imun effects.
	 */
	public Collection<StatusEffect> getImun() {
		return new HashSet<>(imun);
	}
	
	
	/**
	 * Gets the name of the Guy.
	 * @return the name.
	 */
	public String getName() {
		return name;
	}
	
}
