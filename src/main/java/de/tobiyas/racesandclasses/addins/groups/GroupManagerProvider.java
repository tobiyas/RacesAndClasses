package de.tobiyas.racesandclasses.addins.groups;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.addins.groups.impl.DisabledGroupManager;

public class GroupManagerProvider {

	/**
	 * The instance to use.
	 */
	private static GroupManagerProvider instance = new GroupManagerProvider();
	
	
	/**
	 * The GroupManager to use.
	 */
	private GroupManager manager = new DisabledGroupManager();
	
	
	/**
	 * Registers a new Provider.
	 * @param manager to register.
	 */
	public void registerProvider(GroupManager manager){
		if(manager != null) this.manager = manager;
	}
	
	
	/**
	 * Generates a new Group-Manager.
	 * @param manager to register.
	 */
	public void registerProvider(SupportedGroupsManager system){
		if(manager != null) registerProvider(SupportedGroupsManager.generateNew(system));
	}
	
	
	/**
	 * Returns the GroupManager registered.
	 * @return the Group Manager.
	 */
	public GroupManager getProvider(){
		return manager;
	}
	
	
	/**
	 * Reloads the Group-System.
	 */
	public void reload(){
		boolean enabled = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_groups_enable();
		String systemName = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_groups_system();
		
		SupportedGroupsManager system = enabled ? SupportedGroupsManager.None : SupportedGroupsManager.parse(systemName);
		registerProvider(system);
	}
	
	
	/**
	 * Gets the instance.
	 * @return the instance.
	 */
	public static GroupManagerProvider get(){
		return instance;
	}
	
}
