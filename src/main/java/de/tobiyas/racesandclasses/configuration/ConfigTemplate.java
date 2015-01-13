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
package de.tobiyas.racesandclasses.configuration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields;
import de.tobiyas.racesandclasses.configuration.global.GeneralConfigText;
import de.tobiyas.racesandclasses.util.consts.Consts;

public class ConfigTemplate {
	
	private RacesAndClasses plugin;
	private File configFile;
	
	private Map<String,String> replacements = new HashMap<String, String>();
	
	public ConfigTemplate(){
		fillReplacements();
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
		
		String nodeToCheck = node;
		
		//check replacements
		//only if no new Object is present.
		for(Entry<String,String> entry : replacements.entrySet()){
			String key = entry.getKey();
			String replacement = entry.getValue();
			
			if(nodeToCheck.equals(replacement) && plugin.getConfig().contains(nodeToCheck)){
				nodeToCheck = key;
			}
		}
		
		Object obj = plugin.getConfig().contains(nodeToCheck.replace("_", ".")) ? plugin.getConfig().get(nodeToCheck.replace("_", ".")) 
				: plugin.getConfig().get(nodeToCheck);
		
		
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
			plugin.log("Could not create new ConfigTotal File.");
			e.printStackTrace();
		}
		
		String content = GeneralConfigText.GlobalConfigText();
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
			plugin.log("Error on replacing the ConfigTotal File");
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Fills the Replacements to use.
	 */
	private void fillReplacements() {
		replacements.put("gui_useFoodManaBar", GeneralConfigFields.magic_useFoodManaBar);
		replacements.put("gui_manaManagerType", GeneralConfigFields.magic_manaManagerType);
	}
}
