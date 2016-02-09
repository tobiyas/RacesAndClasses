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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.WallTrait;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.magic.AbstractContinousCostMagicSpellTrait;
import de.tobiyas.racesandclasses.translation.languages.Keys;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class WallTrait extends AbstractContinousCostMagicSpellTrait implements Listener  {

	/**
	 * Material of the Wall
	 */
	private Material material = Material.GLASS;
	
	/**
	 * Old wall blocks
	 */
	private Map<String,OldWallBlocks> wallBlocks = new HashMap<String,OldWallBlocks>();
	
	
	@TraitEventsUsed(registerdClasses = {PlayerInteractEvent.class})
	@Override
	public void generalInit() {
	}

	@Override
	public String getName() {
		return "WallTrait";
	}

	
	@Override
	protected String getPrettyConfigIntern(){
		int time = everyXSeconds <= 0 ? durationInSeconds : everyXSeconds;
		return "duration: " + time + " seconds. Mana: " + cost;
	}

	
	@TraitInfos(category="magic", traitName="WallTrait", visible=true)
	@Override
	public void importTrait() {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	
	@Override
	public boolean isBetterThan(Trait trait) {
		if(!(trait instanceof WallTrait)) return false;
		
		WallTrait otherTrait = (WallTrait) trait;
		int time = everyXSeconds <= 0 ? durationInSeconds : everyXSeconds;
		int otherTime = otherTrait.everyXSeconds <= 0 ? otherTrait.durationInSeconds : otherTrait.everyXSeconds;
		return time > otherTime;
	}

	
	@TraitConfigurationNeeded(fields = {
			@TraitConfigurationField(fieldName = "material", classToExpect = String.class, optional = true)
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		this.material = Material.GLASS;
		if(configMap.containsKey("material")){
			String material = (String) configMap.get("material");
			Material castedMaterial = Material.getMaterial(material);
			if(castedMaterial != null && castedMaterial.isBlock()){
				this.material = castedMaterial;
			}
		}
	}


	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "Castes a Wall infront of you.");
		return helpList;
	}


	@Override
	protected boolean activateIntern(RaCPlayer player) {
		@SuppressWarnings("deprecation")
		List<Block> blocks = player.getPlayer().getLineOfSight(new HashSet<Byte>(), 3);
		Location location = blocks.get(blocks.size() - 1).getLocation();
		
		Location from = location.clone();
		Location to = location.clone();
		
		BlockFace face = getDirection(player.getLocation().getYaw(), player.getLocation().getPitch());
		
		switch (face){
			case NORTH : from.add(-2, 1, -2); to.add(2, -1, -2);
				break;
			case EAST : from.add(2, 1, -2); to.add(2, -1, 2);
				break;
			case SOUTH : from.add(-2, 1, 2); to.add(2, -1, 2);
				break;
			case WEST : from.add(-2, 1, -2); to.add(-2, -1, 2);
				break;
			case UP : from.add(-2, 2, -2); to.add(2, 2, 2);
				break;
			case DOWN : from.add(-2, -2, -2); to.add(2, -2, 2);
				break;
				
			default:
				break;
		}
		
		OldWallBlocks oldBlocks = new OldWallBlocks(from, to, material);
		this.wallBlocks.put(player.getName(), oldBlocks);
		
		LanguageAPI.sendTranslatedMessage(player, Keys.trait_wall_success);
		
		return true;
	}
	

	@Override
	protected boolean deactivateIntern(RaCPlayer player){
		OldWallBlocks blocks = wallBlocks.get(player.getName());
		if(blocks != null){
			blocks.removeWall();
		}
		
		LanguageAPI.sendTranslatedMessage(player, Keys.trait_wall_faded);
		return true;
	}
	
	
	@EventHandler
	public void onPlayerBreakBlock(BlockBreakEvent event){
		Block block = event.getBlock();
		
		for(OldWallBlocks old : wallBlocks.values()){
			if(old.contains(block.getLocation())){
				event.setCancelled(true);
				
				old.remove(block);
				break;
			}
		}
	}
	
	
	private BlockFace getDirection(float yaw, float pitch){
		if(pitch < -65) return BlockFace.UP;
		if(pitch > 65) return BlockFace.DOWN;
		
	    yaw = yaw / 90;
	    yaw = (float)Math.round(yaw);
	 
	    if (yaw == -4 || yaw == 0 || yaw == 4) {return BlockFace.SOUTH;}
	    if (yaw == -1 || yaw == 3) {return BlockFace.EAST;}
	    if (yaw == -2 || yaw == 2) {return BlockFace.NORTH;}
	    if (yaw == -3 || yaw == 1) {return BlockFace.WEST;}
	    
	    return BlockFace.NORTH;
	}

	@Override
	protected boolean tickInternal(RaCPlayer player) {
		//nothing to tick.
		return true;
	}

}
