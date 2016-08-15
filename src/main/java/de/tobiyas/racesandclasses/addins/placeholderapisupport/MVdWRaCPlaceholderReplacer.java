package de.tobiyas.racesandclasses.addins.placeholderapisupport;

import java.text.DecimalFormat;

import org.bukkit.entity.Player;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.ClassAPI;
import de.tobiyas.racesandclasses.APIs.LevelAPI;
import de.tobiyas.racesandclasses.APIs.ManaAPI;
import de.tobiyas.racesandclasses.APIs.RaceAPI;
import de.tobiyas.racesandclasses.APIs.SpellAPI;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.playermanagement.playerdisplay.scoreboard.PlayerRaCScoreboardManager;
import de.tobiyas.racesandclasses.playermanagement.playerdisplay.scoreboard.PlayerRaCScoreboardManager.SBCategory;
import de.tobiyas.util.formating.ParseUtils;

public class MVdWRaCPlaceholderReplacer implements PlaceholderReplacer {
	
	
	private static final String BATTLE_LINE = "racBattleLine".toLowerCase();
	
	/**
	 * the plugin to use.
	 */
	private final RacesAndClasses plugin;
	
	private final DecimalFormat format = new DecimalFormat("0.0");
	
	
	
	public MVdWRaCPlaceholderReplacer(RacesAndClasses plugin) {
		this.plugin = plugin;
	}
	
	
	@Override
	public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
		String placeholder = event.getPlaceholder().toLowerCase();
		Player player = event.getPlayer();
		
		if(placeholder.equals("race")) return RaceAPI.getRaceNameOfPlayer(player);
		if(placeholder.equals("class")) return ClassAPI.getClassNameOfPlayer(player);
		
		if(placeholder.equals("mana")) return format.format(ManaAPI.getCurrentMana(player));
		if(placeholder.equals("maxmana")) return format.format(ManaAPI.getMaxMana(player));

		if(placeholder.equals("level")) return String.valueOf(LevelAPI.getCurrentLevel(player));
		if(placeholder.equals("exp")) return format.format(LevelAPI.getCurrentExpOfLevel(player));
		if(placeholder.equals("maxexp")) return format.format(LevelAPI.getMaxEXPToNextLevel(player));

		//Handle spells:
		if(placeholder.equals("currentspell")) return SpellAPI.getCurrentSelectedSpellName(player);
		if(placeholder.equals("currentspellcost")) return format.format(SpellAPI.getCurrentSpellCost(player));
		if(placeholder.equals("currentspellcosttype")) return SpellAPI.getCurrentSelectedSpellCostName(player);
		
		//Handle arrows:
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
		if(placeholder.equalsIgnoreCase("currentarrow")) return racPlayer.getArrowManager().getCurrentArrow().getDisplayName();
		if(placeholder.equalsIgnoreCase("currentarrowcost")) return format.format(racPlayer.getArrowManager().getCurrentArrow().getCost(racPlayer));
		if(placeholder.equalsIgnoreCase("currentarrowcostname")) return racPlayer.getArrowManager().getCurrentArrow().getCostType().name();
		
		//Handle battle-Texts:
		if(placeholder.startsWith(BATTLE_LINE)){
			String numString = placeholder.substring(BATTLE_LINE.length());
			int num = ParseUtils.parseInt(numString, -1);
			if(num >= 0 && num <= 15) return battleLine(racPlayer, num);
		}
		
		return null;
	}
	
	
	/**
	 * Registers the Placeholder.
	 */
	public void register(){
		PlaceholderAPI.registerPlaceholder(plugin, "race", this);
		PlaceholderAPI.registerPlaceholder(plugin, "class", this);
		
		PlaceholderAPI.registerPlaceholder(plugin, "mana", this);
		PlaceholderAPI.registerPlaceholder(plugin, "maxmana", this);
		
		PlaceholderAPI.registerPlaceholder(plugin, "level", this);
		PlaceholderAPI.registerPlaceholder(plugin, "exp", this);
		PlaceholderAPI.registerPlaceholder(plugin, "maxexp", this);

		PlaceholderAPI.registerPlaceholder(plugin, "currentspell", this);
		PlaceholderAPI.registerPlaceholder(plugin, "currentspellcost", this);
		PlaceholderAPI.registerPlaceholder(plugin, "currentspellcosttype", this);

		PlaceholderAPI.registerPlaceholder(plugin, "currentarrow", this);
		PlaceholderAPI.registerPlaceholder(plugin, "currentarrowcost", this);
		PlaceholderAPI.registerPlaceholder(plugin, "currentarrowcostname", this);

		//Lines to use.
		for(int i = 0; i < 16; i++){
			PlaceholderAPI.registerPlaceholder(plugin, BATTLE_LINE+i, this);
		}
	}
	
	
	/**
	 * Unregisters the Placeholder.
	 */
	public void unregister(){
		//TODO unregister somehow?!? seems no API for that.
	}
	
	
	/**
	 * Returns the Line for the Battle.
	 * @param line
	 * @return
	 */
	private String battleLine(RaCPlayer player, int line){
		PlayerRaCScoreboardManager manager = player.getScoreboardManager();
		SBCategory category = manager.getSelectedCategory();
		
		//Quickfix for concurrent modify!
		try{
			if(category == SBCategory.Arrows) return manager.getKeyForValue(category, line);
			if(category == SBCategory.Spells) return manager.getKeyForValue(category, line);
		}catch(Throwable exp) {}
		
		return "";
	}


	/**
	 * Does a simple replace on the Placeholder API.
	 */
	public static String replace(Player player, String toReplace) {
		return PlaceholderAPI.replacePlaceholders(player, toReplace);
	}

}
