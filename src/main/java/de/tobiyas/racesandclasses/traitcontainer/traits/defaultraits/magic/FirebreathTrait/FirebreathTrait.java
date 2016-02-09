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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.FirebreathTrait;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.BlockIterator;

import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.magic.AbstractMagicSpellTrait;
import de.tobiyas.racesandclasses.util.damage.PreEntityDamageEvent;
import de.tobiyas.racesandclasses.util.entitysearch.SearchEntity;
import de.tobiyas.racesandclasses.util.friend.EnemyChecker;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;
import de.tobiyas.racesandclasses.vollotile.ParticleContainer;
import de.tobiyas.racesandclasses.vollotile.Vollotile;

public class FirebreathTrait extends AbstractMagicSpellTrait implements Listener  {

	private double damage = 3;
	
	private int range = 4;
	
	private ParticleContainer particle = new ParticleContainer(de.tobiyas.racesandclasses.vollotile.ParticleEffects.MOBSPAWNER_FLAMES, 1, 0);
	
	
	public FirebreathTrait() {
	}
	
	@TraitEventsUsed()
	@Override
	public void generalInit() {
	}

	@Override
	public String getName() {
		return "FirebreathTrait";
	}

	
	@Override
	protected String getPrettyConfigIntern(){
		return costType.name() + (materialForCasting == null ? " " : (" " + materialForCasting.name() + " ")) + cost;
	}

	
	@TraitInfos(category="magic", traitName="FirebreathTrait", visible=true)
	@Override
	public void importTrait() {
	}

	@TraitConfigurationNeeded(fields = {
			@TraitConfigurationField(classToExpect = Double.class, fieldName = "damage", optional = true),
			@TraitConfigurationField(classToExpect = Integer.class, fieldName = "range", optional = true)
	})
	@Override
	public void setConfiguration(TraitConfiguration configMap)
			throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		if(configMap.containsKey("damage")){
			damage = configMap.getAsDouble("damage");
		}
		
		if(configMap.containsKey("range")){
			range = configMap.getAsInt("range");
		}
	}
	
	
	@Override
	public boolean isBetterThan(Trait trait) {
		if(!(trait instanceof FirebreathTrait)) return false;
		
		FirebreathTrait otherTrait = (FirebreathTrait) trait;
		return cost > otherTrait.cost;
	}

	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "This Trait fires fire infront of you.");
		return helpList;
	}

	
	@Override
	protected void magicSpellTriggered(RaCPlayer player, TraitResults result) {
		Set<Block> blocks = fireLocation(player.getPlayer());
		
		for(Block block : blocks){
			Location loc = block.getLocation();
			for(int i = 0; i < 3; i++) Vollotile.get().sendOwnParticleEffectToAll(particle, loc);
		}
		
		Set<LivingEntity> damageing = SearchEntity.allInLineOfSight(range, player.getPlayer());
		double modDamge = modifyToPlayer(player, damage, "damage");
		
		for(LivingEntity target : damageing){
			if(!EnemyChecker.areAllies(player.getPlayer(), target)){
				double realDamage = PreEntityDamageEvent.getRealDamage(player.getPlayer(), target, DamageCause.MAGIC, modDamge);
				
				de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier.LivingEntity
					.safeDamageEntityByEntity(target, player.getPlayer(), realDamage);
				
			}
		}
		
		result.copyFrom(TraitResults.True());
		return;
	}
	
	
	/**
	 * Calculates the path of the Fire.
	 * 
	 * @param start
	 * @return
	 */
	protected Set<Block> fireLocation(LivingEntity start){
		Set<Block> blocks = new HashSet<Block>();
		
		BlockIterator it = new BlockIterator(start);
		for(int i = 0; i < range; i++){
			blocks.add(it.next());
		}
		
		return blocks;
	}
	
}
