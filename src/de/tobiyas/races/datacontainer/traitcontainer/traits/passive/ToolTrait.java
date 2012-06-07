package de.tobiyas.races.datacontainer.traitcontainer.traits.passive;

import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassManager;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceManager;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.TraitEventManager;
import de.tobiyas.races.datacontainer.traitcontainer.traits.Trait;

public class ToolTrait implements Trait{

	private boolean[] toolPerms;
	private String armorPermsString;
	
	private RaceContainer raceContainer = null;
	private ClassContainer classContainer = null;
	
	public ToolTrait(RaceContainer raceContainer){
		this.raceContainer = raceContainer;
	}
	
	public ToolTrait(ClassContainer classContainer){
		this.classContainer = classContainer;
	}
	
	@Override
	public void generalInit(){
		HashSet<Class<?>> listenedEvents = new HashSet<Class<?>>();
		listenedEvents.add(PlayerInteractEvent.class);
		TraitEventManager.getInstance().registerTrait(this, listenedEvents);
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
		toolPerms = new boolean[]{false, false, false, false, false, false};
		int maxBinLengthValue = (binString.length > 6 ? 5 : binString.length - 1);
	
		for(int i = 0; i < maxBinLengthValue; i++){
			toolPerms[i] = (binString[maxBinLengthValue - i] == '1');
			if(binString.length == i) break;
		}
	}

	@Override
	public boolean modify(Event event) {
		
		//TODO implement me
		return false;
	}
	
	@SuppressWarnings("unused")
	private boolean checkContainer(String playerName){
		if(raceContainer != null){
			RaceContainer container = RaceManager.getManager().getRaceOfPlayer(playerName);
			if(container == null) return true;
			return raceContainer == container;
		}
		if(classContainer != null){
			ClassContainer container = ClassManager.getInstance().getClassOfPlayer(playerName);
			if(container == null) return true;
			return classContainer == container;
		}
		
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
