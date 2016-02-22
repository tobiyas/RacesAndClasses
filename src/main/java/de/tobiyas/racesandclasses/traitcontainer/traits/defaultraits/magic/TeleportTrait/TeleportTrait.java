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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.TeleportTrait;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.player.PlayerInteractEvent;

import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.magic.AbstractMagicSpellTrait;
import de.tobiyas.racesandclasses.translation.languages.Keys;

public class TeleportTrait extends AbstractMagicSpellTrait  {

	@TraitEventsUsed(registerdClasses = {PlayerInteractEvent.class})
	@Override
	public void generalInit() {
	}

	@Override
	public String getName() {
		return "TeleportTrait";
	}

	
	@Override
	protected String getPrettyConfigIntern(){
		return "Mana: " + cost;
	}

	
	@TraitInfos(category="magic", traitName="TeleportTrait", visible=true)
	@Override
	public void importTrait() {
	}

	
	@Override
	public boolean isBetterThan(Trait trait) {
		if(!(trait instanceof TeleportTrait)) return false;
		
		TeleportTrait otherTrait = (TeleportTrait) trait;
		return cost > otherTrait.cost;
	}

	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "This trait teleports you to the place your wand is pointing.");
		return helpList;
	}


	@SuppressWarnings("deprecation")
	@Override
	protected void magicSpellTriggered(RaCPlayer player, TraitResults result) {
		Block toTeleportTo = player.getPlayer().getTargetBlock((HashSet<Byte>)null, 100);
		
		if(toTeleportTo == null){
			LanguageAPI.sendTranslatedMessage(player, Keys.no_taget_found);
			result.setTriggered(false);
			return;
		}
		
		
		for(int i = 0; i <= 2; i++){
			if(toTeleportTo.getRelative(BlockFace.UP).getType().isSolid()
					|| toTeleportTo.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getType().isSolid()){
				
				LanguageAPI.sendTranslatedMessage(player, Keys.trait_teleport_solidtarger);
				result.setTriggered(false);
				return;
			}			
		}
		
		player.getPlayer().teleport(toTeleportTo.getRelative(BlockFace.UP).getLocation());
		LanguageAPI.sendTranslatedMessage(player, Keys.trait_teleport_success);
		player.sendMessage(ChatColor.GREEN + "[RaC] Teleported.");
		
		result.setTriggered(true);
		return;
	}

}
