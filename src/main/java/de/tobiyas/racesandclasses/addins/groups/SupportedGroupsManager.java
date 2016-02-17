package de.tobiyas.racesandclasses.addins.groups;

import de.tobiyas.racesandclasses.addins.groups.impl.DisabledGroupManager;
import de.tobiyas.racesandclasses.addins.groups.impl.HeroesGroupManager;
import de.tobiyas.racesandclasses.addins.groups.impl.McMMOGroupManager;
import de.tobiyas.racesandclasses.addins.groups.impl.OwnGroupManager;
import de.tobiyas.racesandclasses.addins.groups.impl.PartiesGroupManager;

public enum SupportedGroupsManager {
	None,
	RaC,
	Heroes,
	McMMO,
	Parties;

	
	
	public static GroupManager generateNew(SupportedGroupsManager system) {
		switch(system){
			case None : return new DisabledGroupManager();
			case RaC : return new OwnGroupManager();
			case Heroes : return new HeroesGroupManager();
			case McMMO : return new McMMOGroupManager();
			case Parties : return new PartiesGroupManager();
		}
		
		return new DisabledGroupManager();
	}
	
	
	public static SupportedGroupsManager parse(String toParse){
		toParse = toParse.toLowerCase();
		
		if(toParse.startsWith("n")) return None;
		if(toParse.startsWith("r")) return RaC;
		if(toParse.startsWith("m")) return McMMO;
		if(toParse.startsWith("p")) return Parties;
		if(toParse.startsWith("h")) return Heroes;
		
		return None;
	}

}
