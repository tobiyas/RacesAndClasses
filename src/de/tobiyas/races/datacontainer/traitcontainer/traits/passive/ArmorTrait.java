package de.tobiyas.races.datacontainer.traitcontainer.traits.passive;

import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassManager;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceManager;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.TraitEventManager;
import de.tobiyas.races.datacontainer.traitcontainer.traits.Trait;
import de.tobiyas.races.util.items.ItemUtils;
import de.tobiyas.races.util.items.ItemUtils.ItemQuality;

public class ArmorTrait implements Trait {
	
	private boolean[] armorPerms;
	private String armorPermsString;
	
	private RaceContainer raceContainer = null;
	private ClassContainer classContainer = null;

	public ArmorTrait(RaceContainer raceContainer){
		this.raceContainer = raceContainer;
	}
	
	public ArmorTrait(ClassContainer classContainer){
		this.classContainer = classContainer;
	}
	
	@Override
	public void generalInit(){
		HashSet<Class<?>> listenedEvents = new HashSet<Class<?>>();
		listenedEvents.add(InventoryClickEvent.class);
		TraitEventManager.getInstance().registerTrait(this, listenedEvents);
	}
	
	@Override
	public RaceContainer getRace(){
		return raceContainer;
	}
	
	@Override
	public String getName() {
		return "ArmorTrait";
	}

	@Override
	public Object getValue() {
		return armorPerms;
	}
	
	@Override
	public String getValueString(){
		return armorPermsString;
	}

	@Override
	public void setValue(Object obj) {
		int tempArms = Integer.valueOf((String) obj);
		armorPermsString = String.valueOf(tempArms);
		
		char[] binString = Integer.toBinaryString(tempArms).toCharArray();
		armorPerms = new boolean[]{false, false, false, false, false};
		int maxBinLengthValue = (binString.length > 5 ? 4 : binString.length - 1);
	
		for(int i = 0; i <= maxBinLengthValue; i++){
			armorPerms[i] = (binString[maxBinLengthValue - i] == '1');
			if(binString.length == i) break;
		}
		
	}

	@Override
	public boolean modify(Event event) {
		if(!(event instanceof InventoryClickEvent)) return false;
		InventoryClickEvent Eevent = (InventoryClickEvent) event;
		Player player = (Player) Eevent.getWhoClicked();
		
		if(Eevent.getSlotType() != SlotType.ARMOR) return false;
		if(!checkContainer(player.getName())) return true;
		
		ItemStack stack = Eevent.getCursor();
		if(stack == null) return false;
		
		if(!hasPermission(ItemUtils.getItemValue(stack))){ 
			player.sendMessage(ChatColor.RED + "You are not allowed to use this armor.");
			Eevent.setCancelled(true);
		}
				
		return true;
	}
	
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
	
	private boolean hasPermission(ItemQuality quality){
		if(quality == ItemQuality.None) return true;
		
		return armorPerms[quality.getValue()];
	}
	
	

	public static void pasteHelpForTrait(CommandSender sender) {
		sender.sendMessage(ChatColor.YELLOW + "The Value is defined as a binary.");
		sender.sendMessage(ChatColor.YELLOW + "binary 1 = Leather-Armor");
		sender.sendMessage(ChatColor.YELLOW + "binary 2 = Iron-Armor");
		sender.sendMessage(ChatColor.YELLOW + "binary 4 = Gold-Armor");
		sender.sendMessage(ChatColor.YELLOW + "binary 8 = Diamond-Armor");
		sender.sendMessage(ChatColor.YELLOW + "binary 16 = Fire-Armor");
		sender.sendMessage(ChatColor.YELLOW + "Combine the numbers and you have your Permissions.");
		return;
	}
	
	@Override
	public boolean isVisible() {
		return true;
	}

}
