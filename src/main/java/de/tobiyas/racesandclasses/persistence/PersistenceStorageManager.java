package de.tobiyas.racesandclasses.persistence;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.persistence.db.EBeanPersistenceStorage;
import de.tobiyas.racesandclasses.persistence.file.YAMLPersistenceStorage;

public class PersistenceStorageManager {

	
	/**
	 * The Persistence of the Plugin.
	 */
	private static PersistenceStorage persistence;
	
	
	
	/**
	 * Returns the Storage of the Plugin.
	 * 
	 * @return {@link PersistenceStorage} of the Plugin.
	 */
	public static PersistenceStorage getStorage(){
		if(persistence == null){
			initPersistence();
		}
		
		return persistence;
	}



	/**
	 * Inits the Persistence of the Plugin
	 */
	private static void initPersistence() {
		RacesAndClasses plugin = RacesAndClasses.getPlugin();
		boolean useDB = plugin.getConfigManager().getGeneralConfig().isConfig_savePlayerDataToDB();
		
		if(useDB){
			persistence = new EBeanPersistenceStorage();
		}else{
			persistence = new YAMLPersistenceStorage();
		}
		
		persistence.initForStartup();
	}
	
	
	
	/**
	 * Closes the Persistence.
	 */
	public static void shutdownPersistence(){
		if(persistence != null){
			persistence.shutDown();
		}
	}


	/**
	 * Forces a startup of the Persistence.
	 */
	public static void startup() {
		if(persistence != null){
			initPersistence();
		}
	}
	
	
}
