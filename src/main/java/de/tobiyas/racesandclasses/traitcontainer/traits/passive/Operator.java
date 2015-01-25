package de.tobiyas.racesandclasses.traitcontainer.traits.passive;

public enum Operator {

	
	Plus("+"),
	Minus("-"),
	Mult("*"),
	Div("/");
	
	
	/**
	 * The StringRepresentation to use.
	 */
	private final String stringRepresentation;
	
	
	Operator(String stringRepresentation){
		this.stringRepresentation = stringRepresentation;
	}
	
	
	public String getStringRepresentation() {
		return stringRepresentation;
	}
	
	
	
	/**
	 * tries to parse the value passed.
	 * 
	 * @param toParse the value to parse
	 * @return the parsed value or #Mult if not found.
	 */
	public static Operator resolve(String toParse){
		if(toParse == null) return Mult;
		
		for(Operator op : values()){
			if(op.stringRepresentation.equals(toParse)) return op;
		}
		
		return Mult;
	}
}
