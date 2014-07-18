package de.tobiyas.racesandclasses.datacontainer.player;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.configuration.member.file.MemberConfig;
import de.tobiyas.racesandclasses.datacontainer.armorandtool.ArmorToolManager;
import de.tobiyas.racesandclasses.datacontainer.arrow.ArrowManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.playermanagement.health.HealthManager;
import de.tobiyas.racesandclasses.playermanagement.leveling.PlayerLevelManager;
import de.tobiyas.racesandclasses.playermanagement.spellmanagement.PlayerSpellManager;
import de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana.ManaManager;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.bypasses.StaticTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.TraitWithRestrictions;
import de.tobiyas.util.player.PlayerUtils;

public class RaCPlayer {

	/**
	 * The UUID of the player
	 */
	private final UUID playerUUID;
	
	/**
	 * The real name of the Player.
	 */
	private final String playerName;
	
	
	private final RacesAndClasses plugin;
	
	
	public RaCPlayer(UUID playerUUID, String playerName) {
		this.playerUUID = playerUUID;
		this.playerName = playerName;
		this.plugin = RacesAndClasses.getPlugin();
	}
	
	
	/**
	 * @return the playerName
	 */
	public String getName() {
		return playerName;
	}
	


	/**
	 * Returns the Race of the Player
	 * 
	 * @return race
	 */
	public RaceContainer getRace(){
		return (RaceContainer) plugin.getRaceManager().getHolderOfPlayer(this);
	}
	
	
	/**
	 * Returns the Class of the Player
	 */
	public ClassContainer getclass(){
		return (ClassContainer) plugin.getClassManager().getHolderOfPlayer(this);
	}
	
	/**
	 * Sets the Class of this player.
	 * 
	 * @param container to set
	 * @return true if worked
	 */
	public boolean setClass(ClassContainer container){
		return plugin.getClassManager().addPlayerToHolder(this, container.getDisplayName(), true);
	}
	
	/**
	 * Sets the Race of this player.
	 * 
	 * @param container to set
	 * @return true if worked
	 */
	public boolean setRace(RaceContainer container){
		return plugin.getRaceManager().addPlayerToHolder(this, container.getDisplayName(), true);
	}
	
	
	/**
	 * Returns a player object for this player.
	 * 
	 * @return player or null
	 */
	public Player getPlayer(){
		if(!hasUUIDSupport()) return PlayerUtils.getPlayer(playerName);
		return Bukkit.getPlayer(playerUUID);
	}
	
	
	/**
	 * Returns if the Player is online.
	 * 
	 * @return true if online.
	 */
	public boolean isOnline(){
		Player player = getPlayer();
		return player != null && player.isOnline();
	}
	
	
	/**
	 * Returns the OwnManaManager of the Player.
	 * @return
	 */
	public ManaManager getManaManager(){
		return plugin.getPlayerManager().getSpellManagerOfPlayer(this).getManaManager();
	}
	
	
	/**
	 * Returns the Spellmanager of the Player
	 * 
	 * @return
	 */
	public PlayerSpellManager getSpellManager(){
		return plugin.getPlayerManager().getSpellManagerOfPlayer(this);
	}
	
	/**
	 * Returns the ArmorManager of the Player
	 * 
	 * @return
	 */
	public ArmorToolManager getArmorManager(){
		return plugin.getPlayerManager().getArmorToolManagerOfPlayer(this);
	}

	/**
	 * Returns the ArrowManager of the Player
	 * 
	 * @return the arrow manager
	 */
	public ArrowManager getArrowManager(){
		return plugin.getPlayerManager().getArrowManagerOfPlayer(this);
	}
	
	/**
	 * Returns the LevelManager of the Player
	 * 
	 * @return the level manager
	 */
	public PlayerLevelManager getLevelManager(){
		return plugin.getPlayerManager().getPlayerLevelManager(this);
	}
	
	/**
	 * Returns The config for the Player
	 * 
	 * @return config
	 */
	public MemberConfig getConfig(){
		return plugin.getConfigManager().getMemberConfigManager().getConfigOfPlayer(this);
	}
	
	/**
	 * Returns the Health manager
	 * 
	 * @return Health Manager.
	 */
	public HealthManager getHealthManager() {
		return plugin.getPlayerManager().getHealthManager(this);
	}
	
	/**
	 * Returns The config for the Player
	 * 
	 * @return config
	 */
	public double getHealth(){
		return plugin.getPlayerManager().getHealthManager(this).getCurrentHealth();
	}
	
	
	/**
	 * Returns The max Health for the Player
	 * 
	 * @return config
	 */
	public double getMaxHealth(){
		return plugin.getPlayerManager().getHealthManager(this).getMaxHealth();
	}
	
	
	/**
	 * Displays the current Health.
	 * 
	 * @return true if worked.
	 */
	public boolean displayHealth(){
		return plugin.getPlayerManager().displayHealth(this);
	}
	
	
	/**
	 * Returns a list of Traits the Player has.
	 * <br>This excludes ones that are out of his level range.
	 * 
	 * @return reduced Trait list.
	 */
	public List<Trait> getTraits(){
		int level = getLevelManager().getCurrentLevel();
		
		List<Trait> returnList = new LinkedList<Trait>();
		Set<Trait> traits = TraitHolderCombinder.getReducedVisibleTraitsOfPlayer(this);
		
		for(Trait trait : traits){
			if(trait instanceof StaticTrait) continue;
			
			if(trait instanceof TraitWithRestrictions){
				if(((TraitWithRestrictions) trait).isInLevelRange(level)){
					returnList.add(trait);
				}
			}
		}
		
		return returnList;
	}
	

	/**
	 * @return the player
	 */
	public UUID getUniqueId() {
		return playerUUID;
	}
	
	
	private boolean hasUUIDSupport(){
		try{ 
			Bukkit.getPlayer(UUID.randomUUID());
			return true;
		}catch(Throwable exp) { return false; }
	}
	
	
	
	/**
	 * Checks if the Player has Permission for this permission.
	 * 
	 * @param permission to check
	 * 
	 * @return true if has, false if not.
	 */
	public boolean hasPermission(String permission){
		if(isOnline()){
			return plugin.getPermissionManager().checkPermissionsSilent(getPlayer(), permission);
		}else{
			return plugin.getPermissionManager().checkPermissionsSilent(playerName, permission);
		}
	}
	

	/**
	 * Sends a translated Message via the Translation API.
	 */
	public void sendTranslatedMessage(String tag) {
		if(!isOnline()) return;
		LanguageAPI.sendTranslatedMessage(getPlayer(), tag);
	}
	
	/**
	 * Sends a translated Message via the Translation API.
	 */
	public void sendTranslatedMessage(String tag, Map<String,String> replacements) {
		if(!isOnline()) return;
		LanguageAPI.sendTranslatedMessage(getPlayer(), tag, replacements);
	}
	
	/**
	 * Sends a translated Message via the Translation API.
	 */
	public void sendTranslatedMessage(String tag, String... replacements) {
		if(!isOnline()) return;
		LanguageAPI.sendTranslatedMessage(getPlayer(), tag, replacements);
	}
	
	
	
	////////////////////
	//Player proxys/////
	////////////////////
	
	/**
	 * The Location of the Player.
	 * 
	 * @return location or null
	 * @see Player#getLocation()
	 */
	public Location getLocation(){
		if(!isOnline()) return null;
		return getPlayer().getLocation();
	}
	
	/**
	 * The World the player is on.
	 * 
	 * @return World or null
	 * @see Player#getWorld()
	 */
	public World getWorld(){
		if(!isOnline()) return null;
		return getPlayer().getWorld();
	}
	
	
	/**
	 * If the player is sneaking
	 * 
	 * @return true if sneaking
	 * @see Player#isSneaking()
	 */
	public boolean isSneaking(){
		if(!isOnline()) return false;
		return getPlayer().isSneaking();
	}
	
	
	/**
	 * Should not be used, since we use translations.
	 * <br>Try using translation module instead.
	 * 
	 * @param message to send.
	 */
	public void sendMessage(String message){
		if(isOnline()) getPlayer().sendMessage(message);
	}
	
	
	/**
	 * Should not be used, since we use translations.
	 * 
	 * @return the displayName
	 * @see Player#getDisplayName()
	 */
	public String getDisplayName(){
		if(!isOnline()) return playerName;
		return getPlayer().getDisplayName();
	}
	
	
	/**
	 * Returns the item in hand.
	 * 
	 * @return
	 */
	public ItemStack getItemInHand(){
		if(!isOnline()) return null;
		return getPlayer().getItemInHand();
	}

}
