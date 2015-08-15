package de.tobiyas.racesandclasses.addins.spawning;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.util.config.YAMLConfigExtended;

public class RaceSpawnManager {

	/**
	 * The Set of spawns.
	 */
	private final Set<RaceSpawn> spawns = new HashSet<RaceSpawn>();
	
	private final File saveDir;
	private final File saveFile;
	
	private final YAMLConfigExtended config;
	
	
	public RaceSpawnManager(RacesAndClasses plugin) {
		File addinFolder = new File(plugin.getDataFolder(), "addins");
		this.saveDir = new File(addinFolder, "spawns");
		this.saveFile = new File(saveDir, "spawns.yml");
		
		if(!addinFolder.exists()) addinFolder.mkdirs();
		if(!saveDir.exists()) saveDir.mkdirs();
		if(!saveFile.exists()) try{ saveFile.createNewFile(); }catch(IOException exp){}
		
		config = new YAMLConfigExtended(saveFile);
	}
	
	/**
	 * loads all spawns.
	 */
	public void load(){
		spawns.clear();
		config.load();
		
		for(String rootChild : config.getRootChildren()){
			Location spawnLoc = config.getLocation(rootChild + ".spawn");
			String race = config.getString(rootChild + ".race");
			
			if(spawnLoc != null && race != null ) {
				RaceSpawn spawn = new RaceSpawn(race, spawnLoc);
				spawns.add(spawn);
			}
		}
	}
	
	
	/**
	 * Saves the config for the Spawns.
	 * 
	 * @param async if sync or not.
	 */
	public void save(boolean async){
		config.clearConfig();
		for(RaceSpawn spawn : spawns){
			String race = spawn.getRace();
			Location loc = spawn.getSpawnLocation();
			
			config.set(race + ".race", race);
			config.set(race + ".spawn", loc);
		}
		
		if(async) config.saveAsync(); else config.save();
	}
	
	
	/**
	 * Sets a race spawn.
	 * 
	 * @param race to set to
	 * @param loc to set
	 */
	public void setRaceSpawn(String race, Location loc){
		if(race == null || loc == null) return;
		
		for(RaceSpawn spawn : spawns){
			if(spawn.getRace().equalsIgnoreCase(race)){
				spawns.remove(spawn);
				break;
			}
		}
		
		spawns.add(new RaceSpawn(race, loc));
	}
	
	
	/**
	 * Returns the Spawn for the Race container.
	 * Can be null if no Spawn found.
	 * 
	 * @param race to search for.
	 * 
	 * @return the location to spawn. Null if not present
	 */
	public Location getSpawnForRace(String race){
		for(RaceSpawn spawn : spawns){
			if(spawn.getRace().equalsIgnoreCase(race)){
				return spawn.getSpawnLocation();
			}
		}
		
		return null;
	}
}
