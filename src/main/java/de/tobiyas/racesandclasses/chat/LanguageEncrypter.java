package de.tobiyas.racesandclasses.chat;

public class LanguageEncrypter {
	
	public static String encryptToLanguage(String message){
		message = message.replace(" ", "");
		return shuffle(message);
	}
	
	private static String shuffle(String s){
		new java.util.Random();
		String shuffledString = "";
		
		while (s.length() != 0){
			int index = (int) Math.floor(Math.random() * s.length());
			char c = s.charAt(index);
			s = s.substring(0,index)+s.substring(index+1);
			shuffledString += c;
			if(Math.random() > 0.85) shuffledString += " ";
		}

		return shuffledString;

	}
}
