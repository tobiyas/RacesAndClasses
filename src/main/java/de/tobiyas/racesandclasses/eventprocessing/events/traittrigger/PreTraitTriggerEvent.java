package de.tobiyas.racesandclasses.eventprocessing.events.traittrigger;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.TraitRestriction;

public class PreTraitTriggerEvent extends PlayerEvent implements Cancellable {
	
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
	
	
	protected TraitRestriction restriction = TraitRestriction.None;
	
	
	/**
	 * This is a custom event fired when a Trait is triggered.
	 * The world passed is the location the trait is triggered.
	 * 
	 * 
	 * @param world
	 */
	public PreTraitTriggerEvent(Player player, Trait trait, World world) {
		super(player);
		
		this.trait = trait;
		this.world = world;
	}
	
	
	/**
	 * Builds this from a event wrapper.
	 * 
	 * @param wrapper to build from
	 */
	public PreTraitTriggerEvent(EventWrapper wrapper, Trait trait){
		super(wrapper.getPlayer().getPlayer());
		
		this.trait = trait;
		this.world = wrapper.getWorld();
	}
	    

	public World getWorld() {
		return world;
	}
	
	public Trait getTriggeredTrait(){
		return trait;
	}
	
	
	//needed for custom events.
	@Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }


	@Override
	public boolean isCancelled() {
		return restriction != TraitRestriction.None;
	}


	@Override
	public void setCancelled(boolean cancel) {
		restriction = TraitRestriction.Unknown;
	}
	
	public void setCancelled(TraitRestriction restriction) {
		this.restriction = restriction;
	}

	public TraitRestriction getRestriction() {
		return restriction;
	}
	
}
