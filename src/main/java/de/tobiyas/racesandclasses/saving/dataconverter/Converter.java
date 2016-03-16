package de.tobiyas.racesandclasses.saving.dataconverter;

public interface Converter {

	
	/**
	 * Converts the Data.
	 */
	public void convert();
	
	
	/**
	 * If can be applied.
	 * @return true if can be applied.
	 */
	public boolean isApplyable();
	
	
	/**
	 * Returns the Version for Conversion.
	 * @return the version.
	 */
	public int getVersion();
	
}
