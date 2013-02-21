package de.tobiyas.races.configuration.global;

public class ConfigManager {
	
	private GeneralConfig generalConfig;
	private ChannelConfig channelConfig;
	
	public ConfigManager(){
	}
	
	public void init(){
		generalConfig = new GeneralConfig();
		channelConfig = new ChannelConfig();
	}
	
	public GeneralConfig getGeneralConfig(){
		return generalConfig;
	}
	
	public ChannelConfig getChannelConfig(){
		return channelConfig;
	}
	


}
