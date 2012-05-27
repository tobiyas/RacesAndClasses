package de.tobiyas.races.datacontainer.traitcontainer.traits.health;

public class HealthModifyContainer {

	private double amount;
	private String player;
	private String op;
	
	public HealthModifyContainer(String player, double amount, String op){
		this.player = player;
		this.amount = amount;
		this.op = op;
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
}
