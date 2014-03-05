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

import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.util.chat.ChatColorUtils;

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
		modifiedText = modifiedText
				.replaceAll("ä", "ae")
				.replaceAll("Ä", "Ae")
				.replaceAll("ö", "oe")
				.replaceAll("Ö", "Oe")
				.replaceAll("ü", "ue")
				.replaceAll("Ü", "Ue")
				.replaceAll("ß", "ss");
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
	 * Replaces the passed Strings with the passed replacements.
	 * 
	 * @param replaceMap to replace.
	 * 
	 * @return this like a builder.
	 */
	public Translator replace(Map<String, String> replaceMap){		
		for(Entry<String, String> entry : replaceMap.entrySet()){
			String toReplace = "%" + entry.getKey().toUpperCase() + "%";
			String replaceWith = entry.getValue();
			
			modifiedText = modifiedText.replaceAll(toReplace, replaceWith);
		}
		
		return decodeColor();
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
		modifiedText = ChatColorUtils.decodeColors(modifiedText);
		return this;
	}
}
