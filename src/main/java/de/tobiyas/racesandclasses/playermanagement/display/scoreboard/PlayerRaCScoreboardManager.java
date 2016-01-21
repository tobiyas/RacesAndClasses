package de.tobiyas.racesandclasses.playermanagement.display.scoreboard;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
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
	 * The Scoreboard to use.
	 */
	private final Scoreboard scoreboard;
	
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
		this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		this.updater = new ScoreboardUpdater(player, this);
		this.displayTimer = new DisplayTimer();
		
		//Create Scoreboard + other stuff.
		String sbPre = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_gui_scoreboard_name();
		sbPre = ChatColor.translateAlternateColorCodes('&', sbPre);
		
		for(SBCategory category : SBCategory.values()){
			toShow.put(category, new HashMap<String,Integer>());
			
			//Creates an Object for the Category.
			Objective objective = scoreboard.getObjective(category.name());
			if(objective == null) {
				objective = scoreboard.registerNewObjective(category.name(), "dummy");
				objective.setDisplayName(sbPre + " " + category.getHeadline());
			}
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
		if(!needsUpdate) return;
		
		//First clear the old scoreboard
		for(String entry : scoreboard.getEntries()){
			scoreboard.resetScores(entry);
		}
		
		//now repopulate it.
		for(SBCategory category : SBCategory.values()){
			Objective objective = scoreboard.getObjective(category.name());

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
		if(key.length() > 16 
				&& !VollotileCodeManager.getVollotileCode().getVersion().isVersionGreaterOrEqual(MCVersion.v1_8_R1)){
			
			key = key.substring(0,16);
		}
		
		return key;
	}
	
	
	/**
	 * Shows the Scoreboard.
	 */
	public void show(){
		//If nothing is present, hide.
		if(displayTimer.shouldBeHidden() 
				|| toShow.get(this.selectedCategory).size() == 0){
			
			hide();
			return;
		}
		
		//Only show if wanted.
		boolean removeScoreboard = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_disableAllScoreboardOutputs();
		if(!removeScoreboard){
			//Set the correct Objective to display.
			Objective obj = scoreboard.getObjective(this.selectedCategory.name());
			obj.setDisplaySlot(DisplaySlot.SIDEBAR);
			
			player.setScoreboard(scoreboard);
		}
	}
	
	
	/**
	 * Hides the Display and shows the Main board again.
	 */
	public void hide(){
		//If not our board -> nothing to do.
		if(player.getScoreboard() != scoreboard) return;
		
		Scoreboard mainBoard = Bukkit.getScoreboardManager().getMainScoreboard();
		player.setScoreboard(mainBoard);
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
		boolean inf = time >= Long.MAX_VALUE;
		
		displayTimer.setAlwaysShow(inf);
		displayTimer.addHideDelay(time);
	}

	
	/**
	 * Ticks the Container.
	 * This is ment for Timeouts for Showing the Display.
	 */
	public void tick(){
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
		General(ChatColor.YELLOW + "General:");
		
		
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
				case General : return Cooldown;
				
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
