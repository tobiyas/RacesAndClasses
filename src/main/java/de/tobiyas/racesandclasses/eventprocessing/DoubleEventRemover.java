package de.tobiyas.racesandclasses.eventprocessing;

import de.tobiyas.racesandclasses.RacesAndClasses;

public class DoubleEventRemover implements Runnable {

	private TraitEventManager manager;
	
	public DoubleEventRemover(TraitEventManager manager){
		this.manager = manager;
		RacesAndClasses.getPlugin().getServer().getScheduler().scheduleSyncRepeatingTask(RacesAndClasses.getPlugin(), this, 2, 20);
	}
	
	@Override
	public void run() {
		manager.cleanEventList();
	}

}
