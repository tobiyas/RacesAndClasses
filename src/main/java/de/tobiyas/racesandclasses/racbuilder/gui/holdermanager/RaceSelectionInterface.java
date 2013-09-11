package de.tobiyas.racesandclasses.racbuilder.gui.holdermanager;

import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractHolderManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.racbuilder.AbstractHolderBuilder;
import de.tobiyas.racesandclasses.racbuilder.RaceBuilder;
import de.tobiyas.racesandclasses.racbuilder.gui.BasicSelectionInterface;

public class RaceSelectionInterface extends HolderSelectionInterface {

	public RaceSelectionInterface(Player player,
			BasicSelectionInterface parent, AbstractHolderManager holderManager) {
		
		super(player, parent, holderManager);
	}

	@Override
	protected AbstractHolderBuilder generateNewHolderBuilder(String name) {
		return new RaceBuilder(name);
	}

	@Override
	protected AbstractHolderBuilder generateHolderBuilderFor(
			AbstractTraitHolder holder) {
		return new RaceBuilder((RaceContainer) holder);
	}

}
