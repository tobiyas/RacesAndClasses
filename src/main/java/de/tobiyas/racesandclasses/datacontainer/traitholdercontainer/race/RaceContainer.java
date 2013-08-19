package de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.exceptions.HolderConfigParseException;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.exceptions.HolderParsingException;
import de.tobiyas.racesandclasses.traitcontainer.TraitStore;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.Trait;
import de.tobiyas.racesandclasses.util.chat.ChatColorUtils;
import de.tobiyas.racesandclasses.util.consts.Consts;
import de.tobiyas.util.config.YAMLConfigExtended;

public class RaceContainer extends AbstractTraitHolder{

	protected double raceMaxHealth;
	
	protected String raceChatColor;
	protected String raceChatFormat;
	
	
	/**
	 * Creates a Race Container with the passed Config and the passed name.
	 * 
	 * @param config to create with
	 * @param name to create with
	 */
	protected RaceContainer(YAMLConfigExtended config, String name){
		super(config, name);
	}
	
	
	/**
	 * Creates the RaceContainer from a sub-class
	 * 
	 * @param name
	 */
	protected RaceContainer(String name){
		super(new YAMLConfigExtended(Consts.racesYML), name);
	}
	
	@Override
	protected void readConfigSection() throws HolderConfigParseException{
		try{
			this.manaBonus = config.getDouble(holderName + ".config.manabonus", 0);
			this.holderTag = ChatColorUtils.decodeColors(config.getString(holderName + ".config.racetag", "[" + holderName + "]"));
			this.raceMaxHealth = config.getDouble(holderName + ".config.raceMaxHealth", RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_defaultHealth());
			this.raceChatColor = config.getString(holderName + ".config.chat.color", RacesAndClasses.getPlugin().getConfigManager().getChannelConfig().getConfig_racechat_default_color());
			this.raceChatFormat = config.getString(holderName + ".config.chat.format", RacesAndClasses.getPlugin().getConfigManager().getChannelConfig().getConfig_racechat_default_format());
			
			readArmor();
		}catch(Exception exp){
			throw new HolderConfigParseException();
		}
	}
	
	@Override
	protected void addSTDTraits(){
		Trait normalArrow = TraitStore.buildTraitByName("NormalArrow", this);
		normalArrow.setTraitHolder(this);
		traits.add(normalArrow);
	}
	
	/**
	 * loads a RaceContainer from an YamlConfig
	 * 
	 * @param config
	 * @param name
	 * @return the container
	 */
	public static RaceContainer loadRace(YAMLConfigExtended config, String name) throws HolderParsingException{
		RaceContainer container = new RaceContainer(config, name);
		return (RaceContainer) container.load();
	}
	
	@Override
	public boolean containsPlayer(String player){
		AbstractTraitHolder container = RacesAndClasses.getPlugin().getRaceManager().getHolderOfPlayer(player);
		if(container == null) return false;
		return container.getName().equals(holderName);
	}

	
	public double getRaceMaxHealth(){
		return raceMaxHealth;
	}


	public void editTABListEntry(String playerName) {
		Player player = Bukkit.getPlayer(playerName);
		if(player == null) return;
		if(!RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_adaptListName()) return;
		String total = holderTag + player.getName();
		if(total.length() > 16)
			total = total.substring(0, 15);
		player.setPlayerListName(total);
	}
	
	public String getRaceChatColor(){
		return raceChatColor;
	}
	
	public String getRaceChatFormat(){
		return raceChatFormat;
	}
	
	
	/**
	 * Creates the default race with default traits and values.
	 * @return
	 */
	public static RaceContainer generateSTDRace(){
		String defaultName = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_defaultRaceName();
		String defaultTag = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_defaultRaceTag();
		Consts.defaultRace = defaultName;
		
		String holderName = Consts.defaultRace;
		RaceContainer container = new RaceContainer(null, holderName);

		container.holderTag = defaultTag;
		container.raceChatColor = "";
		container.raceChatFormat = "";

		container.raceMaxHealth = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_defaultHealth();
		container.armorUsage = new boolean[] { false, false, false, false, false };
	    container.traits = new HashSet<Trait>();

	    container.addSTDTraits();
	    
	    return container;
	}


	@Override
	protected String getContainerTypeAsString() {
		return "race";
	}
}
