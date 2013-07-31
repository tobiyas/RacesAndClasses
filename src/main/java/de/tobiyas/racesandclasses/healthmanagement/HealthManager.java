package de.tobiyas.racesandclasses.healthmanagement;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.armorandtool.ArmorToolManager;
import de.tobiyas.racesandclasses.datacontainer.arrow.ArrowManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceManager;
import de.tobiyas.racesandclasses.util.consts.Consts;
import de.tobiyas.util.config.YAMLConfigExtended;

public class HealthManager{
	
	private HashMap<String, HealthContainer> playerHealth;
	private RacesAndClasses plugin;
	
	private static HealthManager manager;
	
	public HealthManager(){
		playerHealth = new HashMap<String, HealthContainer>();
		plugin = RacesAndClasses.getPlugin();
		manager = this;
	}
	
	public void init(){
		loadHealthContainer();
	}
	
	public void saveHealthContainer(){
		checkFileExist();
		for(String player : playerHealth.keySet()){
			HealthContainer container = playerHealth.get(player);
			container.save();
		}
	}
	
	public void loadHealthContainer(){
		checkFileExist();
		YAMLConfigExtended config = new YAMLConfigExtended(Consts.playerDataYML).load();
		
		Set<String> players = config.getChildren("playerdata");
		for(String player : players){
			HealthContainer container = HealthContainer.constructContainerFromYML(player);
			if(container != null)
				playerHealth.put(player, container);
		}
	}
	
	private void checkFileExist(){
		File file = new File(plugin.getDataFolder() + File.separator + "PlayerData" + File.separator);
		if(!file.exists())
			file.mkdirs();
		
		File file2 = new File(file.toString() + File.separator + "playerdata.yml");
		
		if(!file2.exists()){
			try {
				file2.createNewFile();
			} catch (IOException e) {
				plugin.log("Could not create playerdata.yml in folder: plugins/Races/PlayerData/");
			}
		}
	}
	
	public void addPlayer(String player){
		RaceContainer container = (RaceContainer) RaceManager.getInstance().getHolderOfPlayer(player);
		int maxHealth = 1;
		
		if(container == null)
			maxHealth = plugin.getConfigManager().getGeneralConfig().getConfig_defaultHealth();
		else
			maxHealth = container.getRaceMaxHealth();
		
		playerHealth.put(player, new HealthContainer(player, maxHealth));
	}
	
	public double getHealthOfPlayer(String playerName){
		HealthContainer container = getCreate(playerName, true);
		if(container == null) return -1;
		return container.getCurrentHealth();
	}
	
	public void checkPlayer(String player){
		HealthContainer hContainer = playerHealth.get(player);
		if(hContainer == null){
			RaceContainer container = (RaceContainer) RaceManager.getInstance().getHolderOfPlayer(player);
			double maxHealth;
			if(container == null)
				maxHealth = plugin.getConfigManager().getGeneralConfig().getConfig_defaultHealth();
			else
				maxHealth = container.getRaceMaxHealth();
			
			HealthContainer healthContainer = new HealthContainer(player, maxHealth);
			healthContainer.checkStats();
			playerHealth.put(player, healthContainer);
		}else{
			hContainer.checkStats();
		}
	}
	
	public static HealthManager getHealthManager(){
		return manager;
	}


	public ArrowManager getArrowManagerOfPlayer(String playerName) {
		return getCreate(playerName, true).getArrowManager();
	}
	
	public ArmorToolManager getArmorToolManagerOfPlayer(String playerName){
		return getCreate(playerName, true).getArmorToolManager();
	}

	public boolean displayHealth(String playerName) {
		HealthContainer container = getCreate(playerName, true);
		if(container == null) return false;
		container.forceHPOut();
		return true;
	}

	public boolean switchGod(String name) {
		HealthContainer container = playerHealth.get(name);
		if(container != null){
			container.switchGod();
			return true;
		}
		return false;
	}

	public double getMaxHealthOfPlayer(String playerName) {
		HealthContainer container = getCreate(playerName, true);
		if(container == null) return -1;
		return container.getMaxHealth();
	}
	
	private HealthContainer getCreate(String playerName){
		return getCreate(playerName, true);
	}
	
	private HealthContainer getCreate(String playerName, boolean create){
		HealthContainer container = playerHealth.get(playerName);
		if(container == null && create){
			checkPlayer(playerName);
			container = playerHealth.get(playerName);
		}
		
		return container;
	}

	/**
	 * Returns if the Player has God mode on.
	 * 
	 * @param playerName to check
	 * @return true if has god on
	 */
	public boolean isGod(String playerName) {
		HealthContainer containerOfPlayer = getCreate(playerName);
		return containerOfPlayer.isGod();
	}

}
