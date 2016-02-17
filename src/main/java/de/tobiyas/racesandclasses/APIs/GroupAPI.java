package de.tobiyas.racesandclasses.APIs;

import java.util.Collection;

import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.addins.groups.GroupManagerProvider;

public class GroupAPI {

	
	/**
	 * If the Player is in the Same Group.
	 * @param player1 to check
	 * @param player2 to check.
	 * 
	 * @return true if is in same Group.
	 */
	public static boolean isInSameGroup(Player player1, Player player2) {
		return GroupManagerProvider.get().getProvider().isInSameGroup(player1, player2);
	}

	/**
	 * If the player has a group.
	 * @param player to check.
	 * @return true if has a group.
	 */
	public static boolean hasGroup(Player player) {
		return GroupManagerProvider.get().getProvider().hasGroup(player);
	}
	
	/**
	 * Adds a player to the Group.
	 * @param groupOwner to use.
	 * @param toAdd the player to add.
	 * @return true if worked.
	 */
	public static boolean addPlayerToGroup(Player groupOwner, Player toAdd) {
		return GroupManagerProvider.get().getProvider().addPlayerToGroup(groupOwner, toAdd);
	}

	/**
	 * The player leaves the Group.
	 * @param player that leaves
	 * @return true if worked.
	 */
	public static boolean playerLeavesGroup(Player player) {
		return GroupManagerProvider.get().getProvider().playerLeavesGroup(player);
	}

	/**
	 * Gets the Members of the Group of this player.
	 * @param player to check for
	 * @return the players that are in the Group.
	 */
	public static Collection<Player> getMembersOfGroup(Player player) {
		return GroupManagerProvider.get().getProvider().getMembersOfGroup(player);
	}

	/**
	 * Gets the Leader of the Group of the player.
	 * @param player to check for
	 * @return the leader.
	 */
	public static Player getLeaderOfGroup(Player player) {
		return GroupManagerProvider.get().getProvider().getLeaderOfGroup(player);
	}

	/**
	 * If the System is enabled.
	 * @return true if is enabled.
	 */
	public static boolean isEnabled() {
		return GroupManagerProvider.get().getProvider().isEnabled();
	}
	
}
