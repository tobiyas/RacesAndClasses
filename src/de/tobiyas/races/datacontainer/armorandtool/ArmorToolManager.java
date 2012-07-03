package de.tobiyas.races.datacontainer.armorandtool;

import java.util.HashSet;

import org.bukkit.inventory.ItemStack;

import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassManager;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceManager;
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
}
