package de.tobiyas.races.datacontainer.armorandtool;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassManager;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceManager;
import de.tobiyas.races.util.items.ItemUtils;
import de.tobiyas.races.util.items.ItemUtils.ItemQuality;

public class ArmorToolManager {

	private HashSet<AbstractItemPermission> itemPerms;
	private String playerName;
	
	public ArmorToolManager(String playerName){
		this.playerName = playerName;
		this.itemPerms = new HashSet<AbstractItemPermission>();
	}
	
	public void rescanPermission(){
		itemPerms.clear();
		RaceContainer container = RaceManager.getManager().getRaceOfPlayer(playerName);
		if(container != RaceManager.getManager().getDefaultContainer()){
			for(ItemQuality quality : container.getArmorPerms())
				addPerm(quality);
			
			//Add ItemIDs or other here.
		}
		
		ClassContainer classContainer = ClassManager.getInstance().getClassOfPlayer(playerName);
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
	
	/*
	private void addPerm(int itemID){
		
	}*/
	
	public boolean hasPermissionForItem(ItemStack stack){
		for(AbstractItemPermission permission : itemPerms)
			if(permission.hasPermission(stack)) 
				return true;
		
		return false;
	}
	
	public double calcDamageToArmor(double damage, DamageCause cause){
		Player player = Bukkit.getPlayer(playerName);
		if(player == null)
			return damage;
		
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
		}
		
		double playerArmor = getArmorLevel(player);
		double playerDamageReduce = 1D - ((8D * playerArmor) / 200D);
		return damage * playerDamageReduce;
	}

	public int getArmorLevel(Player player) {
		int armorLevel = 0;
		ItemStack inventory[] = player.getInventory().getArmorContents();
		
		for(ItemStack stack : inventory)
			armorLevel += ItemUtils.getArmorValueOfItem(stack);
		
		return armorLevel;
	}
	
	
}
