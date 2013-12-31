package de.tobiyas.racesandclasses.traitcontainer.traits.statictraits;

import static de.tobiyas.racesandclasses.translation.languages.Keys.armor_not_allowed;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.eventprocessing.events.inventoryitemevents.PlayerEquipsArmorEvent;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.AbstractBasicTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.bypasses.StaticTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;

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
	public void setConfiguration(Map<String, Object> configMap) {
	}

	@Override
	public boolean trigger(Event event) {
		if(!(event instanceof PlayerEquipsArmorEvent)) return false;
		
		PlayerEquipsArmorEvent playerEquipEvent = (PlayerEquipsArmorEvent) event;
		Player player = (Player) playerEquipEvent.getPlayer();
		if(player == null) return false;

		ItemStack armorItem = playerEquipEvent.getArmorItem();
		if(armorItem == null) return false;
		
		if(!plugin.getPlayerManager().getArmorToolManagerOfPlayer(player.getName()).hasPermissionForItem(armorItem)){ 
			String matName = getMaterialName(armorItem.getType());
			LanguageAPI.sendTranslatedMessage(player, armor_not_allowed, "material", matName);
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
	}

	@Override
	public boolean canBeTriggered(Event event) {
		return true;
	}

	@Override
	public boolean isStackable(){
		return false;
	}

	@Override
	public String getDisplayName() {
		return getName();
	}
}