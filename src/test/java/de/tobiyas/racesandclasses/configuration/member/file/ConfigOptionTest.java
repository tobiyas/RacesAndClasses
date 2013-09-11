package de.tobiyas.racesandclasses.configuration.member.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.tobiyas.racesandclasses.configuration.member.file.ConfigOption.SaveFormat;
import de.tobiyas.racesandclasses.generate.plugin.GenerateRaces;
import de.tobiyas.racesandclasses.util.persistence.YAMLPersistenceProviderSetter;
import de.tobiyas.util.config.YAMLConfigExtended;
import de.tobiyas.utils.tests.generate.server.GenerateBukkitServer;

public class ConfigOptionTest {

	private String playerName = "player";
	
	@Before
	public void setup(){
		GenerateBukkitServer.generateServer();
		GenerateRaces.generateRaces();
	}
	
	@After
	public void teardown(){
		GenerateBukkitServer.dropServer();
		GenerateRaces.dropMock();
	}
	
	@Test
	public void test_all_formats(){
		String stringValue = "test1";
		int intValue = 42;
		double doubleValue = 13.37d;
		boolean boolValue = true;
		
		List<String> unknownValue = new LinkedList<String>();
		unknownValue.add("test1");
		
		
		assertEquals(SaveFormat.STRING, ConfigOption.identifyFormat(stringValue));
		assertEquals(SaveFormat.INT, ConfigOption.identifyFormat(intValue));
		assertEquals(SaveFormat.DOUBLE, ConfigOption.identifyFormat(doubleValue));
		assertEquals(SaveFormat.BOOLEAN, ConfigOption.identifyFormat(boolValue));
		assertEquals(SaveFormat.UNKNOWN, ConfigOption.identifyFormat(unknownValue));
	}
	
	
	@Test
	public void constructor_simple_creation_works(){
		String value = "test1";
		String path = "hallo";
		
		ConfigOption sut = new ConfigOption(path, playerName, value);
		
		//lets check if the values are correct set
		assertEquals(path, sut.getPath());
		assertEquals(path, sut.getDisplayName());
		
		assertEquals(value, sut.getValue());
		assertEquals(value, sut.getDefaultValue());
		
		assertEquals(SaveFormat.STRING, sut.getFormat());
		
		assertTrue(sut.isVisible());
	}
	
	@Test
	public void constructor_visible_ignore_test(){
		String value = "value";
		String defaultValue = "default";
		
		String displayName = "displayName";
		String path = "path";
		
		
		ConfigOption sut = new ConfigOption(path, playerName, displayName, value, defaultValue);
		
		//lets check if the values are correct set
		assertEquals(path, sut.getPath());
		assertEquals(displayName, sut.getDisplayName());
		
		assertEquals(value, sut.getValue());
		assertEquals(defaultValue, sut.getDefaultValue());
		
		assertEquals(SaveFormat.STRING, sut.getFormat());
		
		assertTrue(sut.isVisible());
	}
	
	@Test
	public void constructor_visible_false_test(){
		String value = "value";
		String defaultValue = "default";
		String playerName = "useless";
		
		String displayName = "displayName";
		String path = "path";
		boolean visiblity = false;
		
		
		ConfigOption sut = new ConfigOption(path, playerName, displayName, value, defaultValue, visiblity);
		
		//lets check if the values are correct set
		assertEquals(path, sut.getPath());
		assertEquals(displayName, sut.getDisplayName());
		
		assertEquals(value, sut.getValue());
		assertEquals(defaultValue, sut.getDefaultValue());
		
		assertEquals(SaveFormat.STRING, sut.getFormat());
		
		assertEquals(visiblity, sut.isVisible());
	}
	
	@Test
	public void set_value_works(){
		String value = "test1";
		String path = "hallo";
		
		ConfigOption sut = new ConfigOption(path, playerName, value);
		
		//lets check if the values are correct set
		assertTrue(sut.setObjectValue("test2"));		
	}
	
	@Test
	public void set_incorrect_value_does_not_work(){
		String value = "test1";
		String path = "hallo";
		
		ConfigOption sut = new ConfigOption(path, playerName, value);
		
		//lets check if the values are correct set
		assertFalse(sut.setObjectValue(42));		
	}
	
	
	
	//************************
	// Testing of YAML stuff *
	//************************
	
	
	@Test
	public void YAML_saving_works(){
		String path = "path";
		String value = "value";
		

		try{
			YAMLConfigExtended config = YAMLPersistenceProviderSetter.getLoadedPlayerFile(true);
			String pre = "prefixforsavings";
			config.createSection(pre);
			config.save();
			
			ConfigOption sut = new ConfigOption(path, playerName, value);
			sut.save(pre);

			
			String loadedValue = config.getString(pre + "." + path + "." + ConfigOption.DEFAULT_VALUE_PATH);
			String loadedDefaultValue = config.getString(pre + "." + path + "." + ConfigOption.DEFAULT_DEFAULTVALUE_PATH);
			
			assertEquals(sut.getValue(), loadedValue);
			assertEquals(sut.getDefaultValue(), loadedDefaultValue);

			String loadedDisplayName = config.getString(pre + "." + path + "." + ConfigOption.DEFAULT_DISPLAY_NAME_PATH);
			String loadedFormatName = config.getString(pre + "." + path + "." + ConfigOption.DEFAULT_FORMAT_PATH);
			assertEquals(sut.getDisplayName(), loadedDisplayName);
			assertEquals(sut.getFormat(), SaveFormat.valueOf(loadedFormatName));

			boolean loadedVisibility = config.getBoolean(pre + "." + path + "." + ConfigOption.DEFAULT_VISIBLE_PATH);
			assertEquals(sut.isVisible(), loadedVisibility);
			
		}catch(Exception exp){
			fail(exp.getLocalizedMessage());
		}
		
	}
	
	
	@Test
	public void check_valid_state_works_on_correct_format(){
		String path = "path";
		String value = "value";
		
		ConfigOption sut = new ConfigOption(path, playerName, value);
		
		
		File tempFile = null;

		try{
			tempFile = File.createTempFile("test1", ".yml");
			YAMLConfigExtended config = new YAMLConfigExtended(tempFile);
			String pre = "pre";
			
			config.createSection(pre);
			YAMLPersistenceProviderSetter.setPlayerYAML(config);
			
			sut.save(pre);
			
			assertTrue(ConfigOption.isInValidFormat(pre, playerName, path));			
		}catch(Exception exp){
			fail(exp.getLocalizedMessage());
		}finally{
			if(tempFile != null){
				tempFile.delete();
			}
		}
	}
	
	@Test
	public void check_valid_state_works_on_incorrect_format(){
		String path = "path";
		String value = "value";
		
		ConfigOption sut = new ConfigOption(path, playerName, value);
		
		
		File tempFile = null;

		try{
			tempFile = File.createTempFile("test1", ".yml");
			YAMLConfigExtended config = new YAMLConfigExtended(tempFile);
			String pre = "pre";
			
			config.createSection(pre);
			YAMLPersistenceProviderSetter.setPlayerYAML(config);
			
			sut.save(pre);
			
			assertFalse(ConfigOption.isInValidFormat(pre  + "banane234", playerName, path));
		}catch(Exception exp){
			exp.printStackTrace();
			fail(exp.getLocalizedMessage());
		}finally{
			if(tempFile != null){
				tempFile.delete();
			}
		}
	}
	
	
	@Test
	public void loading_default_works_when_present(){
		String path = "path";
		String value = "value";
		ConfigOption sut = new ConfigOption(path, playerName, value);
		
		File tempFile = null;

		try{
			tempFile = File.createTempFile("test1", ".yml");
			YAMLConfigExtended config = new YAMLConfigExtended(tempFile);
			String pre = "pre";
			
			config.createSection(pre);
			YAMLPersistenceProviderSetter.setPlayerYAML(config);
			
			sut.save(pre);
			
			ConfigOption loadedOption = ConfigOption.loadFromPathOrCreateDefault(pre, playerName, path, "2", "1", false);
			assertEquals(sut, loadedOption);
		}catch(Exception exp){
			fail(exp.getLocalizedMessage());
		}finally{
			if(tempFile != null){
				tempFile.delete();
			}
		}
	}
	
	@Test
	public void loading_default_works_when_not_present(){
		String path = "path";
		String value = "value";
		ConfigOption sut = new ConfigOption(path, playerName, value);
		
		File tempFile = null;

		try{
			tempFile = File.createTempFile("test1", ".yml");
			YAMLConfigExtended config = new YAMLConfigExtended(tempFile);
			String pre = "pre";
			
			config.createSection(pre);
			YAMLPersistenceProviderSetter.setPlayerYAML(config);
			
			sut.save(pre);
			
			ConfigOption loadedOption = ConfigOption.loadFromPathOrCreateDefault(pre + "banane", playerName, path, path, value, true);
			assertEquals(sut, loadedOption);
		}catch(Exception exp){
			fail(exp.getLocalizedMessage());
		}finally{
			if(tempFile != null){
				tempFile.delete();
			}
		}
	}
	
	
	@Test
	public void cover_hashcode_and_equals(){
		String path = "path";
		String value = "value";
		ConfigOption sut1 = new ConfigOption(path, playerName, value);
		ConfigOption sut2 = new ConfigOption(path, playerName, value);
		
		
		int hashCode1 = sut1.hashCode();
		int hashCode2 = sut2.hashCode();
		assertEquals(hashCode1, hashCode2);
		
		//some equals stuff
		assertTrue(sut1.equals(sut1));
		assertFalse(sut1.equals(null));
		assertFalse(sut1.equals(value));
		
		//Null checks not possible.
	}
}
