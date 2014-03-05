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
package de.tobiyas.racesandclasses.eventprocessing.events.chatevent;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.tobiyas.racesandclasses.chat.channels.container.ChannelContainer;

public class PlayerSendChannelChatMessageEvent extends Event implements Cancellable{


	/**
	 * The static list of all handlers that are interested in this event
	 */
	private static final HandlerList handlers = new HandlerList();
	
	/**
	 * Player that sends the message
	 */
	private final Player player;
	
	/**
	 * The Channel the message is sent to
	 */
	private final ChannelContainer channel;
	
	/**
	 * The message that is sent
	 */
	private String message;
	
	/**
	 * Says if the event is canceled or not.
	 */
	private boolean isCanceled;
	
	/**
	 * A Player sends a message to a channel.
	 * 
	 * @param player that sends the message
	 * @param channel that the message is sent to
	 * @param message
	 */
	public PlayerSendChannelChatMessageEvent(Player player, ChannelContainer channel, String message) {
		super(true);
		
		this.player = player;
		this.channel = channel;
		this.message = message;
		this.isCanceled = false;
	}


	public static HandlerList getHandlerList() {
        return handlers;
    }
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public Player getPlayer() {
		return player;
	}


	public ChannelContainer getChannel() {
		return channel;
	}


	public String getMessage() {
		return message;
	}


	@Override
	public boolean isCancelled() {
		return isCanceled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.isCanceled = cancel;
	}

}
