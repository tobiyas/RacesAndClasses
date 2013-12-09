package de.tobiyas.racesandclasses.playermanagement.display;

import de.tobiyas.racesandclasses.RacesAndClasses;

public class SpellScoreBoardDisplay extends AbstractDisplay {

	public SpellScoreBoardDisplay(String playerName, DisplayInfos displayInfo) {
		super(playerName, displayInfo);

	
	}

	@Override
	public void display(double currentHealth, double maxHealth) {
		if(RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().isConfig_disableAllScoreboardOutputs()){
			//server does not want ANY Scoreboards from RaC. 
			//Plugin is sad. :(
			return;
		}
		
		
		
	}

}
