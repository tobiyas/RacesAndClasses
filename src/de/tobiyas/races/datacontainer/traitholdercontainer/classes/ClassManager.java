package de.tobiyas.races.datacontainer.traitholdercontainer.classes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import de.tobiyas.races.Races;
import de.tobiyas.races.configuration.global.YAMLConfigExtended;
import de.tobiyas.races.datacontainer.health.HealthManager;
import de.tobiyas.races.datacontainer.traitholdercontainer.DefaultContainer;
import de.tobiyas.races.util.consts.Consts;

public class ClassManager {

	private HashMap<String, ClassContainer> playerClasses;
	private HashSet<ClassContainer> classList;
	
	private YAMLConfigExtended classConfig;
	private YAMLConfigExtended memberConfig;
	
	private static ClassManager classManager;
	private Races plugin;
	
	public ClassManager(){
		classManager = this;
		plugin = Races.getPlugin();
		
		playerClasses = new HashMap<String, ClassContainer>();
		classList = new HashSet<ClassContainer>();
		
		classConfig = new YAMLConfigExtended(Consts.classesYML);
		memberConfig = new YAMLConfigExtended(Consts.membersYML);
		
		DefaultContainer.createSTDClasses();
	}
	
	public void init(){
		loadClassesFromFile();
		readMemberList();
	}
	
	private void loadClassesFromFile(){
		playerClasses.clear();
		classList.clear();
		
		DefaultContainer.createSTDClasses();
		
		classConfig.load();
		if(!classConfig.getValidLoad()){
			plugin.log("Error on loading classes.yml.");
			return;
		}
		
		for(String className : classConfig.getYAMLChildren("classes")){
			ClassContainer container = ClassContainer.loadClass(classConfig, className);
			if(container != null)
				classList.add(container);
		}
	}
	
	private void readMemberList(){
		playerClasses = new HashMap<String, ClassContainer>();		
		memberConfig.load();
		
		for(String member : memberConfig.getYAMLChildren("playerdata")){
			String className = memberConfig.getString("playerdata." + member + ".class");
			playerClasses.put(member, getClassByName(className));
		}
				
	}	
	
	
	public boolean addPlayerToClass(String player, String potentialClass){
		ClassContainer container = getClassByName(potentialClass);
		if(container == null) return false;
		memberConfig.load();
		
		playerClasses.put(player, container);
		memberConfig.set("playerdata." + player + ".class", container.getName());
		HealthManager.getHealthManager().checkPlayer(player);
		memberConfig.save();
		
		return true;
	}
	
	public boolean changePlayerClass(String player, String potentialClass){
		if(getClassByName(potentialClass) == null) return false;
		playerClasses.remove(player);
		
		memberConfig.load();
		memberConfig.set("playerdata." + player + ".class", null);
		memberConfig.save();
		
		return addPlayerToClass(player, potentialClass);
	}
	
	
	
	
	public ClassContainer getClassByName(String className){
		for(ClassContainer container : classList){
			if(container.getName().equalsIgnoreCase(className))
				return container;
		}
		return null;
	}
	
	public LinkedList<String> getClassNames(){
		LinkedList<String> classes = new LinkedList<String>();
		for(ClassContainer container : classList)
			if(container != null) 
				classes.add(container.getName());
		
		return classes;
	}
	
	public ClassContainer getClassOfPlayer(String player){
		return playerClasses.get(player);
	}
	
	public static ClassManager getInstance(){
		return classManager;
	}
}
