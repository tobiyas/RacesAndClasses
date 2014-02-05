package de.tobiyas.racesandclasses.util.permissions;


public interface PermissionChecker {

	public enum PermState{
		FORCE_PERMISE,
		FORCE_DECLINE,
		NOT_INTERESTED;
	}
	
	
	/**
	 * Checks the Permission of a player
	 *
	 * @param player to check
	 * @param world the World the permission is checked.
	 * @param permission to check
	 * 
	 * @return the PermState wanted.
	 */
	public PermState checkPermissions(String player, String world, String permission);
}
