package de.tobiyas.racesandclasses.addins.groups.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.addins.groups.GroupManager;
import me.alessiodp.parties.Parties;
import me.alessiodp.parties.utils.api.Api;
import me.alessiodp.parties.utils.api.Status;

public class PartiesGroupManager implements GroupManager {

	
	@Override
	public boolean isInSameGroup(Player player1, Player player2) {
		String party1 = getParty(player1.getUniqueId());
		String party2 = getParty(player2.getUniqueId());
		return party1 != null && party2 != null && !party1.isEmpty() && !party2.isEmpty() && party1.equals(party2);
	}

	@Override
	public boolean hasGroup(Player player) {
		return getAPI().haveParty(player.getUniqueId());
	}

	@Override
	public boolean addPlayerToGroup(Player groupOwner, Player toAdd) {
		String ownerParty = getParty(groupOwner.getUniqueId());
		if(ownerParty == null || ownerParty.isEmpty()) return false;
		
		return getAPI().addPlayerInParty(toAdd, ownerParty) == Status.SUCCESS;
	}

	@Override
	public boolean playerLeavesGroup(Player player) {
		return getAPI().removePlayerFromParty(player.getUniqueId()) == Status.SUCCESS;
	}

	@Override
	public Collection<Player> getMembersOfGroup(Player player) {
		String party = getParty(player.getUniqueId());
		if(party == null || party.isEmpty()) return new HashSet<>();
		
		return getAPI().getPartyOnlinePlayers(party);
	}

	@Override
	public Player getLeaderOfGroup(Player player) {
		String party = getParty(player.getUniqueId());
		if(party == null || party.isEmpty()) return null;
		
		Collection<UUID> ids = getAPI().getPartyLeaders(party);
		if(ids == null || ids.isEmpty()) return null;
		
		return Bukkit.getPlayer(ids.iterator().next());
	}

	@Override
	public boolean isEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled("Parties");
	}
	

	@Override
	public void deinit() {}
	
	
	private String getParty(UUID id){
		return getAPI().getPartyName(id);
	}

	
	private Api getAPI(){
		return Parties.getApi();
	}
	
}
