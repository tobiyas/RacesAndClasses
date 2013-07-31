package de.tobiyas.racesandclasses.configuration.member;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class MemberConfigListTest {

	private MemberConfigList sut;
	
	private final String path = "path";
	private final String displayName = "displayName";
	
	private final String invisiblePath = "path2";
	private final String invisibleDisplayName = "displayName2";
	
	@Before
	public void setup(){
		sut = new MemberConfigList();
		sut.add(new ConfigOption("path", "displayName", "value", "defaultValue", true));
		sut.add(new ConfigOption(invisiblePath, invisibleDisplayName, "value2", "defaultValue2", false));
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
