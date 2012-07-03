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
	
	private String config_channel_default_color;
	private String config_channel_default_format;
	private String config_worldchat_default_color;
	private String config_worldchat_default_format;
	private String config_globalchat_default_color;
	private String config_globalchat_default_format;
	
	private String config_localchat_default_color;
	private String config_localchat_default_format;
	private int config_localchat_range;
	
	private int config_imunBetweenDamage;
	private int config_defaultHealth;

	private int config_globalUplinkTickPresition;
	
	private boolean config_adaptListName;
	
	//Enables:
	private boolean config_whisper_enable;
	
	private boolean config_enableDebugOutputs;
	private boolean config_enableErrorUpload;
	private boolean config_enableDebugWriteThrough;
	
	private boolean config_classes_enable;

	private boolean config_channels_enable;

	private boolean config_metrics_enabled;
	
	private boolean config_activate_reminder;
	private int config_reminder_interval;
	private boolean config_enable_expDropBonus;

	public Config(Races plugin){
		this.plugin = plugin;
		setupConfiguration();
		reloadConfiguration();
	}

	private void setupConfiguration(){
		FileConfiguration config = plugin.getConfig();
		//config.options().header("");

		config.addDefault("chat.channel.enable", true);
		config.addDefault("chat.whisper.enable", true);
		config.addDefault("chat.race.encryptForOthers", false);
		config.addDefault("chat.channel.default.color", "&2");
		config.addDefault("chat.channel.default.format", "{color}[{nick}] &f{prefix}{sender}{suffix}{color}: {msg}");
		
		config.addDefault("chat.channel.race.default.color", "&3");
		config.addDefault("chat.channel.race.default.format", "{color}[{nick}] &f{prefix}{sender}{suffix}{color}: {msg}");
		config.addDefault("chat.channel.world.default.color", "&4");
		config.addDefault("chat.channel.world.default.format", "{color}[{nick}] &f{prefix}{sender}{suffix}{color}: {msg}");
		config.addDefault("chat.channel.global.default.color", "&5");
		config.addDefault("chat.channel.global.default.format", "{color}[{nick}] &f{prefix}{sender}{suffix}{color}: {msg}");
		
		config.addDefault("chat.channel.local.default.color", "&f");
		config.addDefault("chat.channel.local.default.format", "{color}[Local] &f{prefix}{sender}{suffix}{color}: {msg}");
		config.addDefault("chat.channel.local.range", 100);
		
		config.addDefault("health.defaultHealth", 20);
		config.addDefault("health.imunBetweenDamage", 500);
		
		config.addDefault("debug.outputs.enable", true);
		config.addDefault("debug.outputs.errorUpload", true);
		config.addDefault("debug.outputs.writethrough", false);
		
		config.addDefault("uplink.globalTickPresition", 10);
		
		config.addDefault("classes.enable", true);
		
		config.addDefault("metrics.enable", true);
		
		config.addDefault("races.remindDefaultRace.enable", true);
		config.addDefault("races.remindDefaultRace.interval", 10);
		config.addDefault("races.display.adaptListName", true);
		
		config.addDefault("races.drops.enable", true);

		config.options().copyDefaults(true);
		plugin.saveConfig();

	}
	
	
	private void reloadConfiguration(){
		plugin.reloadConfig();
		FileConfiguration config = plugin.getConfig();

		config_channels_enable = config.getBoolean("chat.channel.enable", true);
		config_racechat_encrypt = config.getBoolean("chat.race.encryptForOthers", false);
		config_channel_default_color = config.getString("chat.channel.default.color", "&2");
		config_channel_default_format = config.getString("chat.channel.default.format", "{color}[{nick}] &f{prefix}{sender}{suffix}{color}: {msg}");
		
		config_racechat_default_color = config.getString("chat.channel.race.default.color", "&3");
		config_racechat_default_format = config.getString("chat.channel.race.default.format", "{color}[{nick}] &f{prefix}{sender}{suffix}{color}: {msg}");
		config_worldchat_default_color = config.getString("chat.channel.world.default.color", "&4");
		config_worldchat_default_format = config.getString("chat.channel.world.default.format", "{color}[{nick}] &f{prefix}{sender}{suffix}{color}: {msg}");
		config_globalchat_default_color = config.getString("chat.channel.global.default.color", "&5");
		config_globalchat_default_format = config.getString("chat.channel.global.default.format", "{color}[{nick}] &f{prefix}{sender}{suffix}{color}: {msg}");
		
		config_localchat_default_color = config.getString("chat.channel.local.default.color", "&f");
		config_localchat_default_format = config.getString("chat.channel.local.default.format", "{color}[Local] &f{prefix}{sender}{suffix}{color}: {msg}");
		config_localchat_range = config.getInt("chat.channel.local.range", 100);		
		
		config_whisper_enable = config.getBoolean("chat.channelwhisper.enable", true);
		
		config_defaultHealth = config.getInt("health.defaultHealth", 20);
		config_imunBetweenDamage = config.getInt("health.imunBetweenDamage", 1000);
		
		config_enableDebugOutputs = config.getBoolean("debug.outputs.enable", true);
		config_enableErrorUpload = config.getBoolean("debug.outputs.errorUpload", true);
		config_enableDebugWriteThrough = config.getBoolean("debug.outputs.writethrough", false);
		
		config_globalUplinkTickPresition = config.getInt("uplink.globalTickPresition", 10);
		
		config_classes_enable = config.getBoolean("classes.enable", true);
		config_metrics_enabled = config.getBoolean("metrics.enable", true);
		
		config_activate_reminder = config.getBoolean("races.remindDefaultRace.enable", true);
		config_reminder_interval = config.getInt("races.remindDefaultRace.interval", 10);
		config_adaptListName = config.getBoolean("races.display.adaptListName", true);
		
		config_enable_expDropBonus = config.getBoolean("races.drops.enable", true);

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
	
	public boolean getconfig_enableDebugWriteThrough(){
		return config_enableDebugWriteThrough;
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

	public boolean getconfig_channels_enable(){
		return config_channels_enable;
	}

	public boolean getconfig_metrics_enabled() {
		return config_metrics_enabled;
	}

	public String getConfig_racechat_default_color() {
		return config_racechat_default_color;
	}

	public String getConfig_racechat_default_format() {
		return config_racechat_default_format;
	}

	public String getConfig_channel_default_color() {
		return config_channel_default_color;
	}

	public String getConfig_channel_default_format() {
		return config_channel_default_format;
	}

	public String getConfig_worldchat_default_color() {
		return config_worldchat_default_color;
	}

	public String getConfig_worldchat_default_format() {
		return config_worldchat_default_format;
	}

	public String getConfig_globalchat_default_color() {
		return config_globalchat_default_color;
	}

	public String getConfig_globalchat_default_format() {
		return config_globalchat_default_format;
	}
	
	public String getConfig_localchat_default_color(){
		return config_localchat_default_color;
	}
	
	public String getConfig_localchat_default_format(){
		return config_localchat_default_format;
	}

	public int getConfig_localchat_range() {
		return config_localchat_range;
	}
	
	public boolean getConfig_activate_reminder(){
		return config_activate_reminder;
	}
	
	public int getConfig_reminder_interval(){
		return config_reminder_interval;
	}
	
	public boolean getConfig_enable_expDropBonus(){
		return config_enable_expDropBonus;
	}

	public boolean getConfig_AdaptListName() {
		return config_adaptListName;
	}
	
	public boolean getConfig_enableErrorUpload(){
		return config_enableErrorUpload;
	}

}
