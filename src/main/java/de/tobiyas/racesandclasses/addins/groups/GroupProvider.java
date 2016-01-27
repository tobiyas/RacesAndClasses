package de.tobiyas.racesandclasses.addins.groups;

import de.tobiyas.racesandclasses.addins.groups.impl.DisabledGroupManager;

public class GroupProvider {

	/**
	 * The instance to use.
	 */
	private static GroupProvider instance = new GroupProvider();
	
	
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
	 * Returns the GroupManager registered.
	 * @return the Group Manager.
	 */
	public GroupManager getProvider(){
		return manager;
	}
	
	
	/**
	 * Gets the instance.
	 * @return the instance.
	 */
	public static GroupProvider get(){
		return instance;
	}
	
}
