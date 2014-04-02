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
package de.tobiyas.racesandclasses.tutorial;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.util.consts.Consts;
import de.tobiyas.racesandclasses.util.tutorial.TutorialState;
import de.tobiyas.util.config.YAMLConfigExtended;

public class TutorialManager implements Observer{

	private boolean enabled;
	
	private RacesAndClasses plugin;
	private HashMap<UUID, TutorialPath> tutorialStates;
	
	
	public TutorialManager(){
		plugin = RacesAndClasses.getPlugin();
		tutorialStates = new HashMap<UUID, TutorialPath>();
		enabled = true;
	}
	
	public void reload(){
		tutorialStates.clear();
		
		YAMLConfigExtended config = checkFileExists();
		if(config.getValidLoad() == false)
			return;
		
		for(String name : config.getChildren("states")){
			UUID id = null;
			try{ id = UUID.fromString(name); }catch(IllegalArgumentException exp){ continue; }

			String stateName = config.getString("states." + name + ".stateName");
			TutorialState state = TutorialState.getState(stateName);
			int stateValue = config.getInt("states." + name + ".stateValue");
			
			TutorialPath path = new TutorialPath(id, true);
			path.setState(state);
			
			if(stateValue != 1)
				path.setStateStep(stateValue);
			path.activate();
			tutorialStates.put(id, path);
		}
	}
	
	private void save(){
		YAMLConfigExtended config = checkFileExists();
		
		for(UUID player : tutorialStates.keySet()){
			TutorialPath path = tutorialStates.get(player);
			path.save(config);
		}
	}
	
	private YAMLConfigExtended checkFileExists(){
		File tutorialsPath = new File(Consts.tutorialPath);
		if(!tutorialsPath.exists())
			tutorialsPath.mkdirs();
		
		File statesYML = new File(Consts.tutorialYML);
		if(!statesYML.exists())
			try{
				statesYML.createNewFile();
			} catch (IOException exception) {
				plugin.log("Could not create " + statesYML.toString());
			}
		
		YAMLConfigExtended config = new YAMLConfigExtended(Consts.tutorialYML).load();
		if(!config.contains("states")){
			config.createSection("states");
			config.save();
		}
		
		return config;
	}

	@Override
	public void update(Observable clazz, Object potentialContainer) {
		if(!(potentialContainer instanceof TutorialStepContainer))
			return;
		TutorialStepContainer container = (TutorialStepContainer) potentialContainer;
		UUID playerUUID = container.getUUID();
		
		TutorialPath path = tutorialStates.get(playerUUID);
		if(path == null){
			if(container.getState() == TutorialState.start)
				createNew(container.getUUID());
		}else
			path.handle(container);
	}
	
	private void createNew(UUID player){
		synchronized(tutorialStates){
			if(tutorialStates.get(player) == null)
				tutorialStates.put(player, new TutorialPath(player));
		}
	}
	
	
	
	//handle statics to intern
	
	private boolean skipIntern(UUID playerUUID){
		TutorialPath path = tutorialStates.get(playerUUID);
		if(path == null)
			return false;
		
		return path.skip();
	}
	
	private boolean startIntern(UUID player){
		TutorialPath path = tutorialStates.get(player);
		if(path != null)
			tutorialStates.remove(player);
		
		createNew(player);
		return true;
	}
	
	private boolean stopIntern(UUID playerUUID){
		OfflinePlayer player = Bukkit.getPlayer(playerUUID);
		TutorialPath path = tutorialStates.get(playerUUID);
		if(path == null){
			if(player != null && player.isOnline())
				player.getPlayer().sendMessage(ChatColor.RED + "No active Tutorial.");
			return false;
		}
		
		removePerson(playerUUID);
		return path.stop();
	}
	
	private void removePerson(UUID playerUUID){
		YAMLConfigExtended config = checkFileExists();
		if(config.getValidLoad() == false)
			return;
		
		config.set("states." + playerUUID, 0);
		config.set("states." + playerUUID, null);
		config.save();
	}
	
	private boolean resetIntern(UUID playerUUID){
		TutorialPath path = tutorialStates.get(playerUUID);
		if(path == null)
			return false;
		
		return path.reset();
	}
	
	private boolean repostIntern(UUID playerUUID){
		TutorialPath path = tutorialStates.get(playerUUID);
		if(path == null)
			return false;
		
		return path.repostState();
	}
	
	private boolean setStateIntern(UUID playerUUID, String state){
		TutorialPath path = tutorialStates.get(playerUUID);
		if(path == null)
			return false;
		
		TutorialState realState = TutorialState.getState(state);
		if(state == null)
			return false;
		
		return path.setState(realState);
	}
	
	private TutorialStepContainer getCurrentStateIntern(String playerName){
		TutorialPath path = tutorialStates.get(playerName);
		if(path == null)
			return null;
		
		return path.getCurrentState();
	}
		
	
	//Below only Functions visible to the outside!
	
	//helpfull public 
	
	
	//direct Handles
	
	public boolean skip(UUID playerUUID){
		if(!isActive())
			return false;
		
		return skipIntern(playerUUID);
	}
		
	public boolean start(UUID playerUUID){
		if(!isActive())
			return false;
		
		return startIntern(playerUUID);
	}
		
	public boolean stop(UUID playerUUID){
		if(!isActive())
			return false;
		
		return stopIntern(playerUUID);
	}
		
	public boolean reset(UUID playerUUID){
		if(!isActive())
			return false;
		
		return resetIntern(playerUUID);
	}
	
	public boolean repost(UUID playerUUID){
		if(!isActive())
			return false;
		
		return repostIntern(playerUUID);
	}
	
	public boolean setState(UUID playerUUID, String state){
		if(!isActive())
			return false;
		
		return setStateIntern(playerUUID, state);
	}
	
	
	public void disable(){
		enabled = false;
	}
	
	public void enable(){
		enabled = true;
	}
	
	public void shutDown(){
		save();
		tutorialStates.clear();
		disable();
	}
	
	public boolean isActive(){		
		if(enabled == false)
			return false;
			
		return true;
	}
	
	//writeback
	public void unregister(UUID playerUUID){
		if(!isActive())
			return;
		
		synchronized(tutorialStates){
			tutorialStates.remove(playerUUID);
		}
	}
	
	//add Observer
	public void registerObserver(Observable observable){
		if(!isActive())
			return;
		
		observable.addObserver(this);
	}
	
	//For debugging only
	public TutorialStepContainer getCurrentState(String playerName){
		if(!isActive())
			return null;
		
		return getCurrentStateIntern(playerName);
	}
}
