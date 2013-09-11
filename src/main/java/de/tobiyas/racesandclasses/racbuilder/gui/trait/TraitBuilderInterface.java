package de.tobiyas.racesandclasses.racbuilder.gui.trait;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.racbuilder.gui.BasicSelectionInterface;
import de.tobiyas.racesandclasses.racbuilder.gui.stats.StatType;
import de.tobiyas.racesandclasses.racbuilder.gui.stats.StatsSelectionInterfaceFactory;
import de.tobiyas.racesandclasses.traitcontainer.container.TraitsList;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitConfigurationNeeded;

public class TraitBuilderInterface extends BasicSelectionInterface {

	/**
	 * The Config map to fill
	 */
	private final List<TraitConfigOptionContainer> configList;
	
	/**
	 * A temporary config Map to transport stuff back
	 */
	private final Map<String, Object> tempConfigList = new HashMap<String, Object>();
	
	/**
	 * The Field to check on back
	 */
	private TraitConfigOptionContainer optionToCheck;
	
	
	public TraitBuilderInterface(Player player, BasicSelectionInterface parent, String traitName,
			List<TraitConfigOptionContainer> configList) {
		super(player, parent, "Controls", "Trait: " + traitName);
		
		this.configList = configList;
		
		try{
			Class<? extends Trait> traitClass = TraitsList.getClassOfTrait(traitName);
			TraitConfigurationNeeded neededConfig = traitClass.getMethod("setConfiguration", Map.class)
					.getAnnotation(TraitConfigurationNeeded.class);
			
			for(String neededField : neededConfig.neededFields()){
				
				boolean skip = false;
				for(TraitConfigOptionContainer container : this.configList){
					if(container.getName().equalsIgnoreCase(neededField)) {
						skip = true;
					}
				}
				
				if(skip) continue;
				
				ItemStack item = generateItem(Material.ANVIL, ChatColor.RED + neededField, new LinkedList<String>());
				TraitConfigOptionContainer newContainer = new TraitConfigOptionContainer(neededField, StatType.STRING, item);
				this.configList.add(newContainer);
			}			
		}catch(Exception exp){}
		
		redraw();
	}
	
	
	/**
	 * Redraws the Interface
	 */
	private void redraw(){
		selectionInventory.clear();
		
		for(TraitConfigOptionContainer option : configList){
			selectionInventory.addItem(option.getItem());
		}
	}
	

	@Override
	protected boolean onBackPressed() {
		return true;
	}

	@Override
	protected void onAcceptPressed() {
		//Nothing needed. All is saved to the List passed.
		closeAndReturnToParent();
	}

	@Override
	protected void onSelectionItemPressed(ItemStack item) {
		for(TraitConfigOptionContainer traitItem : configList){
			if(traitItem.getItem().equals(item)){
				String key = traitItem.getName();
				tempConfigList.put(key, traitItem.getValue());
				optionToCheck = traitItem;
				
				openNewView(
						StatsSelectionInterfaceFactory.buildInterface(
								traitItem.getType(), player, this, tempConfigList, key)
						);
				
				return;
			}
		}
	}

	@Override
	protected void onControlItemPressed(ItemStack item) {
	}


	@Override
	protected void notifyReopened() {
		super.notifyReopened();
		
		if(optionToCheck == null) return;
		
		
		Object newValue = tempConfigList.get(this.optionToCheck.getName());
		if(newValue != null){
			optionToCheck.setValue(newValue);
			redraw();
		}
	}

	
	
}
