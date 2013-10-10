package de.tobiyas.racesandclasses.traitcontainer.interfaces;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.eventprocessing.events.chatevent.PlayerSendChannelChatMessageEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.HolderSelectEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.leveling.LevelEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.traittrigger.TraitTriggerEvent;

public abstract class AbstractBasicTrait implements Trait,
		TraitWithRestrictions {

	/**
	 * The plugin to call stuff on.
	 */
	protected static final RacesAndClasses plugin = RacesAndClasses.getPlugin();
	
	
	/**
	 * the minimum level to use this trait
	 */
	protected int minimumLevel = 1;
	
	
	/**
	 * the maximum level to use this trait
	 */
	protected int maximumLevel = 90000000;

	
	/**
	 * The Set of biomes restricted.
	 */
	protected Set<Biome> biomes = new HashSet<Biome>(Arrays.asList(Biome.values()));
	
	
	/**
	 * The holder of the Trait.
	 */
	protected AbstractTraitHolder holder;
	
	
	/**
	 * The Config of the Trait
	 */
	protected Map<String, String> currentConfig;
	
	
	
	@Override
	public int getMinimumLevel() {
		return minimumLevel;
	}

	@Override
	public Set<Biome> getBiomeRestrictions() {
		return biomes;
	}

	@Override
	public int getMaximumLevel() {
		return maximumLevel;
	}

	@Override
	public void setTraitHolder(AbstractTraitHolder abstractTraitHolder) {
		this.holder = abstractTraitHolder;
	}

	@Override
	public AbstractTraitHolder getTraitHolder() {
		return holder;
	}

	@Override
	public void setConfiguration(Map<String, String> configMap) {
		this.currentConfig = configMap;
		
		//Reads the min level for the trait if present
		if(configMap.containsKey(TraitWithRestrictions.MIN_LEVEL_PATH)){
			try{
				this.minimumLevel = Integer.parseInt(configMap.get(TraitWithRestrictions.MIN_LEVEL_PATH));
			}catch(Exception exp){}
		}

		//Reads the max level for the trait if present
		if(configMap.containsKey(TraitWithRestrictions.MAX_LEVEL_PATH)){
			try{
				this.maximumLevel = Integer.parseInt(configMap.get(TraitWithRestrictions.MAX_LEVEL_PATH));
			}catch(Exception exp){}
		}
		
		//Reads the biomes for the trait if present
		if(configMap.containsKey(TraitWithRestrictions.BIOME_PATH)){
			try{
				String stringBiomes = configMap.get(TraitWithRestrictions.BIOME_PATH).toLowerCase();
				this.biomes.clear();
				
				for(Biome biome : Biome.values()){
					if(stringBiomes.contains(biome.name().toLowerCase())){
						this.biomes.add(biome);
					}
				}
			}catch(Exception exp){}
		}
	}
	

	@Override
	public Map<String, String> getCurrentconfig(){
		return currentConfig;
	}
	
	
	/**
	 * {@inheritDoc}
	 * 
	 * <br>
	 * <br>To override, use {@link #getAdditionalOptionalConfigFields()}.
	 * <br>This adds the optional Fields to the one added here.
	 */
	@Override
	public final List<String> getOptionalConfigFields(){
		List<String> optionalFields = new LinkedList<String>();
		optionalFields.add(TraitWithRestrictions.BIOME_PATH);
		optionalFields.add(TraitWithRestrictions.MAX_LEVEL_PATH);
		optionalFields.add(TraitWithRestrictions.MIN_LEVEL_PATH);
		
		List<String> additionalOptionalFields = getAdditionalOptionalConfigFields();
		if(additionalOptionalFields != null && !additionalOptionalFields.isEmpty()){
			optionalFields.addAll(additionalOptionalFields);			
		}
		
		return optionalFields;
	}

	/**
	 * Adds the Fields passed here to: {@link #getOptionalConfigFields()}.
	 * 
	 * @return the list to add
	 */
	protected List<String> getAdditionalOptionalConfigFields() {
		return new LinkedList<String>();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <br>
	 * <br>To override, use {@link #getReleventPlayerBefore(Event)}.
	 * <br>This is preProcessed before the default Events are done.
	 */
	@Override
	public final Player getReleventPlayer(Event event) {
		Player preProcessedPlayer = getReleventPlayerBefore(event);
		if(preProcessedPlayer != null){
			return preProcessedPlayer;
		}
		
		if(event instanceof BlockEvent){
			if(event instanceof BlockPlaceEvent){
				return ((BlockPlaceEvent) event).getPlayer();
			}
			
			if(event instanceof BlockBreakEvent){
				return ((BlockBreakEvent) event).getPlayer();
			}
			
			if(event instanceof BlockDamageEvent){
				return ((BlockDamageEvent) event).getPlayer();
			}
		}
		
		//Projectile events. 
		//We need to get the shooter.
		if(event instanceof ProjectileHitEvent){
			ProjectileHitEvent launchEvent = (ProjectileHitEvent) event;
			LivingEntity shooter = launchEvent.getEntity().getShooter();
			if(shooter instanceof Player) return (Player) shooter;
		}
		
		if(event instanceof ProjectileLaunchEvent){
			ProjectileLaunchEvent launchEvent = (ProjectileLaunchEvent) event;
			LivingEntity shooter = launchEvent.getEntity().getShooter();
			if(shooter instanceof Player) return (Player) shooter;
		}
		
		
		if(event instanceof EntityEvent){
			EntityEvent entityEvent = (EntityEvent) event;
			if(entityEvent.getEntityType() == EntityType.PLAYER){
				return (Player) entityEvent.getEntity();
			}
		}
		
		if(event instanceof InventoryEvent){
			InventoryEvent inventoryEvent = (InventoryEvent) event;
			if(inventoryEvent.getInventory().getHolder() instanceof Player){
				return (Player) inventoryEvent.getInventory().getHolder();
			}
		}
		
		if(event instanceof InventoryMoveItemEvent){
			InventoryMoveItemEvent inventoryMoveItemEvent = (InventoryMoveItemEvent) event;
			if(inventoryMoveItemEvent.getSource().getHolder() instanceof Player){
				return (Player) inventoryMoveItemEvent.getSource().getHolder();
			}
		}
		
		if(event instanceof InventoryPickupItemEvent){
			InventoryPickupItemEvent pickupItemEvent = (InventoryPickupItemEvent) event;
			if(pickupItemEvent.getInventory().getHolder() instanceof Player){
				return (Player) pickupItemEvent.getInventory().getHolder();
			}
		}
		
		if(event instanceof PlayerEvent){
			return ((PlayerEvent) event).getPlayer();
		}
		
		if(event instanceof PlayerLeashEntityEvent){
			return ((PlayerLeashEntityEvent) event).getPlayer();
		}
		
		
		if(event instanceof PlayerSendChannelChatMessageEvent){
			return ((PlayerSendChannelChatMessageEvent) event).getPlayer();
		}
		
		if(event instanceof VehicleEvent){
			if(event instanceof VehicleEntityCollisionEvent){
				VehicleEntityCollisionEvent vecevent = (VehicleEntityCollisionEvent) event;
				if(vecevent.getEntity() instanceof Player){
					return (Player) vecevent.getEntity();
				}
			}
			
			if(event instanceof VehicleEnterEvent){
				VehicleEnterEvent vehicleEnterEvent = (VehicleEnterEvent) event;
				if(vehicleEnterEvent.getEntered() instanceof Player){
					return (Player) vehicleEnterEvent.getEntered();
				}
			}
			
			if(event instanceof VehicleExitEvent){
				VehicleExitEvent vehicleExitEvent = (VehicleExitEvent) event;
				if(vehicleExitEvent.getExited() instanceof Player){
					return (Player) vehicleExitEvent.getExited();
				}
			}
		}
		

		//RaC-Plugin Events:		
		if(event instanceof LevelEvent){
			return Bukkit.getPlayer(((LevelEvent) event).getPlayerName());
		}
		
		if(event instanceof HolderSelectEvent){
			return ((HolderSelectEvent) event).getPlayer();
		}
		
		if(event instanceof PlayerSendChannelChatMessageEvent){
			return ((PlayerSendChannelChatMessageEvent)event).getPlayer();
		}
		
		if(event instanceof TraitTriggerEvent){
			return null; //This can not be interesting for a Trait.
		}
		
		
		
		return null;
	}
	
	/**
	 * This method can be Overriden when specific identifying
	 * of a player has to be done.
	 * 
	 * This method is called BEFORE checking the events in {@link #getReleventPlayer(Event)}.
	 * 
	 * Returning null proceeds with the default checks.
	 * Returning a player (not null) stops the search and returns the player.
	 * 
	 * @param event to check
	 * @return the Player found or null if not found or wanted.
	 */
	protected Player getReleventPlayerBefore(Event event){
		return null;
	}

	@Override
	public boolean checkRestrictions(Player player) {
		if(player == null) return true;
		
		String playerName = player.getName();
		int playerLevel = plugin.getPlayerManager().getPlayerLevelManager(playerName).getCurrentLevel();
		if(playerLevel < minimumLevel || playerLevel > maximumLevel) return false;
		
		Biome currentBiome = player.getLocation().getBlock().getBiome();
		if(!biomes.contains(currentBiome)) return false;
		
		return true;
	}
	
}
