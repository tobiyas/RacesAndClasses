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

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.exceptions.HolderConfigParseException;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.exceptions.HolderParsingException;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.exceptions.HolderTraitParseException;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.permissionsettings.PermissionRegisterer;
import de.tobiyas.racesandclasses.healthmanagement.HealthManager;
import de.tobiyas.racesandclasses.util.consts.Consts;
import de.tobiyas.util.config.YAMLConfigExtended;

public abstract class AbstractHolderManager extends Observable{

	protected Map<String, AbstractTraitHolder> memberList;
	protected Set<AbstractTraitHolder> traitHolderList;
	protected YAMLConfigExtended traitHolderConfig;	
	protected YAMLConfigExtended memberConfig;
	
	protected RacesAndClasses plugin;
	
	
	/**
	 * Creation of the holder with it's paths to save / load from / to
	 * 
	 * @param memberPath
	 * @param traitHolderConfigPath
	 */
	public AbstractHolderManager(String memberPath, String traitHolderConfigPath){
		plugin = RacesAndClasses.getPlugin();
		
		this.memberConfig = new YAMLConfigExtended(memberPath);
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

	
	protected void readMemberList(){
		memberList = new HashMap<String, AbstractTraitHolder>();
		
		memberConfig = new YAMLConfigExtended(Consts.playerDataYML).load();
		
		for(String member : memberConfig.getChildren("playerdata")){
			String raceName = memberConfig.getString("playerdata." + member + "." + getConfigPrefix(), Consts.defaultRace);
			memberList.put(member, getHolderByName(raceName));
		}
				
	}
	
	
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
	public boolean addPlayerToHolder(String player, String potentialHolder){
		AbstractTraitHolder container = getHolderByName(potentialHolder);
		if(container == null) return false;
		memberConfig.load();
		
		memberList.put(player, container);
		memberConfig.set("playerdata." + player + "." + getConfigPrefix(), container.getName());
		HealthManager.getHealthManager().checkPlayer(player);
		
		memberConfig.save();
		
		return true;
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
	 * @param player
	 * @param potentialClass
	 * @return
	 */
	public boolean changePlayerHolder(String player, String newHolderName){
		if(getHolderByName(newHolderName) == null) return false;
		memberList.remove(player);
		
		memberConfig.load();
		memberConfig.set("playerdata." + player + "." + getConfigPrefix(), null);
		memberConfig.save();
		
		return addPlayerToHolder(player, newHolderName);
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
			holder = getDefaultHolder();
			memberList.put(playerName, holder);
		}
		
		return holder;
	}
	
	
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
	 * if null is passed, null is returned.
	 * 
	 * @param holderName
	 * @return
	 */
	public List<String> getAllPlayersOfHolder(AbstractTraitHolder holder) {
		if(holder == null){
			return null;
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
	public ArrayList<String> listAllVisibleHolders() {
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
	 * Returns the Type of the Container as Name
	 */
	public abstract String getContainerTypeAsString();
}
