package de.tobiyas.racesandclasses.playermanagement.display.scoreboard;

import java.util.List;

import org.bukkit.ChatColor;

import de.tobiyas.racesandclasses.APIs.CooldownApi;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.display.scoreboard.PlayerRaCScoreboardManager.SBCategory;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.MagicSpellTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.TraitWithRestrictions;
import de.tobiyas.racesandclasses.traitcontainer.traits.arrows.AbstractArrow;

public class ScoreboardUpdater {

	/**
	 * The Player to use.
	 */
	private final RaCPlayer player;
	
	/**
	 * The Scoreboard manager to use.
	 */
	private final PlayerRaCScoreboardManager manager;
	
	
	
	public ScoreboardUpdater(RaCPlayer player, PlayerRaCScoreboardManager manager) {
		this.player = player;
		this.manager = manager;
	}
	
	
	/**
	 * Updates the Arrows for the Player.
	 * 
	 * @param racPlayer to update.
	 */
	public void updatePlayerArrows(){
		AbstractArrow currentArrow = player.getArrowManager().getCurrentArrow();
		manager.clearCategory(SBCategory.Arrows);
		
		int id = player.getArrowManager().getNumberOfArrowTypes();
		if(id > 0){
			//If no arrows we have no arrows available
			for(AbstractArrow arrow : player.getArrowManager().getAllArrows()){
				String name = arrow.getDisplayName();
				boolean selected = arrow == currentArrow;
				
				name = (selected ? ChatColor.RED : ChatColor.YELLOW) + name;
				manager.setValue(SBCategory.Arrows, name, id);
				id--;
			}				
		}
		
		manager.update();
	}
	
	
	/**
	 * Updates the Cooldown Display of the Player.
	 */
	public void updateCooldown(){
		List<Trait> traits = player.getTraits();
		String playerName = player.getName();
		
		//Check for Cooldown Stuff.
		for(Trait trait : traits){
			if(trait instanceof TraitWithRestrictions){
				TraitWithRestrictions twr = (TraitWithRestrictions) trait;
				
				boolean supportsCooldown = twr.getMaxUplinkTime() > 0;
				if(!supportsCooldown) continue;
				
				//we have something with cooldown!
				int cooldown = CooldownApi.getCooldownOfPlayer(playerName, twr.getCooldownName());
				if(cooldown < 0) cooldown = 0;
				
				manager.setValue(SBCategory.Cooldown, twr.getDisplayName(), cooldown);
			}
		}
		
		manager.update();
	}
	
	
	/**
	 * Updates the Data to the Current Spell.
	 */
	public void updateSpells(){
		MagicSpellTrait currentSpell = player.getSpellManager().getCurrentSpell();
		manager.clearCategory(SBCategory.Spells);
		
		if(currentSpell != null){
			//If spell == null, we have no spells available
			int id = player.getSpellManager().getSpellAmount();
			for(MagicSpellTrait spell : player.getSpellManager().getAllSpells()){
				boolean selected = spell == currentSpell;
				ChatColor pre = (selected ? ChatColor.RED : ChatColor.YELLOW);
				ChatColor after = (selected ? ChatColor.AQUA : ChatColor.BLUE);
				
				String name =  getFromCooldown(spell, pre, after);
				manager.setValue(SBCategory.Spells, name, id);
				id--;
			}
		}
		
		manager.update();
	}
	
	
	/**
	 * Generates a String for the Spell.
	 * 
	 * @param spell
	 * @param pre
	 * @param after
	 * 
	 * @return a colorfull name.
	 */
	private String getFromCooldown(MagicSpellTrait spell, ChatColor pre,
			ChatColor after) {
		
		String name = spell.getDisplayName();
		double cooldown = CooldownApi.getCooldownOfPlayer(player.getName(), spell.getCooldownName());
		if(cooldown <= 0) return pre + name;

		
		cooldown = cooldown / spell.getMaxUplinkTime();
		int chars = (int)(cooldown * (double)name.length());
		String first = name.substring(0, chars);
		String second = name.substring(chars, name.length());
		
		return pre + first + after + second;
	}


	/**
	 * Updates the General Part.
	 */
	public void updateGeneral(){
		int health = (int) player.getHealth();
		int mana = (int) player.getManaManager().getCurrentMana();
		int level = player.getLevelManager().getCurrentLevel();
		int exp = player.getLevelManager().getCurrentExpOfLevel();

		manager.setValue(SBCategory.General, ChatColor.RED + "Health", health);
		manager.setValue(SBCategory.General, ChatColor.BLUE + "Mana", mana);
		manager.setValue(SBCategory.General, ChatColor.YELLOW + "Level", level);
		manager.setValue(SBCategory.General, ChatColor.YELLOW + "Exp", exp);
	}

}
