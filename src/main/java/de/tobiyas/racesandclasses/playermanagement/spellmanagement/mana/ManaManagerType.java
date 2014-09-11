package de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana;

import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana.impl.MagicSpellsManaManager;
import de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana.impl.OwnManaManager;

public enum ManaManagerType {

	RaC(),
	MagicSpells()
	;
	
	
	
	/**
	 * Generates a Mana manager from this Type.
	 * 
	 * @param player to generate for.
	 * @return the generated manager.
	 */
	public ManaManager generate(RaCPlayer player){
		if(this == RaC) return new OwnManaManager(player);
		if(this == MagicSpells) return new MagicSpellsManaManager(player);
		
		return new OwnManaManager(player);
	}
	
	public static ManaManagerType resolve(String value){
		value = value.toLowerCase();
		if(value.startsWith("m")) return MagicSpells;
		
		return RaC;
	}
}
