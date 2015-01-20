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
package de.tobiyas.racesandclasses.translation;

import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;

public class Translator {

	/**
	 * The Original Text read.
	 */
	private final String originalText;
	
	/**
	 * The modified Text.
	 */
	private String modifiedText;
	
	/**
	 * Replaces to a player.
	 */
	private RaCPlayer playerReplace;
	
	/**
	 * The translator for the Original Text.
	 * <br>Directly translates ChatColors.
	 * 
	 * @param originalText to modify
	 */
	public Translator(String originalText) {
		this.originalText = originalText;
				
		this.modifiedText = originalText;
		this.tryReplaceReTranslation();
		
		decodeColor();
	}
	
	
	/**
	 * Resets the modified Text to the original one.
	 */
	public void resetToOriginal(){
		this.modifiedText = originalText;
	}
	
	
	/**
	 * Returns the modified String.
	 * Be sure to check if everything is replaced.
	 */
	public String build(){
		replaceUmlauts();
		
		return modifiedText;
	}

	
	/**
	 * Checks and replaces Umlauts
	 */
	private void replaceUmlauts() {
		modifiedText = modifiedText.replace("<o>", '\u00f6' + "")
			.replace("<O>", '\u00D6' + "")
			.replace("<a>", '\u00e4' + "")
			.replace("<A>", '\u00c4' + "")	
			.replace("<u>", '\u00fc' + "")
			.replace("<U>", '\u00dc' + "")
			.replace("<ss>", '\u00df' + "");
	}


	/**
	 * Checks if any params are left in the Expression.
	 * 
	 * @return true if all are filled, false if not.
	 */
	public boolean everyParamFilled(){
		return !modifiedText.matches("*.%.*%.*");
	}
	
	
	/**
	 * Sets the Player to replace stuff
	 * 
	 * @param player to set
	 */
	public void setReplacePlayer(RaCPlayer player){
		this.playerReplace = player;
	}
	
	
	/**
	 * Replaces the passed Strings with the passed replacements.
	 * 
	 * @param replaceMap to replace.
	 * 
	 * @return this like a builder.
	 */
	public Translator replace(Map<String, String> replaceMap){
		if(modifiedText == null || replaceMap == null) return this; 
		
		for(Entry<String, String> entry : replaceMap.entrySet()){
			String toReplace = "%" + entry.getKey().toUpperCase() + "%";
			String replaceWith = entry.getValue();
			
			modifiedText = modifiedText.replaceAll(toReplace, replaceWith);
		}
		
		replaceToPlayer();
		
		return decodeColor();
	}
	
	
	/**
	 * Replaces some player stuff.
	 */
	private void replaceToPlayer() {
		if(playerReplace == null) return;
		
		modifiedText = modifiedText.replace("%PLAYER%", playerReplace.getDisplayName());
		modifiedText = modifiedText.replace("%PLAYERNAME%", playerReplace.getDisplayName());
		
		if(playerReplace.getRace() != null) modifiedText = modifiedText.replace("%RACE%", playerReplace.getRace().getDisplayName());
		if(playerReplace.getclass() != null) modifiedText = modifiedText.replace("%CLASS%", playerReplace.getclass().getDisplayName());
		
		modifiedText = modifiedText.replace("%LEVEL%", String.valueOf(playerReplace.getLevelManager().getCurrentLevel()));
		modifiedText = modifiedText.replace("%EXP%", String.valueOf(playerReplace.getLevelManager().getCurrentExpOfLevel()));

		if(playerReplace.isOnline()){
			Player player = playerReplace.getPlayer();
			modifiedText = modifiedText.replace("%WORLD%", player.getWorld().getName());
		}
	}


	/**
	 * Replaces all occuraces of that word with the passed String.
	 * 
	 * @param toReplace to search
	 * @param replaceWith to replace with
	 * 
	 * @return this like a builder.
	 */
	public Translator replace(String toReplace, String replaceWith){
		toReplace = "%" + toReplace.toUpperCase() + "%";
		modifiedText = modifiedText.replaceAll(toReplace, replaceWith);
		return decodeColor();
	}
	
	
	@Override
	public String toString(){
		return build();
	}
	
	
	/**
	 * Tries to find @TEXT@ and replaces it with a translation
	 */
	public Translator tryReplaceReTranslation(){
		Pattern pattern = Pattern.compile("\\@(.*?)\\@");
		Matcher matcher = pattern.matcher(this.modifiedText);
		
		while(matcher.find()){
			String found = matcher.group(1);
			Translator translator = LanguageAPI.translateIgnoreError(found)
					.tryReplaceReTranslation();
			
			modifiedText = modifiedText.replaceAll("@" + found + "@", 
					translator.build());
		}
		
		return decodeColor();
	}
	
	
	/**
	 * Decodes the color codes of the Translator.
	 * 
	 * @return this like a builder.
	 */
	public Translator decodeColor(){
		modifiedText = ChatColor.translateAlternateColorCodes('&', modifiedText);
		return this;
	}
}
