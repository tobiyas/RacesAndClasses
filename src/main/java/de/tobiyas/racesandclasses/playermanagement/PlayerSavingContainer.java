package de.tobiyas.racesandclasses.playermanagement;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;

@Entity
@Table(name="_player_general_infos")
public class PlayerSavingContainer {

	@Id
	@NotEmpty
	private String playerName;

	@NotNull
	private int playerLevel;
	
	@NotNull
	private int playerLevelExp;
	
	@NotNull
	private boolean hasGod;
	
	
	/**
	 * Generates a new Container with default Values.
	 * 
	 * @param playerName
	 * @return
	 */
	public static PlayerSavingContainer generateNewContainer(String playerName){
		PlayerSavingContainer container = new PlayerSavingContainer();
		container.playerName = playerName;
		
		container.playerLevel = 1;
		container.playerLevelExp = 1;
		
		container.hasGod = false;
		return container;
	}



	public int getPlayerLevel() {
		return playerLevel;
	}



	public void setPlayerLevel(int playerLevel) {
		this.playerLevel = playerLevel;
	}



	public int getPlayerLevelExp() {
		return playerLevelExp;
	}



	public void setPlayerLevelExp(int playerLevelExp) {
		this.playerLevelExp = playerLevelExp;
	}



	public String getPlayerName() {
		return playerName;
	}



	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}



	public boolean isHasGod() {
		return hasGod;
	}



	public void setHasGod(boolean hasGod) {
		this.hasGod = hasGod;
	}
	
}
