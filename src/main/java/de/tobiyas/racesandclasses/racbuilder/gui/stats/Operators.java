package de.tobiyas.racesandclasses.racbuilder.gui.stats;

public enum Operators {

	PLUS('+', 13),
	MINUS('-', 14),
	MULT('*', 1);
	
	private final char charValue;
	private final short woolColor;
	
	private Operators(char charValue, int woolColor) {
		this.charValue = charValue;
		this.woolColor = (short) woolColor;
	}
	
	public short getWoolColor(){
		return woolColor;
	}
	
	public char getCharValue(){
		return charValue;
	}
	
	
	/**
	 * Returns an Operator for a Char value
	 * 
	 * @param charValue
	 */
	public static Operators resolve(char charValue){
		for(Operators operator : values()){
			if(charValue == operator.charValue){
				return operator;
			}
		}
		
		return MULT;
	}
	
	/**
	 * Returns an Operator for a String value.
	 * It takes the first char.
	 * 
	 * null / empty Strings return {@link #MULT}
	 * 
	 * @param stringValue to search
	 */
	public static Operators resolve(String stringValue){
		if(stringValue == null || stringValue.length() == 0) return MULT;
		
		return resolve(stringValue.charAt(0));
	}
	
}
