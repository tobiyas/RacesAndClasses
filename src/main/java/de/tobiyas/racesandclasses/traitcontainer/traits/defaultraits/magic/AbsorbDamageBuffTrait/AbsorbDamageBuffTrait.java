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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.AbsorbDamageBuffTrait;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.pattern.AbstractBuffTrait;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier.EntityDamage;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class AbsorbDamageBuffTrait extends AbstractBuffTrait{
	
	/**
	 * The Material for 'DamageCauses.'
	 */
	private final Set<DamageCause> types = new HashSet<DamageCause>();
	
	/**
	 * The Absorb Value.
	 */
	private double value = 1;
	
	/**
	 * The Map of absorbing for a player.
	 */
	private final Map<RaCPlayer,Double> absorbMap = new HashMap<RaCPlayer, Double>();
	
	
	public AbsorbDamageBuffTrait(){
	}
	
	@TraitEventsUsed()
	@Override
	public void generalInit(){
	}

	@Override
	public String getName() {
		return "AbsorbDamageBuffTrait";
	}


	@Override
	protected String getPrettyConfigIntern(){
		return types + " " + value;
	}

	@TraitConfigurationNeeded( fields = {
			@TraitConfigurationField(fieldName = "types", classToExpect = List.class, optional = false), 
			@TraitConfigurationField(fieldName = "value", classToExpect = Double.class, optional = true), 
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		List<String> typeNames = configMap.getAsStringList("types");
		for(String type : typeNames){
			if(type.equalsIgnoreCase("all")) for(DamageCause cause : DamageCause.values()) types.add(cause);
			DamageCause cause = null;
			try{ cause = DamageCause.valueOf(type.toUpperCase()); }catch(IllegalArgumentException exp){}
			if(cause != null) this.types.add(cause);
		}
		
		if(configMap.containsKey("value")){
			value = configMap.getAsDouble("value");
		}
	}
	

	@Override
	public boolean isBetterThan(Trait trait) {
		return true;
	}

	@TraitInfos(category="magic", traitName="AbsorbDamageBuffTrait", visible=true)
	@Override
	public void importTrait() {
	}
	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "This buff increases your next hit with the desired weapon.");
		return helpList;
	}

	@EventHandler(priority=EventPriority.HIGH)
	public void entityDamage(EntityDamageByEntityEvent event){
		if(!types.contains(event.getCause())) return;
		
		Entity damaged = event.getEntity();
		if(!(damaged instanceof Player)) return;
		
		RaCPlayer damagee = RaCPlayerManager.get().getPlayer((Player)event.getEntity());
		if(!absorbMap.containsKey(damagee)) return;
		if(isActive(damagee)){
			double damage = EntityDamage.safeGetDamage(event);
			double absorbLeft = absorbMap.remove(damagee);
			
			damage -= absorbLeft;
			absorbLeft = 0;
			
			if(damage < 0) {
				absorbLeft = -damage;
				damage = 0;
				
				absorbMap.put(damagee, absorbLeft);
			}else{
				buffActivated(damagee);				
			}

			EntityDamage.safeSetDamage(damage, event);
		}
	}

	
	@Override
	protected void buffActivated(RaCPlayer player) {
		double modified = modifyToPlayer(player, value, "value");
		absorbMap.put(player, modified);
	}

	@Override
	protected void buffTimeouted(RaCPlayer player) {
		absorbMap.remove(player);
	}
	
	
}
