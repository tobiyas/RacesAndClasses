package de.tobiyas.racesandclasses.addins.groups.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.addins.groups.GroupManager;

public class OwnGroupManager implements GroupManager {

	/**
	 * The Own groups.
	 */
	private Set<OwnGroup> groups = new HashSet<>();
	
	
	@Override
	public boolean isInSameGroup(Player player1, Player player2) {
		if(player1 == null || player2 == null) return false;
		
		OwnGroup g1 = getGroup(player1.getUniqueId());
		OwnGroup g2 = getGroup(player2.getUniqueId());
		
		return g1 != null && g2 != null && g1 == g2;
	}
	

	@Override
	public boolean hasGroup(Player player) {
		if(player == null) return false;
		return getGroup(player.getUniqueId()) != null;
	}

	
	@Override
	public boolean addPlayerToGroup(Player groupOwner, Player toAdd) {
		if(groupOwner == null || toAdd == null) return false;
		
		UUID owner = groupOwner.getUniqueId();
		OwnGroup group = getGroup(owner);
		if(group == null){ group = new OwnGroup(owner); groups.add(group); }
		
		playerLeavesGroup(toAdd);
		
		if(group.getOwner() != groupOwner.getUniqueId()) return false;
		group.addMember(toAdd.getUniqueId());
		
		return true;
	}

	@Override
	public boolean playerLeavesGroup(Player player) {
		if(player == null) return false;
		
		UUID id = player.getUniqueId();
		OwnGroup group = getGroup(id);
		if(group == null) return false;
		
		//Removes the player from the Group.
		group.remove(id);
		if(group.isEmpty()) groups.remove(group);
		
		return true;
	}

	@Override
	public Collection<Player> getMembersOfGroup(Player player) {
		if(player == null) return new HashSet<>();
		
		OwnGroup group = getGroup(player.getUniqueId());
		Set<Player> members = new HashSet<>();
		if(group == null) return members;
		
		for(UUID id : group.members()){
			Player pl = Bukkit.getPlayer(id);
			if(pl != null) members.add(pl);
		}
		
		return members;
	}

	@Override
	public Player getLeaderOfGroup(Player player) {
		if(player == null) return null;
		
		OwnGroup group = getGroup(player.getUniqueId());
		if(group == null) return null;
		
		return Bukkit.getPlayer(group.getOwner());
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	

	@Override
	public void deinit() {
		this.groups.clear();
	}

	
	/**
	 * Gets the Group of the Player.
	 * @param id to search
	 * @return the group.
	 */
	private OwnGroup getGroup(UUID id){
		if(id == null) return null;
		
		for(OwnGroup group : groups){
			if(group.members.contains(id)) return group;
		}
		
		return null;
	}
	
	
	
	private class OwnGroup{
		/** The Members of the Group */
		private final Set<UUID> members = new HashSet<>();
		
		/** The owner */
		private UUID owner;
		
		public OwnGroup(UUID owner) {
			this.owner = owner;
			addMember(owner);
		}
		
		public void addMember(UUID id) {
			members.add(id);
		}

		public void remove(UUID id){
			members.remove(id);
			if(members.size() > 0 && owner == id) owner = members.iterator().next();
		}
		
		public Set<UUID> members(){
			return members;
		}
		
		public UUID getOwner() {
			return owner;
		}
		
		public boolean isEmpty(){
			return members.isEmpty();
		}
		
	}
	
}
