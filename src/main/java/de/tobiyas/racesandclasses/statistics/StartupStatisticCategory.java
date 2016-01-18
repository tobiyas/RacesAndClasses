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
package de.tobiyas.racesandclasses.statistics;

public enum StartupStatisticCategory {

	TraitCopy,
	
	ManagerConstructor,
	CooldownManager,
	
	TraitManager,
	RaceManager,
	ClassManager,
	
	PlayerManager,
	ChannelManager,
	
	ConfigTotal,
	PlayerConfigs,
	Conversion;

	
	/**
	 * The Time the Component needed for startup in Milliseconds.
	 */
	public long timeInMiliSeconds;
	
	
	private StartupStatisticCategory() {
		timeInMiliSeconds = -1;
	}
}
