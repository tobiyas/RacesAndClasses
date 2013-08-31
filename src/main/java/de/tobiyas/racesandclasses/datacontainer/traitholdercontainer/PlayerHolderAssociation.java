package de.tobiyas.racesandclasses.datacontainer.traitholdercontainer;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotEmpty;

@Entity
@Table(name="_player_holders")
public class PlayerHolderAssociation {

	@Id
	@NotEmpty
	private String playerName;
		
	private String className;
	
	@NotEmpty
	private String raceName;

	
	
	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getRaceName() {
		return raceName;
	}

	public void setRaceName(String raceName) {
		this.raceName = raceName;
	}	
	
}
