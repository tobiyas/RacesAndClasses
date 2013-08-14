package org.bukkit.command;

import org.bukkit.plugin.Plugin;

public class PlugincommandFactory {

	public static PluginCommand create(String name, Plugin owner){
		return new PluginCommand(name, owner);
	}

}
