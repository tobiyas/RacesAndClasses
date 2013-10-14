package de.tobiyas.racesandclasses.racbuilder.gui.stats;

public enum StatType {

	INTEGER(Integer.class),
	BOOLEAN(Boolean.class),
	STRING(String.class),
	DOUBLE(Double.class),
	OPERATOR(String.class);
	
	private final Class<?> linkedClass;
	
	private StatType(Class<?> linkedClass) {
		this.linkedClass = linkedClass;
	}

	public Class<?> getLinkedClass() {
		return linkedClass;
	}
	
	
	/**
	 * Generates a Type from the Class passed
	 * 
	 * @param toCheck
	 * @return
	 */
	public static StatType getTypeFromClass(Class<?> toCheck){
		for(StatType type : values()){
			if(toCheck == type.getLinkedClass()){
				return type;
			}
		}
		
		return STRING;
	}
}
