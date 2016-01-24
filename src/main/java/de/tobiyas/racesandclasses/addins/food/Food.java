package de.tobiyas.racesandclasses.addins.food;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier.BukkitPlayer;
import de.tobiyas.util.vollotile.ParticleEffects;
import de.tobiyas.util.vollotile.VollotileCodeManager;

public class Food {

	private final String[] AMOUNT_PRE = {"value", "health", "life", "leben"};
	private final String[] SECONDS_PRE = {"seconds", "sekunden"};
	
	/**
	 * The Amount to heal
	 */
	private double amount = -1;
	
	/**
	 * The Ticks to have in total.
	 */
	private int ticks = -1;
	

	public Food(ItemStack item){
		if(item == null) return;
		if(!item.hasItemMeta()) return;
		if(!item.getItemMeta().hasLore()) return;
		
		for(String loreString : item.getItemMeta().getLore()){
			loreString = ChatColor.stripColor(loreString);
			loreString = loreString.toLowerCase();
			
			if(!loreString.contains(": ")) continue;
			String[] split = loreString.split(": ");
			if(split.length != 2) continue;
			
			String pre = split[0];
			String valueString = split[1];			
			
			for(String amountPre : AMOUNT_PRE){
				if(amountPre.equalsIgnoreCase(pre)){
					try{ amount = Double.parseDouble(valueString); }catch(NumberFormatException exp){ continue; }
					break;
				}
			}
			
			for(String secondsPre : SECONDS_PRE){
				if(secondsPre.equalsIgnoreCase(pre)){
					try{ ticks = Integer.parseInt(valueString); }catch(NumberFormatException exp){ continue; }
					break;
				}
			}
		}
		
	}
	
	public boolean isValid(){
		return ticks > 0 && amount > 0;
	}
	
	
	
	/**
	 * Ticks the container.
	 * 
	 * @param player to tick for.
	 */
	public void tick(RaCPlayer player){
		ticks--;
		BukkitPlayer.safeHeal(amount, player.getPlayer());
		
		VollotileCodeManager.getVollotileCode().sendParticleEffectToAll(ParticleEffects.SPELL, player.getLocation().clone().add(0, 1, 0), 1, 10);
	}
}
