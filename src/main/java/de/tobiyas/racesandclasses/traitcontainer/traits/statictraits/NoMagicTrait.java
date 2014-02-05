package de.tobiyas.racesandclasses.traitcontainer.traits.statictraits;

import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.magic.AbstractMagicSpellTrait;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class NoMagicTrait extends AbstractMagicSpellTrait {

	
	@TraitInfos(category="magic", traitName="NoMagicTrait", visible=false)
	@Override
	public void importTrait() {
	}

	@TraitEventsUsed(registerdClasses = {PlayerInteractEvent.class})
	@Override
	public void generalInit(){
	}
	
	@Override
	public String getName() {
		return "NoMagicTrait";
	}	
	
	
	
	
	@Override
	public boolean canBeTriggered(Event event) {
		return super.canBeTriggered(event);
	}
		
	@Override
	public void setConfiguration(Map<String, Object> configMap)
			throws TraitConfigurationFailedException {

		
		super.setConfiguration(configMap);
	}

	@Override
	protected String getPrettyConfigIntern() {
		return "No magic selected";
	}

	@Override
	public boolean isBetterThan(Trait trait) {
		return false;
	}

	@Override
	protected boolean magicSpellTriggered(Player player) {
		return false;
	}

}
