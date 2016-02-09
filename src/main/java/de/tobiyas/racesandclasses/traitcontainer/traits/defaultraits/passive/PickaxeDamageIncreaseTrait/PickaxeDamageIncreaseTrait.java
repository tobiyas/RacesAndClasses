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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.PickaxeDamageIncreaseTrait;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.traits.pattern.AbstractWeaponDamageIncreaseTrait;

public class PickaxeDamageIncreaseTrait extends AbstractWeaponDamageIncreaseTrait{
	
	
	public PickaxeDamageIncreaseTrait(){
		this.weapons.add(Material.WOOD_PICKAXE);
		this.weapons.add(Material.STONE_PICKAXE);
		this.weapons.add(Material.GOLD_PICKAXE);
		this.weapons.add(Material.IRON_PICKAXE);
		this.weapons.add(Material.DIAMOND_PICKAXE);
	}


	@Override
	public String getName() {
		return "PickaxeDamageIncreaseTrait";
	}


	@TraitInfos(category="passive", traitName="PickaxeDamageIncreaseTrait", visible=true)
	@Override
	public void importTrait() {
	}
	
	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "The trait increases the Damage of your Pickaxe.");
		return helpList;
	}
	
}
