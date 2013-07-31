package de.tobiyas.racesandclasses.util.chat;

public class ChatColorUtils {

	/**
	 * Changes the chat coloring to the one Minecraft understands.
	 * 
	 * @param string
	 * @return
	 */
	public static String decodeColors(String string){
		return string.replaceAll("(&([a-f0-9]))", "§$2");
	}
}
