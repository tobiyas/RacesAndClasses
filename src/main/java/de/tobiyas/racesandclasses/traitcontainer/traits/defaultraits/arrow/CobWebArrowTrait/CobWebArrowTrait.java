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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.arrow.CobWebArrowTrait;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.traits.arrows.AbstractArrow;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class CobWebArrowTrait extends AbstractArrow implements Listener {
	
	private int range = 3;
	
	private int seconds = 5;
	
	
	
	public CobWebArrowTrait(){
	}

	
	@TraitEventsUsed(registerdClasses = {})
	@Override
	public void generalInit(){
		Bukkit.getPluginManager().registerEvents(this, (JavaPlugin)plugin);
	}
	
	@Override
	public String getName() {
		return "CobWebArrowTrait";
	}

	@Override
	protected String getPrettyConfigIntern(){
		return "webs " + range + " blocks for" + seconds + " secs.";
	}

	
	
	@TraitConfigurationNeeded(fields = {
			@TraitConfigurationField(fieldName = "range", classToExpect = Integer.class, optional = true),
			@TraitConfigurationField(fieldName = "time", classToExpect = Integer.class, optional = true)
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		if(configMap.containsKey("range")) range = configMap.getAsInt("range");
		if(configMap.containsKey("time")) seconds = configMap.getAsInt("time");
	}

	
	@Override
	protected boolean onShoot(EntityShootBowEvent event){
		return true;
	}
	
	@Override
	protected boolean onHitEntity(EntityDamageByEntityEvent event){
		Entity hitTarget = event.getEntity();
		if(!(hitTarget instanceof LivingEntity)) return false;
		
		webLocation(hitTarget.getLocation());
		return true;
	}

	@Override
	protected String getArrowName(){
		return "CobWeb Arrow";
	}

	@Override
	protected boolean onHitLocation(ProjectileHitEvent event) {
		webLocation(event.getEntity().getLocation());
		return true;
	}

	
	private void webLocation(Location loc){
		final List<Location> toWeb = new LinkedList<Location>();
		
		toWeb.add(loc.getBlock().getRelative(BlockFace.EAST).getLocation());
		toWeb.add(loc.getBlock().getRelative(BlockFace.NORTH).getLocation());
		toWeb.add(loc.getBlock().getRelative(BlockFace.WEST).getLocation());
		toWeb.add(loc.getBlock().getRelative(BlockFace.SOUTH).getLocation());
		toWeb.add(loc);
		
		Iterator<Location> it = toWeb.iterator();
		while(it.hasNext()){
			Location toCheck = it.next();
			if(toCheck.getBlock().getType() == Material.AIR){
				toCheck.getBlock().setType(Material.WEB);
				locs.add(toCheck);
			}else{
				it.remove();
			}
		}
		
		
		Bukkit.getScheduler().scheduleSyncDelayedTask((JavaPlugin)plugin, new Runnable() {
			
			@Override
			public void run() {
				for(Location loc : toWeb){
					if(loc.getBlock().getType() == Material.WEB){
						loc.getBlock().setType(Material.AIR);
						locs.remove(loc);
					}
				}
			}
		}, 20 * seconds);
	}
	
	private List<Location> locs = new LinkedList<Location>();
	
	
	@EventHandler
	public void stopCobWebFarming(BlockBreakEvent event){
		if(locs.contains(event.getBlock().getLocation())){
			event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
		}
		
	}
	
	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "If you hit an enemy with an arrow and choosen the Fire Arrow as current arrow,");
		helpList.add(ChatColor.YELLOW + "He will start burning.");
		return helpList;
	}
	
	
	@TraitInfos(category="arrow", traitName="CobWebArrowTrait", visible=true)
	@Override
	public void importTrait() {
	}

}
