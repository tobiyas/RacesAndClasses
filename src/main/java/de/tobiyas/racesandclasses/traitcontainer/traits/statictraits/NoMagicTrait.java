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
package de.tobiyas.racesandclasses.traitcontainer.traits.statictraits;

import org.bukkit.event.player.PlayerInteractEvent;

import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.magic.AbstractMagicSpellTrait;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
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
	public void setConfiguration(TraitConfiguration configMap)
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
	protected void magicSpellTriggered(RaCPlayer player, TraitResults result) {
	}

	@Override
	public short getCastMaterialDamage(RaCPlayer player) {
		return 0;
	}

	@Override
	public String getCastMaterialName(RaCPlayer player) {
		return null;
	}

}
