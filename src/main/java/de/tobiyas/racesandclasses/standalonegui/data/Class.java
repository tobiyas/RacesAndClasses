package de.tobiyas.racesandclasses.standalonegui.data;

import java.util.HashSet;
import java.util.Set;

public class Class {


	/**
	 * The Traits of the Class
	 */
	private final Set<Trait> traits = new HashSet<Trait>();
	
	/**
	 * The Name of the Class
	 */
	private String className = "NONE";

	
	
	public String getClassName() {
		return className;
	}
	
	public void setClassName(String className) {
		this.className = className;
	}
	
	

	public Set<Trait> getTraits() {
		return traits;
	}
	
}
