package de.tobiyas.racesandclasses.datacontainer.traitholdercontainer;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LevelAPI;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceManager;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.util.config.YAMLConfigExtended;

public class HolderSelectionPreconditions {

	/**
	 * The Empty Race element.
	 */
	public static final HolderSelectionPreconditions EMPTY_RACE = new HolderSelectionPreconditions(-1, null, null, RacesAndClasses.getPlugin().getRaceManager());
	
	/**
	 * The Empty Class element.
	 */
	public static final HolderSelectionPreconditions EMPTY_CLASS = new HolderSelectionPreconditions(-1, null, null, RacesAndClasses.getPlugin().getClassManager());
	
	
	
	/**
	 * The Min level to have.
	 */
	private final int minLevel;
	
	/**
	 * The Holder to have before
	 */
	private final String preHolder;
	
	/**
	 * The Permission to hold.
	 */
	private final String prePerm;
	
	/**
	 * The Holder Manager to use.
	 */
	private final AbstractHolderManager holderManager;
	
	
	
	private HolderSelectionPreconditions(int minLevel, String preHolder, String prePerm, 
			AbstractHolderManager holderManager) {
		
		this.minLevel = minLevel;
		this.preHolder = preHolder;
		this.prePerm = prePerm;
		this.holderManager = holderManager;
	}
	
	
	
	/**
	 * Returns if the Preconditions are met.
	 * 
	 * @param player to check
	 * 
	 * @return true if preconditions are met.
	 */
	public HolderPreconditionResult checkPreconditions(RaCPlayer player){
		//check min level.
		if(minLevel > 0) {
			if( player.getLevelManager().getCurrentLevel() < minLevel ) return HolderPreconditionResult.LEVEL_TOO_LOW;
		}
		
		//check pre holder.
		if(preHolder != null){
			AbstractTraitHolder holder = holderManager.getHolderOfPlayer(player);
			if(holder != null && preHolder.equalsIgnoreCase(holder.getDisplayName())) {
				return HolderPreconditionResult.HOLDER_NOT_PRESENT;
			}
		}
		
		//check permission
		if(prePerm != null){
			if(!RacesAndClasses.getPlugin().getPermissionManager().checkPermissionsSilent(player.getPlayer(), prePerm)){
				return HolderPreconditionResult.PERMISSION_NOT_PRESENT;
			}
		}
		
		return HolderPreconditionResult.RESTRICTIONS_MET;
	}
	
	

	
	/**
	 * Generates a List of Descriptions for Preconditions.
	 * 
	 * @return a list of Descriptions.
	 */
	public List<String> generateDescription(RaCPlayer player) {
		List<String> list = new LinkedList<String>();
		list.add(ChatColor.LIGHT_PURPLE + "Preconditions:");
		
		if(minLevel > 0) {
			boolean hasLevel = LevelAPI.getCurrentLevel(player) >= minLevel;
			list.add((hasLevel ? ChatColor.GREEN : ChatColor.RED) + "- Needs level " + minLevel);
		}
		
		if(preHolder != null){
			AbstractTraitHolder holder = holderManager.getHolderOfPlayer(player);
			
			boolean hasHolder = holder != null && holder.getDisplayName().equalsIgnoreCase(preHolder);
			list.add((hasHolder ? ChatColor.GREEN : ChatColor.RED) 
					+ "- Needs " + holder.getContainerTypeAsString() 
					+ " " + preHolder);
		}
		
		if(prePerm != null) {
			boolean hasPerm = RacesAndClasses.getPlugin().getPermissionManager().checkPermissionsSilent(player.getRealPlayer(), prePerm);
			list.add((hasPerm ? ChatColor.GREEN : ChatColor.RED) + "- Needs Permission");
		}
		
		if(list.size() == 1) list.clear();
		return list;
	}
	
	
	/**
	 * Reads Preconditions from Config.
	 * 
	 * @param config to read from.
	 * 
	 * @return the parsed Holder selection Preconditions.
	 */
	public static HolderSelectionPreconditions parse(YAMLConfigExtended config, AbstractHolderManager manager){
		if(config == null || !config.getValidLoad()) return getEmpty(manager);
		if(config.getRootChildren().isEmpty()) return getEmpty(manager);
		
		String pre = config.getRootChildren().iterator().next();
		pre+=".preconditions.";
		
		int preLevel = config.getInt(pre + "level", -1);
		String holder = config.getString(pre + "holder", null);
		String permission = config.getString(pre + "permission", null);
		
		return new HolderSelectionPreconditions(preLevel, holder, permission, manager);
	}
	
	
	/**
	 * Returns the Empty Container.
	 * 
	 * @param manager to use for selection.
	 * 
	 * @return the empty Container.
	 */
	public static HolderSelectionPreconditions getEmpty(AbstractHolderManager manager){
		if(manager instanceof RaceManager) return EMPTY_RACE;
		if(manager instanceof ClassManager) return EMPTY_CLASS;
		
		return EMPTY_RACE;
	}
	
	
	
	public enum HolderPreconditionResult{
		LEVEL_TOO_LOW,
		HOLDER_NOT_PRESENT,
		PERMISSION_NOT_PRESENT,
		
		RESTRICTIONS_MET;
	}
	
	
}
