package de.tobiyas.racesandclasses.playermanagement.leveling;

public class LevelPackage {

	/**
	 * The Level for calculation
	 */
	private final int level;
	
	/**
	 * The Maximal EXP needed to level up to the next Level
	 */
	private final int maxEXP;
	
	
	/**
	 * This is a holder for a Level.
	 * It contains the level + the Max EXP of the Level.
	 * 
	 * WARNING: The maxEXP and level can never be smaller than 1.
	 * 
	 * @param level 
	 * @param maxExp
	 */
	public LevelPackage(int level, int maxExp) {
		if(maxExp < 1) maxExp = 1;
		if(level < 1) level = 1;
		
		this.level = level;
		this.maxEXP = maxExp;
	}


	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}


	/**
	 * @return the maxEXP
	 */
	public int getMaxEXP() {
		return maxEXP;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + level;
		result = prime * result + maxEXP;
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
		LevelPackage other = (LevelPackage) obj;
		if (level != other.level)
			return false;
		if (maxEXP != other.maxEXP)
			return false;
		return true;
	}

	
	
}
