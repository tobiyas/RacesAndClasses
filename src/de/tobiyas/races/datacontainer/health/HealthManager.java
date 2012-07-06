package de.tobiyas.races.datacontainer.health;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import de.tobiyas.races.Races;
import de.tobiyas.util.config.YAMLConfigExtended;
import de.tobiyas.races.datacontainer.armorandtool.ArmorToolManager;
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
		YAMLConfigExtended config = new YAMLConfigExtended(Consts.playerDataYML).load();
		
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
			maxHealth = plugin.getGeneralConfig().getconfig_defaultHealth();
		else
			maxHealth = container.getRaceMaxHealth();
		
		playerHealth.put(player, new HealthContainer(player, maxHealth, maxHealth));
	}
	
	public double getHealthOfPlayer(String playerName){
		HealthContainer container = getCreate(playerName, true);
		if(container == null) return -1;
		return container.getCurrentHealth();
	}
	
	public void checkPlayer(String player){
		HealthContainer hContainer = playerHealth.get(player);
		if(hContainer == null){
			RaceContainer container = RaceManager.getManager().getRaceOfPlayer(player);
			double maxHealth;
			if(container == null)
				maxHealth = plugin.getGeneralConfig().getconfig_defaultHealth();
			else
				maxHealth = container.getRaceMaxHealth();
			
			HealthContainer healthContainer = new HealthContainer(player, maxHealth, maxHealth);
			healthContainer.checkStats();
			playerHealth.put(player, healthContainer);
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
		damage(container.getPlayer(), container.getAmount(), container.getCause());
	}
	
	private void damage(String playerName, double amount, DamageCause cause){
		Player player = Bukkit.getPlayer(playerName);
		if(player == null){
			plugin.getDebugLogger().logError("Error on Damaging player: " + playerName);
			return;
		}
		HealthContainer hContainer = getCreate(player.getName(), true);
		
		hContainer.reduceLife(amount, cause);
	}
	
	private void heal(HealthModifyContainer container){
		heal(container.getPlayer(), container.getAmount());
	}
	
	private void heal(String playerName, double amount){
		Player player = Bukkit.getPlayer(playerName);
		HealthContainer hContainer =  getCreate(player.getName(), true);
		
		hContainer.increaseLife(amount);
	}

	public void resetHealth(String playerName) {
		HealthContainer container = getCreate(playerName, true);
		if(container == null) return;
		container.fullHeal();
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
	
	private HealthContainer getCreate(String playerName, boolean create){
		HealthContainer container = playerHealth.get(playerName);
		if(container == null && create){
			checkPlayer(playerName);
			container = playerHealth.get(playerName);
		}
		
		return container;
	}

}
