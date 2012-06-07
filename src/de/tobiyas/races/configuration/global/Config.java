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

	private boolean config_racechat_enable;
	private String config_racechat_default_color;
	private boolean config_racechat_encrypt;
	
	private boolean config_whisper_enable;
	
	private int config_imunBetweenDamage;
	private int config_defaultHealth;
	
	private boolean config_enableDebugOutputs;

	private int config_globalUplinkTickPresition;

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
		config.addDefault("racechat.encryptForOthers", false);
		
		config.addDefault("whisper.enable", true);
		
		config.addDefault("health.defaultHealth", 20);
		config.addDefault("health.imunBetweenDamage", 1000);
		
		config.addDefault("debug.outputs.enable", false);
		
		config.addDefault("uplink.globalTickPresition", 10);

		config.options().copyDefaults(true);
		plugin.saveConfig();

	}
	
	
	private void reloadConfiguration(){
		plugin.reloadConfig();
		FileConfiguration config = plugin.getConfig();

		config_racechat_enable = config.getBoolean("racechat.enable", true);
		config_racechat_default_color = config.getString("racechat.default.color", "2");
		config_racechat_encrypt = config.getBoolean("racechat.encryptForOthers", false);
		
		config_whisper_enable = config.getBoolean("whisper.enable", true);
		
		config_defaultHealth = config.getInt("health.defaultHealth", 20);
		config_imunBetweenDamage = config.getInt("health.imunBetweenDamage", 1000);
		
		config_enableDebugOutputs = config.getBoolean("debug.outputs.enable", false);
		
		config_globalUplinkTickPresition = config.getInt("uplink.globalTickPresition", 10);

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

}
