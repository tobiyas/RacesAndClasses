package de.tobiyas.racesandclasses.traitcontainer.modifiers.exceptions;

public class ModifierGenericException extends ModifierConfigurationException {

	private static final long serialVersionUID = 3097532368213490736L;
	
	/**
	 * the actual occured exception.
	 */
	private final Throwable exception;
	
	
	public ModifierGenericException( String total, String type, String descriptor, double parsedMod, String toUseOn, Throwable actualException ) {
		super( total, type, descriptor, parsedMod, toUseOn );
		
		this.exception = actualException;
	}
	
	@Override
	public String formatErrorMSG() {
		return "A generic Exception has occured: " + exception.getMessage() + " - at - "
				+ exception.getStackTrace()[0];
	}

}
