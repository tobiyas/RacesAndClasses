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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.ColdFeetTrait;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

import de.tobiyas.racesandclasses.RacesAndClasses;

public class ScheduleBackToWater {

	private final Block block;
	
	private final int duration;
	
	private final byte oldByte;
	private final Material oldMaterial;
	
	
	@SuppressWarnings("deprecation")
	public ScheduleBackToWater(Block block, int duration) {
		this.block = block;
		
		this.oldByte = block.getData();
		this.oldMaterial = block.getType();
		
		this.duration = duration;
		
		block.setType(Material.ICE);
		scheduleBack();
	}


	private void scheduleBack() {
		Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin) RacesAndClasses.getPlugin(), new Runnable() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				block.setType(oldMaterial);
				block.setData(oldByte);
			}
		}, 20 * duration);
	}


	public Block getBlock() {
		return block;
	}
	
	
	
	
}
