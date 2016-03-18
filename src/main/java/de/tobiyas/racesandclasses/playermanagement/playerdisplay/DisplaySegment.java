package de.tobiyas.racesandclasses.playermanagement.playerdisplay;

public class DisplaySegment {

	/**
	 * The name of the Segment
	 */
	private final String name;
	
	/**
	 * The String to display.
	 */
	private String displayString = "";

	
	
	public DisplaySegment(String name, String displayString) {
		this.name = name;
		this.displayString = displayString;
	}



	public String getDisplayString() {
		return displayString;
	}

	public void setDisplayString(String displayString) {
		this.displayString = displayString;
	}

	public String getName() {
		return name;
	}


	@Override
	public String toString() {
		return "{"+getName() + ": " + displayString + "}";
	}
	
	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	
}
