package de.tobiyas.racesandclasses.commands.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.commands.CommandInterface;

public class CommandMap {


	/**
	 * Returns the Simple command Map.
	 * This may return null on breakdown.s
	 * 
	 * @return the command map or null.
	 */
	public static SimpleCommandMap get(){
		try{
			Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			field.setAccessible(true);
			return (SimpleCommandMap) field.get(Bukkit.getServer());
		}catch(Throwable exp){
			exp.printStackTrace();
			return null;
		}
	}

	
	/**
	 * Registers the Commands passed to the map.
	 * 
	 * @param commands
	 * @param racesAndClasses
	 */
	public static void registerCommands(List<CommandInterface> commands,
			RacesAndClasses racesAndClasses) {
		
		SimpleCommandMap map = get();
		if(map != null){
			List<Command> toRegister = new LinkedList<Command>();
			for(CommandInterface command : commands){
				PluginCommand commandWrapper = generate(command.getCommandName(), racesAndClasses);
				if(commandWrapper == null) continue;
				
				commandWrapper.setAliases(Arrays.asList(command.getAliases()));
				commandWrapper.setExecutor(command);
				toRegister.add(commandWrapper);
			}
			
			map.registerAll(racesAndClasses.getDescription().getName(), toRegister);
		}
	}
	
	/**
	 * Tries to generate a new Instance.
	 */
	private static PluginCommand generate(String name, Plugin plugin){
		try{
			Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
			constructor.setAccessible(true);
			
			return constructor.newInstance(name, plugin);
		}catch(Throwable exp){
			exp.printStackTrace();
			return null;
		}
	}
}
