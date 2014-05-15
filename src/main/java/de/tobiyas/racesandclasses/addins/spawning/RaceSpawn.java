package de.tobiyas.racesandclasses.addins.spawning;

import org.bukkit.Location;

public class RaceSpawn {

	/**
	 * The Race.
	 */
	private final String race;
	
	/**
	 * The Spawn location for this race.
	 */
	private Location spawnLocation;
	
	public RaceSpawn(String race, Location location) {
		this.race = race;
		this.spawnLocation = location;
	}

	
	public Location getSpawnLocation() {
		return spawnLocation;
	}

	public void setSpawnLocation(Location spawnLocation) {
		this.spawnLocation = spawnLocation;
	}

	public String getRace() {
		return race;
	}
}
