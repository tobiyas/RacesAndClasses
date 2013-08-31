package de.tobiyas.racesandclasses.generate.plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.SecureRandom;

import org.bukkit.plugin.java.JavaPlugin;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.util.persistence.YAMLPersistenceProviderSetter;
import de.tobiyas.util.config.YAMLConfigExtended;

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
		
		deleteTemps();
	}
	
	
	/**
	 * Generates a Mock for the Plugin
	 */
	public static void generateRaces(){
		if(RacesAndClasses.getPlugin() != null){
			dropMock();
		}
		
		new MockRaCPlugin().mockAllManagersAndInit();
		
		generateFakeYAMLs();
	}
	
	private static void generateFakeYAMLs() {
		try{
			File tempDirectory = createTempDirectory();
			
			YAMLPersistenceProviderSetter.setClassesYAML(new YAMLConfigExtended(File.createTempFile("classes" + StringGenerator.nextRandomString(42), ".yml", tempDirectory)));
			YAMLPersistenceProviderSetter.setRacesYAML(new YAMLConfigExtended(File.createTempFile("races" + StringGenerator.nextRandomString(42), ".yml", tempDirectory)));
			YAMLPersistenceProviderSetter.setPlayerYAML(new YAMLConfigExtended(File.createTempFile("playerData" + StringGenerator.nextRandomString(42), ".yml", tempDirectory)));
		}catch(Exception exp){
			Assert.fail("YAML Generation failed: " + exp.getLocalizedMessage());
		}
	}
	
	/**
	 * Creates a temporary Directory in the System temp dir.
	 * 
	 * @return
	 * @throws IOException
	 */
	private static File createTempDirectory() throws IOException{
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
	
	private static void deleteTemps(){
		boolean classesDeleted = YAMLPersistenceProviderSetter.getLoadedClassesFile(false).getFileLoadFrom().delete();
		boolean racesDeleted = YAMLPersistenceProviderSetter.getLoadedRacesFile(false).getFileLoadFrom().delete();
		boolean playerDataDeleted = YAMLPersistenceProviderSetter.getLoadedPlayerFile(false).getFileLoadFrom().delete();		
		
		YAMLPersistenceProviderSetter.setClassesYAML(null);
		YAMLPersistenceProviderSetter.setRacesYAML(null);
		YAMLPersistenceProviderSetter.setPlayerYAML(null);
		
		if(!classesDeleted || !racesDeleted || !playerDataDeleted){
			Assert.fail("Could not delete: " + (classesDeleted ? "" : "classes.yml ") + (playerDataDeleted ? "" : "player.yml ") + (racesDeleted ? "" : "races.yml ") );
		}
	}
	
	
	public static class StringGenerator{

		private static SecureRandom random = new SecureRandom();

		public static String nextRandomString(int length) {
			return new BigInteger(length, random).toString(32);
		}

	}
}
