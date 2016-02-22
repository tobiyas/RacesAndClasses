package de.tobiyas.racesandclasses.playermanagement.display;

import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;


public class NoneDisplayBar implements Display {

	public NoneDisplayBar(RaCPlayer player, DisplayInfos infos) {
	}
	
	
	@Override
	public void display(double currentHealth, double maxHealth) {}

	@Override
	public void unregister() {}

	
	@Override
	public DisplayType getType() {
		return DisplayType.None;
	}
	
}
