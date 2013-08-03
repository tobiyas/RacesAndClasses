package de.tobiyas.racesandclasses.util.inventory;

import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;

	/**
	 * Resyncs the Inventory of a Player in the next tick
	 * @author Tobiyas
	 *
	 */
	public class InventoryResync implements Runnable{

		public static void resync(Player player){
			RacesAndClasses plugin = RacesAndClasses.getPlugin();
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new InventoryResync(player), 1);
		}
		
		private final Player player;
		
		public InventoryResync(final Player player){
			this.player = player;
		}
		
		@SuppressWarnings("deprecation") //let's see if it still works
		@Override
		public void run() {
			//it's an attempt
			if(player != null && player.isOnline()){
				try{
					player.updateInventory();
				}catch(Exception exp){}; //since this is depricated, we catch everything to prevent errors.
			}
		}
		
	}