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
		HEALTH("Health", ChatColor.RED, ChatColor.YELLOW, ChatColor.GREEN);

		/**
		 * Display Name
		 */
		private final String name;
		
		//Colors of the bar
		private final ChatColor lowValueColor;
		private final ChatColor midValueColor;
		private final ChatColor highValueColor;
		
		private DisplayInfos(String name, ChatColor lowValueColor, ChatColor midValueColor, ChatColor highValueColor) {
			this.name = name;
			this.lowValueColor = lowValueColor;
			this.midValueColor = midValueColor;
			this.highValueColor = highValueColor;
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
	}
}
