/*******************************************************************************
 * Copyright 2014 Tobias Welther
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.tobiyas.racesandclasses.APIs;

import static de.tobiyas.racesandclasses.translation.languages.Keys.plugin_pre;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayerManager;
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
	 * Translates the tag without throwing an {@link TranslationNotFoundException}.
	 * <br>This indicates that the tag is just returned if the tag was not found.
	 * <br>Also the current language + the STD language (mostly English) is searched.
	 * 
	 * @param tag to search for
	 * @param arg the args that are present in the Translation.
	 * @return the translation or the tag itself if not found.
	 */
	public static String translate(RaCPlayer toTranslateTo, String tag, String... arg){
		Map<String, String> replacements = HashMapUtils.generateStringStringMap(arg);
		return translate(toTranslateTo, tag, replacements);
	}
	
	
	/**
	 * Translates the tag without throwing an {@link TranslationNotFoundException}.
	 * <br>This indicates that the tag is just returned if the tag was not found.
	 * <br>Also the current language + the STD language (mostly English) is searched.
	 * 
	 * @param tag to search for
	 * @param arg the args that are present in the Translation.
	 * @return the translation or the tag itself if not found.
	 */
	public static String translate(RaCPlayer toTranslateTo, String tag, Map<String,String> replacements){
		try{
			Translator translator =  translateIgnoreError(tag);
			if(translator == null) return tag;
			
			translator.setReplacePlayer(toTranslateTo);
			translator.replace(replacements);
			return translator.build();
		}catch(Throwable exp){
			return new Translator(tag).build();
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
		if(sender == null) return;
		
		Translator translator = translateIgnoreError(tag);
		translator.replace(replacements);
		if(sender instanceof Player) translator.setReplacePlayer(RaCPlayerManager.get().getPlayer((Player) sender));
		
		String message = translator.build();
		if("".equals(message)) return; //no message wanted.
		
		String pluginPre = translateIgnoreError(plugin_pre).build();
		sender.sendMessage(pluginPre + message);
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
		sendTranslatedMessage(sender, tag, new HashMap<String, String>());
	}

	
	//BELOW support for RaCPlayer.
	
	/**
	 * Translates a messages and sends it to the passed sender.
	 * 
	 * @param player to send to
	 * @param tag to translate
	 * @param replacements to replace
	 */
	public static void sendTranslatedMessage(RaCPlayer player, String tag, Map<String, String> replacements){
		if(player == null) return;
		sendTranslatedMessage(player.getPlayer(), tag, replacements);
	}
	
	/**
	 * Translates a messages and sends it to the passed sender.
	 * 
	 * @param player to send to
	 * @param tag to translate
	 * @param arg The Map to replace: {arg1, replacement1, arg2, replacement2, ....}
	 */
	public static void sendTranslatedMessage(RaCPlayer player, String tag, String... arg){
		if(player == null) return;
		
		Map<String, String> replacements = HashMapUtils.generateStringStringMap(arg);
		sendTranslatedMessage(player, tag, replacements);
	}
	
	/**
	 * Translates a messages and sends it to the passed sender.
	 * <br>No replacements here!
	 * 
	 * @param player to send to
	 * @param tag to translate
	 */
	public static void sendTranslatedMessage(RaCPlayer player, String tag){
		if(player == null) return;
		
		sendTranslatedMessage(player, tag, new HashMap<String, String>());
	}
}
