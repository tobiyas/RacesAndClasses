package de.tobiyas.racesandclasses.addins.groups;

import java.util.Collection;

import org.bukkit.entity.Player;

public interface GroupManager {

	/**
	 * If the Player is in the Same Group.
	 * @param player1 to check
	 * @param player2 to check.
	 * 
	 * @return true if is in same Group.
	 */
	public boolean isInSameGroup(Player player1, Player player2);
	
	/**
	 * If the player has a group.
	 * @param player to check.
	 * @return true if has a group.
	 */
	public boolean hasGroup(Player player);
	
	/**
	 * Adds a player to the Group.
	 * @param groupOwner to use.
	 * @param toAdd the player to add.
	 * @return true if worked.
	 */
	public boolean addPlayerToGroup(Player groupOwner, Player toAdd);
	
	/**
	 * The player leaves the Group.
	 * @param player that leaves
	 * @return true if worked.
	 */
	public boolean playerLeavesGroup(Player player);
	
	/**
	 * Gets the Members of the Group of this player.
	 * @param player to check for
	 * @return the players that are in the Group.
	 */
	public Collection<Player> getMembersOfGroup(Player player);
	
	/**
	 * Gets the Leader of the Group of the player.
	 * @param player to check for
	 * @return the leader.
	 */
	public Player getLeaderOfGroup(Player player);
	
	/**
	 * If the System is enabled.
	 * @return true if is enabled.
	 */
	public boolean isEnabled();
	
}
