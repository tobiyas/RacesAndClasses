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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.PermissionTrait;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.classevent.AfterClassSelectedEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.raceevent.AfterRaceSelectedEvent;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.RemoveSuperConfigField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.traits.pattern.TickEverySecondsTrait;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;
import de.tobiyas.util.permissions.PermissionManager;

public class PermissionTrait extends TickEverySecondsTrait implements Listener {

	/**
	 * The permissions to give
	 */
	private final List<String> permissions = new LinkedList<String>();
	
	private final List<RaCPlayer> playersWithPerms = new LinkedList<RaCPlayer>();
	

	@Override
	public String getName() {
		return "PermissionTrait";
	}


	@TraitConfigurationNeeded( fields = {
			@TraitConfigurationField(fieldName = "permissions", classToExpect = List.class, optional = true),
			@TraitConfigurationField(fieldName = "permission", classToExpect = String.class, optional = true),
		}, removedFields = {
			@RemoveSuperConfigField(name = "seconds")
	})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		configMap.put("seconds", (Integer)1);
		super.setConfiguration(configMap);
		
		if(configMap.containsKey("permission")){
			permissions.add(configMap.getAsString("permission"));
		}
		
		if(configMap.containsKey("permissions")){
			permissions.addAll(configMap.getAsStringList("permissions"));
		}
		
		if(!permissions.isEmpty()){
			//TODO maybe fix this.
			Bukkit.getPluginManager().registerEvents(this, plugin);
		}
	}

	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "This trait Sets a permission.");
		return helpList;
	}
	

	@TraitInfos(category = "passive", traitName = "PermissionTrait", visible = true)
	@Override
	public void importTrait() {
	}

	
	
	@Override
	protected boolean tickDoneForPlayer(RaCPlayer player) {
		checkPerms(player, true);
		return false;
	}
	
	
	@Override
	protected void restrictionsFailed(RaCPlayer player) {
		checkPerms(player, false);
	}
	
	@EventHandler
	public void playerLogout(PlayerQuitEvent event){
		RaCPlayer player = RaCPlayerManager.get().getPlayer(event.getPlayer());
		checkPerms(player, false);
	}
	
	@EventHandler
	public void playerLeaveHolder(AfterClassSelectedEvent event){
		RaCPlayer player = RaCPlayerManager.get().getPlayer(event.getPlayer());
		checkPerms(player, false);
	}

	@EventHandler
	public void playerLeaveHolder(AfterRaceSelectedEvent event){
		RaCPlayer player = RaCPlayerManager.get().getPlayer(event.getPlayer());
		checkPerms(player, false);
	}
	
	@Override
	public void deInit() {
		super.deInit();
		for(RaCPlayer player : playersWithPerms){
			if(player.isOnline()){
				checkPerms(player, false);
			}
		}
	}
	
	
	/**
	 * Checks and handles permissions.
	 */
	protected void checkPerms(RaCPlayer player, boolean add){
		if(add){
			if(!playersWithPerms.contains(player)){
				playersWithPerms.add(player);
				
				for(String perm : permissions){
					PermissionManager permManager = plugin.getPermissionManager();
					permManager.addPermission(player.getPlayer(), perm);
				}
			}
		}else{
			if(playersWithPerms.contains(player)){
				playersWithPerms.remove(player);
				
				for(String perm : permissions){
					PermissionManager permManager = plugin.getPermissionManager();
					permManager.removePermission(player.getPlayer(), perm);
				}
			}
		}
	}
	
	

	@Override
	protected String getPrettyConfigurationPre() {
		return "";
	}
	
	
	@Override
	protected String getPrettyConfigIntern() {
		String perms = "";
		Iterator<String> it = permissions.iterator();
		while(it.hasNext()){
			perms += it.next();
			if(it.hasNext()) perms += ",";
		}
		
		return "permission: " + perms;
	}
	
	
	@Override
	public boolean isStackable(){
		return true;
	}
}
