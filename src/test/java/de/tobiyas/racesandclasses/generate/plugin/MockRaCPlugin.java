package de.tobiyas.racesandclasses.generate.plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
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
import de.tobiyas.racesandclasses.healthmanagement.HealthManager;
import de.tobiyas.racesandclasses.statistics.StatisticGatherer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.Trait;
import de.tobiyas.racesandclasses.tutorial.TutorialManager;
import de.tobiyas.util.debug.logger.DebugLogger;
import de.tobiyas.util.permissions.PermissionManager;
import static org.mockito.Mockito.*;

public class MockRaCPlugin extends RacesAndClasses {

	
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
		
		this.debugLogger = mock(DebugLogger.class, RETURNS_DEEP_STUBS);
		
		this.errored = false;
		
		this.healthManager = mock(HealthManager.class, RETURNS_DEEP_STUBS);
		
		this.permManager = mock(PermissionManager.class, RETURNS_DEEP_STUBS);
		
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
		
		init();
		
		RacesAndClasses.plugin = this;
		this.testingMode = true;
	}
	
	private void init(){
		PluginLoader loader = mock(PluginLoader.class, RETURNS_DEEP_STUBS);
		Server bukkitServer = Bukkit.getServer() == null ? null : Bukkit.getServer();
		PluginDescriptionFile description = new PluginDescriptionFile("RacesAndClasses", "42", "MockRaCPlugin");
		
		File tempDir = null;
		try {
			tempDir = createTempDirectory();
		} catch (IOException e) {
			Assert.fail("TempDir could not be created.");
		}
		
		this.initialize(loader, bukkitServer, description, tempDir, null, null);
	}
	
	
	@Override
	public void log(String message){
		System.out.println("Error: " + message);
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
	
	
	private void tearDownTempDir(){
		try{
			deleteRecursive(getDataFolder());
		}catch(Exception exp){}
	}
	
	
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
	
	public void tearDown(){
		tearDownTempDir();
		RacesAndClasses.plugin = null;
	}
	
	
	
	
	public void setDebugLogger(DebugLogger debugLogger){
		this.debugLogger = debugLogger;
	}
	

	public void setConfigManager(ConfigManager configManager){
		this.configManager = configManager;
	}
	
	
	public void setHealthManager(HealthManager healthManager){
		this.healthManager = healthManager;
	}
	
	
	public void setCooldownManager(CooldownManager cooldownManager){
		this.cooldownManager = cooldownManager;
	}
	
	
	public void setPermissionManager(PermissionManager permManager){
		this.permManager = permManager;
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
