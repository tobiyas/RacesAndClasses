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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.FasterSprintingTrait;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSprintEvent;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapperFactory;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.AbstractBasicTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.TraitRestriction;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class FasterSprintingTrait extends AbstractBasicTrait {

	
	
	private static final double NORMAL_MOD = 0.3;
	
	
	private double sprintMod = NORMAL_MOD;
	
	
	/**
	 * The Set of sprinting people.
	 */
	private final Set<UUID> sprinting = new HashSet<UUID>();
	
	
	@TraitEventsUsed(registerdClasses = {})
	@Override
	public void generalInit() {
		plugin.registerEvents(this);
	}
	
	
	@EventHandler
	public void playerStartSprinting(PlayerToggleSprintEvent event){
		if(event.isCancelled()) return;
		
		boolean sprinting = event.isSprinting();
		Player player = event.getPlayer();
		UUID id = player.getUniqueId();
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
		if(!TraitHolderCombinder.checkContainer(racPlayer, this)) return;
		
		//Now do the Action:
		if(sprinting){
			//First check restrictions:
			if(super.checkRestrictions(EventWrapperFactory.buildFromEvent(event)) != TraitRestriction.None) return;
			
			this.sprinting.add(id);
			player.setWalkSpeed((float)sprintMod);
		}else{
			this.sprinting.remove(id);
			player.setWalkSpeed((float)NORMAL_MOD);
		}
	}
	
	
	
	@Override
	public String getName(){
		return "FasterSprintingTrait";
	}

	
	@Override
	protected String getPrettyConfigIntern(){
		return "Sprint fast: " + sprintMod;
	}

	@TraitConfigurationNeeded(fields = {
			@TraitConfigurationField(fieldName = "speed", classToExpect = Double.class),
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		this.sprintMod = configMap.getAsDouble("speed");
	}
	

	@Override
	public TraitResults trigger(EventWrapper eventWrapper) {
		return TraitResults.False();
	}
	



	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "The trait lets you sprint faster.");
		
		return helpList;
	}
	
	@Override
	public boolean isBetterThan(Trait trait) {
		if(!(trait instanceof FasterSprintingTrait)) return false;
		FasterSprintingTrait otherTrait = (FasterSprintingTrait) trait;
		
		return sprintMod >= otherTrait.sprintMod;
	}


	@TraitInfos(category="passive", traitName="FasterSprintingTrait", visible=true)
	@Override
	public void importTrait() {
	}

	@Override
	public boolean canBeTriggered(EventWrapper wrapper) {
		return false;
	}

	@Override
	public boolean triggerButHasUplink(EventWrapper wrapper) {
		return false;
	}
	
	@Override
	public boolean notifyTriggeredUplinkTime(EventWrapper wrapper) {
		return false;
	}
	
}
