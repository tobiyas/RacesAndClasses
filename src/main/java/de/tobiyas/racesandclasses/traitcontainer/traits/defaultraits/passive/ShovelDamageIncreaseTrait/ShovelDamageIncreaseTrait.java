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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.ShovelDamageIncreaseTrait;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.traits.pattern.AbstractWeaponDamageIncreaseTrait;

public class ShovelDamageIncreaseTrait extends AbstractWeaponDamageIncreaseTrait {
	
	
	public ShovelDamageIncreaseTrait(){
		this.weapons.add(Material.WOOD_SPADE);
		this.weapons.add(Material.STONE_SPADE);
		this.weapons.add(Material.GOLD_SPADE);
		this.weapons.add(Material.IRON_SPADE);
		this.weapons.add(Material.DIAMOND_SPADE);
	}
	
	
	@Override
	public String getName() {
		return "ShovelDamageIncreaseTrait";
	}


	@TraitInfos(category="passive", traitName="ShovelDamageIncreaseTrait", visible=true)
	@Override
	public void importTrait() {
	}
	
	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "The trait increases the Damage of your Shovels.");
		return helpList;
	}
	
}
