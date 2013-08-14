package de.tobiyas.racesandclasses.healthmanagement.display;


public interface HealthDisplay {
	
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
		Scoreboard,
		
		
		/**
		 * Shows the Display on Spout style
		 * 
		 * @deprecated should not be used since it is not implemented.
		 */
		Spout;
		
		
		
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
			
			if(name.contains("spout")){
				return Spout;
			}
			
			if(name.contains("score") || name.contains("board")){
				return Scoreboard;
			}
				
				
			return Chat;	
		}
	}
}
