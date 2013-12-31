package de.tobiyas.racesandclasses.util.bukkit.versioning;

public class CertainVersionChecker {

	/**
	 * Checks if the Bukkit version is above the passed version
	 * 
	 * @param version to check for
	 * @return true if is above, false otherwise
	 */
	public static boolean bukkitVersionIsAbove(int mainVersion, int subVersion, int subSubVersion, int mainRevision, int subRevision){
		BukkitVersion version = BukkitVersionBuilder.getbukkitBuildNumber();
		BukkitVersion checkVersion = new BukkitVersion(mainVersion, subVersion, subSubVersion, mainRevision, subRevision);
		
		return version.isGreater(checkVersion);
	}
	
	
	
	/**
	 * Checks if the Bukkit version is above Bukkit 1.6
	 * 
	 * @return true if Bukkit version is above 1.7
	 */
	public static boolean isAbove1_6(){
		return bukkitVersionIsAbove(1, 6, 0, 0, 0);
	}



	/**
	 * Checks if the Bukkit version is above Bukkit 1.7
	 * 
	 * @return true if Bukkit version is above 1.7
	 */
	public static boolean isAbove1_7(){
		return bukkitVersionIsAbove(1, 7, 0, 0, 0);
	}
}
