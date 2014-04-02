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

import static de.tobiyas.racesandclasses.translation.languages.Keys.armor_not_allowed;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.eventprocessing.events.inventoryitemevents.PlayerEquipsArmorEvent;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.AbstractBasicTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.bypasses.StaticTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;

public class ArmorTrait extends AbstractBasicTrait implements StaticTrait{

	protected RacesAndClasses plugin = RacesAndClasses.getPlugin();
	
	protected AbstractTraitHolder traitHolder;

	@TraitEventsUsed(registerdClasses = {PlayerEquipsArmorEvent.class})
	@Override
	public void generalInit(){
	}
	
	@Override
	public void setTraitHolder(AbstractTraitHolder abstractTraitHolder){
		this.traitHolder = abstractTraitHolder;
	}
	
	@Override
	public AbstractTraitHolder getTraitHolder(){
		return traitHolder;
	}

	@Override
	public String getName() {
		return "ArmorTrait";
	}


	@Override
	protected String getPrettyConfigIntern(){
		return "";
	}

	@TraitConfigurationNeeded()
	@Override
	public void setConfiguration(Map<String, Object> configMap) {
	}

	@Override	
	public TraitResults trigger(EventWrapper eventWrapper) {
		Event event = eventWrapper.getEvent();
		if(!(event instanceof PlayerEquipsArmorEvent)) return TraitResults.False();
		
		PlayerEquipsArmorEvent playerEquipEvent = (PlayerEquipsArmorEvent) event;
		Player player = (Player) playerEquipEvent.getPlayer();
		if(player == null) return TraitResults.False();

		ItemStack armorItem = playerEquipEvent.getArmorItem();
		if(armorItem == null) return TraitResults.False();
		
		if(!plugin.getPlayerManager().getArmorToolManagerOfPlayer(player.getUniqueId()).hasPermissionForItem(armorItem)){ 
			String matName = getMaterialName(armorItem.getType());
			LanguageAPI.sendTranslatedMessage(player, armor_not_allowed, "material", matName);
			playerEquipEvent.setCancelled(true);
		}

		return TraitResults.True();
	}
	
	
	/**
	 * Give material a realistic name
	 * 
	 * @param material
	 * @return
	 */
	private  String getMaterialName(Material material) {
        StringBuilder materialName = new StringBuilder();
        for(String c : material.toString().toLowerCase().split("_")) {
            materialName.append(Character.toUpperCase(c.charAt(0))).append(c, 1, c.length()).append(" ");
        }
        
        return materialName.toString().trim();
    }

	
	@Override
	public boolean isBetterThan(Trait trait) {
		return true;
	}

	@TraitInfos(category="static", traitName="AromrTrait", visible=false)
	@Override
	public void importTrait() {
	}

	@Override
	public boolean canBeTriggered(EventWrapper wrapper) {
		return true;
	}

	@Override
	public boolean isStackable(){
		return false;
	}

	@Override
	public String getDisplayName() {
		return getName();
	}
}
