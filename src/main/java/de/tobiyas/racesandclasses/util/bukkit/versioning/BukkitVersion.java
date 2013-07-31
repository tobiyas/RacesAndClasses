package de.tobiyas.racesandclasses.util.bukkit.versioning;

public class BukkitVersion {

	private final int bukkitMainVersion;
	private final int bukkitSubVersion;
	private final int bukkitSubSubVersion;
	
	private final int revisionMainVersion;
	private final int revisionSubVersion;
	
	
	private BukkitVersion(){
		this.bukkitMainVersion = -1;
		this.bukkitSubVersion = -1;
		this.bukkitSubSubVersion = -1;
		
		this.revisionMainVersion = -1;
		this.revisionSubVersion = -1;
	}
	
	/**
	 * generate with all fields
	 */
	public BukkitVersion(int bukkitMainVersion, int bukkitSubVersion, int bukkitSubSubVersion, int revisionMainVersion, int revisionSubVersion){
		this.bukkitMainVersion = bukkitMainVersion;
		this.bukkitSubVersion = bukkitSubVersion;
		this.bukkitSubSubVersion = bukkitSubSubVersion;
		
		this.revisionMainVersion = revisionMainVersion;
		this.revisionSubVersion = revisionSubVersion;
	}
	
	
	
	/**
	 * Returns an empty bukkit Version.
	 * All Properties set to -1.
	 * @return
	 */
	public static BukkitVersion empty() {
		return new BukkitVersion();
	}

	public int getBukkitMainVersion() {
		return bukkitMainVersion;
	}

	public int getBukkitSubVersion() {
		return bukkitSubVersion;
	}

	public int getBukkitSubSubVersion() {
		return bukkitSubSubVersion;
	}

	public int getRevisionMainVersion() {
		return revisionMainVersion;
	}

	public int getRevisionSubVersion() {
		return revisionSubVersion;
	}

	
	/**
	 * Returns a related number to the Bukkit build.
	 * 
	 * @return
	 */
	public int getTotalNumber(){
		return revisionSubVersion + (revisionMainVersion * 10) + (bukkitSubSubVersion * 100) 
				+ (bukkitSubVersion * 1000) + (bukkitMainVersion * 10000);
	}

	
	/**
	 * Returns true if the version is greater than the passed Version.
	 * 
	 * @param checkVersion
	 * @return
	 */
	public boolean isGreater(BukkitVersion checkVersion) {
		return getTotalNumber() > checkVersion.getTotalNumber();
	}
}
