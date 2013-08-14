package de.tobiyas.racesandclasses.healthmanagement;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.armorandtool.ArmorToolManager;
import de.tobiyas.racesandclasses.datacontainer.arrow.ArrowManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.util.consts.Consts;
import de.tobiyas.util.config.YAMLConfigExtended;

public class HealthManager{
	
	/**
	 * This is the map of each player's health container
	 */
	private HashMap<String, HealthContainer> playerHealth;
	
	/**
	 * The plugin to access other parts of the Plugin.
	 */
	private RacesAndClasses plugin;
	
	/**
	 * Creates a new HealthManager and resets all values
	 */
	public HealthManager(){
		playerHealth = new HashMap<String, HealthContainer>();
		plugin = RacesAndClasses.getPlugin();
	}
	
	/**
	 * Loads the health manager from the playerdata.yml
	 */
	public void init(){
		loadHealthContainer();
	}
	
	/**
	 * Stores the data in the HealthManager in the playerData.yml.
	 */
	public void saveHealthContainer(){
		checkFileExist();
		for(String player : playerHealth.keySet()){
			HealthContainer container = playerHealth.get(player);
			container.save();
		}
	}
	
	/**
	 * loads the health manager internally
	 */
	private void loadHealthContainer(){
		checkFileExist();
		YAMLConfigExtended config = new YAMLConfigExtended(Consts.playerDataYML).load();
		
		Set<String> players = config.getChildren("playerdata");
		for(String player : players){
			HealthContainer container = HealthContainer.constructContainerFromYML(player);
			if(container != null){
				playerHealth.put(player, container);
			}
		}
	}
	
	/**
	 * Checks if the playerData file exists and creates it if not.
	 */
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
	
	/**
	 * Gives the player a new HealthContainer.
	 * It scans the player's data from the Race and Class container and
	 * calculates the needed values.
	 * 
	 * @param player to create
	 */
	public void addPlayer(String player){
		RaceContainer container = (RaceContainer) plugin.getRaceManager().getHolderOfPlayer(player);
		double maxHealth = 1;
		
		if(container == null)
			maxHealth = plugin.getConfigManager().getGeneralConfig().getConfig_defaultHealth();
		else
			maxHealth = container.getRaceMaxHealth();
		
		playerHealth.put(player, new HealthContainer(player, maxHealth));
	}
	
	/**
	 * This parses the current health of a player.
	 * NOTICE: It redirects to the bukkit function.
	 * 
	 * @param playerName
	 * @return
	 */
	public double getHealthOfPlayer(String playerName){
		HealthContainer container = getCreate(playerName, true);
		if(container == null) return -1;
		return container.getCurrentHealth();
	}
	
	/**
	 * Rescans the player and creates a new {@link HealthContainer} if he has none.
	 *  
	 * @param player to check
	 */
	public void checkPlayer(String player){
		HealthContainer hContainer = playerHealth.get(player);
		if(hContainer == null){
			RaceContainer container = (RaceContainer) plugin.getRaceManager().getHolderOfPlayer(player);
			double maxHealth;
			if(container == null){
				//this should actually not happen, since there is a default Race.
				maxHealth = plugin.getConfigManager().getGeneralConfig().getConfig_defaultHealth();
			}else{
				maxHealth = container.getRaceMaxHealth();
			}
			
			HealthContainer healthContainer = new HealthContainer(player, maxHealth);
			healthContainer.checkStats();
			playerHealth.put(player, healthContainer);
		}else{
			hContainer.checkStats();
		}
	}
	
	/**
	 * Checks if the ArrowManager of a Player exists and returns it.
	 * If the ArrowManager does not exist, it is created.
	 * 
	 * @param playerName to check
	 * @return the {@link ArrowManager} of the Player
	 */
	public ArrowManager getArrowManagerOfPlayer(String playerName) {
		return getCreate(playerName, true).getArrowManager();
	}
	
	/**
	 * Checks if the {@link ArmorToolManager} of a Player exists and returns it.
	 * If the {@link ArmorToolManager} does not exist, it is created.
	 * 
	 * @param playerName to check
	 * @return the {@link ArmorToolManager} of the Player
	 */
	public ArmorToolManager getArmorToolManagerOfPlayer(String playerName){
		return getCreate(playerName, true).getArmorToolManager();
	}

	/**
	 * Forces an HP display output for the player.
	 * 
	 * @param playerName to force the output
	 * @return true, if it worked.
	 */
	public boolean displayHealth(String playerName) {
		HealthContainer container = getCreate(playerName, true);
		if(container == null) return false; //this should not happen...
		container.forceHPOut();
		return true;
	}

	/**
	 * Switches the God Mode of the player.
	 * 
	 * @param name to switch
	 * @return true if worked, false if player not found.
	 */
	public boolean switchGod(String name) {
		HealthContainer container = playerHealth.get(name);
		if(container != null){
			container.switchGod();
			return true;
		}
		return false;
	}

	/**
	 * Returns the maximum Health of a Player.
	 * This should be identical to: {@link Player#getMaxHealth()}
	 * 
	 * @param playerName to check
	 * @return the max health of a player.
	 */
	public double getMaxHealthOfPlayer(String playerName) {
		HealthContainer container = getCreate(playerName, true);
		if(container == null) return -1;
		return container.getMaxHealth();
	}
	
	/**
	 * Gets the current HealthContainer of the Player.
	 * If not found, a new one is created. 
	 * 
	 * @param playerName to check
	 * @return the found or new created container.
	 */
	private HealthContainer getCreate(String playerName){
		return getCreate(playerName, true);
	}
	
	/**
	 * Gets the HealthContainer of a Player.
	 * If the container is not found, a new one is created 
	 * if the flag (create) is set to true.
	 * 
	 * @param playerName
	 * @param create
	 * @return
	 */
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
