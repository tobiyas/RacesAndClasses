package de.tobiyas.racesandclasses.generate;

import java.lang.reflect.Constructor;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.junit.Assert;

public class PluginCommandFactory {

	public static PluginCommand create(String name, Plugin owner){
		try{
			Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
			constructor.setAccessible(true);
			return constructor.newInstance(name, owner);
			
		}catch(Exception exp){
			Assert.fail("PluginCommand could not be build. " + exp);
			return null;
		}
	}

}
