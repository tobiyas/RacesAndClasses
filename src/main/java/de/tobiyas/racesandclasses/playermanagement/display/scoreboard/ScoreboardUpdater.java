package de.tobiyas.racesandclasses.playermanagement.display.scoreboard;

import java.util.List;

import org.bukkit.ChatColor;

import de.tobiyas.racesandclasses.APIs.CooldownApi;
import de.tobiyas.racesandclasses.datacontainer.arrow.ArrowManager;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.display.scoreboard.PlayerRaCScoreboardManager.SBCategory;
import de.tobiyas.racesandclasses.playermanagement.spellmanagement.PlayerSpellManager;
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
	 * Updates the Cooldown Display of the Player.
	 */
	public void updateCooldown(){
		List<Trait> traits = player.getTraits();
		String playerName = player.getName();
		
		manager.clearCategory(SBCategory.Cooldown);
		boolean hasAddedAny = false;
		
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
				hasAddedAny = true;
			}
		}
		
		if(!hasAddedAny)  manager.setValue(SBCategory.Cooldown, ChatColor.RED + "No Cooldowns", 1);
		manager.update();
	}
	
	
	/**
	 * Updates the Data to the Current Spell.
	 */
	public void updateSpells(){
		PlayerSpellManager spellManager = player.getSpellManager();
		MagicSpellTrait currentSpell = spellManager.getCurrentSpell();
		manager.clearCategory(SBCategory.Spells);
		
		boolean hasAddedAny = false;
		if(currentSpell != null){
			//If spell == null, we have no spells available
			int id = spellManager.getSpellAmount();
			for(MagicSpellTrait spell : spellManager.getAllSpells()){
				boolean selected = spell == currentSpell;
				ChatColor pre = (selected ? ChatColor.RED : ChatColor.YELLOW);
				ChatColor after = (selected ? ChatColor.AQUA : ChatColor.BLUE);
				
				String name =  getFromCooldown(spell, pre, after);
				manager.setValue(SBCategory.Spells, name, id);
				id--;
				hasAddedAny = true;
			}
		}
		
		if(!hasAddedAny) manager.setValue(SBCategory.Spells, ChatColor.RED + "No Spells", 1);
		manager.update();
	}
	

	/**
	 * Updates the Arrows.
	 */
	public void updateArrows() {
		ArrowManager arrowManager = player.getArrowManager();
		
		manager.clearCategory(SBCategory.Arrows);
		
		boolean hasAddedAny = false;
		//If arrow == null or we only have 1, we have no Arrows available
		if(arrowManager.getNumberOfArrowTypes() >= 1){
			AbstractArrow currentArrow = arrowManager.getCurrentArrow();
			
			int id = arrowManager.getNumberOfArrowTypes();
			for(AbstractArrow arrow :arrowManager.getAllArrows()){
				boolean selected = arrow == currentArrow;
				ChatColor pre = (selected ? ChatColor.RED : ChatColor.YELLOW);
				ChatColor after = (selected ? ChatColor.AQUA : ChatColor.BLUE);
				
				String name =  getFromCooldown(arrow, pre, after);
				manager.setValue(SBCategory.Arrows, name, id);
				id--;
				hasAddedAny = true;
			}
		}
		
		if(!hasAddedAny) manager.setValue(SBCategory.Arrows, ChatColor.RED + "No Arrows", 1);
		manager.update();
	}
	
	
	/**
	 * Generates a String for the Spell.
	 * 
	 * @param trait to use
	 * @param pre to use
	 * @param after to use
	 * 
	 * @return a colorfull name.
	 */
	private String getFromCooldown(TraitWithRestrictions trait, ChatColor pre, ChatColor after) {
		String name = trait.getDisplayName();
		double cooldown = CooldownApi.getCooldownOfPlayer(player.getName(), trait.getCooldownName());
		if(cooldown <= 0) return pre + name;

		
		cooldown = cooldown / trait.getMaxUplinkTime();
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
