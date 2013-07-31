package de.tobiyas.racesandclasses.util.language;

public class TranslationNotFoundException extends TranslationException {
	private static final long serialVersionUID = -487493971827375957L;

	
	protected String tagNotFound;
	protected String language;
	
	
	/**
	 * Thrown when a tag was not found while translation
	 * 
	 * @param tagNotFound the tag not found
	 */
	public TranslationNotFoundException(String language, String tagNotFound){
		this.tagNotFound = tagNotFound;
		this.language = language;
	}


	public String getTagNotFound() {
		return tagNotFound;
	}


	public String getLanguage() {
		return language;
	}
}
