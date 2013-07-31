package de.tobiyas.racesandclasses.healthmanagement;

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
