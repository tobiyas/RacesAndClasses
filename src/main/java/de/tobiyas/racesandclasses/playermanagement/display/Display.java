/*******************************************************************************
 * Copyright 2014 Tobias Welther
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
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
		 * Shows the Display in the Action bar.
		 */
		Actionbar,
		
		/**
		 * Show on non Display.
		 */
		None;
		
		
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

			if(name.contains("action")){
				return Actionbar;
			}
			
			if(name.contains("none")){
				return None;
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
