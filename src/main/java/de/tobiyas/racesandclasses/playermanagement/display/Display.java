package de.tobiyas.racesandclasses.playermanagement.display;

import org.bukkit.ChatColor;


public interface Display {
	
	/**
	 * Displays the passed Health to the player
	 * 
	 * @param currentHealth the health of the player
	 * @param maxHealth the maximal health the player can have
	 */
	public void display(double currentHealth, double maxHealth);
	
	
	/**
	 * This is called when the Display is thrown away.
	 */
	public void unregister();
	
	/**
	 * This shows the different Types of Displays
	 * 
	 * @author Tobiyas
	 *
	 */
	public enum DisplayType{
		
		/**
		 * Shows the Display in the Player Chat.
		 */
		Chat,
		
		
		/**
		 * Shows the display on the right side as ScoreBoard.
		 */
		Scoreboard;
		
		
		/**
		 * Resolves the DisplayType from the passed String
		 * 
		 * @param name to parse
		 * @return the DisplayType
		 */
		public static DisplayType resolve(String name){
			name = name.toLowerCase();
			if(name.contains("chat")){
				return Chat;
			}
			
			if(name.contains("score") || name.contains("board")){
				return Scoreboard;
			}
				
				
			return Chat;	
		}
	}
	
	public enum DisplayInfos{
		MANA("Mana", ChatColor.WHITE, ChatColor.DARK_AQUA, ChatColor.BLUE),
		HEALTH("Health", ChatColor.RED, ChatColor.YELLOW, ChatColor.GREEN),
		LEVEL_EXP("Exp", ChatColor.BLACK, ChatColor.AQUA, ChatColor.YELLOW),
		LEVEL("Level", ChatColor.BLACK, ChatColor.AQUA, ChatColor.YELLOW, true, true);

		/**
		 * Display Name
		 */
		private final String name;
		
		//Colors of the bar
		private final ChatColor lowValueColor;
		private final ChatColor midValueColor;
		private final ChatColor highValueColor;
		private final boolean useName;
		private final boolean onlyUseOneValue;
		
		private DisplayInfos(String name, ChatColor lowValueColor, ChatColor midValueColor, ChatColor highValueColor) {
			this(name, lowValueColor, midValueColor, highValueColor, false, false);
		}

		private DisplayInfos(String name, ChatColor lowValueColor, ChatColor midValueColor, 
				ChatColor highValueColor, boolean useName, boolean onlyUseOneValue) {
			this.name = name;
			this.lowValueColor = lowValueColor;
			this.midValueColor = midValueColor;
			this.highValueColor = highValueColor;
			this.useName = useName;
			this.onlyUseOneValue = onlyUseOneValue;
		}

		public String getName() {
			return name;
		}

		public ChatColor getLowValueColor() {
			return lowValueColor;
		}

		public ChatColor getMidValueColor() {
			return midValueColor;
		}

		public ChatColor getHighValueColor() {
			return highValueColor;
		}
		
		public boolean useName(){
			return useName;
		}
		
		public boolean onlyUseOneValue(){
			return onlyUseOneValue;
		}
	}
}
