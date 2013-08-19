package de.tobiyas.racesandclasses.statistics;

public enum StartupStatisticCategory {

	TraitCopy,
	
	ManagerConstructor,
	TutorialManager,
	CooldownManager,
	
	TraitManager,
	RaceManager,
	ClassManager,
	
	PlayerManager,
	ChannelManager,
	
	Config;

	
	/**
	 * The Time the Component needed for startup in Milliseconds.
	 */
	public long timeInMiliSeconds;
	
	
	private StartupStatisticCategory() {
		timeInMiliSeconds = -1;
	}
}
