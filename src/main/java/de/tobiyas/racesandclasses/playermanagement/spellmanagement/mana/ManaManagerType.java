package de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana;

import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana.impl.MagicSpellsManaManager;
import de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana.impl.OwnManaManager;
import de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana.impl.SkillAPIManaManager;

public enum ManaManagerType {

	RaC(),
	MagicSpells(),
	SkillAPI()
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
		if(this == SkillAPI) return new SkillAPIManaManager(player);
		
		return new OwnManaManager(player);
	}
	
	public static ManaManagerType resolve(String value){
		value = value.toLowerCase();
		if(value.startsWith("m")) return MagicSpells;
		if(value.startsWith("s")) return SkillAPI;
		
		return RaC;
	}
}
