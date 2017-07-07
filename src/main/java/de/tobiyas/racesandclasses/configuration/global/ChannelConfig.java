/*******************************************************************************
 * Copyright 2014 Tobias Welther
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.tobiyas.racesandclasses.configuration.global;

import java.io.File;
import java.io.IOException;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.util.consts.Consts;
import de.tobiyas.util.config.YAMLConfigExtended;

public class ChannelConfig {
	
	private RacesAndClasses plugin;
	private YAMLConfigExtended config;
	
	//vars to read
	private String config_channel_default_color;
	private String config_channel_default_format;
	
	private String config_racechat_default_color;
	private String config_racechat_default_format;
	
	private String config_worldchat_default_color;
	private String config_worldchat_default_format;
	
	private String config_globalchat_default_color;
	private String config_globalchat_default_format;
	
	private String config_localchat_default_color;
	private String config_localchat_default_format;
	private int config_localchat_range;
	
	private String config_playerJoinMessage;
	private String config_playerLeaveMessage;
	
	
	public ChannelConfig(){
		this.plugin = RacesAndClasses.getPlugin();
		initStructure();
		setupConfiguration();
	}
	
	private void initStructure(){
		File configPath = new File(Consts.channelConfigPathYML);
		if(!configPath.exists())
			configPath.mkdirs();
		
		File configFile = new File(Consts.channelConfigYML);
		if(!configFile.exists())
			try {
				configFile.createNewFile();
			} catch (IOException exception) {
				plugin.log("Counld not create File: " + configFile.toString());
				return;
			}
		
		config = new YAMLConfigExtended(Consts.channelConfigYML);
	}

	private void setupConfiguration(){
		config.load();

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
		
		config.addDefault("chat.channel.format.join", "{color}[{nick}] Player: &f[{sender}] {color}has joined the Channel.");
		config.addDefault("chat.channel.format.leave", "{color}[{nick}] Player: &f[{sender}] {color} has left the Channel.");

		config.options().copyDefaults(true);
		config.forceSave();
	}
	
	
	public void reload(){
		config.load();
		
		config_channel_default_color = config.getString("chat.channel.default.color", "&2");
		config_channel_default_format = config.getString("chat.channel.default.format", "{color}[{nick}] &f{prefix}{race}{sender}{suffix}{color}: {msg}");
		
		config_racechat_default_color = config.getString("chat.channel.race.default.color", "&3");
		config_racechat_default_format = config.getString("chat.channel.race.default.format", "{color}[{nick}] &f{prefix}{race}{sender}{suffix}{color}: {msg}");
		
		config_worldchat_default_color = config.getString("chat.channel.world.default.color", "&4");
		config_worldchat_default_format = config.getString("chat.channel.world.default.format", "{color}[{nick}] &f{prefix}{race}{sender}{suffix}{color}: {msg}");
		
		config_globalchat_default_color = config.getString("chat.channel.global.default.color", "&5");
		config_globalchat_default_format = config.getString("chat.channel.global.default.format", "{color}[{nick}] &f{prefix}{race}{sender}{suffix}{color}: {msg}");
		
		config_localchat_default_color = config.getString("chat.channel.local.default.color", "&f");
		config_localchat_default_format = config.getString("chat.channel.local.default.format", "{color}[Local] &f{prefix}{race}{sender}{suffix}{color}: {msg}");
		config_localchat_range = config.getInt("chat.channel.local.range", 100);
		
		config_playerJoinMessage = config.getString("chat.channel.format.join", "{color}[{nick}] Player: [{sender}] {color} has joined the Channel.");
		config_playerLeaveMessage = config.getString("chat.channel.format.leave", "{color}[{nick}] Player: [{sender}] {color} has left the Channel.");
	}

	public String getConfig_channel_default_color() {
		return config_channel_default_color;
	}

	public String getConfig_channel_default_format() {
		return config_channel_default_format;
	}

	public String getConfig_racechat_default_color() {
		return config_racechat_default_color;
	}

	public String getConfig_racechat_default_format() {
		return config_racechat_default_format;
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

	public String getConfig_localchat_default_color() {
		return config_localchat_default_color;
	}

	public String getConfig_localchat_default_format() {
		return config_localchat_default_format;
	}

	public int getConfig_localchat_range() {
		return config_localchat_range;
	}
	
	public String getConfig_PlayerJoinFormat(){
		return config_playerJoinMessage;
	}
	
	public String getConfig_PlayerLeaveFormat(){
		return config_playerLeaveMessage;
	}

}
