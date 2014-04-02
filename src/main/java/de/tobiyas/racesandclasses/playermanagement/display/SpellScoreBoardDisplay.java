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

public class SpellScoreBoardDisplay extends AbstractDisplay {

	public SpellScoreBoardDisplay(UUID player, DisplayInfos displayInfo) {
		super(player, displayInfo);

	
	}

	@Override
	public void display(double currentHealth, double maxHealth) {
		if(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_disableAllScoreboardOutputs()){
			//server does not want ANY Scoreboards from RaC. 
			//Plugin is sad. :(
			return;
		}
		
		
		
	}

}
