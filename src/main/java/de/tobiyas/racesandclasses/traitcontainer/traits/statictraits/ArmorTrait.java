package de.tobiyas.racesandclasses.traitcontainer.traits.statictraits;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.eventprocessing.events.inventoryitemevents.PlayerEquipsArmorEvent;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.AbstractBasicTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.StaticTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitInfos;

public class ArmorTrait extends AbstractBasicTrait implements StaticTrait{

	protected RacesAndClasses plugin = RacesAndClasses.getPlugin();
	
	protected AbstractTraitHolder traitHolder;

	@TraitEventsUsed(registerdClasses = {PlayerEquipsArmorEvent.class})
	@Override
	public void generalInit(){
	}
	
	@Override
	public void setTraitHolder(AbstractTraitHolder abstractTraitHolder){
		this.traitHolder = abstractTraitHolder;
	}
	
	@Override
	public AbstractTraitHolder getTraitHolder(){
		return traitHolder;
	}

	@Override
	public String getName() {
		return "ArmorTrait";
	}


	@Override
	public String getPrettyConfiguration(){
		return "";
	}

	@TraitConfigurationNeeded()
	@Override
	public void setConfiguration(Map<String, String> configMap) {
	}

	@Override
	public boolean modify(Event event) {
		if(!(event instanceof PlayerEquipsArmorEvent)) return false;
		
		PlayerEquipsArmorEvent playerEquipEvent = (PlayerEquipsArmorEvent) event;
		Player player = (Player) playerEquipEvent.getPlayer();
		if(player == null) return false;

		ItemStack armorItem = playerEquipEvent.getArmorItem();
		if(armorItem == null) return false;
		
		if(!plugin.getPlayerManager().getArmorToolManagerOfPlayer(player.getName()).hasPermissionForItem(armorItem)){ 
			player.sendMessage(ChatColor.RED + "You are not allowed to use " 
					+ ChatColor.LIGHT_PURPLE + getMaterialName(armorItem.getType()) + ChatColor.RED + ".");
			playerEquipEvent.setCancelled(true);
		}

		return true;
	}
	
	
	/**
	 * Give material a realistic name
	 * 
	 * @param material
	 * @return
	 */
	private  String getMaterialName(Material material) {
        StringBuilder materialName = new StringBuilder();
        for(String c : material.toString().toLowerCase().split("_")) {
            materialName.append(Character.toUpperCase(c.charAt(0))).append(c, 1, c.length()).append(" ");
        }
        
        return materialName.toString().trim();
    }

	
	@Override
	public boolean isBetterThan(Trait trait) {
		return true;
	}

	@TraitInfos(category="static", traitName="AromrTrait", visible=false)
	@Override
	public void importTrait() {
		// TODO Auto-generated method stub
	}

}