package de.tobiyas.races.datacontainer.traitcontainer;

import de.tobiyas.races.Races;

public class DoubleEventRemover implements Runnable {

	private TraitEventManager manager;
	
	public DoubleEventRemover(TraitEventManager manager){
		this.manager = manager;
		Races.getPlugin().getServer().getScheduler().scheduleSyncRepeatingTask(Races.getPlugin(), this, 2, 20);
	}
	
	@Override
	public void run() {
		manager.cleanEventList();
	}

}
