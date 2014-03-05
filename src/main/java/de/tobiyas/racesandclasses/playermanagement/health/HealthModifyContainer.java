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
package de.tobiyas.racesandclasses.playermanagement.health;

import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class HealthModifyContainer {

	private double amount;
	private String player;
	private String op;
	private DamageCause cause;
	
	public HealthModifyContainer(String player, double amount, String op){
		this.player = player;
		this.amount = amount;
		this.op = op;
		this.cause = null;
	}
	
	public HealthModifyContainer(String player, double amount, String op, DamageCause cause){
		this.player = player;
		this.amount = amount;
		this.op = op;
		this.cause = cause;
	}
	
	public String getPlayer(){
		return player;
	}
	
	public double getAmount(){
		return amount;
	}
	
	public String getOperation(){
		return op;
	}

	public DamageCause getCause() {
		return cause;
	}
}
