package de.tobiyas.racesandclasses.addins.potions;

import static de.tobiyas.racesandclasses.translation.languages.Keys.health_full;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.APIs.LevelAPI;
import de.tobiyas.racesandclasses.playermanagement.health.HealthManager;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.translation.languages.Keys;
import de.tobiyas.util.schedule.DebugBukkitRunnable;

@SuppressWarnings("deprecation")
public class HealthPotionManager implements Listener {
	
	/**
	 * The Cooldown of the potion (default: 10 seconds)
	 */
	private final int HEALTH_POTION_COOLDOWN = 20 * 10;
	
	/**
	 * the plugin to use.
	 */
	private final RacesAndClasses plugin;
	
	
	/**
	 * the list of names for mana-potions.
	 */
	private final List<String> HEALTH_POTION_NAMES = Arrays.asList("HealingPotion", "HeilTrank");
	
	/**
	 * people with cooldown on Health flasks.
	 */
	private final Set<UUID> cooldown = new HashSet<UUID>();
	
	
	public HealthPotionManager(RacesAndClasses plugin) {
		this.plugin = plugin; 
	}
	
	
	public void reload(){
		cooldown.clear();
		
		HandlerList.unregisterAll(this);
		plugin.registerEvents(this);
	}
	
	
	@EventHandler
	public void healthPotionUsed(PlayerInteractEvent event){
		ItemStack item = event.getItem();
		if(item == null) return;
		if(!item.hasItemMeta()) return;
		
		if(!item.getItemMeta().hasDisplayName()) return;
		if(!item.getItemMeta().hasLore()) return;
		if(item.getItemMeta().getLore().size() < 1) return;
		
		String itemName = item.getItemMeta().getDisplayName().toLowerCase();
		itemName = ChatColor.stripColor(itemName.toLowerCase());
		
		//looking for the Name of the potion.
		boolean found = false;
		for(String name : HEALTH_POTION_NAMES){
			name = name.toLowerCase();
			
			if(itemName.contains(name)){
				found = true;
				break;
			}
		}
		
		if(!found) return;

		//looking for any line with: 'XX Health'.
		int health = 0;
		int minLevel = 0;
		for(String loreLine : item.getItemMeta().getLore()){
			loreLine = ChatColor.stripColor(loreLine.toLowerCase());
			if(loreLine.contains("health") || loreLine.contains("leben")) {
				String manaLine = loreLine.replaceAll("[^0-9]", "");
				try{ health = Integer.parseInt(manaLine); }catch(NumberFormatException exp){ continue; }
			}
			
			if(!loreLine.contains("level")) {
				String levelLine = loreLine.replaceAll("[^0-9]", "");
				try{ minLevel = Integer.parseInt(levelLine); }catch(NumberFormatException exp){ continue; }
			}
		}
		
		if(health <= 0 ) return;
		
		event.setCancelled(true);
		//here we are sure to have a Health potion!
		
		
		//If not enough level -> Break!
		final RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(event.getPlayer());
		if(minLevel > 0 && LevelAPI.getCurrentLevel(racPlayer) < minLevel){
			LanguageAPI.sendTranslatedMessage(event.getPlayer(), Keys.restrictions_not_met_MinimumLevel);
			return;
		}
		
		final HealthManager healthManager = racPlayer.getHealthManager();
		if(healthManager.getCurrentHealth() >= healthManager.getMaxHealth()){
			LanguageAPI.sendTranslatedMessage(event.getPlayer(), health_full);
			return;
		}
		
		healthManager.heal(health);
		cooldown.add(racPlayer.getUniqueId());
		
		if(item.getAmount() == 1) event.getPlayer().getInventory().remove(item);
		if(item.getAmount() > 1) item.setAmount(item.getAmount() - 1);
		
		new DebugBukkitRunnable("HealthPotionCooldownRemover"){
			@Override
			protected void runIntern() {
				cooldown.remove(racPlayer.getUniqueId());
			}
		}.runTaskLater(RacesAndClasses.getPlugin(), HEALTH_POTION_COOLDOWN);
		
		event.getPlayer().getWorld().playEffect(event.getPlayer().getLocation(), Effect.POTION_BREAK, new Potion(PotionType.WATER));
	}
}
