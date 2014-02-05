package de.tobiyas.racesandclasses.util.permissions;

import java.util.LinkedList;
import java.util.List;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.util.permissions.PermissionChecker.PermState;

public class VaultHook extends Permission {

	private static VaultHook hook;
	
	/**
	 * The Hooked Permission.
	 */
	private Permission hooked;
	
	
	private VaultHook() {
		hook = this;
		ServicesManager sm = Bukkit.getServicesManager();
		Permission perms = sm.getRegistration(Permission.class).getProvider();
		hooked = perms;
		
		sm.unregister(perms);		
		sm.register(Permission.class, this, RacesAndClasses.getPlugin(), ServicePriority.Highest);
	}

	
	@Override
	public String[] getGroups() {
		return hooked.getGroups();
	}

	@Override
	public String getName() {
		return hooked.getName();
	}

	@Override
	public String[] getPlayerGroups(String arg0, String arg1) {
		return hooked.getPlayerGroups(arg0, arg1);
	}

	@Override
	public String getPrimaryGroup(String arg0, String arg1) {
		return hooked.getPrimaryGroup(arg0, arg1);
	}

	@Override
	public boolean groupAdd(String arg0, String arg1, String arg2) {
		return hooked.groupAdd(arg0, arg1, arg2);
	}

	@Override
	public boolean groupHas(String arg0, String arg1, String arg2) {
		return hooked.groupHas(arg0, arg1, arg2);
	}

	@Override
	public boolean groupRemove(String arg0, String arg1, String arg2) {
		return hooked.groupRemove(arg0, arg1, arg2);
	}

	@Override
	public boolean hasGroupSupport() {
		return hooked.hasGroupSupport();
	}

	@Override
	public boolean hasSuperPermsCompat() {
		return hooked.hasSuperPermsCompat();
	}

	@Override
	public boolean isEnabled() {
		return hooked.isEnabled();
	}

	@Override
	public boolean playerAdd(String arg0, String arg1, String arg2) {
		return hooked.playerAdd(arg0, arg1, arg2);
	}

	@Override
	public boolean playerAddGroup(String arg0, String arg1, String arg2) {
		return hooked.playerAddGroup(arg0, arg1, arg2);
	}

	@Override
	public boolean playerHas(String player, String world, String permission) {
		for(PermissionChecker checker : permChecker){
			PermState state = checker.checkPermissions(player, world, permission);
			if(state == PermState.FORCE_PERMISE) return true;
			if(state == PermState.FORCE_DECLINE) return false;
		}
		
		return hooked.playerHas(player, world, permission);
	}

	@Override
	public boolean playerInGroup(String arg0, String arg1, String arg2) {
		return hooked.playerInGroup(arg0, arg1, arg2);
	}

	@Override
	public boolean playerRemove(String arg0, String arg1, String arg2) {
		return hooked.playerRemove(arg0, arg1, arg2);
	}

	@Override
	public boolean playerRemoveGroup(String arg0, String arg1, String arg2) {
		return hooked.playerRemoveGroup(arg0, arg1, arg2);
	}
	
	
	/**
	 * The List of permissioncheckers
	 */
	private final List<PermissionChecker> permChecker = new LinkedList<PermissionChecker>();
	
	
	/**
	 * Registers a Permissionchecker
	 * 
	 * @param checker to add
	 */
	public void registerPermissionChecker(PermissionChecker checker){
		if(!permChecker.contains(checker)){
			permChecker.add(checker);
		}
	}
	
	
	/**
	 * This removes a {@link PermissionChecker} from the List of checkers
	 * 
	 * @param checker to remove
	 */
	public void unregisterPermissionChecker(PermissionChecker checker){
		if(permChecker.contains(checker)){
			permChecker.remove(checker);
		}
	}
	
	/**
	 * Returns the Vault hook
	 * <br>This is a lazy init.
	 * 
	 * @return the Vault Hook
	 */
	public static VaultHook getHook(){
		if(hook == null){
			hook = new VaultHook();
		}
		
		return hook;
	}
}
