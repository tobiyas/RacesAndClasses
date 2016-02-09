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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.MiningSpeedTrait;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.passive.AbstractPassiveTrait;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class MiningSpeedTrait extends AbstractPassiveTrait{
	
	/**
	 * List of blocks this works for.
	 */
	private List<Material> blocksToVerify = new LinkedList<Material>();
	
	/**
	 * The Effect to apply
	 */
	private PotionEffect effect = null;
	
	
	@TraitEventsUsed(registerdClasses = {BlockDamageEvent.class})
	@Override
	public void generalInit(){
	}

	@Override
	public String getName() {
		return "MiningSpeedTrait";
	}
	
	
	@Override
	protected String getPrettyConfigIntern() {
		String blocksString = "";
		for(Material mat : blocksToVerify){
			blocksString += " " + mat.name();
		}
		
		return "MiningSpeed: " + effect.getAmplifier() + " blocks: " + blocksString;
	}

	@TraitConfigurationNeeded(fields = {
					@TraitConfigurationField(fieldName = "value", classToExpect = Integer.class),
					@TraitConfigurationField(fieldName = "blocks", classToExpect = List.class, optional = true)
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		int value = (Integer) configMap.get("value");
		if(value == 0 || value > 5 || value < -5){
			throw new TraitConfigurationFailedException("value: " + value + " is not aplyable. "
					+ "It has to be between -5 and 5 (without 0)");
		}
		
		effect = calcPotionEffectFromValue(value);
		
		blocksToVerify.clear();
		@SuppressWarnings("unchecked")
		List<String> blockList = (List<String>) configMap.get("blocks");
		if(blockList == null || blockList.isEmpty()){
			for(Material mat : Material.values()){
				if(mat.isBlock()){
					blocksToVerify.add(mat);
				}
			}
		}else{
			for(String block : blockList){
				Material material = Material.valueOf(block.toUpperCase());
				if(material != null){
					blocksToVerify.add(material);
				}
			}
		}

	}
	

	@Override
	public TraitResults trigger(EventWrapper eventWrapper) {   Event event = eventWrapper.getEvent();
		BlockDamageEvent Eevent = (BlockDamageEvent) event;
		Player player = Eevent.getPlayer();
		
		boolean isValidBlock = blocksToVerify.contains(Eevent.getBlock().getType());
		
		if(isValidBlock){
			player.addPotionEffect(effect, true);
		}else{
			player.removePotionEffect(effect.getType());
			//Eevent.setCancelled(true);
		}
		return TraitResults.True();
	}
	
	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "Your Mining speed gets changed for specific blocks.");
		return helpList;
	}
	
	@Override
	public boolean isBetterThan(Trait trait) {
		if(!(trait instanceof MiningSpeedTrait)) return false;
		MiningSpeedTrait otherTrait = (MiningSpeedTrait) trait;
		
		return effect.getAmplifier() >= otherTrait.effect.getAmplifier();
	}
	
	@TraitInfos(category="passive", traitName="MiningSpeedTrait", visible=true)
	@Override
	public void importTrait() {
	}

	@Override
	public boolean canBeTriggered(EventWrapper wrapper) {
		Event event = wrapper.getEvent();
		if(!(event instanceof BlockDamageEvent)) return false;
		BlockDamageEvent Eevent = (BlockDamageEvent) event;
		Player player = Eevent.getPlayer();
		Block block = Eevent.getBlock();
		if(blocksToVerify.contains(block.getType())){
			return true;
		}else {
			if(player.hasPotionEffect(effect.getType())){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Calculates the potionEffect by the value passed.
	 * <br>Values are between -5 and +5.
	 * <br>Minus means Slower, Plus means faster.
	 * <br>
	 * <br>Level -5 means unbreakable,
	 * <br>level 5 means instant break.
	 * 
	 * 
	 * @param value to evaluate
	 * @return the correct PotionEffect
	 */
	private PotionEffect calcPotionEffectFromValue(int value){
		if(value > 0){
			return new PotionEffect(PotionEffectType.FAST_DIGGING, 10, value, false);
		}else{
			return new PotionEffect(PotionEffectType.SLOW_DIGGING, 10, -value, false);
		}
	}
}
