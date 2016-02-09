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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.PassiveAggroTrait;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityTargetEvent;

import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapperFactory;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.AbstractBasicTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.TraitRestriction;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class PassiveAggroTrait extends AbstractBasicTrait {
	
	/**
	 * The List of aggro holders.
	 */
	private final Map<EntityType,Integer> aggroDistanceList = new HashMap<EntityType,Integer>();
	

	@Override
	public String getName() {
		return "PassiveAggroTrait";
	}


	@SuppressWarnings("deprecation")
	@TraitConfigurationNeeded( fields = {
			@TraitConfigurationField(fieldName = "aggro", classToExpect = List.class, optional = true)
	})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		aggroDistanceList.clear();
		if(configMap.containsKey("aggro")){
			List<String> list = configMap.getAsStringList("aggro");
			for(String entry : list){
				if(!entry.contains("#")) continue;
				
				String[] split = entry.split("#");
				if(split.length != 2) continue;
				
				EntityType type = null;
				for(EntityType tmp : EntityType.values()){
					if(tmp.name().equalsIgnoreCase(split[0])) type = tmp;
				}

				if(type == null) type = EntityType.fromName(split[0].replace("_", "").replace(" ", ""));
				if(type == null) continue;
				
				int range = 20;
				try{range = Integer.parseInt(split[1]); }catch(Throwable exp){ continue; }
				
				aggroDistanceList.put(type, range);
			}
		}
	}

	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "This trait Changes the Aggro Distance.");
		return helpList;
	}
	

	@TraitInfos(category = "passive", traitName = "PassiveAggroTrait", visible = true)
	@Override
	public void importTrait() {
	}
	
	
	@Override
	public void deInit() {
		super.deInit();
		
		HandlerList.unregisterAll(this);
	}


	@EventHandler
	public void onAggroChange(EntityTargetEvent event){
		System.out.println("Got event!");
		
		if(event.isCancelled()) return;
		if(aggroDistanceList.isEmpty()) return;
		
		if(event.getTarget() == null) return;
		if(event.getTarget().getType() != EntityType.PLAYER) return;
		
		EntityType monster = event.getEntityType();
		if(!aggroDistanceList.containsKey(monster)) return;
		
		Player player = (Player)(event.getTarget());
		if(super.checkRestrictions(EventWrapperFactory.buildOnlyWithplayer(player)) != TraitRestriction.None) return;
		
		int maxDist = aggroDistanceList.get(monster);
		if(player.getLocation().distanceSquared(event.getTarget().getLocation()) > maxDist * maxDist) {
			event.setCancelled(true);
		}
	}
	
	
	@Override
	protected String getPrettyConfigIntern() {
		String val = "";
		for(Entry<EntityType,Integer> entry : aggroDistanceList.entrySet()){
			val = val + ", " + entry.getKey().name() + ": " + entry.getValue();
		}
		
		return "MonsterRanges: " + (val.isEmpty() ? val : val.substring(2));
	}
	
	
	@Override
	public boolean isStackable(){
		return true;
	}


	@Override
	public void generalInit() {
		//Not needed.
	}


	@Override
	public TraitResults trigger(EventWrapper wrapper) {
		return TraitResults.False();
	}


	@Override
	public boolean isBetterThan(Trait trait) {
		return false;
	}


	@Override
	public boolean canBeTriggered(EventWrapper wrapper) {
		return false;
	}

}
