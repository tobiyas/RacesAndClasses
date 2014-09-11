package de.tobiyas.racesandclasses.playermanagement.health;

public class HealthModifier {

	
	/**
	 * The Reason for the Modification
	 */
	private final String reason;
	
	/**
	 * The value for the Modification
	 */
	private final double value;
	
	/**
	 * The operation to do
	 */
	private final HealthModEnum operation;
	
	
	public HealthModifier(String reason, double value, HealthModEnum operation) {
		super();
		
		this.reason = reason;
		this.value = value;
		this.operation = operation;
	}
	
	
	public String getReason() {
		return reason;
	}

	public double getValue() {
		return value;
	}

	public HealthModEnum getOperation() {
		return operation;
	}







	public static enum HealthModEnum{
		ADD,
		REMOVE,
		MULT,		
		DIVITE;
	}
}
