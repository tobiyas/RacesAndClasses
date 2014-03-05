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
package de.tobiyas.racesandclasses.APIs;

import java.util.Map;

import de.tobiyas.racesandclasses.RacesAndClasses;

public class StatisticAPI {

	private static RacesAndClasses plugin = RacesAndClasses.getPlugin();
	
	/**
	 * Returns the time this trait has been triggered.
	 * Returns -1 if the Trait does not exist.
	 * 
	 * @param traitName name of the trait
	 * @return the number of triggers
	 */
	public static long getTotalTriggersOfTrait(String traitName){
		Map<String, Long> triggerMap = plugin.getStatistics().getTraitsTriggersTotal();
		
		String realTraitName = null;
		for(String rTraitName : triggerMap.keySet()){
			if(rTraitName.equalsIgnoreCase(traitName)){
				realTraitName = rTraitName;
			}
		}
		
		if(realTraitName == null){
			return -1l;
		}else{
			return triggerMap.get(realTraitName);
		}
	}
	
	/**
	 * Returns the time this trait has been triggered calculated to one Minute.
	 * Returns -1 if the Trait does not exist.
	 * 
	 * @param traitName name of the trait
	 * @return the number of triggers / Minute
	 */
	public static double getTotalTriggersOfTraitPerMinute(String traitName){
		long triggers = getTotalTriggersOfTrait(traitName);
		if(triggers < 0){
			return -1;
		}
		
		double timeTotal = (double)plugin.getStatistics().getTimeRunning() / 60000d;
		
		double eventsPerM = (double)triggers / (double)timeTotal;
		return eventsPerM;
	}
	
	
	/**
	 * Returns the time the plugin runs as String
	 * @return
	 */
	public static String getTimeRunning(){
		return plugin.getStatistics().getTimeRunningAsString();
	}
	
	
	/**
	 * Returns the total number of Events running through the internal Event processor.
	 * 
	 * @return
	 */
	public static long getEventsTriggeredTotal(){
		return plugin.getStatistics().getEventsTriggeredTotal();
	}

	
	/**
	 * Returns the Events per minute the system is triggering.
	 * This is the total amount of events synced on one minute.
	 * 
	 * @return
	 */
	public static double getEventsTotalPerMinute(){
		long numberOfEvents = plugin.getStatistics().getEventsTriggeredTotal();
		double timeTotal = (double)plugin.getStatistics().getTimeRunning() / 60000d;
		
		double eventsPerM = (double)numberOfEvents / (double)timeTotal;
		return eventsPerM;
	}
	
	
	/**
	 * Returns the Time ALL Traits needed together
	 * 
	 * @return the time ALL traits needed
	 */
	public static long getTotalTraitsUsedTime(){
		Map<String, Long> timeMap = plugin.getStatistics().getTimeNeededTotal();
		
		long time = 0;
		for(long timePerTrait : timeMap.values()){
			time += timePerTrait;
		}
		
		return time;
	}
	
	/**
	 * Returns the Time the Trait passed needed together.
	 * Returning -1 means that the Trait does not exist or did not use any time yet.
	 * 
	 * @param traitName the Trait name
	 * 
	 * @return the time the trait needed
	 */
	public static long getTraitsUsedTime(String traitName){
		Map<String, Long> timeMap = plugin.getStatistics().getTimeNeededTotal();
		
		String realTraitName = null;
		for(String rTraitName : timeMap.keySet()){
			if(rTraitName.equalsIgnoreCase(traitName)){
				realTraitName = rTraitName;
			}
		}
		
		if(realTraitName == null){
			return -1;
		}else{
			return timeMap.get(realTraitName);
		}
	}
}
