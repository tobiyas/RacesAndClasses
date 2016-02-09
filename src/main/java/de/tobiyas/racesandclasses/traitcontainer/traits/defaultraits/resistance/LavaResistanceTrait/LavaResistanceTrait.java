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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.resistance.LavaResistanceTrait;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.traits.resistance.AbstractResistance;

public class LavaResistanceTrait extends AbstractResistance {
	
	public LavaResistanceTrait(){
	}

	
	@TraitEventsUsed(registerdClasses = {EntityDamageEvent.class})
	@Override
	public void generalInit(){
		resistances = new LinkedList<DamageCause>();
		resistances.add(DamageCause.LAVA);
	}
	
	@Override
	public String getName() {
		return "LavaResistanceTrait";
	}

	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "You get less damage from Lava.");
		return helpList;
	}
	
	@TraitInfos(category="resistence", traitName="LavaResistanceTrait", visible=true)
	@Override
	public void importTrait() {
	}
}
