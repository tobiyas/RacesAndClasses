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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.LightningTrait;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;

import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.magic.AbstractMagicSpellTrait;
import de.tobiyas.racesandclasses.translation.languages.Keys;
import de.tobiyas.racesandclasses.util.damage.PreEntityDamageEvent;
import de.tobiyas.racesandclasses.util.entitysearch.SearchEntity;
import de.tobiyas.racesandclasses.util.friend.EnemyChecker;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class LightningTrait extends AbstractMagicSpellTrait  {

	private double damage = 3;
	private double maxDist = 10;
	
	
	@TraitEventsUsed(registerdClasses = {PlayerInteractEvent.class})
	@Override
	public void generalInit() {
	}

	@Override
	public String getName() {
		return "LightningTrait";
	}

	
	@Override
	protected String getPrettyConfigIntern(){
		return "Mana: " + cost;
	}

	
	@TraitInfos(category="magic", traitName="LightningTrait", visible=true)
	@Override
	public void importTrait() {
	}
	
	
	@TraitConfigurationNeeded(fields = {
			@TraitConfigurationField(fieldName = "damage", classToExpect = Double.class, optional = true),
			@TraitConfigurationField(fieldName = "distance", classToExpect = Double.class, optional = true)
	})
	@Override
	public void setConfiguration(TraitConfiguration configMap)
			throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		damage = configMap.getAsDouble("damage", 3);
		maxDist = configMap.getAsDouble("distance", 10);
	}

	
	@Override
	public boolean isBetterThan(Trait trait) {
		if(!(trait instanceof LightningTrait)) return false;
		
		LightningTrait otherTrait = (LightningTrait) trait;
		return cost > otherTrait.cost;
	}

	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "This trait triggers a lightning to your target.");
		return helpList;
	}


	@Override
	protected void magicSpellTriggered(RaCPlayer player, TraitResults result) {
		LivingEntity target = SearchEntity.inLineOfSight((int) maxDist, player.getPlayer());
		
		//No target! :(
		if(target == null){
			result.copyFrom(TraitResults.False());
			LanguageAPI.sendTranslatedMessage(player, Keys.no_taget_found);
			return;
		}
		
		//Check if ally:
		if(EnemyChecker.areAllies(player, target)){
			LanguageAPI.sendTranslatedMessage(player, Keys.restrictions_not_met_TargetFriendly);
			result.copyFrom(TraitResults.False());
			return;
		}
		
		//It works! Do damage!
		Location targetLocation = target.getLocation();
		double modDamage = modifyToPlayer(player, damage, "damage");
		double realDamage = PreEntityDamageEvent.getRealDamage(player.getPlayer(), target, DamageCause.MAGIC, modDamage);
		
		targetLocation.getWorld().strikeLightningEffect(targetLocation);
		target.damage(realDamage, player.getPlayer());
		
		result.copyFrom(TraitResults.True());
		return;
	}
	
	
	/**
	 * Gets all Entities in a radius of the location.
	 * 
	 * Stolen from: 'https://forums.bukkit.org/threads/getnearbyentities-of-a-location.101499/#post-1341141'
	 * 
	 * @param location
	 * @param radius
	 * @return set of all Entities near
	 */
	public static List<LivingEntity>  getNearbyEntities(Location location, int radius){
        int chunkRadius = radius < 16 ? 1 : (radius - (radius % 16))/16;
        List<LivingEntity> radiusEntities = new LinkedList<LivingEntity>();
        for (int chX = 0 -chunkRadius; chX <= chunkRadius; chX ++){
            
        	for (int chZ = 0 -chunkRadius; chZ <= chunkRadius; chZ++){
                int x=(int) location.getX(),y=(int) location.getY(),z=(int) location.getZ();
                
                for (Entity e : new Location(location.getWorld(),x+(chX*16),y,z+(chZ*16)).getChunk().getEntities()){
                	if(!(e instanceof LivingEntity)) continue;
                	
                    if (e.getLocation().distance(location) <= radius && e.getLocation().getBlock() != location.getBlock()){
                    	radiusEntities.add((LivingEntity) e);
                    }
                }
            }
        }
            
        return radiusEntities;
    }

}
