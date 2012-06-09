/*
 * Races - by tobiyas
 * http://
 *
 * powered by Kickstarter
 */
 
 package de.tobiyas.races.configuration.global;

 
 import org.bukkit.configuration.file.FileConfiguration;

import de.tobiyas.races.Races;

 
 public class Config{
	private Races plugin;

	private String config_racechat_default_color;
	private String config_racechat_default_format;
	private boolean config_racechat_encrypt;
	
	private int config_imunBetweenDamage;
	private int config_defaultHealth;

	private int config_globalUplinkTickPresition;
	
	//Enables:
	private boolean config_racechat_enable;
	private boolean config_whisper_enable;
	
	private boolean config_enableDebugOutputs;
	private boolean config_classes_enable;

	private boolean config_channels_enable;

	public Config(Races plugin){
		this.plugin = plugin;
		setupConfiguration();
		reloadConfiguration();
	}

	private void setupConfiguration(){
		FileConfiguration config = plugin.getConfig();
		config.options().header("");

		config.addDefault("chat.race.enable", true);
		config.addDefault("chat.default.color", "&2");
		config.addDefault("chat.default.format", "{color}[{nick}] &f{prefix}{sender}{suffix}{color}: {msg}");
		config.addDefault("chat.race.encryptForOthers", false);
		
		config.addDefault("whisper.enable", true);
		
		config.addDefault("health.defaultHealth", 20);
		config.addDefault("health.imunBetweenDamage", 1000);
		
		config.addDefault("debug.outputs.enable", false);
		
		config.addDefault("uplink.globalTickPresition", 10);
		
		config.addDefault("classes.enable", true);

		config.options().copyDefaults(true);
		plugin.saveConfig();

	}
	
	
	private void reloadConfiguration(){
		plugin.reloadConfig();
		FileConfiguration config = plugin.getConfig();

		config_racechat_enable = config.getBoolean("chat.race.enable", true);
		config_channels_enable = config.getBoolean("chat.channel.enable", true);
		config_racechat_default_color = config.getString("chat.default.color", "&2");
		config_racechat_default_format = config.getString("chat.default.format", "{color}[{nick}] &f{prefix}{sender}{suffix}{color}: {msg}");
		config_racechat_encrypt = config.getBoolean("chat.race.encryptForOthers", false);
		
		config_whisper_enable = config.getBoolean("whisper.enable", true);
		
		config_defaultHealth = config.getInt("health.defaultHealth", 20);
		config_imunBetweenDamage = config.getInt("health.imunBetweenDamage", 1000);
		
		config_enableDebugOutputs = config.getBoolean("debug.outputs.enable", false);
		
		config_globalUplinkTickPresition = config.getInt("uplink.globalTickPresition", 10);
		
		config_classes_enable = config.getBoolean("classes.enable", true);

	}
	
	
	public boolean getconfig_racechat_enable(){
		return config_racechat_enable;
	}

	public String getconfig_racechat_default_color(){
		return config_racechat_default_color;
	}
	
	public int getconfig_defaultHealth(){
		return config_defaultHealth;
	}
	
	public boolean getconfig_whisperEnable(){
		return config_whisper_enable;
	}

	public long getconfig_imunBetweenDamage() {
		return config_imunBetweenDamage;
	}
	
	public boolean getconfig_enableDebugOutputs(){
		return config_enableDebugOutputs;
	}
	
	public int getconfig_globalUplinkTickPresition(){
		return config_globalUplinkTickPresition;
	}

	public boolean getconfig_chatEncrypt() {
		return config_racechat_encrypt;
	}
	
	public boolean getconfig_classes_enable(){
		return config_classes_enable;
	}
	
	public String getconfig_racechat_default_format(){
		return config_racechat_default_format;
	}

	public boolean getconfig_channels_enable(){
		return config_channels_enable;
	}

}
