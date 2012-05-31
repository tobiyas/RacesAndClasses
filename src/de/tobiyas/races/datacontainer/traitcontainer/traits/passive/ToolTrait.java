package de.tobiyas.races.datacontainer.traitcontainer.traits.passive;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;

import de.tobiyas.races.datacontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitcontainer.Trait;
import de.tobiyas.races.datacontainer.traitcontainer.TraitEventManager;

public class ToolTrait implements Trait{

	private boolean[] toolPerms;
	private String armorPermsString;
	private RaceContainer raceContainer;

	public ToolTrait(RaceContainer raceContainer){
		this.raceContainer = raceContainer;
		TraitEventManager.getTraitEventManager().registerTrait(this);
	}
	
	@Override
	public String getName() {
		return "ToolTrait";
	}

	@Override
	public RaceContainer getRace() {
		return raceContainer;
	}

	@Override
	public Object getValue() {
		return toolPerms;
	}

	@Override
	public String getValueString() {
		return armorPermsString;
	}

	@Override
	public void setValue(Object obj) {
		int tempArms = (Integer)obj;
		armorPermsString = String.valueOf(tempArms);
		
		char[] binString = Integer.toBinaryString(tempArms).toCharArray();
		toolPerms = new boolean[]{false, false, false, false, false};
		int maxBinLengthValue = (binString.length > 5 ? 4 : binString.length - 1);
	
		for(int i = 0; i < maxBinLengthValue; i++){
			toolPerms[i] = (binString[maxBinLengthValue - i] == '1');
			if(binString.length == i) break;
		}
	}

	@Override
	public boolean modify(Event event) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public static void paistHelpForTrait(CommandSender sender){
		sender.sendMessage(ChatColor.RED + "Nothing to see here yet.");
	}
	
	@Override
	public boolean isVisible() {
		return true;
	}

}
