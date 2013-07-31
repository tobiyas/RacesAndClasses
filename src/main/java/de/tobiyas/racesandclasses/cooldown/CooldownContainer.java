package de.tobiyas.racesandclasses.cooldown;

public class CooldownContainer {

	/**
	 * The player this cooldown container belongs
	 */
	private final String playerName;
	
	/**
	 * The cooldown name
	 */
	private final String cooldownName;
	
	
	/**
	 * The time the cooldown still takes
	 */
	private int cooldownTime;
	
	
	
	/**
	 * Creates a new Cooldown container with the passed properties.
	 * 
	 * @param playerName
	 * @param cooldownName
	 * @param cooldownTime
	 */
	public CooldownContainer(String playerName, String cooldownName, int cooldownTime) {
		this.playerName = playerName;
		this.cooldownName = cooldownName;
		this.cooldownTime = cooldownTime;
	}

	
	/**
	 * ticks the container to reduce the time by 1
	 */
	public void tick(){
		cooldownTime--;
	}


	public String getPlayerName() {
		return playerName;
	}


	public String getCooldownName() {
		return cooldownName;
	}


	public int getCooldownTime() {
		return cooldownTime;
	}


	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((cooldownName == null) ? 0 : cooldownName.hashCode());
		result = prime * result + cooldownTime;
		result = prime * result
				+ ((playerName == null) ? 0 : playerName.hashCode());
		return result;
	}


	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CooldownContainer other = (CooldownContainer) obj;
		if (cooldownName == null) {
			if (other.cooldownName != null)
				return false;
		} else if (!cooldownName.equals(other.cooldownName))
			return false;
		if (cooldownTime != other.cooldownTime)
			return false;
		if (playerName == null) {
			if (other.playerName != null)
				return false;
		} else if (!playerName.equals(other.playerName))
			return false;
		return true;
	}
	
	
	@Override
	public String toString(){
		return playerName + " has: " + cooldownTime + " on:" + cooldownName;
	}
	
}
