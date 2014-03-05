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
package de.tobiyas.racesandclasses.datacontainer.armorandtool;

import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.util.items.ItemUtils;
import de.tobiyas.racesandclasses.util.items.ItemUtils.ItemQuality;

public class MaterialArmorPermission implements AbstractItemPermission{

	private ItemQuality quality;
	
	public MaterialArmorPermission(ItemQuality quality){
		this.quality = quality;
	}

	@Override
	public boolean hasPermission(ItemStack item) {
		return ItemUtils.getItemValue(item) == quality;
	}

	@Override
	public boolean isAlreadyRegistered(ItemQuality quality) {
		return quality == this.quality;
	}
	
	@Override
	public String toString(){
		return quality.name();
	}
}
