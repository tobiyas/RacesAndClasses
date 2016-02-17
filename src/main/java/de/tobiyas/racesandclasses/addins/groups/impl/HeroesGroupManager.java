package de.tobiyas.racesandclasses.addins.groups.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.party.HeroParty;
import com.herocraftonline.heroes.characters.party.PartyManager;

import de.tobiyas.racesandclasses.addins.groups.GroupManager;

public class HeroesGroupManager implements GroupManager {

	
	@Override
	public boolean isInSameGroup(Player player1, Player player2) {
		HeroParty party1 = getParty(player1.getUniqueId());
		HeroParty party2 = getParty(player2.getUniqueId());
		
		return party1 != null && party2 != null && party1 == party2;
	}
	

	@Override
	public boolean hasGroup(Player player) {
		return getParty(player.getUniqueId()) != null;
	}
	

	@Override
	public boolean addPlayerToGroup(Player groupOwner, Player toAdd) {
		Heroes plugin = Heroes.getInstance();
		HeroParty ownerParty = getParty(groupOwner.getUniqueId());
		if(ownerParty == null){
			ownerParty = new HeroParty(plugin.getCharacterManager().getHero(groupOwner), plugin);
			plugin.getPartyManager().addParty(ownerParty);
		}
		
		ownerParty.addMember(plugin.getCharacterManager().getHero(toAdd));
		return true;
	}
	

	@Override
	public boolean playerLeavesGroup(Player player) {
		HeroParty party = getParty(player.getUniqueId());
		if(party == null) return true;
		
		party.removeMember(Heroes.getInstance().getCharacterManager().getHero(player));
		if(party.getMembers().isEmpty()) Heroes.getInstance().getPartyManager().removeParty(party);
		return true;
	}
	

	@Override
	public Collection<Player> getMembersOfGroup(Player player) {
		HeroParty party = getParty(player.getUniqueId());
		Collection<Player> members = new HashSet<>();
		if(party == null) return members;
		for(Hero hero : party.getMembers()) members.add(hero.getPlayer());
		
		return members;
	}
	

	@Override
	public Player getLeaderOfGroup(Player player) {
		HeroParty party = getParty(player.getUniqueId());
		if(party == null) return null;
		
		return party.getLeader().getPlayer();
	}
	

	@Override
	public boolean isEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled("Heroes");
	}
	
	@Override
	public void deinit() {}
	
	
	private PartyManager partyManager(){
		return Heroes.getInstance().getPartyManager();
	}
	
	
	private HeroParty getParty(UUID player){
		for(HeroParty party : partyManager().getParties()){
			for(Hero hero : party.getMembers()){
				if(player.equals(hero.getPlayer().getUniqueId())) return party;
			}
		}
		
		return null;
	}
	
}
