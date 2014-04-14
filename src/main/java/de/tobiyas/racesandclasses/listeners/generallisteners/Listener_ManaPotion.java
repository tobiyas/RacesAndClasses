package de.tobiyas.racesandclasses.listeners.generallisteners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.RacesAndClasses;

public class Listener_ManaPotion implements Listener {

	/**
	 * The Plugin to use.
	 */
	private final RacesAndClasses plugin;
	
	private final String MANA_POTION_NAME = "ManaPotion";
	private final Material potionMat = Material.POTION;
	
	
	public Listener_ManaPotion() {
		RacesAndClasses plugin = RacesAndClasses.getPlugin();
		this.plugin = plugin;
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	
	@EventHandler
	public void manaPotionUsed(PlayerInteractEvent event){
		ItemStack item = event.getItem();
		if(item == null) return;
		if(!item.hasItemMeta()) return;
		if(item.getType() != potionMat) return;
		if(!item.getItemMeta().hasDisplayName()) return;
		if(!item.getItemMeta().hasLore()) return;
		if(item.getItemMeta().getLore().size() != 1) return;
		
		String itemName = item.getItemMeta().getDisplayName().toLowerCase();
		if(!MANA_POTION_NAME.toLowerCase().equalsIgnoreCase(itemName)) return;
		
		String loreLine = item.getItemMeta().getLore().get(0);
		String manaLine = loreLine.replace(" Mana", "");
		int mana = -1;
		try{ mana = Integer.parseInt(manaLine); }catch(NumberFormatException exp){ return; }
		if(mana < 0 ) return;
		
		event.setCancelled(true);
		plugin.getPlayerManager().getSpellManagerOfPlayer(event.getPlayer().getUniqueId()).getManaManager().fillMana(mana);
		if(item.getAmount() == 1) event.getPlayer().getInventory().remove(item);
		if(item.getAmount() > 1) item.setAmount(item.getAmount() - 1);
	}
}
