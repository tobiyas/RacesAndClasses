package de.tobiyas.racesandclasses.playermanagement.playerdisplay.scoreboard;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.util.vollotile.VollotileCode.MCVersion;
import de.tobiyas.util.vollotile.VollotileCodeManager;

public class PlayerRaCScoreboardManager {

	/**
	 * The Infos to show.
	 * <br>Way too long... :D
	 */
	private final Map<SBCategory, Map<String,Integer>> toShow 
		= new EnumMap<SBCategory, Map<String,Integer>>(SBCategory.class);
	
	
	/**
	 * The Player to use.
	 */
	private final RaCPlayer player;
	
	/**
	 * The Selected Category.
	 */
	private SBCategory selectedCategory = SBCategory.General;
	
	/**
	 * If we need an update
	 */
	private boolean needsUpdate = true;
	
	/**
	 * The Updater to use.
	 */
	private final ScoreboardUpdater updater;
	
	/**
	 * The Displaytimer to use.
	 */
	private final DisplayTimer displayTimer;
	
	
	
	public PlayerRaCScoreboardManager(RaCPlayer player) {
		this.player = player;
		this.updater = new ScoreboardUpdater(player, this);
		this.displayTimer = new DisplayTimer();
		
		for(SBCategory category : SBCategory.values()){
			toShow.put(category, new HashMap<String,Integer>());
		}
	}
	
	
	/**
	 * Sets the Key - values.
	 * 
	 * @param key to set
	 * @param value to set
	 */
	public void setValue(SBCategory category, String key, int value){
		if(category == null || key == null || key.isEmpty()) return;
		
		//If we have no cooldown, remove the entry.
		if(category == SBCategory.Cooldown && value <= 0){
			remove(category, key);
			return;
		}
		
		Map<String,Integer> map = toShow.get(category);
		Integer old = map.put(key, value);
		if(old != null && old.equals(value)) return;
		
		needsUpdate = true;
	}
	
	/**
	 * gets the Key - values.
	 * 
	 * @param category to get
	 * @param value to get
	 * 
	 * @return the correct String, EMPTY if none.
	 */
	public String getKeyForValue(SBCategory category, int value){
		if(category == null) return "";
		
		Map<String,Integer> map = toShow.get(category);
		if(map == null) return "";
		
		//Gets the value passed:
		for(Map.Entry<String, Integer> entry : map.entrySet()){
			if(entry.getValue() == value) return entry.getKey();
		}
		
		return "";
	}
	
	
	/**
	 * Clears the Category.
	 * 
	 * @param category to clear
	 */
	public void clearCategory(SBCategory category){
		Map<String,Integer> map = toShow.get(category);
		if(map.isEmpty()) return;
		
		map.clear();
		needsUpdate = true;
	}
	
	
	/**
	 * Removes the Key.
	 * 
	 * @param key to remove.
	 */
	public void remove(SBCategory category, String key){
		if(category == null || key == null || key.isEmpty()) return;
		
		Map<String,Integer> map = toShow.get(category);
		//If not present, Nothing to do!
		if(!map.containsKey(key)) return;
		
		map.remove(key);
		needsUpdate = true;
	}
	
	
	/**
	 * Updates the Display.
	 */
	public void update(){
		boolean removeScoreboard = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_disableAllScoreboardOutputs();
		if(removeScoreboard) return;
		
		if(!needsUpdate) return;
		
		//First clear the old scoreboard
		Scoreboard board = player.getScoreboard();
		if(board == null) return;
		
		for(String entry : board.getEntries()){
			board.resetScores(entry);
		}
		
		//Create Scoreboard + other stuff.
		String sbPre = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_gui_scoreboard_name();
		sbPre = ChatColor.translateAlternateColorCodes('&', sbPre);
		
		//now repopulate it.
		for(SBCategory category : SBCategory.values()){
			Objective objective = board.getObjective(category.name());
			if(objective == null) {
				objective = board.registerNewObjective(category.name(), "dummy");
				objective.setDisplayName(sbPre + " " + category.getHeadline());
			}

			//update Stuff
			Map<String,Integer> map = toShow.get(category);
			for(Map.Entry<String,Integer> entry : map.entrySet()){
				String key = entry.getKey();
				int value = entry.getValue();
				
				Score score = objective.getScore(shortenKey(key));
				score.setScore(value);
			}
		}
		
		//Now show stuff.
		needsUpdate = false;
	}
	
	
	/**
	 * Shortends the Key to the Best length.
	 * 
	 * @param key to shorten
	 * 
	 * @return the shortened Key.
	 */
	private String shortenKey(String key){
		MCVersion version = VollotileCodeManager.getVollotileCode().getVersion();
		if(key.length() > 16 && !version.isVersionGreaterOrEqual(MCVersion.v1_8_R1)){
			key = key.substring(0,16);
		}
		
		if(key.length() > 40 && version.isVersionGreaterOrEqual(MCVersion.v1_8_R1)){
			key = key.substring(0, 39);
		}
		
		return key;
	}
	
	
	/**
	 * Shows the Scoreboard.
	 */
	public void show(){
		boolean removeScoreboard = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_disableAllScoreboardOutputs();
		if(removeScoreboard) return;
		if(!player.isOnline()) return;
		
		//If nothing is present, hide.
		if(displayTimer.shouldBeHidden() || toShow.get(this.selectedCategory).size() == 0){
			hide();
			return;
		}
		
		//Set the correct Objective to display.
		Scoreboard board = player.getScoreboard();
		Objective obj = board.getObjective(this.selectedCategory.name());
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
	}
	
	
	/**
	 * Hides the Display and shows the Main board again.
	 */
	public void hide(){
		boolean removeScoreboard = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_disableAllScoreboardOutputs();
		if(removeScoreboard) return;
		if(!player.isOnline()) return;
		
		//If not our board -> nothing to do.
		Scoreboard board = player.getScoreboard();
		Objective obj = board.getObjective("empty");
		if(obj == null) obj = board.registerNewObjective("empty", "dummy");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
	}

	
	/**
	 * Returns the Selected Category
	 * 
	 * @return the selected Category
	 */
	public SBCategory getSelectedCategory() {
		return selectedCategory;
	}

	/**
	 * Sets the New Category.
	 * 
	 * @param selectedCategory to set
	 */
	public void setSelectedCategory(SBCategory selectedCategory) {
		if(selectedCategory == null 
				|| this.selectedCategory == selectedCategory) {
			
			return;
		}
		
		this.selectedCategory = selectedCategory;
	}
	

	/**
	 * The Category to update and show.
	 * 
	 * @param category to udpate / Show.
	 * @param showTime to show. Use {@link Long#MAX_VALUE} for inf.
	 */
	public void updateSelectAndShow(SBCategory category, long showTime){
		if(category == null) return;
		
		switch (category) {
			case Arrows: updater.updateArrows(); break;
			case Cooldown: updater.updateCooldown(); break;
			case Spells: updater.updateSpells(); break;
			case General: updater.updateGeneral(); break;
			case Group: updater.updateGroup(); break;
	
			default: return;
		}
		
		refreshDisplayTime(showTime);
		
		setSelectedCategory(category);
		update();
		show();
	}
	
	
	/**
	 * The Category to update and show.
	 * <br>Time is set to Infinite!
	 * 
	 * @param category to udpate / Show.
	 */
	public void updateSelectAndShow(SBCategory category){
		updateSelectAndShow(category, Long.MAX_VALUE);
	}
	
	
	/**
	 * Refreshes the Displaytime.
	 * <br> use {@link Long#MAX_VALUE} for infinit.
	 * 
	 * @param time to refresh.
	 */
	public void refreshDisplayTime(long time){
		boolean removeScoreboard = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_disableAllScoreboardOutputs();
		if(removeScoreboard) return;
		
		boolean inf = time >= Long.MAX_VALUE;
		
		displayTimer.setAlwaysShow(inf);
		displayTimer.addHideDelay(time);
	}

	
	/**
	 * Ticks the Container.
	 * This is ment for Timeouts for Showing the Display.
	 */
	public void tick(){
		boolean removeScoreboard = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_disableAllScoreboardOutputs();
		if(removeScoreboard) return;
		
		if(displayTimer.shouldBeHidden()) hide();
	}
	

	/**
	 * Cycles to the next View:
	 */
	public void cycleToNext() {
		SBCategory nextCategory = selectedCategory.getNextCategory();
		updateSelectAndShow(nextCategory);
	}
	

	public enum SBCategory{
		
		Cooldown(ChatColor.RED + "Cooldowns:"),
		Spells(ChatColor.GREEN + "Spells:"),
		Arrows(ChatColor.AQUA + "Arrows:"),
		General(ChatColor.YELLOW + "General:"),
		Group(ChatColor.DARK_BLUE + "Group:");
		
		
		/**
		 * The Headline to use.
		 */
		private final String headline;
		
		
		private SBCategory(String headline) {
			this.headline = headline;
		}
		
		
		public String getHeadline() {
			return headline;
		}
		
		
		/**
		 * Returns the Next category.
		 */
		public SBCategory getNextCategory(){
			switch (this) {
				case Cooldown : return Spells;
				case Spells : return Arrows;
				case Arrows : return General;
				case General : return Group;
				case Group : return Cooldown;
				
				default: return General;
			}
		}
	}




	/**
	 * Returns the Updater for the Manager.
	 * 
	 * @return updater.
	 */
	public ScoreboardUpdater getUpdater() {
		return updater;
	}
	
	
}
