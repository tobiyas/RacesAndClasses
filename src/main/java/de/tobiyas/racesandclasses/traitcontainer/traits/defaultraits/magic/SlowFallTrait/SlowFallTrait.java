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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.SlowFallTrait;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.PlayerAction;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.magic.AbstractContinousCostMagicSpellTrait;

public class SlowFallTrait extends AbstractContinousCostMagicSpellTrait {
	
	
	@TraitEventsUsed(registerdClasses = {PlayerInteractEvent.class, PlayerMoveEvent.class})
	@Override
	public void generalInit() {
	}
	

	@Override
	public String getName(){
		return "SlowFallTrait";
	}

	
	@Override
	protected String getPrettyConfigIntern(){
		int time = everyXSeconds <= 0 ? durationInSeconds : everyXSeconds;
		return "for: " + time + " seconds, mana: " + cost;
	}

	
	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "The trait lets you fall slower.");
		return helpList;
	}
	
	@Override
	public boolean isBetterThan(Trait trait) {
		if(!(trait instanceof SlowFallTrait)) return false;
		SlowFallTrait otherTrait = (SlowFallTrait) trait;
		
		int time = everyXSeconds <= 0 ? durationInSeconds : everyXSeconds;
		int otherTime = otherTrait.everyXSeconds <= 0 ? otherTrait.durationInSeconds : otherTrait.everyXSeconds;
		return (cost / time) <= (otherTrait.cost / otherTime);
	}


	@TraitInfos(category="magic", traitName="SlowFallTrait", visible=true)
	@Override
	public void importTrait() {
	}
	


	@Override
	public boolean canBeTriggered(EventWrapper wrapper) {
		if(wrapper.getPlayerAction() == PlayerAction.PLAYER_MOVED){
			RaCPlayer player = wrapper.getPlayer();
			return activePlayersSchedulerMap.containsKey(player.getName());
		}
		
		return super.canBeTriggered(wrapper);
	}

	
	@Override
	protected TraitResults otherEventTriggered(EventWrapper eventWrapper, TraitResults result){
		if(eventWrapper.getEvent() instanceof PlayerMoveEvent){
			PlayerMoveEvent playerMoveEvent = (PlayerMoveEvent) eventWrapper.getEvent();
			Player player = playerMoveEvent.getPlayer();
			if(!activePlayersSchedulerMap.containsKey(player.getName())){
				return TraitResults.False();
			}
			
			//reduce fallSpeed
			if((playerMoveEvent.getFrom().getY() ) > playerMoveEvent.getTo().getY() + 0.2){
				Vector fallVector = player.getLocation().getDirection();
				
				fallVector.setY(-2);
				player.setVelocity(fallVector.multiply(0.03));
				player.setFallDistance(0);
				return TraitResults.False();
			}
			
			return TraitResults.False();
		}
		
		return super.trigger(eventWrapper);
	}
	

	@Override
	protected boolean activateIntern(RaCPlayer player) {
		//LanguageAPI.sendTranslatedMessage(player, Keys.trait_toggled, "name", getDisplayName());
		return true;
	}

	
	protected boolean deactivateIntern(RaCPlayer player){
		//LanguageAPI.sendTranslatedMessage(player,  Keys.trait_faded,
		//		"name", getDisplayName());
		
		player.getPlayer().setFallDistance(0);
		return true;
	}
	
	@Override
	public void triggerButDoesNotHaveEnoghCostType(EventWrapper wrapper){
		if(wrapper.getPlayerAction() == PlayerAction.PLAYER_MOVED){
			trigger(wrapper);
			return; //containing list is checked in CanBeTriggered already
		}
		
		super.triggerButDoesNotHaveEnoghCostType(wrapper);
		return;
	}


	@Override
	protected boolean tickInternal(RaCPlayer player) {
		return true;
	}
}
