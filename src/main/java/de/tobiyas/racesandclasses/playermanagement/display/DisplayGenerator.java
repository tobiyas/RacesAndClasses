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

import de.tobiyas.racesandclasses.playermanagement.display.Display.DisplayInfos;
import de.tobiyas.racesandclasses.playermanagement.display.Display.DisplayType;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.util.vollotile.VollotileCodeManager;

public class DisplayGenerator {

	
	/**
	 * Creates a Display for the passed infos and Player.
	 * 
	 * @param player to create
	 * @param infos to create
	 * 
	 * @return the generated Display
	 */
	public static Display generateDisplay(RaCPlayer player, DisplayInfos infos, String prefered){
		String displayType = prefered == null || prefered.isEmpty() ? "none" : prefered;
		DisplayType type = DisplayType.resolve(displayType);
		return generateFromType(type, player, infos);
	}
	
	/**
	 * 
	 * 
	 * @param player
	 * @param infos
	 * 
	 * @return
	 */
	public static Display generateDisplay(RaCPlayer player, DisplayInfos infos){
		return generateDisplay(player, infos, "");
	}
	
	
	/**
	 * Generates the Display finally for the passed args.
	 * 
	 * @param type to create
	 * @param name the player to create to
	 * @param infos the infos for the Display to configure
	 * 
	 * @return the generated Display
	 */
	private static Display generateFromType(DisplayType type, RaCPlayer player, DisplayInfos infos){
		if(type == DisplayType.Actionbar 
				&& !VollotileCodeManager.getVollotileCode().getVersion().hasActionBar()){
			
			type = DisplayType.Chat;
		}
		
		switch (type) {
		case Chat:
			return new ChatDisplayBar(player, infos);
		
		case None:
			return new NoneDisplayBar(player, infos);
		
		case Actionbar:
			return new ActionBarDisplay(player, infos);
			
		default:
			return new ChatDisplayBar(player, infos);
		}
	}
}
