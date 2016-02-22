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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.activate.TrollBloodTrait;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.APIs.MessageScheduleApi;
import de.tobiyas.racesandclasses.entitystatusmanager.dot.DamageType;
import de.tobiyas.racesandclasses.entitystatusmanager.dot.DotContainer;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapperFactory;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.PlayerAction;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.AbstractBasicTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.translation.languages.Keys;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class TrollbloodTrait extends AbstractBasicTrait {

	private int duration = 0;
	
	
	@TraitEventsUsed(registerdClasses = {PlayerInteractEvent.class})
	@Override
	public void generalInit() {}
	
	@Override
	public String getName() {
		return "TrollbloodTrait";
	}


	@Override
	protected String getPrettyConfigIntern(){
		return "duration: " + duration + "seconds";
	}

	@TraitConfigurationNeeded(fields = {
			@TraitConfigurationField(fieldName = "duration", classToExpect = Integer.class, optional=true)
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		duration = configMap.getAsInt("duration", 0);
	}

	@Override
	public TraitResults trigger(EventWrapper eventWrapper) {   Event event = eventWrapper.getEvent();
		PlayerInteractEvent Eevent = (PlayerInteractEvent) event;
		Player player = Eevent.getPlayer();

		int removed = plugin.getDotManager().removeAllDots(player);
		if(removed <= 0) return TraitResults.False();
		
		LanguageAPI.sendTranslatedMessage(player, Keys.trait_toggled, "name", getDisplayName());
		MessageScheduleApi.scheduleTranslateMessageToPlayer(player.getName(), duration, Keys.trait_faded, "name", getDisplayName());
		return TraitResults.True();
	}
	
	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "The trait removes all dot effects on you.");
		return helpList;
	}
	

	@Override
	public boolean isBetterThan(Trait trait) {
		if(!(trait instanceof TrollbloodTrait)) return false;
		TrollbloodTrait otherTrait = (TrollbloodTrait) trait;
		
		return duration >= otherTrait.duration;
	}


	@TraitInfos(category="activate", traitName="TrollBloodTrait", visible=true)
	@Override
	public void importTrait() {
	}

	
	@Override
	protected TraitResults bindCastIntern(RaCPlayer player) {
		return trigger(EventWrapperFactory.buildOnlyWithplayer(player));
	}
	

	@Override
	public boolean canBeTriggered(EventWrapper wrapper) {
		if(!(wrapper.getPlayerAction() == PlayerAction.HIT_BLOCK || wrapper.getPlayerAction() == PlayerAction.HIT_AIR)) return false;
		
		RaCPlayer player = wrapper.getPlayer();
		if(player.getPlayer().getItemInHand().getType() != Material.APPLE) return false;
		
		Collection<DotContainer> dots = plugin.getDotManager().getAllDotsOnEntity(player.getPlayer(), DamageType.POISON);
		return dots != null && !dots.isEmpty();
	}

	@Override
	public boolean triggerButHasUplink(EventWrapper wrapper) {
		//Not needed
		return false;
	}
	
	
	@Override
	public boolean isBindable() {
		return true;
	}

}
