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

import java.util.UUID;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.configuration.member.file.MemberConfig;
import de.tobiyas.racesandclasses.playermanagement.display.Display.DisplayInfos;
import de.tobiyas.racesandclasses.playermanagement.display.Display.DisplayType;

public class DisplayGenerator {

	
	/**
	 * Creates a Display for the passed infos and Player.
	 * 
	 * @param playerUUID to create
	 * @param infos to create
	 * 
	 * @return the generated Display
	 */
	public static Display generateDisplay(UUID playerUUID, DisplayInfos infos){
		RacesAndClasses plugin = RacesAndClasses.getPlugin();
		
		MemberConfig config = plugin.getConfigManager().getMemberConfigManager().getConfigOfPlayer(playerUUID);
		Object displayTypeAsObject = config.getValueDisplayName("displayType");
		String displayType = "score";
		
		boolean disableScoreboard = plugin.getConfigManager().getGeneralConfig().isConfig_disableAllScoreboardOutputs();
				
		if(displayTypeAsObject != null && displayTypeAsObject instanceof String){
			displayType = (String) displayTypeAsObject;
		}
		
		DisplayType type = DisplayType.resolve(displayType);
		if(disableScoreboard && type == DisplayType.Scoreboard){
			type = DisplayType.Chat;
		}
		
		return generateFromType(type, playerUUID, infos);
	}
	
	
	/**
	 * Generates the Display finally for the passed args.
	 * 
	 * @param type to create
	 * @param name the playerUUID to create to
	 * @param infos the infos for the Display to configure
	 * 
	 * @return the generated Display
	 */
	private static Display generateFromType(DisplayType type, UUID playerUUID, DisplayInfos infos){
		switch (type) {
		case Chat:
			return new ChatDisplayBar(playerUUID, infos);
		
		case Scoreboard:
			return new NewScoreBoardDisplayBar(playerUUID, infos);
			
		default:
			return new ChatDisplayBar(playerUUID, infos);
		}
	}
}
