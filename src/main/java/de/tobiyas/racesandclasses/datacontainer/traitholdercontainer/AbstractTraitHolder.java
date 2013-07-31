package de.tobiyas.racesandclasses.datacontainer.traitholdercontainer;

import static de.tobiyas.racesandclasses.util.traitutil.TraitConfigParser.configureTraitFromYAML;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.file.YamlConfiguration;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.exceptions.HolderConfigParseException;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.exceptions.HolderParsingException;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.exceptions.HolderTraitParseException;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.permissionsettings.HolderPermissions;
import de.tobiyas.racesandclasses.traitcontainer.TraitStore;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitInfos;
import de.tobiyas.racesandclasses.util.items.ItemUtils.ItemQuality;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public abstract class AbstractTraitHolder {
	protected YamlConfiguration config;
	protected String holderName;
	
	protected String holderTag;
	
	protected boolean[] armorUsage;
	protected HashSet<Trait> traits;
	
	protected HolderPermissions holderPermissions;
	
	/**
	 * Creates an {@link AbstractTraitHolder}
	 * 
	 * @param config
	 * @param name
	 */
	protected AbstractTraitHolder(YamlConfiguration config, String name) {
		this.config = config;
		this.holderName = name;
		
	}
	
	public AbstractTraitHolder load() throws HolderParsingException{
		readConfigSection();
		readTraitSection();
		readPermissionSection();
		
		return this;
	}
	

	protected abstract void readConfigSection() throws HolderConfigParseException;
	
	
	protected void readArmor(){
		armorUsage = new boolean[]{false, false, false, false, false};
		String armorString = config.getString(holderName + ".config.armor", "").toLowerCase();
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
	
	protected String decodeColors(String message){
		if(message == null) return "";
		return message.replaceAll("(&([a-f0-9]))", "§$2");
	}
	
	
	protected void readTraitSection() throws HolderTraitParseException{
		traits = new HashSet<Trait>();
		
		if(!config.isConfigurationSection(holderName + ".traits")){
			throw new HolderTraitParseException("The Holder: " + holderName + " has no 'traits' section.'");
		}
		
		Set<String> traitNames = config.getConfigurationSection(holderName + ".traits").getKeys(false);
		if(traitNames == null || traitNames.size() == 0) return;
		
		for(String traitName : traitNames){
			try{
				Trait trait = TraitStore.buildTraitByName(traitName, this);
				if(trait != null){
					String configPath = holderName + ".traits." + traitName;
					configureTraitFromYAML(config, configPath, trait);
					
					traits.add(trait);
				}
			}catch(TraitConfigurationFailedException exp){
				throw new HolderTraitParseException(exp.getMessage());
			}
		}
		
		addSTDTraits();
	}
	
	protected void readPermissionSection() {
		holderPermissions = new HolderPermissions(getContainerTypeAsString() + "-" + holderName);
		
		if(!config.isList(holderName + ".permissions")){
			return;
		}
		
		List<String> permissionList = config.getStringList(holderName + ".permissions");
		holderPermissions.add(permissionList);
	}
	

	protected abstract void addSTDTraits();
	
	public abstract boolean containsPlayer(String name);
	
	/**
	 * Returns the Permissions this holder has additional
	 * 
	 * @return as {@link List} of {@link String}
	 */
	public HolderPermissions getPermissions(){
		return holderPermissions;
	}
	
	/**
	 * The name of the Holder
	 * 
	 * @return
	 */
	public String getName(){
		return holderName;
	}
	
	
	public String getTag(){
		return holderTag;
	}
	
	
	public Set<Trait> getTraits(){
		return traits;
	}
	
	
	/**
	 * Returns all Traits that are visible to the viewer
	 * 
	 * @return
	 */
	public Set<Trait> getVisibleTraits(){
		Set<Trait> traitSet = new HashSet<Trait>();
		for(Trait trait : traits){
			if(isVisible(trait)){
				traitSet.add(trait);
			}
		}
		
		return traitSet;
	}
	
	
	/**
	 * Checks if a Trait is visible by checking it's import Trait Annotation
	 * 
	 * @param trait
	 * @return
	 */
	private boolean isVisible(Trait trait) {
		try{
			Method importMethod = trait.getClass().getMethod("importTrait");
			TraitInfos infos = importMethod.getAnnotation(TraitInfos.class);
			
			boolean isVisable = infos.visible();
			return isVisable;
		}catch(Exception exp){}
		
		return false;
	}

	/**
	 * Returns a readable String for the Armor Permissions
	 * 
	 * @return
	 */
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
	
	@Override
	public String toString(){
		return holderName;
	}
	
	/**
	 * Returns the type name of the container
	 * 
	 * @return
	 */
	protected abstract String getContainerTypeAsString();
}
