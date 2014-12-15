package de.tobiyas.racesandclasses.standalonegui.data;

import java.util.HashSet;
import java.util.Set;

public class GuiClass implements Comparable<GuiClass> {


	/**
	 * The Traits of the Class
	 */
	private final Set<GuiTrait> traits = new HashSet<GuiTrait>();
	
	/**
	 * The Name of the Class
	 */
	private String className = "NONE";

	
	public GuiClass(String name, Set<GuiTrait> traits) {
		this.traits.addAll(traits);
		this.className = name;
	}
	
	
	
	public String getClassName() {
		return className;
	}
	
	public void setClassName(String className) {
		this.className = className;
	}
	
	

	public Set<GuiTrait> getTraits() {
		return traits;
	}
	
	
	@Override
	public String toString() {
		return className;
	}

	
	@Override
	public int compareTo(GuiClass o) {
		return className.compareTo(o.className);
	}
	
}
