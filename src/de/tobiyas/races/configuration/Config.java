/*
 * Races - by tobiyas
 * http://
 *
 * powered by Kickstarter
 */
 
 package de.tobiyas.races.configuration;

 
 import org.bukkit.configuration.file.FileConfiguration;

import de.tobiyas.races.Races;

 
 public class Config{
	private Races plugin;

	private boolean config_racechat_enable;
	private String config_racechat_default_color;


	public Config(Races plugin){
		this.plugin = plugin;
		setupConfiguration();
		reloadConfiguration();
	}

	private void setupConfiguration(){
		FileConfiguration config = plugin.getConfig();
		config.options().header("");

		config.addDefault("racechat.enable", true);
		config.addDefault("racechat.default.color", "2");

		config.options().copyDefaults(true);
		plugin.saveConfig();

	}
	
	
	private void reloadConfiguration(){
		plugin.reloadConfig();
		FileConfiguration config = plugin.getConfig();

		config_racechat_enable = config.getBoolean("racechat.enable", true);
		config_racechat_default_color = config.getString("racechat.default.color", "2");

	}
	
	
	public boolean getconfig_racechat_enable(){
		return config_racechat_enable;
	}

	public String getconfig_racechat_default_color(){
		return config_racechat_default_color;
	}

}
