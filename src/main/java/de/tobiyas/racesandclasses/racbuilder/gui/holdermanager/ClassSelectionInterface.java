package de.tobiyas.racesandclasses.racbuilder.gui.holdermanager;

import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractHolderManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.racesandclasses.racbuilder.AbstractHolderBuilder;
import de.tobiyas.racesandclasses.racbuilder.ClassBuilder;
import de.tobiyas.racesandclasses.racbuilder.gui.BasicSelectionInterface;

public class ClassSelectionInterface extends HolderSelectionInterface {

	public ClassSelectionInterface(Player player,
			BasicSelectionInterface parent, AbstractHolderManager holderManager) {
		
		super(player, parent, holderManager);
		
	}

	@Override
	protected AbstractHolderBuilder generateNewHolderBuilder(String name) {
		return new ClassBuilder(name);
	}

	@Override
	protected AbstractHolderBuilder generateHolderBuilderFor(
			AbstractTraitHolder holder) {
		return new ClassBuilder((ClassContainer) holder);
	}

}
