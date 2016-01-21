/*******************************************************************************
 * Copyright 2014 Tobias Welther
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.tobiyas.racesandclasses.datacontainer.traitholdercontainer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.exceptions.HolderConfigParseException;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.exceptions.HolderParsingException;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.exceptions.HolderTraitParseException;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.permissionsettings.PermissionRegisterer;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.HolderSelectedEvent;
import de.tobiyas.racesandclasses.persistence.file.YAMLPersistanceSaver;
import de.tobiyas.racesandclasses.persistence.file.YAMLPersistenceProvider;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.util.config.YAMLConfigExtended;
import de.tobiyas.util.file.FileUtils;

public abstract class AbstractHolderManager {

	/**
	 * The Map of Members Member -> Container.
	 */
	protected Map<RaCPlayer, AbstractTraitHolder> memberList;
	
	/**
	 * The Set of all Known Trait Holders.
	 */
	protected Set<AbstractTraitHolder> traitHolderList;
	
	/**
	 * The old file to convert from.
	 */
	protected final File oldFile;
	
	/**
	 * The Folder for the Holder (lol it ryms :D)
	 */
	protected final File folder;
	
	/**
	 * The plugin to use.
	 */
	protected final RacesAndClasses plugin;
	
	
	/**
	 * Creation of the holders with it's paths to save / load from / to
	 * 
	 * @param memberPath
	 * @param traitHolderConfigPath
	 */
	public AbstractHolderManager(String traitHolderConfigPath, String folderName){
		plugin = RacesAndClasses.getPlugin();
		
		this.folder = new File(RacesAndClasses.getPlugin().getDataFolder(), folderName);
		this.oldFile = new File(traitHolderConfigPath);
	
		memberList = new HashMap<RaCPlayer, AbstractTraitHolder>();
		traitHolderList = new HashSet<AbstractTraitHolder>();
	}
	
	/**
	 * Initializes the Manager
	 */
	public void init(){
		clearOldTraitholders();
		readTraitHolderList();
		
		initDefaultHolder();
		readMemberList();

		setupPermissions();
	}
	
	/**
	 * Clears the Old Trait-Holders
	 */
	private void clearOldTraitholders() {
		Set<Trait> toDeinit = new HashSet<Trait>();
		for(AbstractTraitHolder holder : traitHolderList)toDeinit.addAll(holder.getTraits());
		for(Trait trait : toDeinit) trait.deInit();
		
		traitHolderList.clear();
	}

	/**
	 * Here the default holders that the manager always has (without config) should be added.
	 */
	protected abstract void initDefaultHolder();

	/**
	 * Reads the current Trait Holder list.
	 * This is the corresponding yml file to the TraitHolder.
	 */
	protected void readTraitHolderList(){
		readTraitHolderListStep1();
		readTraitHolderListStep2();
		readTraitHolderListStep3();
	}
	
	
	protected void readTraitHolderListStep1(){
		traitHolderList.clear();
		
		Set<File> files = FileUtils.getAllFiles(folder);
		if(files.isEmpty()) return;
		
		//1st step. Load basic structure.
		for(File file : files){
			YAMLConfigExtended config = new YAMLConfigExtended(file).load();
			//check if we have a valid load.
			if(!config.getValidLoad()){
				plugin.log("Could not load " + getConfigPrefix() + " file: " + file.getName()
					+ " because the Synthax is broken.");
				continue;
			}
			
			for(String holderName : config.getRootChildren()){
					AbstractTraitHolder container = generateTraitHolder(config, holderName);
					if(container != null){
						traitHolderList.add(container);
					}
			}
		}
	}
	
	
	protected void readTraitHolderListStep2(){
		//2nd step. load Holder.
		for(AbstractTraitHolder holder : traitHolderList){
			try{
				holder.load();
			} catch (HolderParsingException exp) {
				String errorMessage = "Error: ";
				if(exp instanceof HolderConfigParseException){
					errorMessage += "ConfigTotal of: " + holder.getConfigNodeName() + " is mal formated. Please relook synthax!";
				}else if(exp instanceof HolderTraitParseException){
					HolderTraitParseException rtpe = (HolderTraitParseException) exp;
					errorMessage = rtpe.getLocalizedMessage();
				}
				
				plugin.log(errorMessage);
			}
		}
		
	}
	
	
	protected void readTraitHolderListStep3(){
		//3rd step. Read parents.
		for(AbstractTraitHolder holder : traitHolderList){
			holder.readParents();
		}
	}
	

	/**
	 * Generates the correct {@link AbstractTraitHolder} for the config and the name.
	 * 
	 * @param traitHolderConfig
	 * @param configNodeName
	 * @return
	 */
	protected abstract AbstractTraitHolder generateTraitHolder(
			YAMLConfigExtended traitHolderConfig, String holderName);

	
	/**
	 * Reads the Members from PlayerData or DB.
	 */
	protected void readMemberList(){
		memberList = new HashMap<RaCPlayer, AbstractTraitHolder>();

		if(plugin.getConfigManager().getGeneralConfig().isConfig_savePlayerDataToDB()){
			for(AbstractTraitHolder traitHolder : traitHolderList){
				if(traitHolder == null) continue;
				
				List<PlayerHolderAssociation> holderList = plugin.getDatabase().find(PlayerHolderAssociation.class).where()
						.ieq(getDBFieldName(), traitHolder.getDisplayName()).findList();
				for(PlayerHolderAssociation holder : holderList){
					RaCPlayer player = RaCPlayerManager.get().getPlayer(holder.getPlayerUUID());
					memberList.put(player, traitHolder);				
				}
			}
		}else{
			String defaultHolderName = getDefaultHolder() == null ? null : getDefaultHolder().getDisplayName();
			Set<RaCPlayer> players = YAMLPersistenceProvider.getAllPlayersKnown();
			
			for(RaCPlayer player : players){
				if(player == null || !player.isOnline()) continue;
				
				YAMLConfigExtended playerConfig = YAMLPersistenceProvider.getLoadedPlayerFile(player);
				String holderName = playerConfig.getString(getConfigPrefix(), defaultHolderName);
				memberList.put(player, getHolderByName(holderName));
			}
		}
	}
	
	/**
	 * Loads the Player passed.
	 * <br>Only loads if not exist.
	 * <br>Only loads if not load.
	 * 
	 * @param player to load.
	 */
	public void loadIfNotExists(RaCPlayer player){
		if(memberList.containsKey(player)) return;
		
		String defaultHolderName = getDefaultHolder() == null ? null : getDefaultHolder().getDisplayName();
		if(player == null || !player.isOnline()) return;
		
		YAMLConfigExtended playerConfig = YAMLPersistenceProvider.getLoadedPlayerFile(player.getUniqueId());
		String holderName = playerConfig.getString(getConfigPrefix(), defaultHolderName);
		memberList.put(player, getHolderByName(holderName));
	}
	
	/**
	 * Returns the name of the correct Holder.
	 * 
	 * @param container
	 * @return
	 */
	protected abstract String getCorrectFieldFromDBHolder(PlayerHolderAssociation container);
	
	
	/**
	 * Adds a player name to the member list.
	 * Any previous holders is overwritten.
	 * True is returned, if it worked.
	 * If the holders name does not exist, false is returned.
	 *
	 * @param player to add
	 * @param potentialHolder to add the player to
	 * @return if it worked
	 */
	public boolean addPlayerToHolder(RaCPlayer player, String potentialHolder, boolean callAfterEvent){
		AbstractTraitHolder container = getHolderByName(potentialHolder);
		if(container == null) return false;
		
		saveNewHolderToDB(player, container, false);
		memberList.put(player, container);
		
		//setting permission group afterwards
		String groupName = container.getPermissions().getGroupIdentificationName();
		PermissionRegisterer.addPlayer(player, groupName);
		
		resetPlayerMovementSpeed(player);
		plugin.getPlayerManager().checkPlayer(player);
		
		if(callAfterEvent){
			HolderSelectedEvent event = generateAfterSelectEvent(player, container);
			plugin.fireEventToBukkit(event);
		}
		
		return true;
	}
	
	
	/**
	 * Generates an Event with the Player that Selected the New TraitHolder.
	 * 
	 * @param player that selected
	 * @param newHolder that was selected
	 * 
	 * @return the Event
	 */
	protected abstract HolderSelectedEvent generateAfterSelectEvent(RaCPlayer player, AbstractTraitHolder newHolder);

	
	/**
	 * Generates an Event with the Player that changed from a TraitHolder to a new one.
	 * 
	 * @param player that selected
	 * @param newHolder that was selected
	 * @param oldHolder that was changed from
	 * 
	 * @return the Event
	 */
	protected abstract HolderSelectedEvent generateAfterChangeEvent(RaCPlayer player, AbstractTraitHolder newHolder, AbstractTraitHolder oldHolder);
	
	
	/**
	 * Saves the new Holder to the DB or YAML
	 * 
	 * @param player
	 * @param newHolder
	 */
	private void saveNewHolderToDB(RaCPlayer player, AbstractTraitHolder newHolder, boolean rescanAfter){
		boolean useDB = plugin.getConfigManager().getGeneralConfig().isConfig_savePlayerDataToDB();
		String newHolderName = "";
		if(newHolder != null){
			newHolderName = newHolder.getDisplayName();			
		}
		
		if(useDB){
			PlayerHolderAssociation container = plugin.getDatabase().find(PlayerHolderAssociation.class).where().ieq("player", player.toString()).findUnique();
			if(container == null){
				container = new PlayerHolderAssociation();
				container.setPlayerUUID(player.getUniqueId());
				container.setClassName(null);
				container.setRaceName(plugin.getRaceManager().getDefaultHolder().getDisplayName());
			}
			
			saveContainerToDBField(container, newHolderName);
			
			try{
				plugin.getDatabase().save(container);
			}catch(Exception exp){
				plugin.getDebugLogger().logStackTrace(exp);
			}
		}else{
			YAMLConfigExtended memberConfig = YAMLPersistenceProvider.getLoadedPlayerFile(player);
			memberConfig.set(getConfigPrefix(), newHolderName);
			YAMLPersistanceSaver.flushPlayerNow(player, true);
		}
		
		if(rescanAfter){
			plugin.getPlayerManager().checkPlayer(player);			
		}
	}
	
	
	/**
	 * Saves all containers.
	 */
	/*public void saveAll(){
		for(Map.Entry<RaCPlayer, AbstractTraitHolder> entry : memberList.entrySet()){
			RaCPlayer player = entry.getKey();
			AbstractTraitHolder holder = entry.getValue();
			if(holder == null){
				holder = getDefaultHolder();
				entry.setValue(getDefaultHolder());
			}
			
			saveNewHolderToDB(player, holder, false);
		}
	}*/
	
	
	/**
	 * Returns the fieldName of the HolderType
	 * @return
	 */
	protected abstract String getDBFieldName();
	
	
	/**
	 * Saves the Holder name to the correct field.
	 * 
	 * @param container
	 * @param name
	 */
	protected abstract void saveContainerToDBField(PlayerHolderAssociation container, String name);
	
	/**
	 * Resets the movement speed of a Player.
	 * The Player is identified by the name.
	 * 
	 * @param player to set the speed
	 */
	private void resetPlayerMovementSpeed(RaCPlayer player){
		if(player != null && player.isOnline()){
			player.getPlayer().setWalkSpeed(0.2f);
		}
	}
	
	
	/**
	 * Gets the {@link AbstractTraitHolder} for the name passed.
	 * 
	 * Null if none found.
	 * 
	 * @param configNodeName
	 * @return
	 */
	public AbstractTraitHolder getHolderByName(String holderName){
		if(holderName == null) return getDefaultHolder();
		
		for(AbstractTraitHolder holder : traitHolderList){
			if(holder.getDisplayName().equalsIgnoreCase(holderName)
					|| holder.getConfigNodeName().equals(holderName)){
				
				return holder;
			}
		}
		
		return null;
	}
	
	
	/**
	 * This prefix is needed to store / load data to the correct place.
	 * @return
	 */
	protected abstract String getConfigPrefix();
	
	
	/**
	 * Changes the Holder of a player
	 * false is returned if the Holder does not exist.
	 * 
	 * @param player to change
	 * @param potentialClass to change to 
	 * @param callEvent true if an Changed event chall be called afterwards.
	 * 
	 * @return true if worked, false otherwise
	 */
	public boolean changePlayerHolder(RaCPlayer player, String newHolderName, boolean callEvent){
		if(getHolderByName(newHolderName) == null) return false;
		
		AbstractTraitHolder oldHolder = getHolderOfPlayer(player);
		
		PermissionRegisterer.removePlayer(player, getContainerTypeAsString());
		memberList.remove(player);
		
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_savePlayerDataToDB()){
			YAMLConfigExtended memberConfig = YAMLPersistenceProvider.getLoadedPlayerFile(player);
			memberConfig.set(getConfigPrefix(), null);
		}
		
		boolean worked = addPlayerToHolder(player, newHolderName, false);
		if(callEvent){
			AbstractTraitHolder newHolder = getHolderOfPlayer(player);
			HolderSelectedEvent event = generateAfterChangeEvent(player, newHolder, oldHolder);
			plugin.fireEventToBukkit(event);
		}
		
		return worked;
	}
	
	
	/**
	 * Returns the Holder that is associated to a player.
	 * 
	 * If the player is not registered, a defaultHolder is returned.
	 * The Player gets the default holders linked if he has none.
	 * 
	 * @see #getDefaultHolder()
	 * 
	 * @param player
	 * @return
	 */
	public AbstractTraitHolder getHolderOfPlayer(RaCPlayer player){
		if(player == null) return null;
		
		AbstractTraitHolder holder = memberList.get(player);
		if(holder == null){
			loadIfNotExists(player);
			holder = memberList.get(player);
			
			if(holder == null){
				holder = getStartingHolder();
				memberList.put(player, holder);
			}
		}
		
		return holder;
	}
	
	
	/**
	 * This method gets starting Holder.
	 * <br>This is the one specified in the ConfigTotal or the Default TraitHolder.
	 * 
	 * @return The holders put into (can be null)
	 */
	protected abstract AbstractTraitHolder getStartingHolder();
	
	
	/**
	 * Returns the default holders for the Manager
	 * 
	 * @return
	 */
	public abstract AbstractTraitHolder getDefaultHolder();
	
	
	/**
	 * Returns all names of the holders contained
	 * 
	 * @return
	 */
	public List<String> getAllHolderNames(){
		List<String> holderNameList = new LinkedList<String>();
		for(AbstractTraitHolder holder : traitHolderList){
			holderNameList.add(holder.getDisplayName());
		}
		
		return holderNameList;
	}
	
	/**
	 * Returns all players of a holders
	 * 
	 * if null is passed, an empty list is returnemd is returned.
	 * 
	 * @param configNodeName
	 * @return
	 */
	public List<RaCPlayer> getAllPlayersOfHolder(AbstractTraitHolder holder) {
		if(holder == null){
			return new LinkedList<RaCPlayer>();
		}

		List<RaCPlayer> holderMemberList = new LinkedList<RaCPlayer>();
		for(RaCPlayer playerUUID : memberList.keySet()){
			AbstractTraitHolder toCheckAgainst = memberList.get(playerUUID);
			if(holder.equals(toCheckAgainst)){
				holderMemberList.add(playerUUID);
			}
		}
		
		return holderMemberList;
	}
	
	/**
	 * Returns all Holders except for the default holders.
	 * 
	 * @return
	 */
	public List<String> listAllVisibleHolders() {
		ArrayList<String> holderList = new ArrayList<String>();

		for (AbstractTraitHolder container : traitHolderList) {
			if (!container.equals(getDefaultHolder())) {
				holderList.add(container.getDisplayName());
			}			
		}

		return holderList;
	}

	/**
	 * sets up the Permissions if we have Vault.
	 */
	private void setupPermissions() {
		if(!isVaultUsed()) return;
		
		//We need to schedule this to be sure Vault is up and running after startup.
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new PermissionRegisterer(this.traitHolderList, this.memberList, getContainerTypeAsString()), 1);
	}

	/**
	 * checks if Vault is used as Permissions system
	 * @return
	 */
	private boolean isVaultUsed() {
		return plugin.getPermissionManager().getPermissionsName().equalsIgnoreCase("Vault");
	}
	
	/**
	 * Returns the Type of the Container as Name.
	 * This is mostly the word: 'race' or 'class'.
	 */
	public abstract String getContainerTypeAsString();

}
