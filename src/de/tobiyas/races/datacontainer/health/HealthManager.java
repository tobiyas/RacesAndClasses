package de.tobiyas.races.datacontainer.health;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import de.tobiyas.races.Races;
import de.tobiyas.races.configuration.global.YAMLConfigExtended;
import de.tobiyas.races.datacontainer.arrow.ArrowManager;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceManager;
import de.tobiyas.races.util.consts.Consts;

public class HealthManager implements Observer{
	
	private HashMap<String, HealthContainer> playerHealth;
	private Races plugin;
	
	private static HealthManager manager;
	
	public HealthManager(){
		playerHealth = new HashMap<String, HealthContainer>();
		plugin = Races.getPlugin();
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
		YAMLConfigExtended config = new YAMLConfigExtended(Consts.membersYML).load();
		
		Set<String> players = config.getYAMLChildren("playerdata");
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
		
		if(!file2.exists())
			try {
				file2.createNewFile();
			} catch (IOException e) {
				plugin.log("Could not create playerdata.yml in folder: plugins/Races/PlayerData/");
			}
	}
	
	public void addPlayer(String player){
		RaceContainer container = RaceManager.getManager().getRaceOfPlayer(player);
		int maxHealth = 1;
		
		if(container == null)
			maxHealth = plugin.interactConfig().getconfig_defaultHealth();
		else
			maxHealth = container.getRaceMaxHealth();
		
		playerHealth.put(player, new HealthContainer(player, maxHealth, maxHealth));
	}
	
	public double getHealthOfPlayer(String player){
		HealthContainer container = playerHealth.get(player);
		if(container == null) return -1;
		return container.getCurrentHealth();
	}
	
	public void checkPlayer(String player){
		HealthContainer hContainer = playerHealth.get(player);
		if(hContainer == null){
			RaceContainer container = RaceManager.getManager().getRaceOfPlayer(player);
			double maxHealth;
			if(container == null)
				maxHealth = plugin.interactConfig().getconfig_defaultHealth();
			else
				maxHealth = container.getRaceMaxHealth();
			
			playerHealth.put(player, new HealthContainer(player, maxHealth, (int) maxHealth));
		}else
			hContainer.checkStats();
	}
	
	public static HealthManager getHealthManager(){
		return manager;
	}

	@Override
	public void update(Observable o, Object dmgContainer) {
		if(dmgContainer == null) return;
		
		HealthModifyContainer hmContainer = (HealthModifyContainer) dmgContainer;
		switch(hmContainer.getOperation()){
			case "heal" : heal(hmContainer); break;
			case "damage" : damage(hmContainer); break;
			default: return;
		}

		
	}
	
	private void damage(HealthModifyContainer container){
		damage(container.getPlayer(), container.getAmount());
	}
	
	private void damage(String playerName, double amount){
		Player player = Bukkit.getPlayer(playerName);
		HealthContainer hContainer = playerHealth.get(player.getName());
		
		hContainer.reduceLife(amount);
	}
	
	private void heal(HealthModifyContainer container){
		heal(container.getPlayer(), container.getAmount());
	}
	
	private void heal(String playerName, double amount){
		Player player = Bukkit.getPlayer(playerName);
		HealthContainer hContainer = playerHealth.get(player.getName());
		
		hContainer.increaseLife(amount);
	}

	public void resetHealth(String player) {
		HealthContainer container = playerHealth.get(player);
		if(container == null) return;
		container.fullHeal();
	}

	public ArrowManager getArrowManagerOfPlayer(String playerName) {
		return playerHealth.get(playerName).getArrowManager();
	}

	public boolean displayHealth(String name) {
		HealthContainer container = playerHealth.get(name);
		if(container == null) return false;
		container.forceHPOut();
		return true;
	}

}
