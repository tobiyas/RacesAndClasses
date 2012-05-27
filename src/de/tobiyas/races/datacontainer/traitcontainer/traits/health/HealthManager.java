package de.tobiyas.races.datacontainer.traitcontainer.traits.health;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import de.tobiyas.races.Races;
import de.tobiyas.races.datacontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.race.RaceManager;

public class HealthManager implements Observer{
	
	private HashMap<String, HealthContainer> playerHealth;
	private Races plugin;
	
	private static HealthManager manager;
	
	public HealthManager(){
		playerHealth = new HashMap<String, HealthContainer>();
		plugin = Races.getPlugin();
		manager = this;
	}
	
	public void saveHealthContainer(){
		
	}
	
	public void loadHealthContainer(){
		
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
	
	public static HealthManager getHealthManager(){
		return manager;
	}

	@Override
	public void update(Observable o, Object dmgContainer) {	
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

}
