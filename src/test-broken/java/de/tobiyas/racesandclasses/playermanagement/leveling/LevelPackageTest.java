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
package de.tobiyas.racesandclasses.playermanagement.leveling;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LevelPackageTest {

	@Test
	public void constructor_works(){
		int level = 42;
		int maxExp = 1337;
		
		LevelPackage sut = new LevelPackage(level, maxExp);
		
		assertEquals(level, sut.getLevel());
		assertEquals(maxExp, sut.getMaxEXP());
	}
	
	@Test
	public void constructor_setting_negative_values_sets_to_1(){
		int level = -42;
		int maxExp = -1337;
		
		LevelPackage sut = new LevelPackage(level, maxExp);
		
		assertEquals(1, sut.getLevel());
		assertEquals(1, sut.getMaxEXP());
	}
}
