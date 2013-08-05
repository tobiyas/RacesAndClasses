package de.tobiyas.racesandclasses.generate.plugin;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import org.bukkit.plugin.java.JavaPlugin;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.configuration.global.ChannelConfig;
import de.tobiyas.racesandclasses.configuration.global.ConfigManager;
import de.tobiyas.racesandclasses.configuration.global.GeneralConfig;
import de.tobiyas.racesandclasses.configuration.racetoclass.RaceToClassConfiguration;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceManager;
import de.tobiyas.util.debug.logger.DebugLogger;

@RunWith(PowerMockRunner.class)
@PrepareForTest({JavaPlugin.class, RacesAndClasses.class})
public class GenerateRaces {

	/**
	 * Drops the mock by setting the static {@link RacesAndClasses#plugin} field to null;
	 */
	public static void dropMock(){
		try{
			Field staticPlugin = RacesAndClasses.class.getDeclaredField("plugin");
			staticPlugin.setAccessible(true);
			staticPlugin.set(null, null);
			
			Assert.assertNull(RacesAndClasses.getPlugin());
		}catch(Exception exp){
			Assert.fail("Teardown of RacesAndClasses Plugin failed.");
		}
	}
	
	
	/**
	 * Generates a Mock for the Plugin
	 */
	public static void generateRaces(){
		RacesAndClasses plugin = mock(RacesAndClasses.class, RETURNS_DEEP_STUBS);
		plugin.testingMode = true;
		
		
		try{
			Field staticPlugin = RacesAndClasses.class.getDeclaredField("plugin");
			staticPlugin.setAccessible(true);
			staticPlugin.set(null, plugin);
			
			//Mock outputs
			when(plugin.getDebugLogger()).thenReturn(mock(DebugLogger.class));
			
			//Mock Configs
			mockConfig(plugin);

			
			//Mock RaceManager
			RaceManager raceManagerMock = mock(RaceManager.class);
			when(plugin.getRaceManager()).thenReturn(raceManagerMock);
			
			//Mock ClassManager
			ClassManager classManagerMock = mock(ClassManager.class);
			when(plugin.getClassManager()).thenReturn(classManagerMock);
			
			//Mock pluginDir output
			//File tempDir = createTempDirectory(); //TODO try fixing somehow...
			//PowerMockito.when(plugin, "getDataFolder").thenReturn(tempDir);
			
		}catch(Exception e){
			System.out.println("Problem at setting Races fields.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates a temporary Directory in the System temp dir.
	 * 
	 * @return
	 * @throws IOException
	 */
	public static File createTempDirectory() throws IOException{
	    final File temp;

	    temp = File.createTempFile("temp", Long.toString(System.nanoTime()));

	    if(!(temp.delete())){
	        throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
	    }

	    if(!(temp.mkdir())){
	        throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
	    }

	    return (temp);
	}

	
	/**
	 * Mocks the Config
	 * 
	 * @param plugin
	 */
	private static void mockConfig(RacesAndClasses plugin) {
		ConfigManager configManager = mock(ConfigManager.class);
		
		GeneralConfig generalConfigMock = mock(GeneralConfig.class, RETURNS_SMART_NULLS);
		ChannelConfig channelConfigMock = mock(ChannelConfig.class, RETURNS_SMART_NULLS);
		RaceToClassConfiguration raceClassConfigMock = mock(RaceToClassConfiguration.class, RETURNS_SMART_NULLS);
		
		when(configManager.getGeneralConfig()).thenReturn(generalConfigMock);
		when(configManager.getChannelConfig()).thenReturn(channelConfigMock);
		when(configManager.getRaceToClassConfig()).thenReturn(raceClassConfigMock);
		
		when(plugin.getConfigManager()).thenReturn(configManager);
	}
}
