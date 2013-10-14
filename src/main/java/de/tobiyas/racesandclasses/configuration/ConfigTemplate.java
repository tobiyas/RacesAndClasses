package de.tobiyas.racesandclasses.configuration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.configuration.global.GeneralConfigText;
import de.tobiyas.racesandclasses.util.consts.Consts;

public class ConfigTemplate {
	
	private RacesAndClasses plugin;
	private File configFile;
	
	public ConfigTemplate(){
		plugin = RacesAndClasses.getPlugin();
		
		File pluginFolder = new File(plugin.getDataFolder().toString());
		if(!pluginFolder.exists())
			pluginFolder.mkdirs();
		
		configFile = new File(plugin.getDataFolder() + File.separator + "config.yml");
	}
	
	public boolean isOldConfigVersion(){
		if(!configFile.exists())
			return true;
		
		boolean isOldVersion = true;
		
		try{
			BufferedReader reader = new BufferedReader(new FileReader(configFile));
			
			while(true){
				String currentLine = reader.readLine();
				if(currentLine == null)
					break;

				if(currentLine.contains("#TemplateVersion ")){
					currentLine = currentLine.replace("#TemplateVersion ", "");
					
					if(currentLine.equalsIgnoreCase(Consts.configVersion))
						isOldVersion = false;
					break;
				}
			}
			
			reader.close();
		}catch(Exception e){
			plugin.log("Could not get Version");
		}
		
		return isOldVersion;
	}
	
	private String modifyLine(String line){
		if(line.length() == 0) 
			return line;
		
		if(line.contains("#")) 
			return line;
		
		if(!line.contains(":"))
			return line;
		
		String[] nodes = line.split(":");		
		String node = nodes[0];
		
		Object obj = plugin.getConfig().contains(node.replace("_", ".")) ? plugin.getConfig().get(node.replace("_", ".")) 
				: plugin.getConfig().get(node);
		
		//write default Value
		if(obj == null){
			return line;
		}
		
		String stringAdditions = "";
		if(obj instanceof String){
			stringAdditions = "'";
		}
		
		return node + ": " + stringAdditions + obj.toString() + stringAdditions;
	}
	
	public void writeTemplate(){
		
		if(configFile.exists())
			configFile.delete();
		
		try {
			configFile.createNewFile();
		} catch (IOException e) {
			plugin.log("Could not create new Config File.");
			e.printStackTrace();
		}
		
		String content = GeneralConfigText.GlobalConfigText;
		String[] lines = content.split("\n");
		
		content = "";
		for(String line : lines){
			content += modifyLine(line) + System.getProperty("line.separator");
		}
		
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(configFile));
			out.write(content);
			out.close();
		} catch (IOException e) {
			plugin.log("Error on replacing the Config File");
			e.printStackTrace();
		}
	}
}
