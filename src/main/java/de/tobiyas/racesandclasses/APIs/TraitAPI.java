/*******************************************************************************
 * Copyright 2014 Tobias Welther
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
package de.tobiyas.racesandclasses.APIs;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;

public class TraitAPI {

	/**
	 * Checks if the player has the Trait with the following Name.
	 * <br>If false, plugin is not found or the Player does not have the Trait.
	 * 
	 * @param player to check
	 * @param traitName to check
	 * 
	 * @return true if has the Trait.
	 * 
	 * @deprecated use {@link #hasTrait(OfflinePlayer, String)} instead
	 */
	@Deprecated
	public static boolean hasTrait(String playerName, String traitName){
		return hasTrait(Bukkit.getPlayer(playerName), traitName);
	}

	
	/**
	 * Checks if the player has the Trait with the following Display Name.
	 * <br>If false, plugin is not found or the Player does not have the Trait.
	 * 
	 * @param player to check
	 * @param displayName to check
	 * 
	 * @return true if has the Trait.
	 * 
	 * @deprecated use {@link #hasTraitWithDisplayName(OfflinePlayer, String)} instead
	 */
	@Deprecated
	public static boolean hasTraitWithDisplayName(String playerName, String displayName){
		return hasTraitWithDisplayName(Bukkit.getPlayer(playerName), displayName);
	}
	
	/**
	 * Checks if the player has the Trait with the following Name.
	 * <br>If false, plugin is not found or the Player does not have the Trait.
	 * 
	 * @param player to check
	 * @param traitName to check
	 * 
	 * @return true if has the Trait.
	 */
	public static boolean hasTrait(Player player, String traitName){
		if(player == null || traitName == null) return false;
		traitName = traitName.replace(" ", "");
		
		try{
			RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
			Set<Trait> traits = TraitHolderCombinder.getAllTraitsOfPlayer(racPlayer);
			//no traits -> doesn't have Trait.
			if(traits == null || traits.isEmpty()) return false;
			
			for(Trait trait : traits){
				String realTraitName = trait.getName().replace(" ", "");
				if(realTraitName.equalsIgnoreCase(traitName)){
					return true;
				}
			}
			
			return false;
		}catch(Throwable exp){ return false; }
	}
	
	
	/**
	 * Checks if the player has the Trait with the following Display Name.
	 * <br>If false, plugin is not found or the Player does not have the Trait.
	 * 
	 * @param player to check
	 * @param displayName to check
	 * 
	 * @return true if has the Trait.
	 */
	public static boolean hasTraitWithDisplayName(Player player, String displayName){
		if(player == null || displayName == null) return false;
		
		try{
			RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
			Set<Trait> traits = TraitHolderCombinder.getAllTraitsOfPlayer(racPlayer);
			//no traits -> doesn't have Trait.
			if(traits == null || traits.isEmpty()) return false;
			
			for(Trait trait : traits){
				if(trait.getDisplayName().equalsIgnoreCase(displayName)){
					return true;
				}
			}
			
			return false;
		}catch(Throwable exp){ return false; }
	}
}
