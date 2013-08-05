package de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractHolderManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.DefaultContainer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.exceptions.HolderParsingException;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.reminder.RaceReminder;
import de.tobiyas.racesandclasses.tutorial.TutorialManager;
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

		TutorialManager.registerObserver(this);
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
				addPlayerToHolder(playerName, Consts.defaultRace);
				container = (RaceContainer) getHolderOfPlayer(playerName);
			}

			container.editTABListEntry(playerName);
		}

		for (String playerName : memberList.keySet())
			if (memberList.get(playerName) == null) {
				addPlayerToHolder(playerName, Consts.defaultRace);
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
	public boolean addPlayerToHolder(String player, String newHolderName){
		boolean worked = super.addPlayerToHolder(player, newHolderName);
		if(worked){
			AbstractTraitHolder holder = getHolderOfPlayer(player);
			if(holder instanceof RaceContainer){
				((RaceContainer)holder).editTABListEntry(player);
			}
		}
		
		return worked;
	}

	@Override
	public String getContainerTypeAsString() {
		return "race";
	}

}
