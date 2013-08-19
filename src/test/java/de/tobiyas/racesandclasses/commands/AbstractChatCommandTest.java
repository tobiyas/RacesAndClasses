package de.tobiyas.racesandclasses.commands;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.configuration.managing.ConfigManager;
import de.tobiyas.racesandclasses.generate.PluginCommandFactory;
import de.tobiyas.racesandclasses.generate.plugin.GenerateRaces;
import de.tobiyas.racesandclasses.generate.plugin.MockRaCPlugin;
import de.tobiyas.utils.tests.generate.server.GenerateBukkitServer;

public abstract class AbstractChatCommandTest {

	protected CommandExecutor sut;
	protected final String commandName;
	protected final Class<? extends CommandExecutor> sutClass;
	
	protected CommandSender sender;
	
	protected String playerName = "console";

	
	public AbstractChatCommandTest(Class<? extends CommandExecutor> executorClass, String commandName){
		this.sutClass = executorClass;
		this.commandName = commandName;
	}
	
	
	@Before
	public void setup(){
		GenerateBukkitServer.generateServer();
		GenerateRaces.generateRaces();
		
		ConfigManager configManager = mock(ConfigManager.class, RETURNS_DEEP_STUBS);
		((MockRaCPlugin) RacesAndClasses.getPlugin() ).setConfigManager(configManager);
		
		try{
			sut = sutClass.getConstructor().newInstance();
		}catch(Exception exp){ Assert.fail(); }
			
		sender = mock(Player.class);
		when(sender.getName()).thenReturn(playerName);
	}
	
	
	@After
	public void teardown(){
		GenerateBukkitServer.dropServer();
		GenerateRaces.dropMock();
	}
	
	
	@Test
	public void command_registration_works(){
		PluginCommand command = PluginCommandFactory.create(commandName, RacesAndClasses.getPlugin());
		when(RacesAndClasses.getPlugin().getCommand(commandName)).thenReturn(command);
		
		try{
			sut = sut.getClass().getConstructor().newInstance();
		}catch(Exception exp){
			Assert.fail();
		}
		
		Assert.assertEquals(command.getExecutor(), sut);
	}
	
}
