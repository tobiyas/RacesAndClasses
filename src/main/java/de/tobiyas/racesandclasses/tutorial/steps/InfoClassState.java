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
package de.tobiyas.racesandclasses.tutorial.steps;

import java.util.UUID;

import org.bukkit.ChatColor;

import de.tobiyas.racesandclasses.tutorial.TutorialPath;
import de.tobiyas.racesandclasses.util.tutorial.TutorialState;

public class InfoClassState extends AbstractStep{

	public InfoClassState(UUID playerUUID, TutorialPath writeBack){
		this.playerUUID = playerUUID;
		this.writeBack = writeBack;
		this.currentState = TutorialState.infoClass;
		
		this.startMessage = ChatColor.YELLOW + "[Tutorial]: Now to Class Selection. First you have to inform yourself for which Classes there are. This can be done with:" +
				ChatColor.RED + " /class list" + ChatColor.YELLOW + ". To get to the next step, you need to use this command.";
		this.doneMessage = ChatColor.YELLOW + "[Tutorial]: Very good!";
		
		finished = false;
		postStartMessage();
	}
}
