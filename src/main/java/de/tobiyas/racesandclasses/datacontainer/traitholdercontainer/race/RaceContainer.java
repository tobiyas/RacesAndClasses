package de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.exceptions.HolderConfigParseException;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.exceptions.HolderParsingException;
import de.tobiyas.racesandclasses.traitcontainer.TraitStore;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.Trait;
import de.tobiyas.racesandclasses.util.chat.ChatColorUtils;
import de.tobiyas.racesandclasses.util.consts.Consts;

public class RaceContainer extends AbstractTraitHolder{

	private int raceMaxHealth;
	
	private String raceChatColor;
	private String raceChatFormat;
	
	
	protected RaceContainer(YamlConfiguration config, String name){
		super(config, name);
	}
	
	
	@Override
	protected void readConfigSection() throws HolderConfigParseException{
		try{
			if(!config.isConfigurationSection(holderName + ".config")){
				throw new Exception();
			}
			
			holderTag = ChatColorUtils.decodeColors(config.getString(holderName + ".config.racetag"));
			raceMaxHealth = config.getInt(holderName + ".config.raceMaxHealth", RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_defaultHealth());
			raceChatColor = config.getString(holderName + ".config.chat.color", RacesAndClasses.getPlugin().getConfigManager().getChannelConfig().getConfig_racechat_default_color());
			raceChatFormat = config.getString(holderName + ".config.chat.format", RacesAndClasses.getPlugin().getConfigManager().getChannelConfig().getConfig_racechat_default_format());
			
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
	public static RaceContainer loadRace(YamlConfiguration config, String name) throws HolderParsingException{
		RaceContainer container = new RaceContainer(config, name);
		
		return (RaceContainer) container.load();
	}
	
	@Override
	public boolean containsPlayer(String player){
		AbstractTraitHolder container = RaceManager.getInstance().getHolderOfPlayer(player);
		if(container == null) return false;
		return container.getName().equals(holderName);
	}

	
	public int getRaceMaxHealth(){
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
		String holderName = Consts.defaultRace;
		RaceContainer container = new RaceContainer(null, holderName);

		container.holderTag = "[NoRace]";
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
