package de.tobiyas.races.tests.generate.plugin;

import java.lang.reflect.Field;
import java.util.logging.Logger;

import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import de.tobiyas.races.Races;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Races.class)
public class GenerateRaces {

	public static void generateRaces(){
		Races plugin = Mockito.mock(Races.class);
		plugin.testingMode = true;
		
		
		try{
			Field staticPlugin = Races.class.getDeclaredField("plugin");
			staticPlugin.setAccessible(true);
			staticPlugin.set(null, plugin);
			
			Field logger = Races.class.getDeclaredField("log");
			logger.setAccessible(true);
			logger.set(plugin, Logger.getAnonymousLogger());
		}catch(Exception e){
			System.out.println("Problem at setting Races fields.");
			e.printStackTrace();
		}
	}
}
