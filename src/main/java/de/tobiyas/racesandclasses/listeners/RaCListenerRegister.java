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
package de.tobiyas.racesandclasses.listeners;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.addins.food.FoodListener;
import de.tobiyas.racesandclasses.listeners.classchangelistener.ClassChangeSelectionListener;
import de.tobiyas.racesandclasses.listeners.equipement.Listener_PlayerEquipChange;
import de.tobiyas.racesandclasses.listeners.equipement.Listener_raceClassRestrictionOnItems;
import de.tobiyas.racesandclasses.listeners.externalchatlistener.DefaultChatReplacer;
import de.tobiyas.racesandclasses.listeners.externalchatlistener.HeroChatListener;
import de.tobiyas.racesandclasses.listeners.externalchatlistener.TownyChatListener;
import de.tobiyas.racesandclasses.listeners.externalchatlistener.VaultChatListener;
import de.tobiyas.racesandclasses.listeners.generallisteners.Listener_GodModeDamagePrevent;
import de.tobiyas.racesandclasses.listeners.generallisteners.Listener_ManaPotion;
import de.tobiyas.racesandclasses.listeners.generallisteners.Listener_MaxHP_Setting;
import de.tobiyas.racesandclasses.listeners.generallisteners.Listener_Player;
import de.tobiyas.racesandclasses.listeners.generallisteners.Listener_PlayerRespawn;
import de.tobiyas.racesandclasses.listeners.generallisteners.Listener_RaceSpawn;
import de.tobiyas.racesandclasses.listeners.generallisteners.Listener_WandAndBowEquip;
import de.tobiyas.racesandclasses.listeners.generallisteners.PlayerLastDamageListener;
import de.tobiyas.racesandclasses.listeners.generallisteners.StunCancelListener;
import de.tobiyas.racesandclasses.listeners.holderchangegui.ClassChangeListenerGui;
import de.tobiyas.racesandclasses.listeners.holderchangegui.RaceChangeListenerGui;
import de.tobiyas.racesandclasses.listeners.racechangelistener.RaceChangeSelectionListener;
import de.tobiyas.racesandclasses.listeners.traitgui.TraitGuiListener;

public class RaCListenerRegister {

	
	/**
	 * Registers all custom listeners for {@link RacesAndClasses}
	 * that are registered to Bukkit Event system.
	 */
	public static void registerCustoms(){
		new ClassChangeSelectionListener();
		new RaceChangeSelectionListener();
		
		new ClassChangeListenerGui();
		new RaceChangeListenerGui();
		
		new TraitGuiListener();
	}
	
	
	/**
	 * Registers all proxys to the internal event system.
	 */
	public static void registerProxys(){
		new Listener_Player();
		new Listener_PlayerEquipChange();
	}
	
	
	/**
	 * Registers all other Listeners that are important
	 */
	public static void registerGeneral(){
		new Listener_GodModeDamagePrevent();
		new Listener_PlayerRespawn();
		new Listener_MaxHP_Setting();
		new StunCancelListener();
		new Listener_WandAndBowEquip();
		new PlayerLastDamageListener();
		new Listener_ManaPotion();
		new Listener_RaceSpawn();
		new FoodListener();
		
		new Listener_raceClassRestrictionOnItems();
	}
	
	/**
	 * Chat Listeners
	 */
	public static void registerChatListeners(){
		new HeroChatListener();
		new DefaultChatReplacer();
		new VaultChatListener();
		new TownyChatListener();
	}
}
