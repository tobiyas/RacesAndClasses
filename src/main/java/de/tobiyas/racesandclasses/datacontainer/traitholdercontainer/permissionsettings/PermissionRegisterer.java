/*******************************************************************************
 * Copyright 2014 Tobias Welther
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.permissionsettings;

import java.util.Map;
import java.util.Set;

import net.milkbowl.vault.permission.Permission;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.Group;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;

public class PermissionRegisterer implements Runnable{

	/**
	 * The lists of traitHolders
	 */
	private final Set<AbstractTraitHolder> traitHolderList;
	
	/**
	 * The Member config to set the Permissions to
	 */
	private final Map<RaCPlayer, AbstractTraitHolder> memberList;
	
	/**
	 * The prefix of the Permissions
	 */
	private final String typeName;
	
	
	/**
	 * The Vault Permission Object
	 */
	private Permission vaultPermissions;
	
	/**
	 * The Prefix for specific PermissionSystems
	 */
	private static String permissionSpecificPrefix = "";
	
	
	public PermissionRegisterer(Set<AbstractTraitHolder> traitHolderList, Map<RaCPlayer, AbstractTraitHolder> memberList, String typeName){
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
		
		checkForSpecificPrefix();
		removeAllGroupsWithPrefix();
		
		registerTraitHolders();
		giveMembersAccessToGroups();
	}
	
	
	/**
	 * Checks if a Permission Plugins needs specific prefixes.
	 */
	private void checkForSpecificPrefix() {
		if("GroupManager".equals(vaultPermissions.getName())){
			permissionSpecificPrefix = "g:";
		}
	}


	/**
	 * Removes all old groups
	 */
	private void removeAllGroupsWithPrefix(){
		String[] groupNames = vaultPermissions.getGroups();
		for(String groupName : groupNames){
			if(groupName.toLowerCase().startsWith(this.typeName.toLowerCase() + "-")){
				
				//remove all players from Group.
				//This is VERY inefficient!
				
				for(OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()){
					vaultPermissions.playerRemoveGroup((String)null, offlinePlayer.getName(), 
							permissionSpecificPrefix + groupName);
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
			precreateGroupIfNotExist(groupName);
			
			for(String permission : permissions.getPermissions()){
				if(!vaultPermissions.groupAdd((String)null, permissionSpecificPrefix + groupName, permission)){
					RacesAndClasses.getPlugin().getDebugLogger().logWarning("Could NOT Register Group: " + permissionSpecificPrefix + groupName 
							+ " Permission: " + permission + ". Used: " + vaultPermissions.getName());
				};
			}
		}
	}


	/**
	 * Pre creates the Group if necessary.
	 * This is Permission Plugin specific.
	 * 
	 * 
	 * @param groupName
	 */
	private void precreateGroupIfNotExist(String groupName) {
		//create for GroupManager
		if("GroupManager".equals(vaultPermissions.getName())){
			if(!GroupManager.getGlobalGroups().hasGroup(permissionSpecificPrefix + groupName)){
				Group newGroup = new Group(permissionSpecificPrefix + groupName);
				GroupManager.getGlobalGroups().newGroup(newGroup);
			}
		}
	}


	/**
	 * Adds the people to the corresponding groups
	 */
	private void giveMembersAccessToGroups() {
		if(memberList == null || memberList.isEmpty()) return;
		
		for(RaCPlayer member : memberList.keySet()){
			AbstractTraitHolder holder = memberList.get(member);
			if(holder == null) continue;
			
			String groupName = holder.getPermissions().getGroupIdentificationName();
			
			String name = member.getName();
			if(name == null) continue;
			
			
			String[] groups = vaultPermissions.getPlayerGroups((String)null, name);
			if(groups == null || groups.length == 0) continue;
			
			for(String group : groups){
				if(group.startsWith(permissionSpecificPrefix + typeName)){
					vaultPermissions.playerRemoveGroup((String) null, name, permissionSpecificPrefix + group);
				}
			}
			
			vaultPermissions.playerAddGroup((String) null, name, permissionSpecificPrefix + groupName);
		}
	}

	/**
	 * Checks if Vault is active.
	 * Also Checks for Group support.
	 * 
	 * @return
	 */
	private static boolean isVaultActive(){
		try{
			boolean isPresent = RacesAndClasses.getPlugin().getServer().getPluginManager().isPluginEnabled("Vault");
			if(!isPresent) return false;
			
			RegisteredServiceProvider<Permission> rsp = RacesAndClasses.getPlugin().getServer()
					.getServicesManager().getRegistration(Permission.class);
	        Permission perms = rsp.getProvider();
	        
	        return perms.hasGroupSupport();
		}catch(NoSuchMethodError exp){
			return false;
		}catch(Exception exp){
			return false;
		}
	}
	

	/**
	 * Gets the service for Vault
	 * 
	 * WARNING: Returns null if the Groups are not supported!!!
	 */
	private static Permission checkVault(){
		try{
			if(!isVaultActive()) return null;
			
	        RegisteredServiceProvider<Permission> rsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
	        Permission perms = rsp.getProvider();
	        
	        return perms.hasGroupSupport() ? perms : null;
		}catch(NoSuchMethodError exp){
			return null;
		}catch(Exception exp){
			return null;
		}
	}


	
	/**
	 * removes an Player from all groups with the passed prefix.
	 * This only works if Vault is active.
	 */
	public static void removePlayer(RaCPlayer player, String prefix) {
		if(!isVaultActive()) return;
		
		Permission vaultPermissions = checkVault();
		if(vaultPermissions != null){			
			String[] groupNames = vaultPermissions.getPlayerGroups((String)null, player.getName());
			if(groupNames == null) return; //nothing to do
			
			for(String groupName : groupNames){
				if(groupName.startsWith(permissionSpecificPrefix + prefix + "-")){
					vaultPermissions.playerRemoveGroup((String)null,  player.getName(), permissionSpecificPrefix + groupName);
				}
			}
		}
	}
	
	
	/**
	 * removes an Player from the group.
	 * This only works if Vault is active.
	 */
	public static void addPlayer(RaCPlayer player, String groupName) {
		if(!isVaultActive()) return;
		
		Permission vaultPermissions = checkVault();
		if(vaultPermissions != null){	
			vaultPermissions.playerAddGroup((String) null, player.getName(), permissionSpecificPrefix + groupName);
		}
	}

}
