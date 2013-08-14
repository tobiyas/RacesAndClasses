package de.tobiyas.racesandclasses.racbuilder;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.traitcontainer.TraitStore;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.Trait;

public abstract class AbstractHolderBuilder {

	/**
	 * The name of the Holder
	 */
	protected final String name;
	

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
	protected boolean[] armorPermission;
	
	
	
	/**
	 * The plugin to call stuff on
	 */
	protected final RacesAndClasses plugin;

	
	
	/**
	 * Generates a builder for the Race name
	 * 
	 * @param name to build with
	 */
	public AbstractHolderBuilder(String name) {
		this.name = name;
		this.holderTag = "[" + name + "]";
		this.armorPermission = new boolean[]{false, false, false, false, false};
				
		this.traitSet = new HashSet<Trait>();
		this.permissionList = new LinkedList<String>();
		
		this.plugin = RacesAndClasses.getPlugin();
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
	 * If the Trait is already added, false is returned.
	 * If the Trait is not found, false is returned.
	 * If the config does not satisfy the trait, false is returned. 
	 * 
	 * @param traitName to add
	 * @param configuration that satisfied the configuration of the Trait.
	 * @return true if worked, false if not.
	 */
	public boolean addTrait(String traitName, Map<String, String> configuration){
		if(containsTrait(traitName)) return false;
		
		try{
			Trait trait = TraitStore.buildTraitWithoutHolderByName(traitName);
			if(trait == null){
				return false;
			}
			
			trait.setConfiguration(configuration);
			
			traitSet.add(trait);
			return true;
		}catch(Exception exp){
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
		if(armorString == null){
			return;
		}
		
		if(armorString.contains("leather")){
			armorPermission[0] = toSet;
		}
		
		if(armorString.contains("iron")){
			armorPermission[1] = toSet;
		}
		
		if(armorString.contains("gold")){
			armorPermission[2] = toSet;
		}
		
		if(armorString.contains("diamond")){
			armorPermission[3] = toSet;
		}
		
		if(armorString.contains("chain")){
			armorPermission[4] = toSet;
		}
		
		if(armorString.contains("all")){
			armorPermission[0] = toSet;
			armorPermission[1] = toSet;
			armorPermission[2] = toSet;
			armorPermission[3] = toSet;
			armorPermission[4] = toSet;
		}
	}
	
	
}
