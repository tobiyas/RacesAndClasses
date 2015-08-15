package de.tobiyas.racesandclasses.standalonegui.data;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.tobiyas.racesandclasses.standalonegui.data.option.TraitConfigOption;
import de.tobiyas.racesandclasses.standalonegui.data.option.specific.TraitConfigStringOption;
import de.tobiyas.util.config.YAMLConfigExtended;

public class GuiClass implements Comparable<GuiClass>, NeedsSave {


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
	
	/**
	 * The Config to use for re-Sereializing.
	 */
	private final YAMLConfigExtended ymlConfig;
	
	/**
	 * If we currently need save.
	 */
	private boolean needsSave = false;

	
	public GuiClass(YAMLConfigExtended ymlConfig, String name, String nodeName, String tag, String manaBonus, String armor, Set<GuiTrait> traits) {
		this.ymlConfig = ymlConfig;
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
		this.needsSave = true;
	}
	
	public String getClassNodeName() {
		return classNodeName;
	}

	public void setClassNodeName(String classNodeName) {
		this.classNodeName = classNodeName;
		this.needsSave = true;
	}
	
	/**
	 * Saves to YML file.
	 */
	public void save(){
		if(!needsSave()) return;
		
		//First clear the config.
		ymlConfig.clearConfig();
		
		//second save config!
		for( TraitConfigOption option : config ){
			ymlConfig.set(classNodeName + ".config." + option.getName(), option.getCurrentSelection());
		}
		
		//Last save the Traits:
		for( GuiTrait trait : traits ){
			trait.saveTo(ymlConfig, classNodeName + ".traits.");
		}
		
		ymlConfig.save();
		this.needsSave = false;
	}
	
	
	@Override
	public boolean needsSave() {
		if( needsSave ) return true;
		
		for(GuiTrait trait : traits){
			if(trait.needsSave()) return true;
		}
		
		for( TraitConfigOption option : config ){
			if(option.needsSave()) return true;
		}
		
		return false;
	}


	public Set<GuiTrait> getTraits() {
		return traits;
	}
	

	public void addTrait(GuiTrait trait) {
		trait.setBelongingClass(this);
		traits.add(trait);
		this.needsSave = true;
	}
	
	public void removeTrait(GuiTrait trait) {
		traits.remove(trait);
		this.needsSave = true;
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
