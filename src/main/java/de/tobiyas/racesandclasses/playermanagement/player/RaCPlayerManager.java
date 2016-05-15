package de.tobiyas.racesandclasses.playermanagement.player;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.util.config.YAMLConfigExtended;
import de.tobiyas.util.player.PlayerUtils;

public class RaCPlayerManager {

	/**
	 * The Set to use.
	 */
	private final Set<RaCPlayer> playerSet = new HashSet<RaCPlayer>();
	
	/**
	 * The Cacche to use.
	 */
	private YAMLConfigExtended cacheConfig;
	
	private static RaCPlayerManager instance;
	
	
	protected RaCPlayerManager() {
		//nothing to load.
		if(hasUUIDSupport()) return;
		
		File cacheFolder = new File(RacesAndClasses.getPlugin().getDataFolder(), "cache");
		if(!cacheFolder.exists()) cacheFolder.mkdirs();
		
		File cacheFile = new File(cacheFolder, "uuid_cache.yml");
		cacheConfig = new YAMLConfigExtended(cacheFile).load();
		
		for(String key : cacheConfig.getRootChildren()){
			try{
				UUID id = UUID.fromString(key);
				String name = cacheConfig.getString(key);
				
				playerSet.add(new RaCPlayer(id, name));
			}catch(Throwable exp){}
		}
	}
	
	
	/**
	 * Gets a RaCPlayer.
	 * 
	 * @param player to get
	 * 
	 * @return the player.
	 */
	public RaCPlayer getPlayer(Player player){
		if(player == null) return null;
		
		if(hasUUIDSupport()){
			UUID id = player.getUniqueId();
			for(RaCPlayer racPlayer : playerSet){
				if(racPlayer.getUniqueId().equals(id)){
					return racPlayer;
				}
			}
			
			RaCPlayer neu = createNew(player);
			playerSet.add(neu);
			return neu;
		}else{
			String name = player.getName();
			for(RaCPlayer racPlayer : playerSet){
				if(racPlayer.getName().equals(name)){
					return racPlayer;
				}
			}
			
			RaCPlayer neu = createNew(player);
			playerSet.add(neu);
			return neu;
		}
	}
	
	/**
	 * Gets a RaCPlayer.
	 * 
	 * @param player to get
	 * 
	 * @return the player.
	 */
	public RaCPlayer getPlayerByName(String playerName){
		if(playerName == null) return null;
		
		//First check the players online.
		Player player = PlayerUtils.getPlayer(playerName);
		if(player != null) return getPlayer(player);
		
		for(RaCPlayer racPlayer : playerSet){
			if(racPlayer.getName().equals(playerName)){
				return racPlayer;
			}
		}
		
		//Can not find the guy!
		return null;
	}
	
	
	/**
	 * Gets a RaCPlayer.
	 * <br>WARNING: This will most likely only work for MC 1.7.5 +. 
	 * Otherwise it will ALWAYS return null if no player is registered to that UUID.
	 * 
	 * @param playerID to get
	 * 
	 * @return the player.
	 */
	public RaCPlayer getPlayer(UUID playerUuid){
		if(playerUuid == null) return null;
		
		for(RaCPlayer racPlayer : playerSet){
			if(racPlayer.getUniqueId().equals(playerUuid)){
				return racPlayer;
			}
		}
		
		if(!hasUUIDSupport()) return null;

		Player player = Bukkit.getPlayer(playerUuid);
		if(player == null) return null;
		
		RaCPlayer neu = createNew(player);
		playerSet.add(neu);
		return neu;
	}
	
	
	
	private RaCPlayer createNew(Player player){
		if(player == null) return null;
		
		if(hasUUIDSupport()){
			return new RaCPlayer(player.getUniqueId(), player.getName());
		}else{
			UUID newID = UUID.randomUUID();
			String playerName = player.getName();
			
			cacheConfig.set(newID.toString(), playerName);
			cacheConfig.saveAsync();
			
			return new RaCPlayer(newID, playerName);
		}
	}
	
	
	/**
	 * If the System has UUID support.
	 * 
	 * @return true if has UUID support.
	 */
	private boolean hasUUIDSupport(){
		try{
			Bukkit.getPlayer(UUID.randomUUID());
			return true;
		}catch(Throwable exp) { return false; }
	}
	
	
	/**
	 * Returns all Online Players as {@link RaCPlayer}
	 * 
	 * @return a Collection of RaC Players online.
	 */
	public Collection<RaCPlayer> getOnlinePlayers() {
		Set<RaCPlayer> online = new HashSet<RaCPlayer>();
		for(Player pl : PlayerUtils.getOnlinePlayers()){
			RaCPlayer racPlayer = getPlayer(pl);
			if(racPlayer != null) online.add(racPlayer);
		}
		
		return online;
	}
	
	
	public static RaCPlayerManager get(){
		if (instance == null) instance = new RaCPlayerManager();
		return instance;
	}

}
