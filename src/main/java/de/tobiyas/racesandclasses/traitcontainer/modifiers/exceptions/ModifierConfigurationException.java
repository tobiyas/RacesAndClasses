package de.tobiyas.racesandclasses.traitcontainer.modifiers.exceptions;

public abstract class ModifierConfigurationException extends Exception {

	private static final long serialVersionUID = 1337L;
	
	/**
	 * the total line to parse.
	 */
	protected final String total;
	
	/**
	 * The type of the Modifier.
	 */
	protected final String type;
	
	/**
	 * the descriptor / configuration of the Modifier.
	 */
	protected final String descriptor;
	
	/**
	 * the value of the modifier
	 */
	protected final double value;
	
	
	/**
	 * On what to apply the modifier.
	 */
	protected final String appliedOn;
	
	
	public ModifierConfigurationException( String total, String type, String descriptor, double value, String appliedOn) {
		super();
		
		this.total = total;
		this.type = type;
		this.descriptor = descriptor;
		this.value = value;
		this.appliedOn = appliedOn;
	}


	public String getTotal() {
		return total;
	}

	public String getType() {
		return type;
	}


	public String getDescriptor() {
		return descriptor;
	}


	public double getValue() {
		return value;
	}


	public String getAppliedOn() {
		return appliedOn;
	}
	
	/**
	 * Formats a nice text to post to the Console.
	 * 
	 * @param holders the owner of the Trait.
	 * @param displayNameOfTrait the display name of the Trait.
	 * @return a formattet text to display.
	 */
	public String formatToText( String holders, String displayNameOfTrait ){
		return "Modifier: '" + total + "' of Trait: '" + displayNameOfTrait 
				+ "' from Holders: '" + holders + "' could not be parsed. "
				+ " " + formatErrorMSG();
	}
	
	
	protected abstract String formatErrorMSG();
	

}
