package de.tobiyas.racesandclasses.APIs;

import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.MagicSpellTrait;

public class SpellAPI {

	
	/**
	 * Gets the currently selected spell name of the Player.
	 * <br>May be empty if no spell selected!
	 * @param player to use.
	 * @return the currently selected spell.
	 */
	public static String getCurrentSelectedSpellName(Player player){
		MagicSpellTrait spell = getCurrentSelectedSpell(player);
		return spell == null ? "" : spell.getDisplayName();
	}
	
	
	/**
	 * Gets the currently selected spell name of the Player.
	 * <br>May be empty if no spell selected!
	 * @param player to use.
	 * @return the currently selected spell.
	 */
	public static MagicSpellTrait getCurrentSelectedSpell(Player player){
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
		if(racPlayer == null) return null;
		
		return racPlayer.getSpellManager().getCurrentSpell();
	}
	
	/**
	 * Gets the currently selected spell cost of the Player.
	 * <br>Is 0 if no spell selectable!
	 * @param player to use.
	 * @return the cost of the spell.
	 */
	public static double getCurrentSpellCost(Player player){
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
		MagicSpellTrait spell = getCurrentSelectedSpell(player);
		if(spell == null) return 0;
		return spell.getCost(racPlayer);
	}
	
	/**
	 * Gets the currently selected spell cost of the Player.
	 * <br>Is 0 if no spell selectable!
	 * @param player to use.
	 * @return the cost of the spell.
	 */
	public static String getCurrentSelectedSpellCostName(Player player){
		MagicSpellTrait spell = getCurrentSelectedSpell(player);
		if(spell == null) return "";
		return spell.getCostType().name();
	}
	
}
