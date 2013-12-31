package de.tobiyas.racesandclasses.traitcontainer.container;

import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;

public class TraitInfoContainer {

	private String name;
	private Class<? extends Trait> clazz;
	private String category;
	private boolean visible;
	
	public TraitInfoContainer(String name, Class<? extends Trait> clazz, String category, boolean visible){
		this.name = name;
		this.clazz = clazz;
		this.category = category;
		this.visible = visible;
	}
	
	public String getName(){
		return name;
	}
	
	public Class<? extends Trait> getClazz(){
		return clazz;
	}
	
	public String getCategory(){
		return category;
	}
	
	public boolean isVisible(){
		return visible;
	}
}
