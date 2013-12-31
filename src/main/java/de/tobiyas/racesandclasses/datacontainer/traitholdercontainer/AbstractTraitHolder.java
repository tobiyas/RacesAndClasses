package de.tobiyas.racesandclasses.datacontainer.traitholdercontainer;

import static de.tobiyas.racesandclasses.util.traitutil.TraitConfigParser.configureTraitFromYAML;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.exceptions.HolderConfigParseException;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.exceptions.HolderParsingException;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.exceptions.HolderTraitParseException;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.permissionsettings.HolderPermissions;
import de.tobiyas.racesandclasses.traitcontainer.TraitStore;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.util.items.ItemUtils.ItemQuality;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;
import de.tobiyas.util.config.YAMLConfigExtended;
import de.tobiyas.util.items.MaterialParser;

public abstract class AbstractTraitHolder {
	
	/**
	 * The config of the holder to store / load stuff
	 */
	protected final YAMLConfigExtended config;
	
	/**
	 * The name of the holder
	 */
	protected final String holderName;
	
	/**
	 * The pretty tag of the holder
	 */
	protected String holderTag;
	
	/**
	 * The armor permissions of the Holder
	 */
	protected boolean[] armorUsage;
	
	/**
	 * A set of Traits that the holder contains
	 */
	protected Set<Trait> traits;
	
	/**
	 * The permission container holding all Permissions for the holder
	 */
	protected final HolderPermissions holderPermissions;
	
	/**
	 * The magic bonus to the mana pool.
	 */
	protected double manaBonus;
	
	/**
	 * Logs all parsing exceptions happening during startup.
	 */
	protected final List<HolderTraitParseException> parsingExceptionsHappened;
	
	/**
	 * Material additionally used for wands.
	 */
	protected final Set<Material> additionalWandMaterials;
	
	
	/**
	 * Creates an {@link AbstractTraitHolder}
	 * 
	 * @param config to load from
	 * @param name of the holder
	 */
	protected AbstractTraitHolder(YAMLConfigExtended config, String name) {
		this.config = config;
		this.holderName = name;
		this.parsingExceptionsHappened = new LinkedList<HolderTraitParseException>();
		this.armorUsage = new boolean[]{false, false, false, false, false};
		this.traits = new HashSet<Trait>();
		this.holderTag = "[" + name + "]";
		this.additionalWandMaterials = new HashSet<Material>();
		
		this.holderPermissions = new HolderPermissions(getContainerTypeAsString() + "-" + holderName);
		this.manaBonus = 0;
	}
	
	/**
	 * Loads the Holder from the config file passed in constructor.
	 * If parsing fails, a HolderParsingException is thrown.
	 * 
	 * @return the parsed Holder
	 * 
	 * @throws HolderParsingException if the parsing failed.
	 */
	public AbstractTraitHolder load() throws HolderParsingException{
		readConfigSection();
		readTraitSection();
		readPermissionSection();
		readAdditionalWandMaterial();
		
		return this;
	}
	

	/**
	 * Reads the WandMaterial Section.
	 */
	protected void readAdditionalWandMaterial() {
		additionalWandMaterials.clear();
		if(!config.contains(holderName + ".config.wandMaterial")) return;
		
		List<String> wandMatList = config.getStringList(holderName + ".config.wandMaterial");
		additionalWandMaterials.addAll(MaterialParser.parseToMaterial(wandMatList));
	}
	

	/**
	 * Reads the configuration section of the holder.
	 * Expect this to be called in the load process.
	 * 
	 * @throws HolderConfigParseException if the parsing failed.
	 */
	protected abstract void readConfigSection() throws HolderConfigParseException;
	
	
	/**
	 * Reads the Armor permissions from the Holder and parses it.
	 */
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
		
		if(armorString.contains("all")){
			armorUsage[0] = true;
			armorUsage[1] = true;
			armorUsage[2] = true;
			armorUsage[3] = true;
			armorUsage[4] = true;
		}
	}
	
	
	/**
	 * Parses the Trait section of the Holder.
	 * When parsing fails, an {@link HolderTraitParseException} is thrown.
	 * 
	 * @throws HolderTraitParseException if parsing fails.
	 */
	protected void readTraitSection(){
		traits = new HashSet<Trait>();
		addSTDTraits();
		
		if(!config.isConfigurationSection(holderName + ".traits")){
			return; //trait section is not necessary
		}
		
		Set<String> traitNames = config.getConfigurationSection(holderName + ".traits").getKeys(false);
		if(traitNames == null || traitNames.size() == 0) return;
		
		List<HolderTraitParseException> exceptionList = new LinkedList<HolderTraitParseException>();
		
		for(String traitName : traitNames){
			try{
				Trait trait = TraitStore.buildTraitByName(traitName, this);
				if(trait != null){
					String configPath = holderName + ".traits." + traitName;
					configureTraitFromYAML(config, configPath, trait);
					trait.generalInit();
					
					traits.add(trait);
				}
			}catch(TraitConfigurationFailedException exp){
				exceptionList.add(new HolderTraitParseException(exp.getMessage()));
				RacesAndClasses.getPlugin().log("Error on parsing: '" + getName() + "' Problem was: '" + exp.getMessage() 
						+ "' On Trait: '" + traitName + "'.");
			}
		}
		
		this.parsingExceptionsHappened.addAll(exceptionList);
	}
	
	/**
	 * Reads the Permission section of the Holder.
	 */
	protected void readPermissionSection() {
		holderPermissions.clear();
		
		if(!config.isList(holderName + ".permissions")){
			return;
		}
		
		List<String> permissionList = config.getStringList(holderName + ".permissions");
		holderPermissions.add(permissionList);
	}
	

	/**
	 * Adds STD Traits that every holder has to the Trait list.
	 */
	protected abstract void addSTDTraits();
	
	
	/**
	 * Returns if a Player is member of this holder
	 * 
	 * @param playerName to check
	 * @return true if is member, false otherwise
	 */
	public abstract boolean containsPlayer(String playerName);
	
	
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
		Set<ItemQuality> qualities = getArmorPerms();
		String armorString = "";
		for(ItemQuality quality : qualities)
			armorString += quality.name() + " ";
		
		return armorString;
	}

	
	/**
	 * Returns a List of {@link ItemQuality} what this Holder can wear
	 */
	public Set<ItemQuality> getArmorPerms(){
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
	 * Returns the type name of the container.
	 * WARNING: This method is already called in the Constructor!!! So make sure it returns an constant String!
	 * 
	 * @return
	 */
	protected abstract String getContainerTypeAsString();

	/**
	 * @return the parsingExceptionsHappened
	 */
	public List<HolderTraitParseException> getParsingExceptionsHappened() {
		return parsingExceptionsHappened;
	}

	/**
	 * @return the manaBonus
	 */
	public double getManaBonus() {
		return manaBonus;
	}

	/**
	 * Returns the Armor perms as loaded naturaly
	 * @return
	 */
	public boolean[] getArmorPermsAsBoolArray() {
		return armorUsage;
	}
	
	
	/**
	 * Returns the HolderManager of the TraitHolder.
	 * 
	 * @return the holderManager of the TraitHolder
	 */
	public abstract AbstractHolderManager getHolderManager();

	
	/**
	 * Returns all Wand materials for the Holder
	 * <br>List can be empty.
	 * <br>This is only a clone set.
	 * 
	 * @return set of all Wand Materials.
	 */
	public Set<Material> getAdditionalWandMaterials() {
		return new HashSet<Material>(additionalWandMaterials);
	}
	
	
}
