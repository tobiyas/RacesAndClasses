package de.tobiyas.races.datacontainer.traitcontainer.traits.statictraits;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.races.datacontainer.health.HealthManager;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitcontainer.traits.Trait;

public class ArmorTrait implements Trait {

	private RaceContainer raceContainer = null;
	private ClassContainer classContainer = null;

	public ArmorTrait(){
	}

	@TraitInfo(registerdClasses = {InventoryClickEvent.class})
	@Override
	public void generalInit(){
	}

	@Override
	public RaceContainer getRace(){
		return raceContainer;
	}

	@Override
	public ClassContainer getClazz() {
		return classContainer;
	}

	@Override
	public String getName() {
		return "ArmorTrait";
	}

	@Override
	public Object getValue() {
		return null;
	}

	@Override
	public String getValueString(){
		return "";
	}

	@Override
	public void setValue(Object obj) {
	}

	@Override
	public boolean modify(Event event) {
		if(!(event instanceof InventoryClickEvent)) return false;
		InventoryClickEvent Eevent = (InventoryClickEvent) event;
		Player player = (Player) Eevent.getWhoClicked();
		if(player == null) return false;

		ItemStack stack = Eevent.getCursor();
		if(stack == null || stack.getType() == Material.AIR) return false;
		
		if(Eevent.getSlotType() != SlotType.ARMOR) return false;
		if(!HealthManager.getHealthManager().getArmorToolManagerOfPlayer(player.getName()).hasPermissionForItem(stack)){ 
			player.sendMessage(ChatColor.RED + "You are not allowed to use this armor.");
			Eevent.setCancelled(true);
		}

		return true;
	}

	@Override
	public boolean isVisible() {
		return false;
	}

	@Override
	public boolean isBetterThan(Trait trait) {
		return true;
	}

}