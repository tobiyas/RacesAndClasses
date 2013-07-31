package de.tobiyas.racesandclasses.util.bukkit.versioning;

import org.bukkit.Bukkit;

public class BukkitVersionBuilder {

	/**
	 * Returns the build number of the CraftBukkit build.
	 * 
	 * If it could not be read, {@link BukkitVersion#empty()} is returned.
	 * @return
	 */
	public static BukkitVersion getbukkitBuildNumber(){
		String bukkitVersion = Bukkit.getServer().getBukkitVersion();
		String[] bukkitVersionSplit = bukkitVersion.split("-");
		if(bukkitVersionSplit.length < 2){
			return BukkitVersion.empty();
		}
		
		String[] minecraftVersionSplit = bukkitVersionSplit[0].split("\\.");
		if(minecraftVersionSplit.length < 3){
			return BukkitVersion.empty();
		}
		
		String[] revisionVersionSplit = bukkitVersionSplit[1].split("\\.");
		if(revisionVersionSplit.length < 2){
			return BukkitVersion.empty();
		}
		
		try{
			int bukkitMainVersion = Integer.parseInt(minecraftVersionSplit[0]);
			int bukkitSubVersion = Integer.parseInt(minecraftVersionSplit[1]);
			int bukkitSubSubVersion = Integer.parseInt(minecraftVersionSplit[2]);
			
			int revisionMainVersion = Integer.parseInt(revisionVersionSplit[0].replace("R", ""));
			int revisionSubVersion = Integer.parseInt(revisionVersionSplit[1]);
			
			return new BukkitVersion(bukkitMainVersion, bukkitSubVersion, bukkitSubSubVersion, revisionMainVersion, revisionSubVersion);
		}catch(NumberFormatException exp){
			return BukkitVersion.empty();
		}
	}

}
