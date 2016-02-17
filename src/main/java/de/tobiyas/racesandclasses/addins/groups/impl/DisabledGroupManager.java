package de.tobiyas.racesandclasses.addins.groups.impl;

import java.util.Arrays;
import java.util.Collection;

import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.addins.groups.GroupManager;

public class DisabledGroupManager implements GroupManager {

	@Override
	public boolean isInSameGroup(Player player1, Player player2) {
		return false;
	}

	@Override
	public boolean hasGroup(Player player) {
		return false;
	}

	@Override
	public boolean addPlayerToGroup(Player groupOwner, Player toAdd) {
		return false;
	}

	@Override
	public boolean playerLeavesGroup(Player player) {
		return false;
	}
	
	@Override
	public boolean isEnabled() {
		return false;
	}

	@Override
	public Player getLeaderOfGroup(Player player) {
		return player;
	}
	
	@Override
	public Collection<Player> getMembersOfGroup(Player player) {
		return Arrays.asList(player);
	}
	
	@Override
	public void deinit() {}
	
}
