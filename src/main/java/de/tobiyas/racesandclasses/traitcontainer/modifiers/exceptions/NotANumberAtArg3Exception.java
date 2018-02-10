package de.tobiyas.racesandclasses.traitcontainer.modifiers.exceptions;

public class NotANumberAtArg3Exception extends ModifierConfigurationException {

	private static final long serialVersionUID = 309753236813490736L;
	
	private final String notParseableNumber;
	
	public NotANumberAtArg3Exception( String total, String type, String descriptor, String number, String appliedOn ) {
		super( total, type, descriptor, 0, appliedOn );
		
		this.notParseableNumber = number;
	}
	

	@Override
	public String formatErrorMSG() {
		return "The 3rd Argument (" + notParseableNumber +  ") is not a number or can not be parsed as a number.";
	}

}
