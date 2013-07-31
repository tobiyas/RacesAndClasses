package de.tobiyas.racesandclasses.tutorial;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.util.consts.Consts;
import de.tobiyas.racesandclasses.util.tutorial.TutorialState;
import de.tobiyas.util.config.YAMLConfigExtended;

public class TutorialManager implements Observer{

	private static TutorialManager tutorialManager;
	private boolean enabled;
	
	private RacesAndClasses plugin;
	private HashMap<String, TutorialPath> tutorialStates;
	
	
	private TutorialManager(){
		tutorialManager = this;
		plugin = RacesAndClasses.getPlugin();
		tutorialStates = new HashMap<String, TutorialPath>();
		enabled = true;
		
		if(!plugin.testingMode)
			load();
	}
	
	private void load(){
		YAMLConfigExtended config = checkFileExists();
		if(config.getValidLoad() == false)
			return;
		
		for(String name : config.getChildren("states")){
			String stateName = config.getString("states." + name + ".stateName");
			TutorialState state = TutorialState.getState(stateName);
			int stateValue = config.getInt("states." + name + ".stateValue");
			
			TutorialPath path = new TutorialPath(name, true);
			path.setState(state);
			
			if(stateValue != 1)
				path.setStateStep(stateValue);
			path.activate();
			tutorialStates.put(name, path);
		}
	}
	
	private void save(){
		YAMLConfigExtended config = checkFileExists();
		
		for(String player : tutorialStates.keySet()){
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
		String playerName = container.getName();
		
		TutorialPath path = tutorialStates.get(playerName);
		if(path == null){
			if(container.getState() == TutorialState.start)
				createNew(container.getName());
		}else
			path.handle(container);
	}
	
	private void createNew(String playerName){
		synchronized(tutorialStates){
			if(tutorialStates.get(playerName) == null)
				tutorialStates.put(playerName, new TutorialPath(playerName));
		}
	}
	
	
	
	//handle statics to intern
	
	private boolean skipIntern(String playerName){
		TutorialPath path = tutorialStates.get(playerName);
		if(path == null)
			return false;
		
		return path.skip();
	}
	
	private boolean startIntern(String playerName){
		TutorialPath path = tutorialStates.get(playerName);
		if(path != null)
			tutorialStates.remove(playerName);
		
		createNew(playerName);
		return true;
	}
	
	private boolean stopIntern(String playerName){
		Player player = Bukkit.getPlayer(playerName);
		TutorialPath path = tutorialStates.get(playerName);
		if(path == null){
			if(player != null)
				player.sendMessage(ChatColor.RED + "No active Tutorial.");
			return false;
		}
		
		removePerson(playerName);
		return path.stop();
	}
	
	private void removePerson(String playerName){
		YAMLConfigExtended config = checkFileExists();
		if(config.getValidLoad() == false)
			return;
		
		config.set("states." + playerName, 0);
		config.set("states." + playerName, null);
		config.save();
	}
	
	private boolean resetIntern(String playerName){
		TutorialPath path = tutorialStates.get(playerName);
		if(path == null)
			return false;
		
		return path.reset();
	}
	
	private boolean repostIntern(String playerName){
		TutorialPath path = tutorialStates.get(playerName);
		if(path == null)
			return false;
		
		return path.repostState();
	}
	
	private boolean setStateIntern(String playerName, String state){
		TutorialPath path = tutorialStates.get(playerName);
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
		
	
	//Below only statics!
	
	//helpfull private/public statics
	
	
	//direct Handles
	
	public static boolean skip(String playerName){
		if(!isActive())
			return false;
		
		return tutorialManager.skipIntern(playerName);
	}
		
	public static boolean start(String playerName){
		if(!isActive())
			return false;
		
		return tutorialManager.startIntern(playerName);
	}
		
	public static boolean stop(String playerName){
		if(!isActive())
			return false;
		
		return tutorialManager.stopIntern(playerName);
	}
		
	public static boolean reset(String playerName){
		if(!isActive())
			return false;
		
		return tutorialManager.resetIntern(playerName);
	}
	
	public static boolean repost(String playerName){
		if(!isActive())
			return false;
		
		return tutorialManager.repostIntern(playerName);
	}
	
	public static boolean setState(String playerName, String state){
		if(!isActive())
			return false;
		
		return tutorialManager.setStateIntern(playerName, state);
	}
	
	
	//Other direkt addresses
	public static boolean init(){
		if(tutorialManager != null){
				enable();
			return false;
		}
			
		tutorialManager = new TutorialManager();
		return true;
	}
	
	public static void disable(){
		tutorialManager.enabled = false;
	}
	
	public static void enable(){
		tutorialManager.enabled = true;
	}
	
	public static void shutDown(){
		tutorialManager.save();
		tutorialManager.tutorialStates.clear();
		disable();
	}
	
	public static boolean isActive(){
		if(tutorialManager == null)
			return false;
		
		if(tutorialManager.enabled == false)
			return false;
			
		return true;
	}
	
	//writeback
	public static void unregister(String playerName){
		if(!isActive())
			return;
		
		synchronized(tutorialManager.tutorialStates){
			tutorialManager.tutorialStates.remove(playerName);
		}
	}
	
	//add Observer
	public static void registerObserver(Observable observable){
		if(!isActive())
			return;
		
		observable.addObserver(TutorialManager.tutorialManager);
	}
	
	//For debugging only
	public static TutorialStepContainer getCurrentState(String playerName){
		if(!isActive())
			return null;
		
		return tutorialManager.getCurrentStateIntern(playerName);
	}
}
