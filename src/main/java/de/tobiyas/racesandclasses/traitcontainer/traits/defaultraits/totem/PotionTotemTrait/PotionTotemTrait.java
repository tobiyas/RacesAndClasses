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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.totem.PotionTotemTrait;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.pattern.AbstractTotemTrait;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class PotionTotemTrait extends AbstractTotemTrait {

	/**
	 * The Value to heal.
	 */
	private int amplifier = 1;
	
	/**
	 * The Effect ID to add.
	 */
	private int effectId = 0; 
	
	/**
	 * The Potion Effect to add
	 */
	private PotionEffectType effect = null;
	

	@Override
	public String getName() {
		return "PotionTotemTrait";
	}


	@SuppressWarnings("deprecation")
	@TraitConfigurationNeeded( fields = {
			@TraitConfigurationField(fieldName = "effect", classToExpect = Integer.class, optional = false),
			@TraitConfigurationField(fieldName = "amplifier", classToExpect = Integer.class, optional = true)
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		if(configMap.containsKey("effect")){
			effectId = (Integer) configMap.get("effect");
			effect = PotionEffectType.getById(effectId);
		}
		
		if(configMap.containsKey("amplifier")){
			amplifier = (Integer) configMap.get("amplifier");
		}
	}

	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "This Trait places a totem on the ground that applies a potion effect.");
		return helpList;
	}
	

	@TraitInfos(category = "totem", traitName = "PotionTotemTrait", visible = true)
	@Override
	public void importTrait() {
	}


	@Override
	public boolean isBetterThan(Trait trait) {
		return false;
	}


	@Override
	protected void tickOnPlayer(TotemInfos infos, Player player) {
		if(effect == null) return; //no effect -> nothing to add.
		
		int modAmp = modifyToPlayer(infos.getOwner(), amplifier, "amplifier");
		PotionEffect toApply = new PotionEffect(effect, 2 * 20, modAmp);
		player.addPotionEffect(toApply);
	}

	@Override
	protected void tickOnNonPlayer(TotemInfos infos, LivingEntity entity) {
		if(effect == null) return; //no effect -> nothing to add.
		
		int modAmp = modifyToPlayer(infos.getOwner(), amplifier, "potion");
		PotionEffect toApply = new PotionEffect(effect, 2 * 20, modAmp);
		entity.addPotionEffect(toApply);
	}

	@Override
	protected String getPrettyConfigIntern() {
		return "Applies " + effect + " every " + tickEvery / 20;
	}


}
