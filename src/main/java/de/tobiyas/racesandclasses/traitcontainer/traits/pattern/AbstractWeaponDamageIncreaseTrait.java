package de.tobiyas.racesandclasses.traitcontainer.traits.pattern;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.PlayerAction;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.passive.AbstractPassiveTrait;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public abstract class AbstractWeaponDamageIncreaseTrait extends AbstractPassiveTrait {

	/**
	 * The Set of Weapons to trigger on.
	 */
	protected final Set<Material> weapons = new HashSet<>();
	
	
	@TraitEventsUsed(registerdClasses = {EntityDamageByEntityEvent.class})
	@Override
	public void generalInit(){
	}


	@Override
	protected String getPrettyConfigIntern(){
		return operation + " " +  value;
	}

	@TraitConfigurationNeeded( fields = {
			@TraitConfigurationField(fieldName = "operation", classToExpect = String.class, optional = true), 
			@TraitConfigurationField(fieldName = "value", classToExpect = Double.class, optional = true)
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		operation = configMap.getAsString("operation", "+");
		value = configMap.getAsDouble("value", 0);
	}
	
	@Override
	public TraitResults trigger(EventWrapper eventWrapper) {   
		Event event = eventWrapper.getEvent();
		EntityDamageByEntityEvent Eevent = (EntityDamageByEntityEvent) event;
		double newValue = getNewValue(eventWrapper.getPlayer(), Eevent.getDamage(), "damage");
		
		CompatibilityModifier.EntityDamage.safeSetDamage(newValue, Eevent);
		return TraitResults.True();
	}
	
	/**
	 * Checks if the Weapon is one of the one wanted.
	 * 
	 * @param stack to check
	 * @return true if is relevent.
	 */
	private boolean checkItemIsWeapon(ItemStack stack){
		if(stack == null) return false;
		
		Material itemMat = stack.getType();
		for(Material mat : weapons){
			if(mat == itemMat) return true;
		}
		
		return false;
	}

	@Override
	public boolean isBetterThan(Trait trait) {
		if(!(trait instanceof AbstractWeaponDamageIncreaseTrait)) return false;
		AbstractWeaponDamageIncreaseTrait otherTrait = (AbstractWeaponDamageIncreaseTrait) trait;
		
		return value >= otherTrait.value;
	}
	
	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "The trait increases the Damage of your Weapon.");
		
		return helpList;
	}

	
	@Override
	public boolean canBeTriggered(EventWrapper wrapper) {
		if(wrapper.getPlayerAction() != PlayerAction.DO_DAMAGE) return false;
		Player player = wrapper.getPlayer().getPlayer();
		if(!checkItemIsWeapon(player.getInventory().getItem(player.getInventory().getHeldItemSlot()))) return false;
		
		return true;
	}
	

}
