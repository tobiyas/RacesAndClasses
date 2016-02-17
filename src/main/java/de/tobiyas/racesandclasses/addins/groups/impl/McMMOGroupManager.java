package de.tobiyas.racesandclasses.addins.groups.impl;

import java.util.Collection;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.gmail.nossr50.api.PartyAPI;
import com.gmail.nossr50.datatypes.party.Party;

import de.tobiyas.racesandclasses.addins.groups.GroupManager;

public class McMMOGroupManager implements GroupManager {

	@Override
	public boolean isInSameGroup(Player player1, Player player2) {
		Party party1 = getParty(player1);
		Party party2 = getParty(player2);
		
		return party1 != null && party2 != null && party1 == party2;
	}

	@Override
	public boolean hasGroup(Player player) {
		return getParty(player) != null;
	}

	@Override
	public boolean addPlayerToGroup(Player groupOwner, Player toAdd) {
		Party party = getParty(groupOwner);
		if(party == null) return false;

		party.addOnlineMember(toAdd);
		return true;
	}

	@Override
	public boolean playerLeavesGroup(Player player) {
		Party party = getParty(player);
		if(party == null) return false;
		
		party.removeOnlineMember(player);
		return true;
	}

	@Override
	public Collection<Player> getMembersOfGroup(Player player) {
		Party party = getParty(player);
		return party == null ? new HashSet<Player>() : party.getOnlineMembers();
	}

	@Override
	public Player getLeaderOfGroup(Player player) {
		Party party = getParty(player);
		return party == null ? null : Bukkit.getPlayer(party.getLeader().getUniqueId());
	}

	@Override
	public boolean isEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled("McMMO");
	}

	@Override
	public void deinit() {}

	
	private Party getParty(Player player){
		for(Party party : PartyAPI.getParties()){
			if(party.getOnlineMembers().contains(player)) return party;
		}
		
		return null;
	}
	
}
