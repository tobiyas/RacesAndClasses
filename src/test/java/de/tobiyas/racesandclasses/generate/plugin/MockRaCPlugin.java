package de.tobiyas.racesandclasses.generate.plugin;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import org.bukkit.plugin.PluginDescriptionFile;
import org.junit.Assert;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.chat.channels.ChannelManager;
import de.tobiyas.racesandclasses.configuration.global.ChannelConfig;
import de.tobiyas.racesandclasses.configuration.global.GeneralConfig;
import de.tobiyas.racesandclasses.configuration.managing.ConfigManager;
import de.tobiyas.racesandclasses.configuration.racetoclass.RaceToClassConfiguration;
import de.tobiyas.racesandclasses.cooldown.CooldownManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceManager;
import de.tobiyas.racesandclasses.generate.translator.MockLanguageTranslator;
import de.tobiyas.racesandclasses.playermanagement.PlayerManager;
import de.tobiyas.racesandclasses.statistics.StatisticGatherer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.translation.TranslationManagerHolder;
import de.tobiyas.racesandclasses.tutorial.TutorialManager;
import de.tobiyas.util.debug.logger.DebugLogger;
import de.tobiyas.util.permissions.PermissionManager;

public class MockRaCPlugin extends RacesAndClasses {
	
	/**
	 * Generates a mock Plugin
	 */
	public MockRaCPlugin() {
		super(  
			new PluginDescriptionFile("RacesAndClasses", "42", "MockRaCPlugin"),
			createTempDir()
			);
	}

	
	public void mockAllManagersAndInit(){
		this.channelManager = mock(ChannelManager.class, RETURNS_DEEP_STUBS);
		
		this.classManager= mock(ClassManager.class);
		when(classManager.getDefaultHolder()).thenReturn(null);
		
		
		this.configManager = mock(ConfigManager.class);
		
		GeneralConfig generalConfigMock = mock(GeneralConfig.class, RETURNS_SMART_NULLS);
		ChannelConfig channelConfigMock = mock(ChannelConfig.class, RETURNS_SMART_NULLS);
		RaceToClassConfiguration raceClassConfigMock = mock(RaceToClassConfiguration.class, RETURNS_SMART_NULLS);
		
		when(configManager.getGeneralConfig()).thenReturn(generalConfigMock);
		when(configManager.getChannelConfig()).thenReturn(channelConfigMock);
		when(configManager.getRaceToClassConfig()).thenReturn(raceClassConfigMock);
		
		
		this.cooldownManager = mock(CooldownManager.class, RETURNS_DEEP_STUBS);
		
		this.errored = false;
		
		this.playerManager = mock(PlayerManager.class, RETURNS_DEEP_STUBS);
		
		//Mock RaceManager
		raceManager = mock(RaceManager.class);
		
		RaceContainer defaultContainer = mock(RaceContainer.class);
		when(defaultContainer.getName()).thenReturn("DefaultRace");
		when(defaultContainer.getTag()).thenReturn("[NoRace]");
		when(defaultContainer.getTraits()).thenReturn(new HashSet<Trait>());
		
		when(raceManager.getDefaultHolder()).thenReturn(defaultContainer);
		when(raceManager.getHolderOfPlayer(anyString())).thenReturn(defaultContainer);
		
		this.statistics = mock(StatisticGatherer.class, RETURNS_DEEP_STUBS);
		this.tutorialManager = mock(TutorialManager.class, RETURNS_DEEP_STUBS);
		
		RacesAndClasses.plugin = this;
		this.testingMode = true;
		
		TranslationManagerHolder.forceManager(new MockLanguageTranslator());
	}
	
	
	private PermissionManager mockPermManager = mock(PermissionManager.class, RETURNS_DEEP_STUBS);	
	
	@Override
	public PermissionManager getPermissionManager() {
		return mockPermManager;
	}

	
	private DebugLogger mockLogger = mock(DebugLogger.class, RETURNS_DEEP_STUBS); 

	@Override
	public DebugLogger getDebugLogger() {
		return mockLogger;
	}


	@Override
	public void logWarning(String message) {
		System.err.println("Warning: " + message);
	}


	@Override
	public void logStackTrace(String message, Exception exp) {
		System.out.println("Stacktrace: " + message);
		exp.printStackTrace();
	}


	@Override
	public void log(String message){
		System.out.println("Log: " + message);
	}
	
	
	/**
	 * Creates a Temp dir.
	 * Returns null if not possible.
	 * 
	 * @return
	 */
	private static File createTempDir(){
		File tempDir = null;
		try {
			tempDir = createTempDirectory();
			return tempDir;
		} catch (IOException e) {
			Assert.fail("TempDir could not be created.");
		};
		
		return null;
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
	
	/**
	 * Removes everything setup for mocking.
	 */
	private void tearDownTempDir(){
		try{
			deleteRecursive(getDataFolder());
		}catch(Exception exp){}
	}
	
	
	/**
	 * Deletes everything in this dir or the file passed
	 * 
	 * @param file
	 * @throws IOException
	 */
	private void deleteRecursive(File file) throws IOException{
		if(file.isFile()){
			file.delete();
			return;
		}
		
		if(file.isDirectory()){
			for(File child : file.listFiles()){
				deleteRecursive(child);
			}
		}
	}
	
	/**
	 * Clears the Mock
	 */
	public void tearDown(){
		tearDownTempDir();
		RacesAndClasses.plugin = null;
	}
	
	
	////////////////////////////////////////////////
	//Setter for most managers for easier mocking //
	////////////////////////////////////////////////
	
	public void setConfigManager(ConfigManager configManager){
		this.configManager = configManager;
	}
	
	
	public void setHealthManager(PlayerManager healthManager){
		this.playerManager = healthManager;
	}
	
	
	public void setCooldownManager(CooldownManager cooldownManager){
		this.cooldownManager = cooldownManager;
	}
	
	
	public void setStatisticGatherer(StatisticGatherer statistics){
		this.statistics = statistics;
	}
	
	
	public void setRaceManager(RaceManager raceManager){
		this.raceManager = raceManager;
	}
	
	public void setClassManager(ClassManager classManager){
		this.classManager = classManager;
	}
	
	
	public void setChannelManager(ChannelManager channelManager){
		this.channelManager = channelManager;
	}
	
	
	public void setTutorialManager(TutorialManager tutorialManager){
		this.tutorialManager = tutorialManager;
	}

}
