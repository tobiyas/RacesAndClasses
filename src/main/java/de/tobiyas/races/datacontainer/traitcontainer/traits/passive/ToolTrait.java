package de.tobiyas.races.datacontainer.traitcontainer.traits.passive;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitcontainer.traits.Trait;

public class ToolTrait implements Trait{

	private boolean[] toolPerms;
	private String armorPermsString;
	
	private RaceContainer raceContainer = null;
	private ClassContainer classContainer = null;
	
	public ToolTrait(){
	}
	
	@Override
	public void setRace(RaceContainer container) {
		this.raceContainer = container;
	}

	@Override
	public void setClazz(ClassContainer container) {
		this.classContainer = container;
	}
	
	@TraitInfo(registerdClasses = {PlayerInteractEvent.class})
	@Override
	public void generalInit(){
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
	public ClassContainer getClazz() {
		return classContainer;
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
		toolPerms = new boolean[]{false, false, false, false, false, false};
		int maxBinLengthValue = (binString.length > 6 ? 5 : binString.length - 1);
	
		for(int i = 0; i < maxBinLengthValue; i++){
			toolPerms[i] = (binString[maxBinLengthValue - i] == '1');
			if(binString.length == i) break;
		}
	}

	@Override
	public boolean modify(Event event) {
		if(!(event instanceof PlayerInteractEvent)) return false;
		//TODO implement me
		return false;
	}
	
	public static void paistHelpForTrait(CommandSender sender){
		sender.sendMessage(ChatColor.RED + "Nothing to see here yet.");
	}
	
	@Override
	public boolean isVisible() {
		return true;
	}
	
	@Override
	public boolean isBetterThan(Trait trait) {
		//TODO not implemented yet
		return true;
	}

}
