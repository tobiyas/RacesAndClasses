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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.ToolTrait;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.PlayerAction;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.passive.AbstractPassiveTrait;
import de.tobiyas.racesandclasses.translation.languages.Keys;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;
import de.tobiyas.racesandclasses.util.MCPrettyName;

public class ToolTrait extends AbstractPassiveTrait{

	private List<Material> allowed = new LinkedList<Material>();
	private List<Material> forbidden = new LinkedList<Material>();
	
	private boolean allowNonForbidden = true;
	
	public ToolTrait(){
	}
	
	
	@TraitEventsUsed(registerdClasses = {PlayerInteractEvent.class, EntityDamageByEntityEvent.class})
	@Override
	public void generalInit(){
	}
	
	@Override
	public String getName() {
		return "ToolTrait";
	}


	@Override
	protected String getPrettyConfigIntern(){
		String forbidden = "Forbidden: ";
		for(Material forbid : this.forbidden){
			String name = MCPrettyName.getPrettyName(forbid);
			forbidden += Character.toString(name.charAt(0)).toUpperCase() + name.toLowerCase().substring(1) + ", ";
		}
		
		if(forbidden.isEmpty()) forbidden += " None.  ";
		return forbidden.substring(0, forbidden.length() - 2);
	}

	
	@TraitConfigurationNeeded(fields = {
			@TraitConfigurationField(fieldName = "tools", classToExpect = List.class, optional = true),
			@TraitConfigurationField(fieldName = "allowNonForbidden", classToExpect = Boolean.class, optional = true),			
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		forbidden.clear();
		allowed.clear();
		
		if(configMap.containsKey("tools")){
			List<String> toParse = configMap.getAsStringList("tools");
			for(String matName : toParse){
				if(matName == null || matName.isEmpty()) continue;
				boolean notAllowed = matName.charAt(0) == '-';
				matName = matName.replace("+", "").replace("-", "").toUpperCase();
				
				try{
					Material mat = Material.matchMaterial(matName);
					if(mat == null) continue;
						
					if(notAllowed){
						forbidden.add(mat);
					}else{
						allowed.add(mat);
					}
				}catch(Throwable exp){ continue; }
			}
		}
		
		if(configMap.containsKey("allowNonForbidden")){
			allowNonForbidden = configMap.getAsBool("allowNonForbidden");
		}
	}
	
	
	@Override
	public TraitResults trigger(EventWrapper eventWrapper) {   
		ItemStack interactingWith = null;

		PlayerInventory inv = eventWrapper.getPlayer().getInventory();
		if(eventWrapper.getPlayer() != null) interactingWith = inv.getItem(inv.getHeldItemSlot());
		if(interactingWith == null) return TraitResults.False();
		
		if(isOnForbidList(interactingWith)) {
			Cancellable event = (Cancellable)eventWrapper.getEvent();
			event.setCancelled(true);
			
			LanguageAPI.sendTranslatedMessage(eventWrapper.getPlayer(), Keys.trait_tool_trait_fail);
			return TraitResults.False();
		}
		
		
		if(!isOnAllowList(interactingWith) && !allowNonForbidden) {
			Cancellable event = (Cancellable)eventWrapper.getEvent();
			event.setCancelled(true);
			
			LanguageAPI.sendTranslatedMessage(eventWrapper.getPlayer(), Keys.trait_tool_trait_fail);
			return TraitResults.False();
		}
		
		return TraitResults.True();
	}
	
	
	/**
	 * Checks if the Tool is on the allowed List.
	 * 
	 * @param interactingWith
	 * @return
	 */
	private boolean isOnAllowList(ItemStack interactingWith) {
		if(allowNonForbidden) return false;
		
		Material mat = interactingWith.getType();
		if(allowed.contains(mat)) return false;
		
		return true;
	}


	/**
	 * Checks if the tool is on the forbidlist.
	 * 
	 * @param interactingWith
	 * @return
	 */
	private boolean isOnForbidList(ItemStack interactingWith) {
		return forbidden.contains(interactingWith.getType());
	}


	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.RED + "Nothing to see here yet.");
		return helpList;
	}
	
	
	@Override
	public boolean isBetterThan(Trait trait) {
		return true;
	}

	@TraitInfos(category="passive", traitName="ToolTrait", visible=true)
	@Override
	public void importTrait() {
	}


	@Override
	public boolean canBeTriggered(EventWrapper wrapper){
		PlayerAction action = wrapper.getPlayerAction();
		if(action == PlayerAction.DO_DAMAGE || action == PlayerAction.INTERACT_BLOCK || action == PlayerAction.INTERACT_AIR || action == PlayerAction.INTERACT_ENTITY) return true;
		return false;
	}
}
