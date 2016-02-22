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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.LightTrait;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.PlayerAction;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.magic.AbstractContinousCostMagicSpellTrait;

public class LightTrait extends AbstractContinousCostMagicSpellTrait{

	
	@TraitEventsUsed(registerdClasses = {PlayerInteractEvent.class, PlayerMoveEvent.class})
	@Override
	public void generalInit() {
	}
	

	@Override
	public String getName(){
		return "LightTrait";
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
		if(!(trait instanceof LightTrait)) return false;
		LightTrait otherTrait = (LightTrait) trait;
		int time = everyXSeconds <= 0 ? durationInSeconds : everyXSeconds;
		int othertime = otherTrait.everyXSeconds <= 0 ? otherTrait.durationInSeconds : otherTrait.everyXSeconds;
		
		return (cost / time) <= (otherTrait.cost / othertime);
	}


	@TraitInfos(category="magic", traitName="LightTrait", visible=true)
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

	
	/**
	 * This triggers when NO {@link PlayerInteractEvent} is triggered.
	 * 
	 * @param event that triggered
	 * @return true if triggering worked and Mana should be drained.
	 */
	protected TraitResults otherEventTriggered(EventWrapper eventWrapper, TraitResults result){
		//TODO check if this works.
		if(eventWrapper.getEvent() instanceof PlayerMoveEvent){
			RaCPlayer player = eventWrapper.getPlayer();
			if(!activePlayersSchedulerMap.containsKey(player.getName())){
				return TraitResults.False();
			}
			
			Location from = ((PlayerMoveEvent) eventWrapper.getEvent()).getFrom().subtract(0,1,0);
			Location to = ((PlayerMoveEvent) eventWrapper.getEvent()).getTo().subtract(0,1,0);
			if(from.getBlock().equals(to.getBlock())) return TraitResults.False();
			
			setLightUnderPlayer(player, from, to);
			//send lightBlock
			
			return TraitResults.True();
		}
		
		return TraitResults.False();
	}
	
	
	@SuppressWarnings("deprecation")
	private void setLightUnderPlayer(RaCPlayer player, Location from, Location to){
		if(from != null && from.getBlock().getType().isSolid()) 
			player.getPlayer().sendBlockChange(from, from.getBlock().getType().getId(), from.getBlock().getData());
		
		if(to != null && to.getBlock().getType() != Material.AIR && to.getBlock().getType().isSolid()){
			player.getPlayer().sendBlockChange(to, Material.GLOWSTONE.getId(), (byte)0);
		}
	}
	

	@Override
	protected boolean activateIntern(final RaCPlayer player) {
		//LanguageAPI.sendTranslatedMessage(player, Keys.trait_toggled, "name", getDisplayName());
		setLightUnderPlayer(player, null, player.getLocation().clone().subtract(0, 1, 0));
		return true;
	}

	
	@SuppressWarnings("deprecation")
	@Override
	protected boolean deactivateIntern(RaCPlayer player){
		if(player == null) return true;
		if(!player.isOnline()) return true;
		
		//LanguageAPI.sendTranslatedMessage(player, Keys.trait_faded, "name", getDisplayName());
		Location current = player.getLocation().clone().subtract(0, 1, 0);
		player.getPlayer().sendBlockChange(current, current.getBlock().getType().getId(), current.getBlock().getData());
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
