package de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.permissionsettings;

import java.util.LinkedList;
import java.util.List;

public class HolderPermissions {

	/**
	 * The name of the Holder of this permission pack with prefix for identification
	 */
	private final String groupIdentification;
	
	/**
	 * The Permissions the Holder has
	 */
	private final List<String> permissionList;
	
	
	public HolderPermissions(String groupIdentification) {
		this.groupIdentification = groupIdentification;
		this.permissionList = new LinkedList<String>();
	}

	
	public String getGroupIdentificationName(){
		return groupIdentification;
	}


	public void add(List<String> permissionList) {
		this.permissionList.addAll(permissionList);
	}
	
	
	public List<String> getPermissions(){
		return permissionList;
	}
	
	
	public void clear(){
		this.permissionList.clear();
	}
}
