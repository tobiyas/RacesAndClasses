package de.tobiyas.racesandclasses.listeners.generallisteners;

import java.util.Collection;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.classevent.AfterClassChangedEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.classevent.AfterClassSelectedEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.raceevent.AfterRaceChangedEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.raceevent.AfterRaceSelectedEvent;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.TraitJoinLeave;

public class Listener_TraitJoinLeave implements Listener {

	
	public Listener_TraitJoinLeave() {
		RacesAndClasses.getPlugin().registerEvents(this);
	}
	
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void playerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);

		Collection<Trait> traits = TraitHolderCombinder.getAllTraitsOfPlayer(racPlayer);
		for(Trait trait : traits) {
			if(trait instanceof TraitJoinLeave) {
				((TraitJoinLeave) trait).playerJoines(racPlayer);
			}
		}
	}
	
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void playerQuit(PlayerQuitEvent event){
		Player player = event.getPlayer();
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);

		Collection<Trait> traits = TraitHolderCombinder.getAllTraitsOfPlayer(racPlayer);
		for(Trait trait : traits) {
			if(trait instanceof TraitJoinLeave) {
				((TraitJoinLeave) trait).playerLeaves(racPlayer);
			}
		}
	}
	
	
	
	
	@EventHandler
	public void playerChangeClass(AfterClassChangedEvent event){
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(event.getPlayer());
		Collection<Trait> traits = event.getOldClass().getTraits();
		for(Trait trait : traits) {
			if(trait instanceof TraitJoinLeave) {
				((TraitJoinLeave) trait).playerLeaves(racPlayer);
			}
		}

		traits = event.getClassToSelect().getTraits();
		for(Trait trait : traits) {
			if(trait instanceof TraitJoinLeave) {
				((TraitJoinLeave) trait).playerJoines(racPlayer);
			}
		}
	}
	
	@EventHandler
	public void playerSelectClass(AfterClassSelectedEvent event){
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(event.getPlayer());
		Collection<Trait> traits = event.getClassToSelect().getTraits();
		for(Trait trait : traits) {
			if(trait instanceof TraitJoinLeave) {
				((TraitJoinLeave) trait).playerJoines(racPlayer);
			}
		}
	}
	
	@EventHandler
	public void playerChangeRace(AfterRaceChangedEvent event){
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(event.getPlayer());
		Collection<Trait> traits = event.getOldRace().getTraits();
		for(Trait trait : traits) {
			if(trait instanceof TraitJoinLeave) {
				((TraitJoinLeave) trait).playerLeaves(racPlayer);
			}
		}
		
		traits = event.getRaceToSelect().getTraits();
		for(Trait trait : traits) {
			if(trait instanceof TraitJoinLeave) {
				((TraitJoinLeave) trait).playerJoines(racPlayer);
			}
		}
	}
	
	@EventHandler
	public void playerSelectRace(AfterRaceSelectedEvent event){
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(event.getPlayer());
		Collection<Trait> traits = event.getRaceToSelect().getTraits();
		for(Trait trait : traits) {
			if(trait instanceof TraitJoinLeave) {
				((TraitJoinLeave) trait).playerJoines(racPlayer);
			}
		}
	}
	
}
