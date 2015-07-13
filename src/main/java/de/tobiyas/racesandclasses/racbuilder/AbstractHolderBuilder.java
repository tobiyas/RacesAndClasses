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
package de.tobiyas.racesandclasses.racbuilder;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.traitcontainer.TraitStore;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.util.items.ItemUtils.ItemQuality;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.util.config.YAMLConfigExtended;

public abstract class AbstractHolderBuilder {

	/**
	 * The name of the Holder
	 */
	protected String name;
	

	/**
	 * The Tag of the Holder
	 */
	protected String holderTag;
	
	
	/**
	 * The Set of Traits to add to the Holder
	 */
	protected final Set<Trait> traitSet;
	
	
	/**
	 * The List of Permissions to add to the Holder
	 */
	protected final List<String> permissionList;
	
	
	/**
	 * The permission for Armor
	 */
	protected Set<ItemQuality> armorPermission;
	
	
	
	/**
	 * The plugin to call stuff on
	 */
	protected final RacesAndClasses plugin = RacesAndClasses.getPlugin();

	
	/**
	 * tells if the Builder has been populated
	 */
	protected boolean readyForBuilding = false;
	
	/**
	 * The Health the Builder has
	 */
	protected double health;
	
	
	/**
	 * Generates a builder for the Race name
	 * 
	 * @param name to build with
	 */
	public AbstractHolderBuilder(String name) {
		this.name = name;
		if(name != null){
			this.holderTag = "[" + name + "]";
		}
		
		this.armorPermission = new HashSet<ItemQuality>();
				
		this.traitSet = new HashSet<Trait>();
		this.permissionList = new LinkedList<String>();
	}

	
	/**
	 * Reads the Values of the Holder passed and sets the values wanted.
	 * 
	 * @param holders
	 */
	public AbstractHolderBuilder(AbstractTraitHolder holder) {
		this.name = holder.getDisplayName();
		this.holderTag = holder.getTag();
		this.armorPermission = holder.getArmorPerms();
		
		this.traitSet = holder.getVisibleTraits();
		this.permissionList = new LinkedList<String>();
	}


	/**
	 * Adds a Permission node to the Holder
	 * 
	 * @param permissionNode to add
	 * @return true if worked, false if already present
	 */
	public boolean addPermissionNode(String permissionNode){
		if(permissionNode == null){
			return false;
		}
		
		return permissionList.add(permissionNode);
	}
	
	
	/**
	 * removes a Permission node from the Holder
	 * 
	 * @param permissionNode to remove
	 * @return true if removed, false if not found
	 */
	public boolean removePermissionNode(String permissionNode){
		if(permissionNode == null){
			return false;
		}
		
		return permissionList.remove(permissionNode);
	}
	
	
	/**
	 * Builds a Trait and adds it to the Holder.
	 * 
	 * If the Trait is already added, it is replaced.
	 * If the Trait is not found, false is returned.
	 * If the config does not satisfy the trait, false is returned. 
	 * 
	 * @param traitName to add
	 * @param configuration that satisfied the configuration of the Trait.
	 * @return true if worked, false if not.
	 */
	public boolean addTrait(String traitName, TraitConfiguration configuration){
		if(containsTrait(traitName)) removeTrait(traitName);
		
		try{
			Trait trait = TraitStore.buildTraitWithoutHolderByName(traitName);
			if(trait == null){
				return false;
			}
			
			trait.setConfiguration(configuration);
			
			traitSet.add(trait);
			return true;
		}catch(Exception exp){
			exp.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * Removes the Trait with the specific Name.
	 * Returns true if remove worked.
	 * Returns false if the Trait was not found.
	 * 
	 * @param traitName to remove
	 * @return true if worked, false if not found.
	 */
	public boolean removeTrait(String traitName){
		if(!containsTrait(traitName)) return false;
		Trait trait = getTrait(traitName);
		traitSet.remove(trait);
		return true;
	}
	
	
	/**
	 * Returns the Trait with the name.
	 * Returns Null if the Trait is already contained.
	 * 
	 * @param traitName to check
	 * @return Trait that was searched or null if not found.
	 */
	private Trait getTrait(String traitName){
		for(Trait trait : traitSet){
			if(trait.getName().equalsIgnoreCase(traitName)){
				return trait;
			}
		}
		
		return null;
	}
	
	
	/**
	 * Returns true if the trait is contained.
	 * Returns false if not contained.
	 * 
	 * @param traitName to search
	 * @return true if contained
	 */
	public boolean containsTrait(String traitName){
		return getTrait(traitName) != null;
	}
	
	
	
	/**
	 * Sets the Tag of the Holder
	 * 
	 * @param holderTag
	 */
	public void setHolderTag(String holderTag){
		this.holderTag = holderTag;
	}
	
	
	/**
	 * Sets the armor of the Holder
	 * Possible Strings are: leather, gold, iron, chain, diamond
	 * 
	 * @param armor to use
	 */
	public void addArmor(String armorString){
		setArmor(armorString, true);
	}
	

	/**
	 * Removes the armor permission of from the Holder.
	 * 
	 * @param armorString
	 */
	public void removeArmor(String armorString){
		setArmor(armorString, false);
	}
	
	
	/**
	 * Sets the Armor to the wanted value
	 * 
	 * @param armorString
	 * @param toSet
	 */
	protected void setArmor(String armorString, boolean toSet){
		Collection<ItemQuality> stuff = ItemQuality.parse(armorString);
		if(toSet) armorPermission.addAll(stuff);
		else armorPermission.removeAll(stuff);
	}


	/**
	 * Tells if the TraitHolder is ready for building.
	 */
	public boolean isReadyForBuilding() {
		return readyForBuilding;
	}


	/**
	 * Sets the builder to say that it is ready.
	 */
	public void setReadyForBuilding(boolean ready) {
		this.readyForBuilding = ready;
	}
	

	/**
	 * Builds the TraitHolder and returns it
	 * 
	 * @return
	 */
	public abstract AbstractTraitHolder build();


	/**
	 * Returns all traits registered
	 * 
	 * @return
	 */
	public Set<Trait> getTraits() {
		return traitSet;
	}
	
	
	/**
	 * Returns the name of the Holder of the Builder.
	 * @return
	 */
	public String getName(){
		return name;
	}

	/**
	 * Gets the Tag of the Builder
	 * @return
	 */
	public String getHolderTag() {
		return holderTag;
	}

	/**
	 * Sets the name of the Builder.
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
		
		if(this.holderTag == null && name != null){
			holderTag = "[" + name + "]";
		}
	}

	
	/**
	 * Returns the Health set
	 * @return
	 */
	public double getHealth() {
		return health;
	}


	/**
	 * Sets the health to a new modificator.
	 * 
	 * @param health
	 */
	public void setHealth(double health) {
		this.health = health;
	}
	
	
	public void saveToFile() {
		if(!isReadyForBuilding()) return;
		
		YAMLConfigExtended holderConfig = getHolderYAMLFile();
		if(!holderConfig.isConfigurationSection(name)){
			holderConfig.createSection(name);
		}
		
		if(!holderConfig.isConfigurationSection(name + ".config")){
			holderConfig.createSection(name + ".config");
		}
		
		if(traitSet.size() > 0 
				&& !holderConfig.isConfigurationSection(name + ".traits")){
			holderConfig.createSection(name + ".traits");
		}
		
		for(Trait trait : traitSet){
			holderConfig.createSection(name + ".traits." + trait.getName());
			for(String key : trait.getCurrentconfig().keySet()){
				Object value = trait.getCurrentconfig().get(key);
				
				holderConfig.set(name + ".traits." + trait.getName() + "." + key, value);
			}
		}

		String armorString = buildArmorString();
		if(armorString.length() > 0){
			holderConfig.set(name + ".config.armor", armorString);
		}
		
		saveFurtherToFile(holderConfig);
		holderConfig.save();
	}
	
	/**
	 * Builds the Armor String from the Armor Permissions
	 * 
	 * @return
	 */
	protected String buildArmorString(){
		boolean hasAll = ItemQuality.values().size() == armorPermission.size();
		
		String armorString = "";
		for(ItemQuality quality : this.armorPermission) armorString += quality + ",";
		
		if(hasAll) armorString = "all";
		return armorString;
	}
	
	
	/**
	 * Overwriting for further saving.
	 * @param config
	 */
	protected abstract void saveFurtherToFile(YAMLConfigExtended config);


	/**
	 * Returns the ConfigTotal of the holders type
	 * @return
	 */
	protected abstract YAMLConfigExtended getHolderYAMLFile();
	
}
