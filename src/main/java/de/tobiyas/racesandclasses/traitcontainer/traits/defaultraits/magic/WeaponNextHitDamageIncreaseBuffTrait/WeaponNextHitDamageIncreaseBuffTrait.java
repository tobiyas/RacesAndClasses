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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.WeaponNextHitDamageIncreaseBuffTrait;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.TraitHolderCombinder;
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

public class WeaponNextHitDamageIncreaseBuffTrait extends AbstractBuffTrait implements Listener{
	
	/**
	 * The Material for 'Weapons.'
	 */
	private final Set<Material> weaponMats = new HashSet<Material>();
	
	/**
	 * The Additional damage.
	 */
	private double damage = 1;
	
	
	public WeaponNextHitDamageIncreaseBuffTrait(){
		Bukkit.getPluginManager().registerEvents(this, (Plugin)RacesAndClasses.getPlugin());
	}
	
	@TraitEventsUsed()
	@Override
	public void generalInit(){
	}

	@Override
	public String getName() {
		return "WeaponNextHitDamageIncreaseBuffTrait";
	}


	@Override
	protected String getPrettyConfigIntern(){
		return weaponMats + " " + damage;
	}

	@TraitConfigurationNeeded( fields = {
			@TraitConfigurationField(fieldName = "weapons", classToExpect = List.class, optional = false), 
			@TraitConfigurationField(fieldName = "damage", classToExpect = Double.class, optional = true), 
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		List<String> weapons = configMap.getAsStringList("weapons");
		for(String weapon : weapons){
			Material mat = Material.matchMaterial(weapon);
			if(mat != null) this.weaponMats.add(mat);
		}
		
		if(configMap.containsKey("damage")){
			damage = configMap.getAsDouble("damage");
		}
	}
	

	@Override
	public boolean isBetterThan(Trait trait) {
		return true;
	}

	@TraitInfos(category="magic", traitName="WeaponNextHitDamageIncreaseBuffTrait", visible=true)
	@Override
	public void importTrait() {
	}
	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "This buff increases your next hit with the desired weapon.");
		return helpList;
	}

	@EventHandler
	public void entityDamage(EntityDamageByEntityEvent event){
		if(event.getCause() != DamageCause.ENTITY_ATTACK) return;

		Entity damagerEntity = event.getDamager();
		if(!(damagerEntity instanceof Player)) return;
		
		RaCPlayer damager = RaCPlayerManager.get().getPlayer((Player)event.getDamager());
		if(!TraitHolderCombinder.checkContainer(damager, this)) return;
		
		if(isActive(damager)){
			ItemStack inHand = damager.getPlayer().getItemInHand();
			if(inHand == null) return;
			if(!weaponMats.contains(inHand.getType())) return;
			
			double damage = EntityDamage.safeGetDamage(event);
			damage += modifyToPlayer(damager, this.damage, "damage");
			
			EntityDamage.safeSetDamage(damage, event);
			buffUsed(damager);
			
			damager.sendMessage(ChatColor.GREEN + "~~ HARD HIT ~~");
		}
	}

	
	@Override
	protected void buffActivated(RaCPlayer arg0) {
	}

	@Override
	protected void buffTimeouted(RaCPlayer arg0) {
	}
	
	
}
