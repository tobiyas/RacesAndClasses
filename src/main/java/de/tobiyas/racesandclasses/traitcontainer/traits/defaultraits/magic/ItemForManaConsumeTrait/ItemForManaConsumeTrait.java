/*******************************************************************************
 * Copyright 2014 Tob
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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.ItemForManaConsumeTrait;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.eventprocessing.events.mana.ManaRegenerationEvent;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.magic.AbstractMagicSpellTrait;
import de.tobiyas.racesandclasses.translation.languages.Keys;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class ItemForManaConsumeTrait extends AbstractMagicSpellTrait {

	
	private RacesAndClasses plugin = RacesAndClasses.getPlugin();
	
	private Double value = 0d;
	
	
	@TraitEventsUsed()
	@Override
	public void generalInit() {
	}

	@Override
	public String getName() {
		return "ItemForManaConsumeTrait";
	}

	
	
	@TraitConfigurationNeeded(fields = {
			@TraitConfigurationField(fieldName = ITEM_TYPE_PATH, classToExpect = Material.class),
			@TraitConfigurationField(fieldName = "value", classToExpect = Double.class),
	})
	@Override
	public void setConfiguration(TraitConfiguration configMap)
			throws TraitConfigurationFailedException {

		super.setConfiguration(configMap);		
		
		cost = Math.floor(cost);
		value = (Double) configMap.get("value");
		
		costType = CostType.ITEM;
		
		materialForCasting = (Material) configMap.get(ITEM_TYPE_PATH);
		if(materialForCasting == null){
			throw new TraitConfigurationFailedException("Could not identify item to use.");
		}
	}

	@Override
	protected String getPrettyConfigIntern(){
		return cost + " " + materialForCasting.name() + ": " + value;
	}

	
	@TraitInfos(category="magic", traitName="ItemForManaConsumeTrait", visible=true)
	@Override
	public void importTrait() {
	}

	
	@Override
	public boolean isBetterThan(Trait trait) {
		if(!(trait instanceof ItemForManaConsumeTrait)) return false;
		
		ItemForManaConsumeTrait otherTrait = (ItemForManaConsumeTrait) trait;
		return value > otherTrait.value;
	}

	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "This trait converts Items To Mana.");
		return helpList;
	}


	@Override
	protected void magicSpellTriggered(RaCPlayer player, TraitResults result) {		
		if(player.getManaManager().isManaFull()){
			LanguageAPI.sendTranslatedMessage(player, Keys.mana_already_full);
			result.setTriggered(false);
			return;
		}
		
		double modValue = modifyToPlayer(player, value, "value");
		ManaRegenerationEvent event = new ManaRegenerationEvent(player.getPlayer(), modValue);
		plugin.fireEventToBukkit(event);
		
		double newValue = event.getAmount();
		
		if(event.isCancelled() || newValue < 0){
			LanguageAPI.sendTranslatedMessage(player, Keys.trait_failed, 
					"name", getDisplayName());
			result.setTriggered(false);
			return;
		}
		
		player.getManaManager().fillMana(newValue);
		
		LanguageAPI.sendTranslatedMessage(player, Keys.trait_consume_success,
				"value", String.valueOf(newValue), "material", materialForCasting.name());
		
		result.setTriggered(true).setRemoveCostsAfterTrigger(true);
		return;
	}

}
