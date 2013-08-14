package de.tobiyas.racesandclasses.traitcontainer.traits.magic;

import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitEventsUsed;

public abstract class AbstractMagicSpellTrait implements MagicSpellTrait, Trait {

	/**
	 * The Cost of the Spell.
	 * 
	 * It has the default Cost of 0.
	 */
	protected double cost = 0;
	
	/**
	 * The CostType of the Spell.
	 * 
	 * It has the Default CostType: {@link CostType#MANA}.
	 */
	protected CostType costType = CostType.MANA;
	
	/**
	 * The TraitHolder holding this trait.
	 */
	protected AbstractTraitHolder traitHolder;
	
	
	/**
	 * The Plugin to call stuff on
	 */
	protected final RacesAndClasses plugin = RacesAndClasses.getPlugin();
	

	
	@Override
	public abstract void importTrait();

	
	@TraitEventsUsed(registerdClasses = {PlayerInteractEvent.class})
	@Override
	public void generalInit(){
		//We need nothing to be inited here.
	}
	
	
	@Override
	public abstract String getName();

	
	@Override
	public abstract String getPrettyConfiguration();
	

	@Override
	public abstract void setConfiguration(Map<String, String> configMap);

	
	@Override
	public boolean modify(Event event) {
		if(event instanceof PlayerInteractEvent){
			PlayerInteractEvent playerInteractEvent = (PlayerInteractEvent) event;
			Player player = playerInteractEvent.getPlayer();
			
			int wandId = plugin.getConfigManager().getGeneralConfig().getConfig_itemForMagic();
			ItemStack itemInHand = player.getItemInHand();
			
			//early out for not wand in hand.
			if(itemInHand == null || itemInHand.getTypeId() != wandId) return false;
			
			//this trait is not belonging to the Player.
			if(!TraitHolderCombinder.checkContainer(player.getName(), this)) return false;
			
			Action action = playerInteractEvent.getAction();
			if(action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK){
				return magicSpellTriggered(player);
			}
			
			if(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK){
				return changeMagicSpell(player);
			}
		}
		
		return false;
	}
	
	/**
	 * Changes the current magic spell.
	 * 
	 * @param player the player triggering the spell
	 * 
	 * @return true if the Spell could be changed, false if not.
	 */
	protected boolean changeMagicSpell(Player player){
		
		return false;
	}
	
	
	/**
	 * This method is called, when the caster uses THIS magic spell.
	 * 
	 * @param player the Player triggering the spell.
	 * 
	 * @return true if spell summoning worked, false if failed.
	 */
	protected abstract boolean magicSpellTriggered(Player player);

	
	@Override
	public abstract boolean isBetterThan(Trait trait);

	
	@Override
	public void setTraitHolder(AbstractTraitHolder abstractTraitHolder) {
		this.traitHolder = abstractTraitHolder;
	}

	
	@Override
	public AbstractTraitHolder getTraitHolder() {
		return traitHolder;
	}

	
	@Override
	public double getCost(){
		return cost;
	}

	
	@Override
	public CostType getCostType(){
		return costType;
	}

}
