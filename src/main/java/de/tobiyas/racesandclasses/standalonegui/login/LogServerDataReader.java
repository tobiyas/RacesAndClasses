package de.tobiyas.racesandclasses.standalonegui.login;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class LogServerDataReader {

	
	public static final String CONNECT_TAG = "RaC-Connect";
	
	
	/**
	 * Read from the Default Minecraft Client.
	 * 
	 * @return the data read. If present.
	 */
	public static ServerConnectionData readFromLogFile(){
		File appdata = new File(System.getenv("APPDATA"));
		if(!appdata.exists()) return null;
		
		File minecraftFolder = new File(appdata, ".minecraft");
		if(!minecraftFolder.exists()) return null;
		
		File logFolder = new File(minecraftFolder, "logs");
		if(!logFolder.exists()) return null;
		
		File currentLog = new File(logFolder, "latest.log");
		if(!logFolder.exists()) return null;
		
		try{
			BufferedReader reader = new BufferedReader(new FileReader(currentLog));
			List<String> lines = new LinkedList<String>();
			
			String line = "";
			while((line = reader.readLine()) != null) lines.add(line);
			reader.close();
			
			//try to find the Correct Line:
			for(int i = lines.size() - 1; i > 0; i--){
				String logLine = lines.get(i);
				if(!logLine.contains("[CHAT] ")) continue;
				
				String[] split = logLine.split(Pattern.quote("[CHAT] "));
				if(split.length != 2) continue;
				
				String toParse = split[1];
				
				if(toParse.contains(CONNECT_TAG + " ")){
					//only on correct Text.
					toParse = toParse.split(Pattern.quote(CONNECT_TAG + " "))[1];
					String[] parseable = toParse.split(" ");
					
					if(parseable.length != 4) continue;
					
					try{
						String host = parseable[0];
						int port = Integer.parseInt(parseable[1]);
						String username = parseable[2];
						String connectionToken = parseable[3];
						
						return new ServerConnectionData(host, port, username, connectionToken);
					}catch(Throwable exp){}
				}
			}
		}catch(Throwable exp){
			exp.printStackTrace();
		}
		
		return null;
	}

}
