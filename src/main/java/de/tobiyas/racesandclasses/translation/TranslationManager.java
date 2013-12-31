package de.tobiyas.racesandclasses.translation;

import de.tobiyas.racesandclasses.translation.exception.TranslationNotFoundException;

public interface TranslationManager {

	
	/**
	 * Tries to translate the given tag into the current language.
	 * <br>Also creates the structure if not present
	 * <br>
	 * <br> 
	 * @param tag that shall be translated
	 * @param tryInStdLanguageIfFails if true, tries to translate in the STD language 
	 * if translation in the current language fails.
	 * 
	 * @return the wanted translation, or an {@link TranslationNotFoundException}
	 * 
	 * @throws TranslationNotFoundException if the tag was not found in the translation table.
	 * This includes the STD translation table.
	 */
	public Translator translate(String key, boolean tryInStdLanguageIfFails) throws TranslationNotFoundException;
	
	
	/**
	 * Inits the language system
	 * 
	 * @return itself for buildchains.
	 */
	public TranslationManager init();
	
	
	/**
	 * "Reloads" the language.
	 * <br>Meaning drops the old one to lazy init a new one on call.
	 */
	public void reload();
	
	
	/**
	 * Removes all Relevant data that can leak.
	 */
	public void shutdown();
	
	
	/**
	 * returns the current language
	 */
	public String getCurrentLanguage();
}
