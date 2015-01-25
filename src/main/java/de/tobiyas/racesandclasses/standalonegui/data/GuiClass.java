package de.tobiyas.racesandclasses.standalonegui.data;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.tobiyas.racesandclasses.standalonegui.data.option.TraitConfigOption;
import de.tobiyas.racesandclasses.standalonegui.data.option.specific.TraitConfigStringOption;

public class GuiClass implements Comparable<GuiClass> {


	/**
	 * The Traits of the Class
	 */
	private final Set<GuiTrait> traits = new HashSet<GuiTrait>();
	
	/**
	 * The Name of the Class
	 */
	private String className = "NONE";
	
	/**
	 * The NodeName of the Class
	 */
	private String classNodeName = "NONE";
	
	/**
	 * The Config to use.
	 */
	private final List<TraitConfigOption> config = new LinkedList<TraitConfigOption>();

	
	public GuiClass(String name, String nodeName, String tag, String manaBonus, String armor, Set<GuiTrait> traits) {
		this.traits.addAll(traits);
		this.classNodeName = nodeName;
		this.className = name;
		
		for(GuiTrait trait : traits) trait.setBelongingClass(this);
		
		//setting the config.
		this.config.add(new TraitConfigStringOption("name", true, className));
		this.config.add(new TraitConfigStringOption("manaBonus", false, manaBonus));
		this.config.add(new TraitConfigStringOption("tag", true, tag));
		
		Collections.sort(config);
	}
	
	
	
	public String getClassName() {
		return className;
	}
	
	public void setClassName(String className) {
		this.className = className;
	}
	
	public String getClassNodeName() {
		return classNodeName;
	}

	public void setClassNodeName(String classNodeName) {
		this.classNodeName = classNodeName;
	}


	public Set<GuiTrait> getTraits() {
		return traits;
	}
	

	public void addTrait(GuiTrait trait) {
		trait.setBelongingClass(this);
		traits.add(trait);
	}
	
	public void removeTrait(GuiTrait trait) {
		traits.remove(trait);
	}
	
	
	
	@Override
	public String toString() {
		return className;
	}

	
	@Override
	public int compareTo(GuiClass o) {
		return className.compareTo(o.className);
	}



	public List<TraitConfigOption> getConfig() {
		return config;
	}
	
}
