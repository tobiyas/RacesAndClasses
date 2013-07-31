package de.tobiyas.racesandclasses.generate.plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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
@PrepareForTest(RacesAndClasses.class)
public class GenerateRaces {

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
		RacesAndClasses plugin = Mockito.mock(RacesAndClasses.class, Mockito.RETURNS_DEEP_STUBS);
		plugin.testingMode = true;
		
		
		try{
			Field staticPlugin = RacesAndClasses.class.getDeclaredField("plugin");
			staticPlugin.setAccessible(true);
			staticPlugin.set(null, plugin);
			
			//Mock outputs
			Mockito.when(plugin.getDebugLogger()).thenReturn(Mockito.mock(DebugLogger.class));
			
			//Mock Configs
			mockConfig(plugin);

			
			//Mock RaceManager
			RaceManager raceManagerMock = Mockito.mock(RaceManager.class);
			Field staticRaceManager = RaceManager.class.getDeclaredField("manager");
			staticRaceManager.setAccessible(true);
			staticRaceManager.set(null, raceManagerMock);
			
			//Mock ClassManager
			ClassManager classManagerMock = Mockito.mock(ClassManager.class);
			Field staticClassManager = ClassManager.class.getDeclaredField("classManager");
			staticClassManager.setAccessible(true);
			staticClassManager.set(null, classManagerMock);
			
			//Mock pluginDir output
			Mockito.when(plugin.getDataFolder()).thenReturn(createTempDirectory());
			
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
		ConfigManager configManager = Mockito.mock(ConfigManager.class);
		
		GeneralConfig generalConfigMock = Mockito.mock(GeneralConfig.class, Mockito.RETURNS_SMART_NULLS);
		ChannelConfig channelConfigMock = Mockito.mock(ChannelConfig.class, Mockito.RETURNS_SMART_NULLS);
		RaceToClassConfiguration raceClassConfigMock = Mockito.mock(RaceToClassConfiguration.class, Mockito.RETURNS_SMART_NULLS);
		
		Mockito.when(configManager.getGeneralConfig()).thenReturn(generalConfigMock);
		Mockito.when(configManager.getChannelConfig()).thenReturn(channelConfigMock);
		Mockito.when(configManager.getRaceToClassConfig()).thenReturn(raceClassConfigMock);
		
		Mockito.when(plugin.getConfigManager()).thenReturn(configManager);
	}
}
