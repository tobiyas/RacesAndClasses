package de.tobiyas.racesandclasses.listeners;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.listeners.classchangelistener.ClassChangeSelectionListener;
import de.tobiyas.racesandclasses.listeners.equipement.Listener_PlayerEquipChange;
import de.tobiyas.racesandclasses.listeners.generallisteners.Listener_GodModeDamagePrevent;
import de.tobiyas.racesandclasses.listeners.generallisteners.Listener_Player;
import de.tobiyas.racesandclasses.listeners.generallisteners.Listener_PlayerRespawn;
import de.tobiyas.racesandclasses.listeners.holderchangegui.ClassChangeListenerGui;
import de.tobiyas.racesandclasses.listeners.holderchangegui.RaceChangeListenerGui;

public class RaCListenerRegister {

	
	/**
	 * Registers all custom listeners for {@link RacesAndClasses}
	 * that are registered to Bukkit Event system.
	 */
	public static void registerCustoms(){
		new ClassChangeSelectionListener();
		new ClassChangeListenerGui();
		new RaceChangeListenerGui();
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
	}
}
