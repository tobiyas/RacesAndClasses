package de.tobiyas.racesandclasses.datacontainer.armorandtool;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceManager;
import de.tobiyas.racesandclasses.util.items.ItemUtils;
import de.tobiyas.racesandclasses.util.items.ItemUtils.ArmorSlot;
import de.tobiyas.racesandclasses.util.items.ItemUtils.ItemQuality;

public class ArmorToolManager {

	private HashSet<AbstractItemPermission> itemPerms;
	private String playerName;
	
	public ArmorToolManager(String playerName){
		this.playerName = playerName;
		this.itemPerms = new HashSet<AbstractItemPermission>();
	}
	
	public void rescanPermission(){
		itemPerms.clear();
		AbstractTraitHolder container = RaceManager.getInstance().getHolderOfPlayer(playerName);
		
		for(ItemQuality quality : container.getArmorPerms()){
			addPerm(quality);
		}
			
		//Add ItemIDs or other here.
		
		AbstractTraitHolder classContainer = ClassManager.getInstance().getHolderOfPlayer(playerName);
		if(classContainer != null){
			for(ItemQuality quality : classContainer.getArmorPerms()){
				addPerm(quality);
			}
			
			//Add Allowed ItemIDs here
		}
		
		
	}
	
	private void addPerm(ItemQuality quality){
		for(AbstractItemPermission perm : itemPerms)
			if(perm.alreadyIsRegistered(quality)) 
				return;
		
		itemPerms.add(new MaterialArmorPermission(quality));
	}
	
	
	public boolean hasPermissionForItem(ItemStack stack){
		if(stack == null || stack.getType() == Material.AIR){
			return true;
		}
		
		for(AbstractItemPermission permission : itemPerms){
			if(permission.hasPermission(stack)){
				return true;
			}
		}
		
		return false;
	}
	
	public double calcDamageToArmor(double damage, DamageCause cause){
		Player player = Bukkit.getPlayer(playerName);
		if(player == null){
			return damage;
		}
		
		//check Cause
		switch(cause){
			case DROWNING:
			case FIRE_TICK:
			case FIRE:
			case LAVA:
			case FALL:
			case POISON:
			case STARVATION:
			case SUICIDE:
			case LIGHTNING: return damage;
			default: break;
		}
		
		double playerArmor = getArmorLevel(player);
		double playerDamageReduce = 1D - ((8D * playerArmor) / 200D);
		return damage * playerDamageReduce;
	}

	public int getArmorLevel(Player player) {
		int armorLevel = 0;
		ItemStack inventory[] = player.getInventory().getArmorContents();
		
		for(ItemStack stack : inventory){
			armorLevel += ItemUtils.getArmorValueOfItem(stack);
		}
			
		return armorLevel;
	}

	/**
	 * Checks if Armor is equipped that is not valid to the current permissions.
	 * If found, it is stored in Inventory or thrown away if no place.
	 * 
	 * @return true if settings is correct. False if something got thrown away.
	 */
	public boolean checkArmorNotValidEquiped() {
		Player player = Bukkit.getPlayer(playerName);
		if(player == null){
			return true;
		}
		
		ItemStack helmet = ItemUtils.getItemInArmorSlotOfPlayer(player, ArmorSlot.HELMET);
		ItemStack chest = ItemUtils.getItemInArmorSlotOfPlayer(player, ArmorSlot.CHESTPLATE);
		ItemStack legs = ItemUtils.getItemInArmorSlotOfPlayer(player, ArmorSlot.LEGGINGS);
		ItemStack boots = ItemUtils.getItemInArmorSlotOfPlayer(player, ArmorSlot.BOOTS);
		
		boolean everythingOK = true;
		
		//check helmet
		if(!hasPermissionForItem(helmet)){
			if(!player.getInventory().addItem(helmet).isEmpty()){
				player.getWorld().dropItem(player.getLocation(), helmet);
			}
			
			player.getInventory().setHelmet(new ItemStack(Material.AIR));
			everythingOK = false;
		}
		
		//check chest
		if(!hasPermissionForItem(chest)){
			if(!player.getInventory().addItem(chest).isEmpty()){
				player.getWorld().dropItem(player.getLocation(), chest);
			}
			
			player.getInventory().setChestplate(new ItemStack(Material.AIR));
			everythingOK = false;
		}
		
		//check legs
		if(!hasPermissionForItem(legs)){
			if(!player.getInventory().addItem(legs).isEmpty()){
				player.getWorld().dropItem(player.getLocation(), legs);
			}
			
			player.getInventory().setLeggings(new ItemStack(Material.AIR));
			everythingOK = false;
		}
		
		//check boots
		if(!hasPermissionForItem(boots)){
			if(!player.getInventory().addItem(boots).isEmpty()){
				player.getWorld().dropItem(player.getLocation(), boots);
			}
			
			player.getInventory().setBoots(new ItemStack(Material.AIR));
			everythingOK = false;
		}
		
		return everythingOK;
	}
	
	
}
