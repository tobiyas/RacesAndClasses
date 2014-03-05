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
