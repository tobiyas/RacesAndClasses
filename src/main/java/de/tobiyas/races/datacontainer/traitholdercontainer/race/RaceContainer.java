package de.tobiyas.races.datacontainer.traitholdercontainer.race;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.tobiyas.races.Races;
import de.tobiyas.races.datacontainer.traitcontainer.TraitStore;
import de.tobiyas.races.datacontainer.traitcontainer.traits.Trait;
import de.tobiyas.races.util.consts.Consts;
import de.tobiyas.races.util.items.ItemUtils.ItemQuality;

public class RaceContainer {

	private YamlConfiguration config;
	private String raceName;
	
	private String raceTag;
	private int raceMaxHealth;
	
	private String raceChatColor;
	private String raceChatFormat;
	
	private boolean[] armorUsage;
	
	private HashSet<Trait> traits;
	
	private static RaceManager manager = RaceManager.getManager();
	
	private RaceContainer(YamlConfiguration config, String name){
		this.config = config;
		this.raceName = name;
		
		readConfigSection();
		readTraitSection();
	}
	
	public RaceContainer(){
		config = null;
		raceName = Consts.defaultRace;
		
		raceTag = "[NoRace]";
		raceChatColor = "";
		raceChatFormat = "";
		
		raceMaxHealth = Races.getPlugin().getGeneralConfig().getConfig_defaultHealth();
		armorUsage = new boolean[]{false, false, false, false, false};
		traits = new HashSet<Trait>();
		
		addSTDTraits();
	}
	
	private void readConfigSection(){
		raceTag = decodeColors(config.getString("races." + raceName + ".config.racetag"));
		raceMaxHealth = config.getInt("races." + raceName + ".config.raceMaxHealth", Races.getPlugin().getGeneralConfig().getConfig_defaultHealth());
		raceChatColor = config.getString("races." + raceName + ".config.chat.color", Races.getPlugin().getChannelConfig().getConfig_racechat_default_color());
		raceChatFormat = config.getString("races." + raceName + ".config.chat.format", Races.getPlugin().getChannelConfig().getConfig_racechat_default_format());
		
		readArmor();
	}
	
	private void readArmor(){
		armorUsage = new boolean[]{false, false, false, false, false};
		String armorString = config.getString("races." + raceName + ".config.armor", "").toLowerCase();
		if(armorString.contains("leather"))
			armorUsage[0] = true;
		
		if(armorString.contains("iron"))
			armorUsage[1] = true;
		
		if(armorString.contains("gold"))
			armorUsage[2] = true;
		
		if(armorString.contains("diamond"))
			armorUsage[3] = true;
		
		if(armorString.contains("chain"))
			armorUsage[4] = true;
	}
	
	private String decodeColors(String message){
		return message.replaceAll("(&([a-f0-9]))", "§$2");
	}
	
	private void readTraitSection(){
		traits = new HashSet<Trait>();
		
		Set<String> traitNames = config.getConfigurationSection("races." + raceName + ".traits").getKeys(false);
		if(traitNames == null || traitNames.size() == 0) return;
		
		for(String traitName : traitNames){
			Trait trait = TraitStore.buildTraitByName(traitName, this);
			if(trait != null){
				Object value = config.getString("races." + raceName + ".traits." + traitName);
				trait.setValue(value);
				traits.add(trait);
			}
		}
		
		addSTDTraits();
	}
	
	private void addSTDTraits(){
		Trait normalArrow = TraitStore.buildTraitByName("NormalArrow", this);
		traits.add(normalArrow);
	}
	
	/**
	 * loads a RaceContainer from an YamlConfig
	 * 
	 * @param config
	 * @param name
	 * @return the container
	 */
	public static RaceContainer loadRace(YamlConfiguration config, String name){
		RaceContainer container = new RaceContainer(config, name);
		
		return container;
	}
	
	public boolean containsPlayer(String player){
		RaceContainer container = manager.getRaceOfPlayer(player);
		if(container == null) return false;
		return container.getName().equals(raceName);
	}
	
	public String getName(){
		return raceName;
	}
	
	public String getTag(){
		return raceTag;
	}
	
	public Set<Trait> getTraits(){
		return traits;
	}
	
	public Set<Trait> getVisibleTraits(){
		Set<Trait> traitSet = new HashSet<Trait>();
		for(Trait trait : traits)
			if(trait.isVisible())
				traitSet.add(trait);
		
		return traitSet;
	}
	
	public String getArmorString(){
		HashSet<ItemQuality> qualities = getArmorPerms();
		String armorString = "";
		for(ItemQuality quality : qualities)
			armorString += quality.name() + " ";
		
		return armorString;
	}
	
	public HashSet<ItemQuality> getArmorPerms(){
		HashSet<ItemQuality> perms = new HashSet<ItemQuality>();
		if(armorUsage[0])
			perms.add(ItemQuality.Leather);
		if(armorUsage[1])
			perms.add(ItemQuality.Iron);
		if(armorUsage[2])
			perms.add(ItemQuality.Gold);
		if(armorUsage[3])
			perms.add(ItemQuality.Diamond);
		if(armorUsage[4])
			perms.add(ItemQuality.Chain);
		
		return perms;
	}
	
	public int getRaceMaxHealth(){
		return raceMaxHealth;
	}
	
	@Override
	public String toString(){
		return raceName;
	}

	public void setListEntry(Player player) {
		if(player == null) return;
		if(!Races.getPlugin().getGeneralConfig().getConfig_AdaptListName()) return;
		String name = player.getName();
		String total = raceTag + name;
		if(total.length() > 16)
			total = total.substring(0, 16);
		player.setPlayerListName(total);
	}
	
	public String getRaceChatColor(){
		return raceChatColor;
	}
	
	public String getRaceChatFormat(){
		return raceChatFormat;
	}
	
}
