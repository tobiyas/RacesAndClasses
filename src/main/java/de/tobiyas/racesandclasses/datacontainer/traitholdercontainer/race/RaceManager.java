package de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractHolderManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.DefaultContainer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.PlayerHolderAssociation;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.exceptions.HolderParsingException;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.permissionsettings.PermissionRegisterer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.reminder.RaceReminder;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.HolderSelectEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.raceevent.AfterRaceChangedEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.raceevent.AfterRaceSelectedEvent;
import de.tobiyas.racesandclasses.util.consts.Consts;
import de.tobiyas.util.config.YAMLConfigExtended;

public class RaceManager extends AbstractHolderManager {

	public RaceManager() {
		super(Consts.playerDataYML, Consts.racesYML);

		DefaultContainer.createSTDRaces();
	}

	@Override
	public void init() {
		super.init();

		checkForPossiblyWrongInitialized();
		new RaceReminder();

		plugin.getTutorialManager().registerObserver(this);
		this.setChanged();
	}

	@Override
	protected void readMemberList() {
		DefaultContainer.createSTDMembers();
		super.readMemberList();
	}

	/**
	 * This is needed to check if players are online on reloads or inits.
	 * They would not be affected by join events.
	 */
	private void checkForPossiblyWrongInitialized() {
		Player[] players = Bukkit.getOnlinePlayers();

		for (Player player : players) {
			String playerName = player.getName();
			RaceContainer container = (RaceContainer) getHolderOfPlayer(playerName);
			if (container == null) {
				addPlayerToHolder(playerName, Consts.defaultRace, false);
				container = (RaceContainer) getHolderOfPlayer(playerName);
			}

			container.editTABListEntry(playerName);
		}

		for (String playerName : memberList.keySet())
			if (memberList.get(playerName) == null) {
				addPlayerToHolder(playerName, Consts.defaultRace, false);
			}
	}


	@Override
	protected AbstractTraitHolder generateTraitHolderAndLoad(
			YAMLConfigExtended traitHolderConfig, String holderName)
			throws HolderParsingException {
		
		return RaceContainer.loadRace(traitHolderConfig, holderName);
	}

	@Override
	protected String getConfigPrefix() {
		return "race";
	}

	@Override
	public AbstractTraitHolder getDefaultHolder() {
		return getHolderByName(Consts.defaultRace);
	}

	@Override
	protected void initDefaultHolder() {
		traitHolderList.add(RaceContainer.generateSTDRace());
	}
	
	@Override
	public boolean changePlayerHolder(String player, String newHolderName, boolean callEvent){
		if(getHolderByName(newHolderName) == null) return false;
		
		String oldRace = getHolderOfPlayer(player).getName();
		
		PermissionRegisterer.removePlayer(player, getContainerTypeAsString());
		
		memberList.remove(player);
		
		memberConfig.load();
		memberConfig.set("playerdata." + player + "." + getConfigPrefix(), null);
		memberConfig.save();
		
		if(plugin.getConfigManager().getGeneralConfig().isConfig_channels_enable()){
			plugin.getChannelManager().playerLeaveRace(oldRace, Bukkit.getPlayer(player));
		}
		
		return addPlayerToHolder(player, newHolderName, callEvent);
	}
	
	@Override
	public boolean addPlayerToHolder(String player, String newHolderName, boolean callEvent){		
		boolean worked = super.addPlayerToHolder(player, newHolderName, callEvent);
		if(worked){
			AbstractTraitHolder holder = getHolderOfPlayer(player);
			if(holder instanceof RaceContainer){
				((RaceContainer)holder).editTABListEntry(player);
			}
			
			if(plugin.getConfigManager().getGeneralConfig().isConfig_channels_enable()){
				plugin.getChannelManager().playerJoinRace(holder.getName(), Bukkit.getPlayer(player));
			}
		}
		
		return worked;
	}

	@Override
	public String getContainerTypeAsString() {
		return "race";
	}

	@Override
	protected String getCorrectFieldFromDBHolder(
			PlayerHolderAssociation container) {
		return container.getRaceName();
	}

	@Override
	protected String getDBFieldName() {
		return "raceName";
	}

	@Override
	protected void saveContainerToDBField(PlayerHolderAssociation container,
			String name) {
		container.setRaceName(name);
	}
	
	@Override
	protected HolderSelectEvent generateAfterSelectEvent(String player,
			AbstractTraitHolder newHolder) {
		return new AfterRaceSelectedEvent(Bukkit.getPlayer(player), (RaceContainer)newHolder);
	}


	@Override
	protected HolderSelectEvent generateAfterChangeEvent(String player,
			AbstractTraitHolder newHolder, AbstractTraitHolder oldHolder) {
		return new AfterRaceChangedEvent(Bukkit.getPlayer(player), (RaceContainer) newHolder, (RaceContainer) oldHolder);
	}

	@Override
	protected AbstractTraitHolder getStartingHolder() {
		String race = plugin.getConfigManager().getGeneralConfig().getConfig_takeRaceWhenNoRace();
		if(race == null || "".equals(race)){
			return getDefaultHolder();
		}
		
		AbstractTraitHolder holder = getHolderByName(race);
		
		return holder != null ? holder : getDefaultHolder();
	}

}
