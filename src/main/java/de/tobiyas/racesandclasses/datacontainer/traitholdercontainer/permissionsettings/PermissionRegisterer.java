package de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.permissionsettings;

import java.util.Map;
import java.util.Set;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

import de.tobiyas.racesandclasses.RacesAndClasses;
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
	 * The Vault Permission Object
	 */
	private Permission vaultPermissions;
	
	
	
	public PermissionRegisterer(Set<AbstractTraitHolder> traitHolderList, Map<String, AbstractTraitHolder> memberList, String typeName){
		this.traitHolderList = traitHolderList;
		this.memberList = memberList;
		this.typeName = typeName;
	}

	
	@Override
	public void run() {
		if(!isVaultActive()) return;
		
		vaultPermissions = checkVault();
		if(traitHolderList == null || traitHolderList.size() <= 0) return;
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
				//vaultPermissions.groupRemove((World)null, groupName, "*");
			}
		}
	}
	
	/**
	 * Registers the TraitHolders as Group when supported
	 */
	private void registerTraitHolders() {
		if(traitHolderList == null || traitHolderList.isEmpty()) return;
		
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
		if(memberList == null || memberList.isEmpty()) return;
		
		for(String member : memberList.keySet()){
			AbstractTraitHolder holder = memberList.get(member);
			if(holder == null) continue;
			
			String groupName = holder.getPermissions().getGroupIdentificationName();
			
			String[] groups = vaultPermissions.getPlayerGroups((String)null, member);
			for(String group : groups){
				if(group.startsWith(typeName)){
					vaultPermissions.playerRemoveGroup((String) null, member, group);
				}
			}
			
			vaultPermissions.playerAddGroup((String) null, member, groupName);
		}
	}

	/**
	 * Checks if Vault is active.
	 * @return
	 */
	private static boolean isVaultActive(){
		return RacesAndClasses.getPlugin().getServer().getPluginManager().isPluginEnabled("Vault");
	}
	

	/**
	 * Gets the service for Vault
	 * 
	 * WARNING: Returns null if the Groups are not supported!!!
	 */
	private static Permission checkVault(){
		if(!isVaultActive()) return null;
		
        RegisteredServiceProvider<Permission> rsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        Permission perms = rsp.getProvider();
        
        return perms.hasGroupSupport() ? perms : null;
	}


	
	/**
	 * removes an Player from all groups with the passed prefix.
	 * This only works if Vault is active.
	 */
	public static void removePlayer(String player, String prefix) {
		if(!isVaultActive()) return;
		
		Permission vaultPermissions = checkVault();
		if(vaultPermissions != null){
			String[] groupNames = vaultPermissions.getPlayerGroups((String)null, player);
			for(String groupName : groupNames){
				if(groupName.startsWith(prefix + "-")){
					vaultPermissions.playerRemoveGroup((String)null, player, groupName);
				}
			}
		}
	}
	
	
	/**
	 * removes an Player from the group.
	 * This only works if Vault is active.
	 */
	public static void addPlayer(String player, String groupName) {
		if(!isVaultActive()) return;
		
		Permission vaultPermissions = checkVault();
		if(vaultPermissions != null){
			vaultPermissions.playerAddGroup((String) null, player, groupName);
		}
	}

}
