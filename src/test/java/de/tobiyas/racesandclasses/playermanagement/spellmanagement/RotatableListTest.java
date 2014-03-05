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
package de.tobiyas.racesandclasses.playermanagement.spellmanagement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class RotatableListTest {

	
	private RotatableList<String> sut;
	
	
	@Before
	public void init(){
		sut = new RotatableList<String>();
	}
	
	
	@Test
	public void test_string_setting_works(){
		String element1 = "test 1";
		String element2 = "test 2";
		String element3 = "test 3";
		
		List<String> list = new LinkedList<String>();
		list.add(element1);
		list.add(element2);
		list.add(element3);
		
		sut.setEntries(list);
		
		assertEquals(list, sut.getAllEntries());
	}
	
	
	@Test
	public void test_string_rotating_works(){
		String element1 = "test 1";
		String element2 = "test 2";
		String element3 = "test 3";
		
		List<String> list = new LinkedList<String>();
		list.add(element1);
		list.add(element2);
		list.add(element3);
		
		sut.setEntries(list);
		
		assertEquals(element1, sut.currentEntry());
		assertEquals(element2, sut.next());
		assertEquals(element3, sut.next());
		assertEquals(element1, sut.next());
		
		assertEquals(element1, sut.currentEntry());
	}
	
	
	@Test
	public void test_empty_list_returns_null(){
		List<String> list = new LinkedList<String>();
		sut.setEntries(list);
		
		assertEquals(null, sut.currentEntry());
	}
	
	@Test
	public void test_constructor_creats_empty_list(){		
		assertEquals(null, sut.currentEntry());
		assertTrue(sut.getAllEntries().isEmpty());
	}
	
	@Test
	public void test_empty_list_next_returns_null(){
		assertNull(sut.next());
		assertNull(sut.currentEntry());
	}
	
	@Test
	public void test_passing_null_as_list_gives_empty_list(){
		sut.setEntries(null);
		
		assertTrue(sut.getAllEntries() != null);
	}

}
