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
