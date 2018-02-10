package de.tobiyas.racesandclasses.traitcontainer.modifiers.exceptions;

public class GeneratorNotFoundException extends ModifierConfigurationException {

	private static final long serialVersionUID = 3097532368213490736L;
	
	
	public GeneratorNotFoundException( String total, String type, String descriptor, double parsedMod, String toUseOn ) {
		super( total, type, descriptor, parsedMod, toUseOn );
	}
	
	@Override
	public String formatErrorMSG() {
		return "The Generator for '" + type + "' was not found";
	}

}
