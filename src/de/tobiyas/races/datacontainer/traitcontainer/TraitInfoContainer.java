package de.tobiyas.races.datacontainer.traitcontainer;

public class TraitInfoContainer {

	private String name;
	private Class<?> clazz;
	private String category;
	private boolean visible;
	
	public TraitInfoContainer(String name, Class<?> clazz, String category, boolean visible){
		this.name = name;
		this.clazz = clazz;
		this.category = category;
		this.visible = visible;
	}
	
	public String getName(){
		return name;
	}
	
	public Class<?> getClazz(){
		return clazz;
	}
	
	public String getCategory(){
		return category;
	}
	
	public boolean isVisible(){
		return visible;
	}
}
