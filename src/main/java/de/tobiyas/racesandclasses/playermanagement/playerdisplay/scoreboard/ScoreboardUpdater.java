package de.tobiyas.racesandclasses.playermanagement.playerdisplay.scoreboard;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.APIs.CooldownApi;
import de.tobiyas.racesandclasses.APIs.GroupAPI;
import de.tobiyas.racesandclasses.datacontainer.arrow.ArrowManager;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.playerdisplay.scoreboard.PlayerRaCScoreboardManager.SBCategory;
import de.tobiyas.racesandclasses.playermanagement.spellmanagement.PlayerSpellManager;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.MagicSpellTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.TraitWithCost;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.TraitWithRestrictions;
import de.tobiyas.racesandclasses.traitcontainer.traits.arrows.AbstractArrow;
import de.tobiyas.util.math.Math2;

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
		TraitWithCost currentSpell = spellManager.getCurrentSpell();
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
		if(arrowManager.hasAnyArrow()){
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
		
		double percent = cooldown / (double)trait.getMaxUplinkTime();
		percent = Math2.clamp(0, percent, 1);
		
		return colorizeToPercent(name, percent, pre, after);
	}
	
	/**
	 * Colorizes the String by the options passed.
	 * @param toColorize the string to colorize
	 * @param percent the percent of the String to colorize.
	 * @param front The front color
	 * @param back the end color.
	 * @return the colorized String.
	 */
	private String colorizeToPercent(String toColorize, double percent, ChatColor front, ChatColor back){
		int max = toColorize.length();
		int chars = (int)(percent * (double)max);
		chars = Math2.clamp(0, chars, max);
		
		String first = toColorize.substring(0, chars);
		String second = toColorize.substring(chars, max);
		
		return front + first + back + second;
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
	
	
	/**
	 * Updates the Group view
	 */
	public void updateGroup(){
		Collection<Player> toShow = new HashSet<>(GroupAPI.getMembersOfGroup(player.getPlayer()));
		toShow.add(player.getPlayer());
		
		manager.clearCategory(SBCategory.Group);
		for(Player player : toShow) {
			manager.setValue(SBCategory.Group, generateColorTextFromPlayer(player), (int)player.getHealth());
		}
	}
	
	
	/**
	 * Generates a Player percent for Color.
	 * @param player to use.
	 * @return the colorized String.
	 */
	private String generateColorTextFromPlayer(Player player){
		double percent = player.getHealth() / player.getMaxHealth();
		return colorizeToPercent(player.getDisplayName(), percent, ChatColor.GREEN, ChatColor.RED);
	}

}
