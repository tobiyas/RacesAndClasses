package de.tobiyas.races.configuration.member;

import de.tobiyas.races.util.consts.Consts;
import de.tobiyas.util.economy.defaults.YAMLConfigExtended;

public class MemberConfig {

	private String player;
	private boolean enableLifeDisplay;
	private static YAMLConfigExtended config = new YAMLConfigExtended(Consts.membersYML);
	
	private MemberConfig(String player){
		config.load();
		this.player = player;
		enableLifeDisplay = config.getBoolean("playerData." + player + ".enableLifeDisplay", true);
		
		//Add other vars
		config.save();
	}
	
	
	public static MemberConfig createMemberConfig(String player){
		return new MemberConfig(player);
	}
	
	public void setEnableLifeDisplay(boolean enable){
		config.load();
		config.set("playerdata." + player + ".enableLifeDisplay", enable);
		config.save();
	}
	
	public boolean getEnableLifeDisplay(){
		return enableLifeDisplay;
	}
}
