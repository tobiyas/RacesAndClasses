package de.tobiyas.racesandclasses.playermanagement.player;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.InFightAPI;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.datacontainer.armorandtool.ArmorToolManager;
import de.tobiyas.racesandclasses.datacontainer.arrow.ArrowManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.hotkeys.HotKeyInventory;
import de.tobiyas.racesandclasses.pets.PlayerPetManager;
import de.tobiyas.racesandclasses.playermanagement.health.HealthManager;
import de.tobiyas.racesandclasses.playermanagement.leveling.PlayerLevelManager;
import de.tobiyas.racesandclasses.playermanagement.playerdisplay.PlayerActionBarDisplay;
import de.tobiyas.racesandclasses.playermanagement.playerdisplay.scoreboard.PlayerRaCScoreboardManager;
import de.tobiyas.racesandclasses.playermanagement.skilltree.PlayerSkillTreeManager;
import de.tobiyas.racesandclasses.playermanagement.spellmanagement.PlayerSpellManager;
import de.tobiyas.racesandclasses.playermanagement.spellmanagement.mana.ManaManager;
import de.tobiyas.racesandclasses.saving.PlayerSavingData;
import de.tobiyas.racesandclasses.saving.PlayerSavingManager;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.bypasses.StaticTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.TraitWithRestrictions;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier.BukkitPlayer;
import de.tobiyas.util.player.PlayerUtils;

public class RaCPlayer extends PlayerProxy {

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
	 * Returns the Class of the Player
	 */
	public PlayerSavingData getPlayerSaveData(){
		return PlayerSavingManager.get().getPlayerData(playerUUID);
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
	
	
	@Override
	public Player getRealPlayer() {
		if(!hasUUIDSupport()) return PlayerUtils.getPlayer(playerName);
		return Bukkit.getPlayer(playerUUID);
	}
	
	
	
	
	/**
	 * Returns a player object for this player.
	 * 
	 * @return player or null
	 */
	public OfflinePlayer getOfflinePlayer(){
		if(!hasUUIDSupport()) return PlayerUtils.getPlayer(playerName);
		return Bukkit.getOfflinePlayer(playerUUID);
	}
	
	
	/**
	 * Returns if the Player is online.
	 * 
	 * @return true if online.
	 */
	@Override
	public boolean isOnline(){
		Player player = getPlayer();
		return player != null && player.isOnline();
	}
	
	
	/**
	 * Returns the current channel.
	 * 
	 * @return current channel
	 */
	public String getCurrentChatChannel() {
		return plugin.getChannelManager().getCurrentChannel(this);
	}
	
	/**
	 * Sets the new Current channel.
	 * 
	 * @param newChannel to set.
	 */
	public void setCurrentChatChannel(String newChannel){
		plugin.getChannelManager().changeCurrentChannel(this, newChannel);
	}
	
	
	@Override
	public Player getPlayer() {
		return PlayerUtils.getPlayer(playerUUID);
	}
	
	
	/**
	 * Returns the OwnManaManager of the Player.
	 * @return
	 */
	public ManaManager getManaManager(){
		return plugin.getPlayerManager().getContainer(this).getSpellManager().getManaManager();
	}
	
	
	/**
	 * Returns the Spellmanager of the Player
	 * 
	 * @return
	 */
	public PlayerSpellManager getSpellManager(){
		return plugin.getPlayerManager().getContainer(this).getSpellManager();
	}
	
	/**
	 * Returns the ActionBar of the Player
	 * @return the Actionbar Manager.
	 */
	public PlayerActionBarDisplay getActionbarDisplay(){
		return plugin.getPlayerManager().getContainer(this).getActionbarDisplay();
	}
	
	/**
	 * Returns the ArmorManager of the Player
	 * 
	 * @return
	 */
	public ArmorToolManager getArmorManager(){
		return plugin.getPlayerManager().getContainer(this).getArmorToolManager();
	}

	/**
	 * Returns the ArrowManager of the Player
	 * 
	 * @return the arrow manager
	 */
	public ArrowManager getArrowManager(){
		return plugin.getPlayerManager().getContainer(this).getArrowManager();
	}
	
	/**
	 * Returns the LevelManager of the Player
	 * 
	 * @return the level manager
	 */
	public PlayerLevelManager getLevelManager(){
		return plugin.getPlayerManager().getContainer(this).getPlayerLevelManager();
	}
	
	/**
	 * Returns the Health manager
	 * 
	 * @return Health Manager.
	 */
	public HealthManager getHealthManager() {
		return plugin.getPlayerManager().getContainer(this).getHealthManager();
	}
	
	/**
	 * Returns The config for the Player
	 * 
	 * @return config
	 */
	public double getHealth(){
		return getHealthManager().getCurrentHealth();
	}
	
	
	/**
	 * Returns The max Health for the Player
	 * 
	 * @return config
	 */
	public double getMaxHealth(){
		return getHealthManager().getMaxHealth();
	}
	
	
	/**
	 * Displays the current Health.
	 * 
	 * @return true if worked.
	 */
	public boolean displayHealth(){
		getHealthManager().forceHPOut();
		return true;
	}
	
	/**
	 * Returns the Scoreboard Manager of the player.
	 * 
	 * @return the scoreboard Manager.
	 */
	public PlayerRaCScoreboardManager getScoreboardManager(){
		return plugin.getPlayerManager().getContainer(this).getPlayerScoreboardManager();
	}
	

	public PlayerSkillTreeManager getSkillTreeManager() {
		return plugin.getPlayerManager().getContainer(this).getSkillTreeManager();
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
		Set<Trait> traits = TraitHolderCombinder.getSkillTreeReducedTraitsOfPlayer(this);
		
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
	 * Returns the PlayerPetmanager of the Player.
	 * 
	 * @return the PlayerPetManager.
	 */
	public PlayerPetManager getPlayerPetManager(){
		return plugin.getPlayerManager().getContainer(this).getPlayerPetManager();
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
	
	
	/**
	 * Returns the Hotkey Inventory associated to this player.
	 * 
	 * @return the associated HotKey Inv.
	 */
	public HotKeyInventory getHotkeyInventory() {
		return plugin.getHotkeyManager().getInv(this);
	}
	
	
	/**
	 * If the Player is in Fight.
	 * 
	 * @return true if in fight.
	 */
	public boolean isInFight(){
		return InFightAPI.isInFight(this);
	}
	

	/**
	 * heals the Player by the Value passed.
	 * 
	 * @param healValue to heal.
	 */
	public void heal(double healValue) {
		BukkitPlayer.safeHeal(healValue, getPlayer());
	}
	
	
	//BELOW ONLY HASHCODE / EQUALS//
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((playerName == null) ? 0 : playerName.hashCode());
		result = prime * result
				+ ((playerUUID == null) ? 0 : playerUUID.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RaCPlayer other = (RaCPlayer) obj;
		if (playerName == null) {
			if (other.playerName != null)
				return false;
		} else if (!playerName.equals(other.playerName))
			return false;
		if (playerUUID == null) {
			if (other.playerUUID != null)
				return false;
		} else if (!playerUUID.equals(other.playerUUID))
			return false;
		return true;
	}


	@Override
	public boolean hasGravity() {
		return getRealPlayer().hasGravity();
	}


	@Override
	public boolean isSilent() {
		return getRealPlayer().isSilent();
	}


	@Override
	public void setGravity(boolean arg0) {
		getRealPlayer().setGravity(arg0);
	}


	@Override
	public void setSilent(boolean arg0) {
		getRealPlayer().setSilent(arg0);
	}


	@Override
	public void stopSound(Sound arg0) {
		getRealPlayer().stopSound(arg0);
	}


	@Override
	public void stopSound(String arg0) {
		getRealPlayer().stopSound(arg0);
	}
	
}
