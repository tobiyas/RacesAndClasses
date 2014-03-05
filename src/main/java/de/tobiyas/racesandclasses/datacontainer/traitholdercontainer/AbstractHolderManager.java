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
import java.util.Observable;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.exceptions.HolderConfigParseException;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.exceptions.HolderParsingException;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.exceptions.HolderTraitParseException;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.permissionsettings.PermissionRegisterer;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.HolderSelectedEvent;
import de.tobiyas.racesandclasses.persistence.file.YAMLPersistenceProvider;
import de.tobiyas.util.config.YAMLConfigExtended;

public abstract class AbstractHolderManager extends Observable{

	protected Map<String, AbstractTraitHolder> memberList;
	protected Set<AbstractTraitHolder> traitHolderList;
	protected YAMLConfigExtended traitHolderConfig;	
	
	protected RacesAndClasses plugin;
	
	
	/**
	 * Creation of the holder with it's paths to save / load from / to
	 * 
	 * @param memberPath
	 * @param traitHolderConfigPath
	 */
	public AbstractHolderManager(String traitHolderConfigPath){
		plugin = RacesAndClasses.getPlugin();
		
		this.traitHolderConfig = new YAMLConfigExtended(traitHolderConfigPath);
	
		memberList = new HashMap<String, AbstractTraitHolder>();
		traitHolderList = new HashSet<AbstractTraitHolder>();
	}
	
	/**
	 * Initializes the Manager
	 */
	public void init(){
		readTraitHolderList();
		
		initDefaultHolder();
		readMemberList();

		setupPermissions();
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
		traitHolderList.clear();
		traitHolderConfig.load();
		
		if(!traitHolderConfig.getValidLoad()){
			File traitHolderConfigFile = traitHolderConfig.getFileLoadFrom();
			plugin.log(traitHolderConfigFile.getName() + " could not be loaded correctly.");
			return;
		}
		
		for(String holderName : traitHolderConfig.getRootChildren()){
			try{
				AbstractTraitHolder container = generateTraitHolderAndLoad(traitHolderConfig, holderName);
				if(container != null){
					traitHolderList.add(container);
				}
				
			} catch (HolderParsingException exp) {
				String errorMessage = "Error: ";
				if(exp instanceof HolderConfigParseException){
					errorMessage += "Config of: " + holderName + " is mal formated. Please relook synthax!";
				}else if(exp instanceof HolderTraitParseException){
					HolderTraitParseException rtpe = (HolderTraitParseException) exp;
					errorMessage = rtpe.getLocalizedMessage();
				}
				
				plugin.log(errorMessage);
			}
		}
	}

	/**
	 * Generates the correct {@link AbstractTraitHolder} for the config and the name.
	 * 
	 * @param traitHolderConfig
	 * @param holderName
	 * @return
	 */
	protected abstract AbstractTraitHolder generateTraitHolderAndLoad(
			YAMLConfigExtended traitHolderConfig, String holderName) throws HolderParsingException;

	
	/**
	 * Reads the Members from PlayerData or DB.
	 */
	protected void readMemberList(){
		memberList = new HashMap<String, AbstractTraitHolder>();

		if(plugin.getConfigManager().getGeneralConfig().isConfig_savePlayerDataToDB()){
			for(AbstractTraitHolder traitHolder : traitHolderList){
				if(traitHolder == null) continue;
				
				List<PlayerHolderAssociation> holderList = plugin.getDatabase().find(PlayerHolderAssociation.class).where()
						.ieq(getDBFieldName(), traitHolder.getName()).findList();
				for(PlayerHolderAssociation holder : holderList){
					memberList.put(holder.getPlayerName(), traitHolder);				
				}
			}
		}else{			
			String defaultHolderName = getDefaultHolder() == null ? null : getDefaultHolder().getName();
			Set<String> playerNames = YAMLPersistenceProvider.getAllPlayersKnown();
			
			for(String playerName : playerNames){
				YAMLConfigExtended playerConfig = YAMLPersistenceProvider.getLoadedPlayerFile(playerName);
				String holderName = playerConfig.getString("playerdata." + playerName + "." + getConfigPrefix(), defaultHolderName);
				memberList.put(playerName, getHolderByName(holderName));
			}
		}
				
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
	 * Any previous holder is overwritten.
	 * True is returned, if it worked.
	 * If the holder name does not exist, false is returned.
	 *
	 * @param player to add
	 * @param potentialHolder to add the player to
	 * @return if it worked
	 */
	public boolean addPlayerToHolder(String player, String potentialHolder, boolean callAfterEvent){
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
	protected abstract HolderSelectedEvent generateAfterSelectEvent(String player, AbstractTraitHolder newHolder);

	
	/**
	 * Generates an Event with the Player that changed from a TraitHolder to a new one.
	 * 
	 * @param player that selected
	 * @param newHolder that was selected
	 * @param oldHolder that was changed from
	 * 
	 * @return the Event
	 */
	protected abstract HolderSelectedEvent generateAfterChangeEvent(String player, AbstractTraitHolder newHolder, AbstractTraitHolder oldHolder);
	
	
	/**
	 * Saves the new Holder to the DB or YAML
	 * 
	 * @param playerName
	 * @param newHolder
	 */
	private void saveNewHolderToDB(String playerName, AbstractTraitHolder newHolder, boolean rescanAfter){
		boolean useDB = plugin.getConfigManager().getGeneralConfig().isConfig_savePlayerDataToDB();
		String newHolderName = "";
		if(newHolder != null){
			newHolderName = newHolder.getName();			
		}
		
		if(useDB){
			PlayerHolderAssociation container = plugin.getDatabase().find(PlayerHolderAssociation.class).where().ieq("playerName", playerName).findUnique();
			if(container == null){
				container = new PlayerHolderAssociation();
				container.setPlayerName(playerName);
				container.setClassName(null);
				container.setRaceName(plugin.getRaceManager().getDefaultHolder().getName());
			}
			
			saveContainerToDBField(container, newHolderName);
			
			try{
				plugin.getDatabase().save(container);
			}catch(Exception exp){
				plugin.getDebugLogger().logStackTrace(exp);
			}
		}else{
			YAMLConfigExtended memberConfig = YAMLPersistenceProvider.getLoadedPlayerFile(playerName);
			memberConfig.set("playerdata." + playerName + "." + getConfigPrefix(), newHolderName);
		}
		
		if(rescanAfter){
			plugin.getPlayerManager().checkPlayer(playerName);			
		}
	}
	
	
	/**
	 * Saves all containers.
	 */
	public void saveAll(){
		for(Map.Entry<String, AbstractTraitHolder> entry : memberList.entrySet()){
			String playerName = entry.getKey();
			AbstractTraitHolder holder = entry.getValue();
			if(holder == null){
				holder = getDefaultHolder();
				entry.setValue(getDefaultHolder());
			}
			
			saveNewHolderToDB(playerName, holder, false);
		}
	}
	
	
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
	 * @param playerName to set the speed
	 */
	private void resetPlayerMovementSpeed(String playerName){
		Player player = Bukkit.getPlayer(playerName);
		if(player != null && player.isOnline()){
			player.setWalkSpeed(0.2f);
		}
	}
	
	
	/**
	 * Gets the {@link AbstractTraitHolder} for the name passed.
	 * 
	 * Null if none found.
	 * 
	 * @param holderName
	 * @return
	 */
	public AbstractTraitHolder getHolderByName(String holderName){
		for(AbstractTraitHolder holder : traitHolderList){
			if(holder.getName().equalsIgnoreCase(holderName)){
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
	public boolean changePlayerHolder(String player, String newHolderName, boolean callEvent){
		if(getHolderByName(newHolderName) == null) return false;
		
		AbstractTraitHolder oldHolder = getHolderOfPlayer(player);
		
		PermissionRegisterer.removePlayer(player, getContainerTypeAsString());
		
		memberList.remove(player);
		
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_savePlayerDataToDB()){
			YAMLConfigExtended memberConfig = YAMLPersistenceProvider.getLoadedPlayerFile(player);
			memberConfig.set("playerdata." + player + "." + getConfigPrefix(), null);
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
	 * The Player gets the default holder linked if he has none.
	 * 
	 * @see #getDefaultHolder()
	 * 
	 * @param playerName
	 * @return
	 */
	public AbstractTraitHolder getHolderOfPlayer(String playerName){
		AbstractTraitHolder holder = memberList.get(playerName);
		if(holder == null){
			holder = getStartingHolder();
			memberList.put(playerName, holder);
		}
		
		return holder;
	}
	
	
	/**
	 * This method gets starting Holder.
	 * <br>This is the one specified in the Config or the Default TraitHolder.
	 * 
	 * @return The holder put into (can be null)
	 */
	protected abstract AbstractTraitHolder getStartingHolder();
	
	
	/**
	 * Returns the default holder for the Manager
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
			holderNameList.add(holder.getName());
		}
		
		return holderNameList;
	}
	
	/**
	 * Returns all players of a holder
	 * 
	 * if null is passed, an empty list is returnemd is returned.
	 * 
	 * @param holderName
	 * @return
	 */
	public List<String> getAllPlayersOfHolder(AbstractTraitHolder holder) {
		if(holder == null){
			return new LinkedList<String>();
		}

		List<String> holderMemberList = new LinkedList<String>();
		for(String playerName : memberList.keySet()){
			AbstractTraitHolder toCheckAgainst = memberList.get(playerName);
			if(holder.equals(toCheckAgainst)){
				holderMemberList.add(playerName);
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
				holderList.add(container.getName());
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
