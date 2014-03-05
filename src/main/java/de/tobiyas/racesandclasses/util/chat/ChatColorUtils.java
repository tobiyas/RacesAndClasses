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
package de.tobiyas.racesandclasses.util.chat;

public class ChatColorUtils {

	/**
	 * Changes the chat coloring to the one Minecraft understands.
	 * 
	 * @param string
	 * @return
	 */
	public static String decodeColors(String message){
		if(message == null) return "";
		return message.replaceAll("(&([a-f0-9]))", "§$2");
	}
	
	/**
	 * Changes the chat coloring from Minecraft Colors to & colors
	 * 
	 * @param string
	 * @return
	 */
	public static String encodeColors(String message){
		if(message == null) return "";
		return message.replaceAll("(§([a-f0-9]))", "&$2");
	}
}
