package de.tobiyas.racesandclasses.configuration.managing;

import de.tobiyas.racesandclasses.configuration.global.ChannelConfig;
import de.tobiyas.racesandclasses.configuration.global.GeneralConfig;
import de.tobiyas.racesandclasses.configuration.member.MemberConfigManager;
import de.tobiyas.racesandclasses.configuration.racetoclass.RaceToClassConfiguration;
import de.tobiyas.racesandclasses.configuration.traits.TraitConfigManager;

public class ConfigManager {
	
	private GeneralConfig generalConfig;
	private ChannelConfig channelConfig;
	private RaceToClassConfiguration raceToClassConfig;
	
	private MemberConfigManager memberConfigManager;
	private TraitConfigManager traitConfigManager;
	
	
	/**
	 * Creates all Configurations
	 */
	public ConfigManager(){
		generalConfig = new GeneralConfig();
		channelConfig = new ChannelConfig();
		raceToClassConfig = new RaceToClassConfiguration();
		memberConfigManager = new MemberConfigManager();
		traitConfigManager = new TraitConfigManager();
	}
	
	/**
	 * reloads all Configurations
	 */
	public void reload(){
		generalConfig.reload();
		channelConfig.reload();
		raceToClassConfig.reload();
		memberConfigManager.reload();
		traitConfigManager.reload();
	}
	
	
	/**
	 * Gets the general global config with general options
	 * 
	 * @return
	 */
	public GeneralConfig getGeneralConfig(){
		return generalConfig;
	}
	
	
	/**
	 * Gets the config for the Channels
	 * @return
	 */
	public ChannelConfig getChannelConfig(){
		return channelConfig;
	}

	
	/**
	 * Gets the Selection config from Race -> Class
	 * 
	 * @return
	 */
	public RaceToClassConfiguration getRaceToClassConfig() {
		return raceToClassConfig;
	}

	/**
	 * The member config manager to get configs of a member
	 * 
	 * @return the memberConfigManager
	 */
	public MemberConfigManager getMemberConfigManager() {
		return memberConfigManager;
	}

	/**
	 * @return the traitConfigManager
	 */
	public TraitConfigManager getTraitConfigManager() {
		return traitConfigManager;
	}
	


}
