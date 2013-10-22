package de.tobiyas.racesandclasses.APIs;

import de.tobiyas.racesandclasses.util.language.LanguageTranslationUtil;
import de.tobiyas.racesandclasses.util.language.TranslationNotFoundException;
import de.tobiyas.racesandclasses.util.language.Translator;

public class LanguageAPI {

	/**
	 * Translates the given tag to the current language defined in Configuration.
	 * <br>If not found, an exception is thrown
	 * <br>
	 * @param tag to translate
	 * @return the translated tag or an Exception
	 * 
	 * @throws TranslationNotFoundException if the tag was not found
	 */
	public static Translator translateToCurrentLanguage(String tag) throws TranslationNotFoundException{
		return LanguageTranslationUtil.tryTranslate(tag, false);
	}
	
	
	
	/**
	 * Translates the given tag to the current language defined in Configuration.
	 * <br>Also the STD  language (mostly English) is searched.
	 * <br>If not found, an exception is thrown
	 * <br>
	 * @param tag to translate
	 * @return the translated tag or an Exception
	 * 
	 * @throws TranslationNotFoundException 
	 */
	public static Translator translateToCurrentLanguageWithFallback(String tag) throws TranslationNotFoundException{
		return LanguageTranslationUtil.tryTranslate(tag, true);
	}
	
	
	
	/**
	 * Translates the tag without throwing an {@link TranslationNotFoundException}.
	 * <br>This indicates that the tag is just returned if the tag was not found.
	 * <br>Also the current language + the STD language (mostly English) is searched.
	 * 
	 * @param tag to search for
	 * @return the translation or the tag itself if not found.
	 */
	public static Translator translateIgnoreError(String tag){
		try{
			return LanguageTranslationUtil.tryTranslate(tag, true);
		}catch(TranslationNotFoundException exp){
			return new Translator(tag);
		}
	}
}
