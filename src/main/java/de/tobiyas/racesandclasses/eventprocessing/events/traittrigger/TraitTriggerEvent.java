package de.tobiyas.racesandclasses.eventprocessing.events.traittrigger;

import org.bukkit.World;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;

public class TraitTriggerEvent extends Event implements Cancellable{
	
	private boolean isCancled = false;
	
	/**
	 * Some strange list. Whatever it tells...
	 */
	private static final HandlerList handlers = new HandlerList();
	
	/**
	 * World the event triggered on.
	 */
	protected World world;
	
	
	/**
	 * The trait triggered.
	 */
	protected final Trait trait;
	
	
	/**
	 * This is a custom event fired when a Trait is triggered.
	 * The world passed is the location the trait is triggered.
	 * 
	 * 
	 * @param world
	 */
	public TraitTriggerEvent(Trait trait, World world) {
		this.trait = trait;
		this.world = world;
	}
	    

	public World getWorld() {
		return world;
	}
	
	public Trait getTriggeredTrait(){
		return trait;
	}
	

	@Override
	public boolean isCancelled() {
		return isCancled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		isCancled = cancel;		
	}
	
	
	//needed for custom events.
	@Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
