package de.tobiyas.racesandclasses.playermanagement.playerdisplay.scoreboard;

public class DisplayTimer {

	/**
	 * When the Display should be hiden.
	 */
	private long whenToHideAgain = -1;
	
	
	/**
	 * If we should infinitely show.
	 */
	private boolean alwaysShow = false;

	
	
	/**
	 * If the Display should be hidden.
	 * 
	 * @return true if should be hidden.
	 */
	public boolean shouldBeHidden(){
		if( alwaysShow ) return false;
		if( whenToHideAgain <= 0) return true;
		
		long now = System.currentTimeMillis();
		if(now > whenToHideAgain) whenToHideAgain = -1;
		
		return whenToHideAgain <= 0;
	}
	
	
	/**
	 * If the Display should be shown always.
	 * 
	 * @param alwaysShow to true if should be shown always.
	 */
	public void setAlwaysShow(boolean alwaysShow){
		this.alwaysShow = alwaysShow;
	}
	
	
	/**
	 * Adds the Hide delay by the time passed (in ms).
	 * 
	 * @param time to add
	 */
	public void addHideDelay(long time){
		this.whenToHideAgain = System.currentTimeMillis() + time;
	}
	
}
