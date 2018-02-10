package de.tobiyas.racesandclasses.traitcontainer.modifiers.exceptions;

public class NotEnoughArgumentsForModifierException extends ModifierConfigurationException {

	private static final long serialVersionUID = 3097532368213490736L;
	
	
	public NotEnoughArgumentsForModifierException( String total ) {
		super( total, "", "", 0, "*" );
	}
	
	
	@Override
	public String formatErrorMSG() {
		return "There are not enough Arguments. Look into the definition to check it.";
	}

}
