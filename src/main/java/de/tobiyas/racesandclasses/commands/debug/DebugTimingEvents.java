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
package de.tobiyas.racesandclasses.commands.debug;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.eventprocessing.TraitEventManager;
import de.tobiyas.racesandclasses.util.consts.Consts;

public class DebugTimingEvents implements Runnable{

	private RacesAndClasses plugin;
	
	private CommandSender sender;
	private long timing;
	private long calls;
	private long startTime;
	
	private int seconds;
	
	private int currentID;
	
	public DebugTimingEvents(CommandSender sender){
		plugin = RacesAndClasses.getPlugin();
		this.sender = sender;
		currentID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, 20);
		
		timing = 0;
		seconds = 0;
		
		TraitEventManager.timingResults();
		TraitEventManager.getCalls();
		startTime = System.currentTimeMillis();
	}

	@Override
	public void run() {		
		timing += TraitEventManager.timingResults();
		calls += TraitEventManager.getCalls();
		
		seconds++;
		
		if(seconds > Consts.timingLength){
			long takenMS = System.currentTimeMillis() - startTime;
			double percent = timing*100 / takenMS;
			
			if(sender != null){
				sender.sendMessage(ChatColor.GREEN + "Time taken in " + ChatColor.LIGHT_PURPLE + takenMS + ChatColor.GREEN +
									" milliseconds: " + ChatColor.LIGHT_PURPLE + timing +
									ChatColor.GREEN + " ms. This is: " + ChatColor.LIGHT_PURPLE + percent + ChatColor.GREEN + 
									"% of the total computing time. There where " + ChatColor.LIGHT_PURPLE + calls + ChatColor.GREEN + " Event-calls.");
			}
			
			Bukkit.getScheduler().cancelTask(currentID);
			return;
		}
	}
}
