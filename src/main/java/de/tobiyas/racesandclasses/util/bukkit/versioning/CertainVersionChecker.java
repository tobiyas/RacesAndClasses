package de.tobiyas.racesandclasses.util.bukkit.versioning;

public class CertainVersionChecker {

	/**
	 * Checks if the Bukkit version is above the passed version
	 * 
	 * @param version
	 * @return
	 */
	public static boolean bukkitVersionIsAbove(int mainVersion, int subVersion, int subSubVersion, int mainRevision, int subRevision){
		BukkitVersion version = BukkitVersionBuilder.getbukkitBuildNumber();
		BukkitVersion checkVersion = new BukkitVersion(mainVersion, subVersion, subSubVersion, mainRevision, subRevision);
		
		return version.isGreater(checkVersion);
	}
	
	
	
	/**
	 * Checks if the Bukkit version is above Bukkit 1.6
	 * 
	 * @return
	 */
	public static boolean isAbove1_6(){
		return bukkitVersionIsAbove(1, 6, 0, 0, 0);
	}
}
