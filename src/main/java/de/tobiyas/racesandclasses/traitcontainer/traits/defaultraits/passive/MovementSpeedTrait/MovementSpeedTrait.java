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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.MovementSpeedTrait;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitRunnable;

import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.classevent.AfterClassChangedEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.raceevent.AfterRaceChangedEvent;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.RemoveSuperConfigField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.pattern.TickEverySecondsTrait;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;
import de.tobiyas.util.math.Math2;

public class MovementSpeedTrait extends TickEverySecondsTrait {

	private double value;
	
	private final float DEFAULT_SPEED = 0.2f;
	
	
	@TraitEventsUsed(registerdClasses = {AfterClassChangedEvent.class, AfterRaceChangedEvent.class})
	@Override
	public void generalInit() {
		super.generalInit();
	}

	@Override
	public String getName() {
		return "MovementSpeedTrait";
	}

	@Override
	protected String getPrettyConfigIntern(){
		return "speed: " + value;
	}

	
	@TraitInfos(category="passive", traitName="MovementSpeedTrait", visible=true)
	@Override
	public void importTrait() {
	}

	@Override
	public boolean isBetterThan(Trait trait) {
		if(trait instanceof MovementSpeedTrait){
			return value > ((MovementSpeedTrait)trait).value;
		}
		return false;
	}
	
	@Override
	public boolean canBeTriggered(EventWrapper wrapper) {
		Event event = wrapper.getEvent();
		
		//indicator for ticks.
		if(event == null && wrapper.getPlayer() != null){
			//we have a tick event.
			return true;
		}
		
		if(event instanceof AfterClassChangedEvent){
			AfterClassChangedEvent classEvent = (AfterClassChangedEvent) event;
			if(holders.contains(classEvent.getOldClass())){
				setNewSpeed(RaCPlayerManager.get().getPlayer(classEvent.getPlayer()), DEFAULT_SPEED);
			}
		}

		if(event instanceof AfterRaceChangedEvent){
			AfterRaceChangedEvent classEvent = (AfterRaceChangedEvent) event;
			if(holders.contains(classEvent.getOldRace())){
				setNewSpeed(RaCPlayerManager.get().getPlayer(classEvent.getPlayer()), DEFAULT_SPEED);
			}
		}
		
		return false;
	}

	
	@TraitConfigurationNeeded(fields = {
			@TraitConfigurationField( fieldName = "value", classToExpect = Double.class)
		}, removedFields = {@RemoveSuperConfigField(name = "seconds")})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		if( !configMap.containsKey("seconds") ) configMap.put("seconds", 5);
		super.setConfiguration(configMap);
		
		this.value = (Double) configMap.get("value");
	}

	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "The trait changes (increases or decreases) the movement speed of a Player.");
		return helpList;
	}	
	
	
	@Override
	protected boolean tickDoneForPlayer(final RaCPlayer player) {
		boolean isOnDisabledWorld = checkDisabledPerWorld(player.getWorld());
		
		final float convertedValue = isOnDisabledWorld ? DEFAULT_SPEED : (float) modifyToPlayer(player, value, "value");
		setNewSpeed(player, convertedValue);
		return false;
	}
	
	@Override
	protected void restrictionsFailed(RaCPlayer player) {
		setNewSpeed(player, DEFAULT_SPEED);
	}
	
	private void setNewSpeed(final RaCPlayer player, float convertedValue){
		final float clampedValue = Math2.clamp(-1, convertedValue, 1);
		
		//Schedule this to the next tick since it will be overwritten...
		new BukkitRunnable() {
			@Override
			public void run() {
				if(!player.isOnline()) return;
				
				//Check if we need to set it:
				Player pl = player.getPlayer();
				float currentSpeed = pl.getWalkSpeed();
				if( Math.abs( currentSpeed - clampedValue ) > 0.02 ){
					player.getPlayer().setWalkSpeed(clampedValue);					
				}
			}
		}.runTaskLater( plugin, 2 );
	}
	
	/**
	 * Checks if the world is on the disabled list.
	 * 
	 * True if it is, false if not.
	 * 
	 * @param event
	 * @return
	 */
	private boolean checkDisabledPerWorld(World world) {
		List<String> worldsDisabledOn = plugin.getConfigManager().getGeneralConfig().getConfig_worldsDisabled();
		
		String worldName = world == null ? "" : world.getName();
		for(String disabledWorldName : worldsDisabledOn){
			if(disabledWorldName.equalsIgnoreCase(worldName)){
				return true;
			}
		}
		
		return false;
	}

	@Override
	protected String getPrettyConfigurationPre() {
		return "";
	}
}
