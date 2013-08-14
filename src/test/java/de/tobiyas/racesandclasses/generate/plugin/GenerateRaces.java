package de.tobiyas.racesandclasses.generate.plugin;

import java.lang.reflect.Field;

import org.bukkit.plugin.java.JavaPlugin;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import de.tobiyas.racesandclasses.RacesAndClasses;

@RunWith(PowerMockRunner.class)
@PrepareForTest({JavaPlugin.class, RacesAndClasses.class})
public class GenerateRaces {

	/**
	 * Drops the mock by setting the static {@link RacesAndClasses#plugin} field to null;
	 */
	public static void dropMock(){
		RacesAndClasses plugin = RacesAndClasses.getPlugin();
		if(plugin == null) return;
		
		if(plugin instanceof MockRaCPlugin){
			((MockRaCPlugin) plugin).tearDown();
		}else{
			try{
				Field staticPlugin = RacesAndClasses.class.getDeclaredField("plugin");
				staticPlugin.setAccessible(true);
				staticPlugin.set(null, null);
				
				Assert.assertNull(RacesAndClasses.getPlugin());
			}catch(Exception exp){
				Assert.fail("Teardown of RacesAndClasses Plugin failed.");
			}			
		}
	}
	
	
	/**
	 * Generates a Mock for the Plugin
	 */
	public static void generateRaces(){
		if(RacesAndClasses.getPlugin() != null){
			dropMock();
		}
		
		new MockRaCPlugin().mockAllManagersAndInit();
		
		/*
		try{
			Field staticPlugin = RacesAndClasses.class.getDeclaredField("plugin");
			staticPlugin.setAccessible(true);
			staticPlugin.set(null, plugin);
			
			//Mock Configs
			mockConfig(plugin);
		}catch(Exception e){
			System.out.println("Problem at setting Races fields.");
			e.printStackTrace();
		}*/
	}
}
