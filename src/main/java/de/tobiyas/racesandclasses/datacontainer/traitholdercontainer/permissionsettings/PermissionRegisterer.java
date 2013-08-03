package de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.permissionsettings;

import java.util.Map;
import java.util.Set;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.plugin.RegisteredServiceProvider;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;

public class PermissionRegisterer implements Runnable{

	/**
	 * The lists of traitHolders
	 */
	private final Set<AbstractTraitHolder> traitHolderList;
	
	/**
	 * The Member config to set the Permissions to
	 */
	private final Map<String, AbstractTraitHolder> memberList;
	
	/**
	 * The prefix of the Permissions
	 */
	private final String typeName;
	
	/**
	 * Vaults' Permissions to interact with
	 */
	private Permission vaultPermissions;
	
	
	public PermissionRegisterer(Set<AbstractTraitHolder> traitHolderList, Map<String, AbstractTraitHolder> memberList, String typeName){
		this.traitHolderList = traitHolderList;
		this.memberList = memberList;
		this.typeName = typeName;
	}

	
	@Override
	public void run() {
		checkVault();
		if(traitHolderList.size() <= 0) return;
		if(vaultPermissions == null) return;
		if(!vaultPermissions.hasGroupSupport()) return;
		
		removeAllGroupsWithPrefix();
		
		registerTraitHolders();
		giveMembersAccessToGroups();
	}
	
	/**
	 * Removes all old groups
	 */
	private void removeAllGroupsWithPrefix(){
		String[] groupNames = vaultPermissions.getGroups();
		for(String groupName : groupNames){
			if(groupName.startsWith(this.typeName + "-")){
				
				//remove all players from Group.
				//This is VERY inefficient!
				for(OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()){
					vaultPermissions.playerRemoveGroup((String)null, offlinePlayer.getName(), groupName);
				}
				
				//remove the group from the list TODO
				vaultPermissions.groupRemove((World)null, groupName, "*");
			}
		}
	}
	
	/**
	 * Registers the TraitHolders as Group when supported
	 */
	private void registerTraitHolders() {
		for(AbstractTraitHolder holder : traitHolderList){
			HolderPermissions permissions = holder.getPermissions();
			if(permissions == null || permissions.getPermissions().size() ==0){
				continue;
			}
			
			String groupName = permissions.getGroupIdentificationName();
			
			for(String permission : permissions.getPermissions()){
				vaultPermissions.groupAdd((String)null, groupName, permission);				
			}
		}
	}


	/**
	 * Adds the people to the corresponding groups
	 */
	private void giveMembersAccessToGroups() {
		for(String member : memberList.keySet()){
			AbstractTraitHolder holder = memberList.get(member);
			if(holder == null) continue; // || holder.getPermissions() == null) continue;
			
			String groupName = holder.getPermissions().getGroupIdentificationName();
			vaultPermissions.playerAddGroup((String)null, member, groupName);
		}
	}


	/**
	 * Gets the service for Vault
	 */
	private void checkVault(){
        RegisteredServiceProvider<Permission> rsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        vaultPermissions = rsp.getProvider();
	}

}
