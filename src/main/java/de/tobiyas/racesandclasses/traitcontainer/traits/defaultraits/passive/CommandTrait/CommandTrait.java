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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.CommandTrait;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.magic.AbstractMagicSpellTrait;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class CommandTrait extends AbstractMagicSpellTrait  {

	private String startCommmand = "";
	private String endCommand = "";
	
	private int timeBetween = 10;
	
	
	@TraitEventsUsed(registerdClasses = {PlayerInteractEvent.class})
	@Override
	public void generalInit() {
	}

	@Override
	public String getName() {
		return "CommandTrait";
	}

	
	@Override
	protected String getPrettyConfigIntern(){
		return "Mana: " + cost;
	}

	
	@TraitInfos(category="magic", traitName="CommandTrait", visible=true)
	@Override
	public void importTrait() {
	}

	
	@Override
	public boolean isBetterThan(Trait trait) {
		if(!(trait instanceof CommandTrait)) return false;
		
		CommandTrait otherTrait = (CommandTrait) trait;
		return cost > otherTrait.cost;
	}

	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "This trait uses a command at start and end.");
		return helpList;
	}


	@Override
	protected void magicSpellTriggered(final RaCPlayer player, TraitResults result) {
		
		final String startCommandToEdit = startCommmand.replaceAll("@p", player.getName());
		final String endCommandToEdit = endCommand.replaceAll("@p", player.getName());
		
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), startCommandToEdit);
		Bukkit.getScheduler().scheduleSyncDelayedTask((JavaPlugin)plugin, new Runnable(){

			@Override
			public void run() {
				if(!player.isOnline()) return;
				
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), endCommandToEdit);				
			}
			
		}, 20 * timeBetween);
		
		result.setTriggered(true);
		return;
	}

	
	@TraitConfigurationNeeded(fields = {
			@TraitConfigurationField(fieldName = "start", classToExpect = String.class, optional = true),
			@TraitConfigurationField(fieldName = "end", classToExpect = String.class, optional = true),
			@TraitConfigurationField(fieldName = "time", classToExpect = Integer.class)
	})
	@Override
	public void setConfiguration(TraitConfiguration configMap)
			throws TraitConfigurationFailedException {
		
		if(configMap.containsKey("start")){
			startCommmand = (String) configMap.get("start");
		}

		if(configMap.containsKey("end")){
			endCommand = (String) configMap.get("end");
		}
		
		if(configMap.containsKey("time")){
			timeBetween = (Integer) configMap.get("time");
		}

		super.setConfiguration(configMap);
	}

	@Override
	public boolean isStackable(){
		return true;
	}
	
}
