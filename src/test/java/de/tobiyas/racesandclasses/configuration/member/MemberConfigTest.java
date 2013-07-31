package de.tobiyas.racesandclasses.configuration.member;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.tobiyas.racesandclasses.generate.plugin.GenerateRaces;
import de.tobiyas.utils.tests.generate.server.GenerateBukkitServer;

public class MemberConfigTest {

	private String playerName = "playerName";
	
	private MemberConfig sut;
	
	@Before
	public void init(){
		GenerateRaces.generateRaces();
		GenerateBukkitServer.generateServer();

		playerName = StringGenerator.nextRandomString(12);
		sut = MemberConfig.createMemberConfig(playerName);
	}
	
	
	@After
	public void teardown(){
		GenerateRaces.dropMock();
		GenerateBukkitServer.dropServer();
	}
	
	
	@Test
	public void constructor_sets_values_correct(){
		assertEquals(playerName, sut.getName());
	}
	
	
	@Test
	public void set_for_predefined_values_works(){
		String newChannel = "Test";
		assertTrue(sut.changeAttribute(MemberConfig.chatChannel, newChannel));
		assertEquals(newChannel, sut.getCurrentChannel());
		
		
		boolean newCooldownInform = false;
		assertTrue(sut.changeAttribute(MemberConfig.cooldownInformation, newCooldownInform));
		assertEquals(newCooldownInform, sut.getInformCooldownReady());
		
		
		int newDisplayInterval = 42;
		assertTrue(sut.changeAttribute(MemberConfig.displayInterval, newDisplayInterval));
		assertEquals(newDisplayInterval, sut.getLifeDisplayInterval());
		
		
		boolean newLifeDisplayEnable = true;
		assertTrue(sut.changeAttribute(MemberConfig.lifeDisplayEnable, newLifeDisplayEnable));
		assertEquals(newLifeDisplayEnable, sut.getEnableLifeDisplay());
	}
	
	
	@Test
	public void set_wrong_format_for_predefined_values_does_not_work(){
		int newChannel = 42;
		assertFalse(sut.changeAttribute(MemberConfig.chatChannel, newChannel));
		assertEquals("Global", sut.getCurrentChannel());
		
		
		String newCooldownInform = "incorrect";
		assertFalse(sut.changeAttribute(MemberConfig.cooldownInformation, newCooldownInform));
		assertEquals(true, sut.getInformCooldownReady());
		
		
		String newDisplayInterval = "incorrect";
		assertFalse(sut.changeAttribute(MemberConfig.displayInterval, newDisplayInterval));
		assertEquals(60, sut.getLifeDisplayInterval());
		
		
		String newLifeDisplayEnable = "incorrect";
		assertFalse(sut.changeAttribute(MemberConfig.lifeDisplayEnable, newLifeDisplayEnable));
		assertEquals(false, sut.getEnableLifeDisplay());
	}
	
	
	@Test
	public void change_unknown_operation_does_not_work(){
		assertFalse(sut.changeAttribute("invalid", false));
	}
	
	
	@Test
	public void getValue_works(){
		assertEquals(sut.getCurrentChannel(), sut.getValueDisplayName(MemberConfig.chatChannel));
		assertEquals(sut.getCurrentChannel(), sut.getValueOfPath("channels.current"));
	}
	
	
	@Test
	public void getValue_on_non_existing_givs_null(){
		assertNull(sut.getValueDisplayName("invalid"));
		assertNull(sut.getValueOfPath("invalid"));
	}
	
	
	@Test
	public void get_supported_values(){
		List<String> supportedValues = sut.getSupportetAttributes(false);
		
		assertTrue(supportedValues.size() >= 4);
		assertTrue(supportedValues.contains(MemberConfig.chatChannel));
		assertTrue(supportedValues.contains(MemberConfig.cooldownInformation));
		assertTrue(supportedValues.contains(MemberConfig.displayInterval));
		assertTrue(supportedValues.contains(MemberConfig.lifeDisplayEnable));
	}
	
	@Test
	public void get_default_config(){
		Map<String, Object> config = sut.getCurrentConfig(false);
		
		assertEquals("Global", config.get(MemberConfig.chatChannel));
		assertEquals(true, config.get(MemberConfig.cooldownInformation));
		assertEquals(60, config.get(MemberConfig.displayInterval));
		assertEquals(false, config.get(MemberConfig.lifeDisplayEnable));
	}
	
	
	@Test
	public void set_invisible_config_is_overall_invisible(){
		String path = "path";
		String displayName = "invis";
		
		Object value = true;
		Object defaultValue = false;
		
		boolean visible = false;
		
		sut.addOption(path, displayName, value, defaultValue, visible);
		Map<String, Object> config = sut.getCurrentConfig(false);
		List<String> supportedValues = sut.getSupportetAttributes(false);
		assertFalse(config.containsKey(displayName));
		assertFalse(supportedValues.contains(displayName));
		
		config = sut.getCurrentConfig(true);
		supportedValues = sut.getSupportetAttributes(true);
		assertTrue(config.containsKey(displayName));
		assertTrue(supportedValues.contains(displayName));
	}
	
	@Test
	public void adding_invalid_options_does_not_work(){
		String path = "path";
		String displayName = "displayName";
				
		Object value = true;
		Object defaultValue = false;
		
		boolean visible = false;
		
		//path already exists
		assertFalse(sut.addOption("channels.current", displayName, value, defaultValue, visible));
		
		//displayName already exists
		assertFalse(sut.addOption(path, MemberConfig.chatChannel, value, defaultValue, visible));
		
		//Unknown Value
		assertFalse(sut.addOption(path, displayName, new LinkedList<String>(), defaultValue, visible));
	}
	
	
	@Test
	public void setting_value_correct_works(){
		String newValue = "Hallo";
		assertTrue(sut.setValue(MemberConfig.chatChannel, newValue));
		assertEquals(newValue, sut.getValueDisplayName(MemberConfig.chatChannel));
	}
	
	
	@Test
	public void setting_value_incorect_returns_false(){
		boolean newValue = false;
		
		Object valueBefore = sut.getValueDisplayName(MemberConfig.chatChannel);
		assertFalse(sut.setValue(MemberConfig.chatChannel, newValue));
		assertEquals(valueBefore, sut.getValueDisplayName(MemberConfig.chatChannel));
	}
	
	
	@Test
	public void setting_unknown_value_does_not_work(){
		assertFalse(sut.setValue("invalid", false));
	}
	
	
	@Test
	public void loading_works_with_custom_operations(){
		String path = "path";
		String displayName = "displayName";
		
		Object value = true;
		Object defaultValue = false;
		boolean visible = true;
		
		sut.addOption(path, displayName, value, defaultValue, visible);
		sut.save();
		
		sut = new MemberConfig(playerName);
		assertEquals(value, sut.getValueDisplayName(displayName));
	} 
	
	
	
	public static class StringGenerator{

	  private static SecureRandom random = new SecureRandom();

	  public static String nextRandomString(int length){
	    return new BigInteger(length, random).toString(32);
	  }

	}
}
