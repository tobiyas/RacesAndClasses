package de.tobiyas.racesandclasses.APIs;

import java.util.Map;

import org.bukkit.command.CommandSender;

import de.tobiyas.racesandclasses.translation.TranslationManagerHolder;
import de.tobiyas.racesandclasses.translation.Translator;
import de.tobiyas.racesandclasses.translation.exception.TranslationNotFoundException;
import de.tobiyas.util.collections.HashMapUtils;

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
		return TranslationManagerHolder.getTranslationManager().translate(tag, false);
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
		return TranslationManagerHolder.getTranslationManager().translate(tag, true);
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
			return translateToCurrentLanguageWithFallback(tag);
		}catch(TranslationNotFoundException exp){
			return new Translator(tag);
		}
	}

	/**
	 * Checks if the Translation is contained currently.
	 * 
	 * @param tag to search for
	 * @return true if the translation is found.
	 */
	public static boolean containsTranslation(String tag){
		try{
			TranslationManagerHolder.getTranslationManager().translate(tag, true);
			return true;
		}catch(TranslationNotFoundException exp){
			return false;
		}
	}
	
	
	/**
	 * Translates a messages and sends it to the passed sender.
	 * 
	 * @param sender to send to
	 * @param tag to translate
	 * @param replacements to replace
	 */
	public static void sendTranslatedMessage(CommandSender sender, String tag, Map<String, String> replacements){
		Translator translator = translateIgnoreError(tag);
		translator.replace(replacements);
		String message = translator.build();
		if("".equals(message)) return; //no message wanted.
		
		sender.sendMessage(message);
	}
	
	/**
	 * Translates a messages and sends it to the passed sender.
	 * 
	 * @param sender to send to
	 * @param tag to translate
	 * @param arg The Map to replace: {arg1, replacement1, arg2, replacement2, ....}
	 */
	public static void sendTranslatedMessage(CommandSender sender, String tag, String... arg){
		Map<String, String> replacements = HashMapUtils.generateStringStringMap(arg);
		sendTranslatedMessage(sender, tag, replacements);
	}
	
	/**
	 * Translates a messages and sends it to the passed sender.
	 * <br>No replacements here!
	 * 
	 * @param sender to send to
	 * @param tag to translate
	 */
	public static void sendTranslatedMessage(CommandSender sender, String tag){
		Translator translator = translateIgnoreError(tag);
		String message = translator.build();
		if("".equals(message)) return; //no message wanted.
		
		sender.sendMessage(message);
	}
}
