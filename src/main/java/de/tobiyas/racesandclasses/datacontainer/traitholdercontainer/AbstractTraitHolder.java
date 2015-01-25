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
package de.tobiyas.racesandclasses.datacontainer.traitholdercontainer;

import static de.tobiyas.racesandclasses.util.traitutil.TraitConfigParser.configureTraitFromYAML;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.exceptions.HolderConfigParseException;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.exceptions.HolderParsingException;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.exceptions.HolderTraitParseException;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.permissionsettings.HolderPermissions;
import de.tobiyas.racesandclasses.traitcontainer.TraitStore;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.util.items.ItemUtils.ItemQuality;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;
import de.tobiyas.util.config.YAMLConfigExtended;
import de.tobiyas.util.items.MaterialParser;

public abstract class AbstractTraitHolder {
	
	/**
	 * The config of the holders to store / load stuff
	 */
	protected final YAMLConfigExtended config;
	
	/**
	 * The name of the holders
	 */
	protected final String configNodeName;
	
	/**
	 * The Display Name to use.
	 */
	protected String displayName;
	
	/**
	 * The pretty tag of the holders
	 */
	protected String holderTag;
	
	/**
	 * The armor permissions of the Holder
	 */
	protected boolean[] armorUsage;

	/**
	 * The Material of the Holder to select
	 */
	protected ItemStack holderSelectionItem;
	
	/**
	 * A set of Traits that the holders contains
	 */
	protected Set<Trait> traits;
	
	/**
	 * The permission container holding all Permissions for the holders
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
	 * The Description of the Holder.
	 * If null, none is present.
	 */
	protected String holderDescription = null;
	
	/**
	 * The Pre holder needed.
	 * <br>If null no preHolder needed.
	 */
	protected String needsPreHolder = null;
	
	/**
	 * The parents of this holders.
	 */
	protected final Set<AbstractTraitHolder> parents = new HashSet<AbstractTraitHolder>();
	
	
	
	/**
	 * Creates an {@link AbstractTraitHolder}
	 * 
	 * @param config to load from
	 * @param name of the holders
	 */
	protected AbstractTraitHolder(YAMLConfigExtended config, String name) {
		this.config = config;
		this.configNodeName = name;
		this.displayName = name;
		this.parsingExceptionsHappened = new LinkedList<HolderTraitParseException>();
		this.armorUsage = new boolean[]{false, false, false, false, false};
		this.traits = new HashSet<Trait>();
		this.holderTag = "[" + name + "]";
		this.additionalWandMaterials = new HashSet<Material>();
		this.holderSelectionItem = new ItemStack(Material.BOOK_AND_QUILL);
		
		this.holderPermissions = new HolderPermissions(getContainerTypeAsString() + "-" + configNodeName);
		this.manaBonus = 0;
		
		//we need to set the name to start. This is needed for inheritence.
		try{
			this.displayName = config.getString(configNodeName + ".config.name", configNodeName);
		}catch(Throwable exp){}
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
		readArmor();
		readTraitSection();
		readPermissionSection();
		readAdditionalWandMaterial();
		readHolderSelectionItem();
		readHolderDescription();
		
		return this;
	}
	

	/**
	 * Reads the parents section for the Trait.
	 */
	public void readParents() {
		List<String> parentsName = config.getStringList(configNodeName + ".config.parents");
		this.parents.clear();
		if(parentsName.isEmpty()) return;
		
		for(String name : parentsName){
			AbstractTraitHolder parent = getHolderManager().getHolderByName(name);
			if(parent == null) continue;
			
			if(hasParentCyle(parent, 0, 50)){
				RacesAndClasses.getPlugin().logError("The " + getContainerTypeAsString() + " has a cyclic Parent!"
						+ " Please remove " + name + " in some sort from the cycle!");
				continue;
			}
			
			this.parents.add(parent);
			addHolderToAddParents(this, parent, 0, 50);
		}
	}
	
	/**
	 * Checks if this traitHolder has a cyclic depend.
	 * 
	 * @param holders to check.
	 * @param step to do
	 * 
	 * @return true if it has.
	 */
	protected boolean hasParentCyle(AbstractTraitHolder holder, int step, int border){
		if(step > border) return false;
		if(holder.getParents().contains(this)) return true;
		
		for(AbstractTraitHolder parent : holder.getParents()) {
			if(hasParentCyle(parent, step + 1, border)) return true;
		}
		
		return false;
	}
	
	/**
	 * Checks if this traitHolder has a cyclic depend.
	 * 
	 * @param holders to check.
	 * @param step to do
	 */
	protected void addHolderToAddParents(AbstractTraitHolder origin, AbstractTraitHolder holder, int step, int border){
		if(step > border) return;
		for(Trait trait : holder.getTraits()) {
			origin.traits.add(trait);
			trait.addTraitHolder(origin);
		}
		
		for(AbstractTraitHolder parent : holder.getParents()) {
			parent.addHolderToAddParents(origin, parent, step + 1, border);
		}
	}
	


	/**
	 * Reads the Description from the config.
	 */
	protected void readHolderDescription() {		
		String toSet = config.getString(configNodeName + ".description", null);
		if(toSet != null) holderDescription = toSet;
	}
	
	

	/**
	 * Reads the Holder selection Item from the config.
	 */
	@SuppressWarnings("deprecation")
	protected void readHolderSelectionItem() {
		Material mat = Material.BOOK_AND_QUILL;
		try{
			mat = Material.getMaterial(config.getInt(configNodeName + ".gui.item.id", Material.BOOK_AND_QUILL.getId()));
		}catch(IllegalArgumentException exp){}
		
		short damageValue = (short) config.getInt(configNodeName + ".gui.item.damage", 0);
		
		this.holderSelectionItem = new ItemStack(mat, 1, damageValue);
	}
	
	

	/**
	 * Reads the WandMaterial Section.
	 */
	protected void readAdditionalWandMaterial() {
		additionalWandMaterials.clear();
		if(!config.contains(configNodeName + ".config.wandMaterial")) return;
		
		List<String> wandMatList = config.getStringList(configNodeName + ".config.wandMaterial");
		additionalWandMaterials.addAll(MaterialParser.parseToMaterial(wandMatList));
	}
	

	/**
	 * Reads the configuration section of the holders.
	 * Expect this to be called in the load process.
	 * 
	 * @throws HolderConfigParseException if the parsing failed.
	 */
	protected void readConfigSection() throws HolderConfigParseException{
		try{
			this.displayName = config.getString(configNodeName + ".config.name", configNodeName);
			this.manaBonus = config.getDouble(configNodeName + ".config.manabonus", 0);
			this.holderTag = ChatColor.translateAlternateColorCodes('&', config.getString(configNodeName + ".config.tag", "[" + configNodeName + "]"));
		}catch(Exception exp){
			throw new HolderConfigParseException();
		}
	}
	
	
	/**
	 * Reads the Armor permissions from the Holder and parses it.
	 */
	protected void readArmor(){
		armorUsage = new boolean[]{false, false, false, false, false};
		String armorString = config.getString(configNodeName + ".config.armor", "").toLowerCase();
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
		
		if(!config.isConfigurationSection(configNodeName + ".traits")){
			return; //trait section is not necessary
		}
		
		Set<String> traitNames = config.getConfigurationSection(configNodeName + ".traits").getKeys(false);
		if(traitNames == null || traitNames.size() == 0) return;
		
		List<HolderTraitParseException> exceptionList = new LinkedList<HolderTraitParseException>();
		
		for(String traitName : traitNames){
			String realTraitName = traitName;
			if(traitName.contains("#")){
				//always select the first part.
				realTraitName = traitName.split("#")[0];
			}
			
			if(config.isString(configNodeName + ".traits." + traitName + ".trait")){
				realTraitName = config.getString(configNodeName + ".traits." + traitName + ".trait");
			}
			
			try{
				Trait trait = TraitStore.buildTraitByName(realTraitName, this);
				if(trait != null){
					String configPath = configNodeName + ".traits." + traitName;
					configureTraitFromYAML(config, configPath, trait);
					trait.generalInit();
					
					traits.add(trait);
				}
			}catch(TraitConfigurationFailedException exp){
				exceptionList.add(new HolderTraitParseException(exp.getMessage(), this));
				RacesAndClasses.getPlugin().log("Error on parsing: '" + getDisplayName() + "' Problem was: '" + exp.getMessage() 
						+ "' On Trait: '" + realTraitName + "'.");
			}
		}
		
		this.parsingExceptionsHappened.addAll(exceptionList);
	}
	
	/**
	 * Reads the Permission section of the Holder.
	 */
	protected void readPermissionSection() {
		holderPermissions.clear();
		
		if(!config.isList(configNodeName + ".permissions")){
			return;
		}
		
		List<String> permissionList = config.getStringList(configNodeName + ".permissions");
		holderPermissions.add(permissionList);
	}
	

	/**
	 * Adds STD Traits that every holders has to the Trait list.
	 */
	protected abstract void addSTDTraits();
	
	
	/**
	 * Returns if a Player is member of this holders
	 * 
	 * @param player to check
	 * @return true if is member, false otherwise
	 */
	public abstract boolean containsPlayer(RaCPlayer player);
	
	
	/**
	 * Returns the Permissions this holders has additional
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
	public String getDisplayName(){
		return displayName;
	}
	
	/**
	 * The name of the Holder
	 * 
	 * @return
	 */
	public String getConfigNodeName(){
		return configNodeName;
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
		for(Trait trait : getTraits()){
			if(trait.isVisible()) traitSet.add(trait);
		}
		
		return traitSet;
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
		return displayName;
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

	
	/**
	 * Returns the Holder Selection item representing the holders in any GUI.
	 * 
	 */
	public ItemStack getHolderSelectionItem() {
		return holderSelectionItem;
	}

	
	/**
	 * @return the holderDescription
	 */
	public String getHolderDescription() {
		return holderDescription;
	}
	
	
	/**
	 * @return if has a Holder Description
	 */
	public boolean hasHolderDescription() {
		return holderDescription != null;
	}
	
	/**
	 * This gives the Max Health mod back.
	 * 
	 * @return max health mod.
	 */
	public abstract double getMaxHealthMod();
	
	
	/**
	 * Returns the parents of this Holder.
	 * 
	 * @return parents.
	 */
	public Set<AbstractTraitHolder> getParents(){
		return new HashSet<AbstractTraitHolder>(parents);
	}
	
}
