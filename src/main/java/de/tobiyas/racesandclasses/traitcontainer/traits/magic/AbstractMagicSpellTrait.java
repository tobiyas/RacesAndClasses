package de.tobiyas.racesandclasses.traitcontainer.traits.magic;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.AbstractBasicTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitEventsUsed;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public abstract class AbstractMagicSpellTrait extends AbstractBasicTrait implements MagicSpellTrait {

	//static naming for YML elements
	public static final String COST_TYPE_PATH= "costType";
	public static final String COST_PATH= "cost";
	public static final String ITEM_TYPE_PATH= "item";
	
	
	/**
	 * This map is to prevent instant retriggers!
	 */
	private static final Map<String, Long> lastCastMap = new HashMap<String, Long>(); 
	
	
	/**
	 * The Cost of the Spell.
	 * 
	 * It has the default Cost of 0.
	 */
	protected double cost = 0;
	
	/**
	 * The Material for casting with {@link CostType#ITEM}
	 */
	protected Material materialForCasting = Material.FEATHER;
	
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
	

	
	@TraitEventsUsed(registerdClasses = {PlayerInteractEvent.class})
	@Override
	public void generalInit(){
		//We need nothing to be inited here.
	}

	
	@Override
	public boolean canBeTriggered(Event event){
		if(canOtherEventBeTriggered(event)) return true;
		
		if(!(event instanceof PlayerInteractEvent)) return false;
		
		PlayerInteractEvent playerInteractEvent = (PlayerInteractEvent) event;
		Player player = playerInteractEvent.getPlayer();
		
		Material wandMaterial = plugin.getConfigManager().getGeneralConfig().getConfig_itemForMagic();
		ItemStack itemInHand = player.getItemInHand();
		
		//early out for not wand in hand.
		if(itemInHand == null || itemInHand.getType() != wandMaterial) return false;
		
		//check if the Spell is the current selected Spell
		if(this != plugin.getPlayerManager().getSpellManagerOfPlayer(player.getName()).getCurrentSpell()) return false;
		
		
		return true;
	}
	
	/**
	 * This is a pre-call to {@link #canBeTriggered(Event)}.
	 * When returning true, true will be passed.
	 * 
	 * @param event that wants to be triggered
	 * 
	 * @return true if interested, false if not.
	 */
	protected boolean canOtherEventBeTriggered(Event event){
		return false;
	}


	@Override
	public boolean triggerButHasUplink(Event event) {
		if(event instanceof PlayerInteractEvent){
			PlayerInteractEvent playerInteractEvent = (PlayerInteractEvent) event;
			Action action = playerInteractEvent.getAction();
			Player player = playerInteractEvent.getPlayer();
			
			if(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK){
				changeMagicSpell(player);
				
				return true;
			}
		}
		
		return false;
	}
	
	
	@Override
	public boolean triggerButDoesNotHaveEnoghCostType(Event event){
		if(event instanceof PlayerInteractEvent){
			PlayerInteractEvent playerInteractEvent = (PlayerInteractEvent) event;
			Action action = playerInteractEvent.getAction();
			Player player = playerInteractEvent.getPlayer();
			
			if(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK){
				changeMagicSpell(player);
				
				return true;
			}
		}
		
		return false;
	}


	@Override
	public boolean trigger(Event event) {
		if(event instanceof PlayerInteractEvent){
			PlayerInteractEvent playerInteractEvent = (PlayerInteractEvent) event;
			Player player = playerInteractEvent.getPlayer();
			
			Material wandMaterial = plugin.getConfigManager().getGeneralConfig().getConfig_itemForMagic();
			ItemStack itemInHand = player.getItemInHand();
			
			//early out for not wand in hand.
			if(itemInHand == null || itemInHand.getType() != wandMaterial) return false;
			
			//check if the Spell is the current selected Spell
			if(this != plugin.getPlayerManager().getSpellManagerOfPlayer(player.getName()).getCurrentSpell()) return false;
			
			Action action = playerInteractEvent.getAction();
			if(action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK){
				String playerName = player.getName();
				if(lastCastMap.containsKey(playerName)){
					if(System.currentTimeMillis() - lastCastMap.get(playerName) < 100){
						//2 casts directly after each other.
						return false;
					}else{
						lastCastMap.remove(playerName);
					}
					
				}
				
				lastCastMap.put(player.getName(), System.currentTimeMillis());
				
				boolean casted = magicSpellTriggered(player);
				if(casted){
					removeCost(player);
				}
				
				return casted;
			}
			
			if(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK){
				return changeMagicSpell(player);
			}
		}
		
		return otherEventTriggered(event);
	}
	
	
	/**
	 * This triggers when NO {@link PlayerInteractEvent} is triggered.
	 * 
	 * @param event that triggered
	 * @return true if triggering worked and Mana should be drained.
	 */
	protected boolean otherEventTriggered(Event event){
		return false;
	}
	
	/**
	 * Removes the spell cost from the player
	 * 
	 * @param player
	 */
	private void removeCost(Player player) {
		String playerName = player.getName();
		
		switch(costType){
			case HEALTH: CompatibilityModifier.BukkitPlayer.safeSetHealth(plugin.getPlayerManager()
							.getHealthOfPlayer(playerName) - cost, player); 
							break;
			
			case MANA: plugin.getPlayerManager().getSpellManagerOfPlayer(playerName).getManaManager().playerCastSpell(this); break;
			case ITEM: player.getInventory().removeItem(new ItemStack(materialForCasting, (int) cost)); break;
		}
	}


	/**
	 * Checks if the Player has enough Resources to cast the spell.
	 * 
	 * @return
	 */
	protected boolean checkCosts(Player player) {
		String playerName = player.getName();
		
		switch(costType){
			case HEALTH: return plugin.getPlayerManager().getHealthOfPlayer(playerName) > cost;
			case MANA: return plugin.getPlayerManager().getSpellManagerOfPlayer(playerName).getManaManager().hasEnoughMana(this);
			case ITEM: return player.getInventory().contains(materialForCasting, (int) cost);
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
		String playerName = player.getName();
		
		if(plugin.getPlayerManager().getSpellManagerOfPlayer(playerName).getCurrentSpell() == null) return false;
		
		if(plugin.getPlayerManager().getSpellManagerOfPlayer(player.getName()).getSpellAmount() == 0){
			player.sendMessage(ChatColor.GREEN + "You can not cast any spells.");
			return true;
		}

		MagicSpellTrait currentSpell 
			= plugin.getPlayerManager().getSpellManagerOfPlayer(player.getName()).changeToNextSpell();
		
		if(currentSpell != null){
			player.sendMessage(ChatColor.GREEN + "Changed Spell to: " + ChatColor.LIGHT_PURPLE 
					+ ((Trait) currentSpell).getName() + ChatColor.GREEN + ".");
			return true;
		}else{
			//switching too fast.
			return true;
		}
	}
	
	
	/**
	 * This method is called, when the caster uses THIS magic spell.
	 * 
	 * @param player the Player triggering the spell.
	 * 
	 * @return true if spell summoning worked, false if failed.
	 */
	protected abstract boolean magicSpellTriggered(Player player);
	
	
	//This is just for Mana + CostType
	@TraitConfigurationNeeded( fields = {
			@TraitConfigurationField(fieldName = COST_PATH, classToExpect = Double.class),
			@TraitConfigurationField(fieldName = COST_TYPE_PATH, classToExpect = String.class, optional = true),
			@TraitConfigurationField(fieldName = ITEM_TYPE_PATH, classToExpect = String.class, optional = true)
		})
	@Override
	public void setConfiguration(Map<String, Object> configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		cost = (Double) configMap.get(COST_PATH);
		
		if(configMap.containsKey(COST_TYPE_PATH)){
			String costTypeName = (String) configMap.get(COST_TYPE_PATH);
			costType = CostType.tryParse(costTypeName);
			if(costType == null){
				throw new TraitConfigurationFailedException(getName() + " is incorrect configured. costType could not be read.");
			}
			
			if(costType == CostType.ITEM){
				if(!configMap.containsKey(ITEM_TYPE_PATH)){
					throw new TraitConfigurationFailedException(getName() + " is incorrect configured. 'costType' was ITEM but no Item is specified at 'item'.");
				}
				
				String material = (String) configMap.get(ITEM_TYPE_PATH);
				materialForCasting = Material.valueOf(material);
				if(materialForCasting == null){
					throw new TraitConfigurationFailedException(getName() + " is incorrect configured."
							+ " 'costType' was ITEM but the item read is not an Item. Items are CAPITAL. "
							+ "See 'https://github.com/Bukkit/Bukkit/blob/master/src/main/java/org/bukkit/Material.java' for all Materials");
				}
			}
			
		}
	}


	@Override
	public double getCost(){
		return cost;
	}

	
	@Override
	public CostType getCostType(){
		return costType;
	}


	@Override
	public Material getCastMaterialType() {
		return materialForCasting;
	}

	@Override
	public boolean isStackable(){
		return false;
	}
	
	@Override
	public boolean needsCostCheck(Event event){
		return event instanceof PlayerInteractEvent;
	}
}
