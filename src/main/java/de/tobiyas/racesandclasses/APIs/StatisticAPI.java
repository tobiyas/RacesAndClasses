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
}
