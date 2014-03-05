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
package de.tobiyas.racesandclasses.eventprocessing.events.holderevent;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;

/**
 * This Event is fired when the Holder tries to select / change his holder.
 * <br>If it is not cancled, it works.
 * 
 * @author Tobiyas
 *
 */
public class HolderPreSelectEvent extends HolderSelectedEvent implements Cancellable{

	private static HandlerList handlers = new HandlerList();
	
	/**
	 * Tells if the event is canceled or not
	 */
	protected boolean isCancelled;
	
	/**
	 * If the cooldown should be checked.
	 */
	protected final boolean checkCooldown;
	
	/**
	 * Check the Permissions.
	 */
	protected final boolean checkPermissions;

	
	/**
	 * The message that is sent when canceling. 
	 * <br>Needs to be set when canceled.
	 */
	protected String cancelMessage;
	
	/**
	 * This event is an indicator to check if the player can select this holder.
	 * 
	 * @param player to call
	 * @param holderToSelect to call to
	 */
	public HolderPreSelectEvent(Player player,
			AbstractTraitHolder holderToSelect) {
		super(player, holderToSelect);
		
		this.checkPermissions = true;
		this.checkCooldown = true;
	}
	
	/**
	 * This event is an indicator to check if the player can select this holder.
	 * 
	 * @param player to call
	 * @param holderToSelect to call to
	 * 
	 * @param checkCooldown if the cooldown should be checked.
	 * @param checkPermissions if the Permissions should be checked.
	 */
	public HolderPreSelectEvent(Player player,
			AbstractTraitHolder holderToSelect, boolean checkCooldown, boolean checkPermissions) {
		super(player, holderToSelect);
		
		this.checkCooldown = checkCooldown;
		this.checkPermissions = checkPermissions;
	}
	
	@Override
	public boolean isCancelled() {
		return this.isCancelled;
	}


	/**
	 * @deprecated use {@link #setCancelled(String)} instead.
	 */
	@Override
	public void setCancelled(boolean cancel) {
		this.isCancelled = cancel;
	}
	
	
	/**
	 * Cancels the Event + sets a message why it is cancelled.
	 * 
	 * @param message
	 */
	public void setCancelled(String message){
		this.isCancelled = true;
		this.cancelMessage = message;		
	}
	
	public String getCancelMessage() {
		return cancelMessage;
	}
	
	
	public boolean isCheckCooldown() {
		return checkCooldown;
	}

	public boolean isCheckPermissions() {
		return checkPermissions;
	}

	/**
	 * needed for Bukkit to get the list of Handlers interested
	 * @return
	 */
	public static HandlerList getHandlerList() {
        return handlers;
    }
	
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}


}
