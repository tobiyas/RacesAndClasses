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
package de.tobiyas.racesandclasses.configuration.member;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.tobiyas.racesandclasses.configuration.member.file.ConfigOption;


public class MemberConfigListTest {

	private MemberConfigList<ConfigOption> sut;
	
	private final String path = "path";
	private final String displayName = "displayName";
	private final String playerName = "player";
	
	private final String invisiblePath = "path2";
	private final String invisibleDisplayName = "displayName2";
	
	@Before
	public void setup(){
		sut = new MemberConfigList<ConfigOption>();
		sut.add(new ConfigOption("path", playerName, "displayName", "value", "defaultValue", true));
		sut.add(new ConfigOption(invisiblePath, playerName, invisibleDisplayName, "value2", "defaultValue2", false));
	}
	
	
	@Test
	public void contains_displayName_works(){
		assertTrue(sut.contains(displayName));
		assertTrue(sut.contains(invisibleDisplayName));
		
		assertFalse(sut.contains(path));
		assertFalse(sut.contains(invisiblePath));		
	}
	
	
	@Test
	public void contains_pathName_works(){
		assertTrue(sut.containsPathName(path));
		assertTrue(sut.containsPathName(invisiblePath));

		assertFalse(sut.containsPathName(invisibleDisplayName));
		assertFalse(sut.containsPathName(displayName));
	}
	
}
