/*******************************************************************************
 * Copyright 2014 Tobias Welther
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.tobiyas.racesandclasses.traitcontainer.interfaces;

import static de.tobiyas.racesandclasses.translation.languages.Keys.trait_cooldown;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.listeners.generallisteners.PlayerLastDamageListener;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.TraitRestriction;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.TraitWithRestrictions;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigParser;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;
import de.tobiyas.racesandclasses.util.traitutil.TraitVisible;

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
	protected final Set<Biome> biomes = new HashSet<Biome>(Arrays.asList(Biome.values()));
	
	/**
	 * The set of Items NEEDED to be weared.s
	 */
	protected final Set<Material> wearing = new HashSet<Material>();
	
	/**
	 * Tells if the Trait only works in the water
	 */
	protected boolean onlyInWater = false;
	
	/**
	 * Tells if the Trait only works on land
	 */
	protected boolean onlyOnLand = false;

	/**
	 * Tells if the Trait only works in lava
	 */
	protected boolean onlyInLava = false;
	
	/**
	 * Tells if the Trait only works on Snow
	 */
	protected boolean onlyOnSnow = false;
	
	/**
	 * Tells if the Trait can only trigger in Night.
	 */
	protected boolean onlyInNight = false;
	
	/**
	 * Tells if the Trait can only trigger on Day.
	 */
	protected boolean onlyOnDay = false;
	
	/**
	 * The Time the Trait has cooldown. (in Seconds)
	 */
	protected int cooldownTime = 0;
	
	/**
	 * The DisplayName to show.
	 */
	protected String displayName;
	
	/**
	 * The Elevation the player has to be above
	 */
	protected int aboveElevation = Integer.MIN_VALUE;
	
	/**
	 * The Elevation the player has to be below
	 */
	protected int belowElevation = Integer.MAX_VALUE;
	
	/**
	 * Tells if the Trait can only be triggered in the Rain.
	 */
	protected boolean onlyInRain = false;

	/**
	 * Tells if the Trait may only be used after player has been damaged.
	 * <br>Time in seconds.
	 * <br> onlyAfterDamage <= 0 = no check.
	 */
	protected int onlyAfterDamaged = -1;
	
	/**
	 * Tells if the Trait may only be used after player has NOT been damaged.
	 * <br>Time in seconds.
	 * <br> onlyAfterNotDamaged <= 0 = no check.
	 */
	protected int onlyAfterNotDamaged = -1;

	/**
	 * The maximal uplink time to show.
	 */
	protected long minUplinkShowTime = 3; 
		
	/**
	 * This disables the Cooldown notice.
	 */
	protected boolean disableCooldownNotice = false;
	
	/**
	 * Tells the Trait can be activated only on certain blocks.
	 */
	protected final List<Material> onlyOnBlocks = new LinkedList<Material>();
	
	/**
	 * Tells the Trait can NOT activated be activated on certain blocks.
	 */
	protected final List<Material> notOnBlocks = new LinkedList<Material>();
	
	/**
	 * Tells the Trait can be activated while the player sneaks.
	 */
	protected boolean onlyWhileSneaking = false;
	
	/**
	 * Tells the Trait can be activated while the player does NOT sneaks.
	 */
	protected boolean onlyWhileNotSneaking = false;

	/**
	 * The needed Permission for the skill
	 */
	protected String neededPermission = "";
	

	/**
	 * The Description of the Trait.
	 */
	protected String traitDiscription = "";
	
	/**
	 * If the Trait is visible.
	 */
	protected boolean visible = true;
	
	
	/**
	 * The holders of the Trait.
	 */
	protected final Set<AbstractTraitHolder> holders = new HashSet<AbstractTraitHolder>();
	
	
	/**
	 * The ConfigTotal of the Trait
	 */
	protected TraitConfiguration currentConfig;
	
	/**
	 * The map of Uplink that can be notified.
	 */
	protected final HashMap<String,Long> uplinkNotifyList = new HashMap<String, Long>();
	

	@Override
	public void addTraitHolder(AbstractTraitHolder abstractTraitHolder) {
		this.holders.add(abstractTraitHolder);
	}

	@Override
	public Set<AbstractTraitHolder> getTraitHolders() {
		return holders;
	}

	
	@SuppressWarnings("deprecation")
	@TraitConfigurationNeeded(fields = {
		@TraitConfigurationField(fieldName = MIN_LEVEL_PATH, classToExpect = Integer.class, optional = true),	
		@TraitConfigurationField(fieldName = MAX_LEVEL_PATH, classToExpect = Integer.class, optional = true),	
		@TraitConfigurationField(fieldName = BIOME_PATH, classToExpect = List.class, optional = true),
		@TraitConfigurationField(fieldName = ONLY_ON_BLOCK_PATH, classToExpect = List.class, optional = true),
		@TraitConfigurationField(fieldName = ONLY_IN_WATER_PATH, classToExpect = Boolean.class, optional = true),
		@TraitConfigurationField(fieldName = ONLY_ON_LAND_PATH, classToExpect = Boolean.class, optional = true),
		@TraitConfigurationField(fieldName = ONLY_IN_LAVA_PATH, classToExpect = Boolean.class, optional = true),
		@TraitConfigurationField(fieldName = ONLY_ON_SNOW, classToExpect = Boolean.class, optional = true),		
		@TraitConfigurationField(fieldName = ONLY_ON_DAY_PATH, classToExpect = Boolean.class, optional = true),
		@TraitConfigurationField(fieldName = ONLY_IN_NIGHT_PATH, classToExpect = Boolean.class, optional = true),
		@TraitConfigurationField(fieldName = ONLY_IN_RAIN_PATH, classToExpect = Boolean.class, optional = true),
		@TraitConfigurationField(fieldName = ONLY_AFTER_DAMAGED_PATH, classToExpect = Integer.class, optional = true),
		@TraitConfigurationField(fieldName = ONLY_AFTER_NOT_DAMAGED_PATH, classToExpect = Integer.class, optional = true),
		@TraitConfigurationField(fieldName = ONLY_WHILE_SNEAKING_PATH, classToExpect = Boolean.class, optional = true),
		@TraitConfigurationField(fieldName = ONLY_WHILE_NOT_SNEAKING_PATH, classToExpect = Boolean.class, optional = true),
		@TraitConfigurationField(fieldName = COOLDOWN_TIME_PATH, classToExpect = Integer.class, optional = true),
		@TraitConfigurationField(fieldName = ABOVE_ELEVATION_PATH, classToExpect = Integer.class, optional = true),
		@TraitConfigurationField(fieldName = BELOW_ELEVATION_PATH, classToExpect = Integer.class, optional = true),
		@TraitConfigurationField(fieldName = DISPLAY_NAME_PATH, classToExpect = String.class, optional = true),
		@TraitConfigurationField(fieldName = DESCRIPTION_PATH, classToExpect = String.class, optional = true),
		@TraitConfigurationField(fieldName = NEEDED_PERMISSION_PATH, classToExpect = String.class, optional = true),
		@TraitConfigurationField(fieldName = MIN_UPLINK_SHOW_PATH, classToExpect = Integer.class, optional = true),
		@TraitConfigurationField(fieldName = DISABLE_COOLDOWN_NOTICE_PATH, classToExpect = Boolean.class, optional = true),
	})
	
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		this.currentConfig = configMap;
		
		//If a permission is needed
		if(configMap.containsKey(TraitWithRestrictions.NEEDED_PERMISSION_PATH)){
			this.neededPermission = configMap.getAsString(TraitWithRestrictions.NEEDED_PERMISSION_PATH);
		}
		
		//checks if the Trait is visible.
		if(configMap.containsKey(TraitWithRestrictions.VISIBLE_PATH)){
			this.visible = configMap.getAsBool(TraitWithRestrictions.VISIBLE_PATH);
		}
		if(!TraitVisible.isVisible(this)) this.visible = false;
		
		//Gets the Cooldown of the Trait.
		if(configMap.containsKey(TraitWithRestrictions.COOLDOWN_TIME_PATH)){
			this.cooldownTime = (Integer) configMap.get(TraitWithRestrictions.COOLDOWN_TIME_PATH);
		}
		
		//Description
		if(configMap.containsKey(TraitWithRestrictions.DESCRIPTION_PATH)){
			this.traitDiscription = (String) configMap.get(TraitWithRestrictions.DESCRIPTION_PATH);
		}
		
		//Reads the min level for the trait if present
		if(configMap.containsKey(TraitWithRestrictions.MIN_LEVEL_PATH)){
			this.minimumLevel = (Integer) configMap.get(TraitWithRestrictions.MIN_LEVEL_PATH);
		}

		//Reads the max level for the trait if present
		if(configMap.containsKey(TraitWithRestrictions.MAX_LEVEL_PATH)){
			this.maximumLevel = (Integer) configMap.get(TraitWithRestrictions.MAX_LEVEL_PATH);
		}
		
		//Reads the only after damage value
		if(configMap.containsKey(TraitWithRestrictions.ONLY_AFTER_DAMAGED_PATH)){
			this.onlyAfterDamaged = (Integer) configMap.get(TraitWithRestrictions.ONLY_AFTER_DAMAGED_PATH);
		}

		//Reads the only after damage value
		if(configMap.containsKey(TraitWithRestrictions.ONLY_AFTER_NOT_DAMAGED_PATH)){
			this.onlyAfterNotDamaged = (Integer) configMap.get(TraitWithRestrictions.ONLY_AFTER_NOT_DAMAGED_PATH);
		}

		//above elevation
		if(configMap.containsKey(TraitWithRestrictions.ABOVE_ELEVATION_PATH)){
			this.aboveElevation = (Integer) configMap.get(TraitWithRestrictions.ABOVE_ELEVATION_PATH);
		}
		
		//below elevation
		if(configMap.containsKey(TraitWithRestrictions.BELOW_ELEVATION_PATH)){
			this.belowElevation = (Integer) configMap.get(TraitWithRestrictions.BELOW_ELEVATION_PATH);
		}
		
		//Reads the biomes for the trait if present
		if(configMap.containsKey(TraitWithRestrictions.BIOME_PATH)){
			try{
				@SuppressWarnings("unchecked")
				List<String> stringBiomes = (List<String>) configMap.get(TraitWithRestrictions.BIOME_PATH);
				this.biomes.clear();
				
				for(String biome : stringBiomes){
					biome = biome.toUpperCase();
					Biome biom = Biome.valueOf(biome);
					
					if(biom != null){
						biomes.add(biom);
					}
				}
			}catch(Exception exp){}
		}

		//Reads the blocks for the trait if present
		if(configMap.containsKey(TraitWithRestrictions.ONLY_ON_BLOCK_PATH)){
			try{
				@SuppressWarnings("unchecked")
				List<String> stringBlocks = (List<String>) configMap.get(TraitWithRestrictions.ONLY_ON_BLOCK_PATH);
				this.onlyOnBlocks.clear();
				
				for(String block : stringBlocks){
					block = block.toUpperCase();
					Material type =  null;
					//try String parsing
					try{
						type = Material.valueOf(block);
					}catch(IllegalArgumentException exp){}
					
					//try id parsing
					if(type == null){
						try{
							int id = Integer.parseInt(block);
							type = Material.getMaterial(id);
						}catch(NumberFormatException exp){}
					}
					
					if(type != null){
						onlyOnBlocks.add(type);
					}
				}
			}catch(Exception exp){}
		}

		//Reads the blocks for the trait if present
		if(configMap.containsKey(TraitWithRestrictions.NOT_ON_BLOCK_PATH)){
			try{
				@SuppressWarnings("unchecked")
				List<String> stringBlocks = (List<String>) configMap.get(TraitWithRestrictions.NOT_ON_BLOCK_PATH);
				this.notOnBlocks.clear();
				
				for(String block : stringBlocks){
					block = block.toUpperCase();
					Material type =  null;
					//try String parsing
					try{
						type = Material.valueOf(block);
					}catch(IllegalArgumentException exp){}
					
					//try id parsing
					if(type == null){
						try{
							int id = Integer.parseInt(block);
							type = Material.getMaterial(id);
						}catch(NumberFormatException exp){}
					}
					
					if(type != null){
						notOnBlocks.add(type);
					}
				}
			}catch(Exception exp){}
		}

		//Reads the Armor for the trait to wear
		if(configMap.containsKey(TraitWithRestrictions.WEARING_PATH)){
			try{
				@SuppressWarnings("unchecked")
				List<String> stringBlocks = (List<String>) configMap.get(TraitWithRestrictions.WEARING_PATH);
				this.wearing.clear();
				
				for(String armor : stringBlocks){
					armor = armor.toUpperCase();
					Material type = Material.valueOf(armor);
					
					if(type != null){
						wearing.add(type);
					}
				}
			}catch(Exception exp){}
		}
		
		//Only in water
		if(configMap.containsKey(TraitWithRestrictions.ONLY_IN_WATER_PATH)){
			this.onlyInWater = (Boolean) configMap.get(TraitWithRestrictions.ONLY_IN_WATER_PATH);
		}
		
		//Only in Rain
		if(configMap.containsKey(TraitWithRestrictions.ONLY_IN_RAIN_PATH)){
			this.onlyInRain = (Boolean) configMap.get(TraitWithRestrictions.ONLY_IN_RAIN_PATH);
		}

		//Only on snow
		if(configMap.containsKey(TraitWithRestrictions.ONLY_ON_SNOW)){
			this.onlyOnSnow = (Boolean) configMap.get(TraitWithRestrictions.ONLY_ON_SNOW);
		}

		//Only on land
		if(configMap.containsKey(TraitWithRestrictions.ONLY_ON_LAND_PATH)){
			this.onlyOnLand = (Boolean) configMap.get(TraitWithRestrictions.ONLY_ON_LAND_PATH);
		}
		
		//Only on Day
		if(configMap.containsKey(TraitWithRestrictions.ONLY_ON_DAY_PATH)){
			this.onlyOnDay = (Boolean) configMap.get(TraitWithRestrictions.ONLY_ON_DAY_PATH);
		}
		
		//Only in Night
		if(configMap.containsKey(TraitWithRestrictions.ONLY_IN_NIGHT_PATH)){
			this.onlyInNight = (Boolean) configMap.get(TraitWithRestrictions.ONLY_IN_NIGHT_PATH);
		}
				
		//Display Name
		if(configMap.containsKey(TraitWithRestrictions.DISPLAY_NAME_PATH)){
			this.displayName = (String) configMap.get(TraitWithRestrictions.DISPLAY_NAME_PATH);
		}
		
		//Sneaking
		if(configMap.containsKey(TraitWithRestrictions.ONLY_WHILE_SNEAKING_PATH)){
			this.onlyWhileSneaking = (Boolean) configMap.get(TraitWithRestrictions.ONLY_WHILE_SNEAKING_PATH);
		}
		
		//Not sneaking
		if(configMap.containsKey(TraitWithRestrictions.ONLY_WHILE_NOT_SNEAKING_PATH)){
			this.onlyWhileNotSneaking = (Boolean) configMap.get(TraitWithRestrictions.ONLY_WHILE_NOT_SNEAKING_PATH);
		}
		
		//min uplink to show.
		if(configMap.containsKey(TraitWithRestrictions.MIN_UPLINK_SHOW_PATH)){
			this.minUplinkShowTime = (Integer) configMap.get(TraitWithRestrictions.MIN_UPLINK_SHOW_PATH);
		}
		
		//disable uplink Notice.
		if(configMap.containsKey(TraitWithRestrictions.DISABLE_COOLDOWN_NOTICE_PATH)){
			this.disableCooldownNotice = (Boolean) configMap.get(TraitWithRestrictions.DISABLE_COOLDOWN_NOTICE_PATH);
		}
	}
	

	@Override
	public TraitConfiguration getCurrentconfig(){
		return currentConfig;
	}
	
	
	@Override
	public void deInit(){
		//Meant to be overwritten!!
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
		List<TraitConfigurationField> configFields = TraitConfigParser.getAllTraitConfigFieldsOfTrait(this);
		List<String> optionalFields = new LinkedList<String>();

		for(TraitConfigurationField field : configFields){
			if(field.optional() == true){
				optionalFields.add(field.fieldName());
			}
		}
		
		return optionalFields;
	}

	
	@Override
	public final String getPrettyConfiguration() {
		if("".equals(traitDiscription)){
			return getPrettyConfigIntern();
		}
		
		return traitDiscription;
	}
	
	/**
	 * Returns the Pretty config.
	 * @return pretty name
	 */
	protected abstract String getPrettyConfigIntern();
	
	@Override
	public boolean checkRestrictions(EventWrapper wrapper) {
		RaCPlayer player = wrapper.getPlayer();
		if(player == null) return true;
		
		//players not online will most likely fail everything.
		if(!player.isOnline()) return false;
		
		String playerName = player.getName();
		int playerLevel = player.getLevelManager().getCurrentLevel();
		if(playerLevel < minimumLevel || playerLevel > maximumLevel) return false;
		
		Location playerLocation = player.getPlayer().getLocation();
		Block feetBlock = playerLocation.getBlock();
		Block locBlock = feetBlock.getRelative(BlockFace.DOWN);
		
		Material feetType = feetBlock.getType();
		Material belowFeetType = locBlock.getType();

		Biome currentBiome = locBlock.getBiome();
		if(!biomes.contains(currentBiome)) return false;
		
		//Check if player is in water
		if(onlyInWater){
			if(feetType != Material.WATER && feetType != Material.STATIONARY_WATER){
				triggerButHasRestriction(TraitRestriction.OnlyInWater, wrapper);
				return false;
			}
		}

		//Sneaking
		if(onlyWhileSneaking){
			if(!player.getPlayer().isSneaking()) {
				triggerButHasRestriction(TraitRestriction.OnlyWhileSneaking, wrapper);
				return false;
			}
		}
		
		//Not sneaking
		if(onlyWhileNotSneaking){
			if(player.getPlayer().isSneaking()) {
				triggerButHasRestriction(TraitRestriction.OnlyWhileNotSneaking, wrapper);
				return false;
			}
		}
		
		//check if player is on land
		if(onlyOnLand){
			if(feetType == Material.WATER || feetType == Material.STATIONARY_WATER){
				triggerButHasRestriction(TraitRestriction.OnlyOnLand, wrapper);
				return false;
			}
		}
		
		//check permission
		if(!neededPermission.isEmpty()){
			if(!plugin.getPermissionManager().checkPermissionsSilent(wrapper.getPlayer().getPlayer(), neededPermission)){
				triggerButHasRestriction(TraitRestriction.NeededPermission, wrapper);
				return false;
			}
		}
		
		//check if player is in lava
		if(onlyInLava){
			if(feetType != Material.LAVA && feetType != Material.STATIONARY_LAVA){
				triggerButHasRestriction(TraitRestriction.OnlyInLava, wrapper);
				return false;
			}
		}

		//check if player is on Snow
		if(onlyOnSnow){
			if(!(feetType == Material.SNOW || feetType == Material.SNOW_BLOCK
					|| belowFeetType == Material.SNOW || belowFeetType == Material.SNOW_BLOCK)){
				triggerButHasRestriction(TraitRestriction.OnlyOnSnow, wrapper);
				return false;
			}
		}
		
		//check if player is in Rain
		if(onlyInRain){
			if(!wrapper.getWorld().hasStorm()) return false;
			int ownY = feetBlock.getY();
			int highestY = feetBlock.getWorld().getHighestBlockYAt(feetBlock.getX(), feetBlock.getZ());
			//This means having a roof over oneself
			if(ownY != highestY) {
				triggerButHasRestriction(TraitRestriction.OnlyInRain, wrapper);
				return false;
			}
		}
		
		
		//checking for wearing
		if(!wearing.isEmpty()){
			boolean found = false;
			for(Material mat : wearing){
				found = false;
				for(ItemStack item : player.getPlayer().getInventory().getArmorContents()){
					if(item == null) continue;
					
					if(mat == item.getType()){
						found = true;
						break;
					}
				}
				
				if(!found) {
					triggerButHasRestriction(TraitRestriction.Wearing, wrapper);
					return false;
				}
			}
		}
		
		//check above elevation
		if(aboveElevation != Integer.MIN_VALUE){
			if(feetBlock.getY() <= aboveElevation) {
				triggerButHasRestriction(TraitRestriction.AboveLevitation, wrapper);
				return false;
			}
		}

		//check below elevation
		if(belowElevation != Integer.MAX_VALUE){
			if(feetBlock.getY() >= belowElevation) {
				triggerButHasRestriction(TraitRestriction.BelowLevitation, wrapper);
				return false;
			}
		}

		//check onlyAfterDamaged
		if(onlyAfterDamaged > 0){
			int lastDamage = PlayerLastDamageListener.getTimePassedSinceLastDamageInSeconds(playerName);
			if(lastDamage > onlyAfterDamaged) {
				triggerButHasRestriction(TraitRestriction.OnlyAfterDamage, wrapper);
				return false;
			}
		}
		
		//check onlyAfterDamaged
		if(onlyAfterNotDamaged > 0){
			int lastDamage = PlayerLastDamageListener.getTimePassedSinceLastDamageInSeconds(playerName);
			if(onlyAfterNotDamaged > lastDamage) {
				triggerButHasRestriction(TraitRestriction.OnlyAfterNotDamage, wrapper);
				return false;
			}
		}
		
		//check blocks on
		if(!onlyOnBlocks.isEmpty()){
			if(!onlyOnBlocks.contains(belowFeetType)) {
				triggerButHasRestriction(TraitRestriction.OnlyOnBlock, wrapper);
				return false;
			}
		}

		//check blocks on
		if(!notOnBlocks.isEmpty()){
			if(notOnBlocks.contains(belowFeetType)) {
				triggerButHasRestriction(TraitRestriction.NotOnBlock, wrapper);
				return false;
			}
		}
		
		//check cooldown
		String cooldownName = "trait." + getDisplayName();
		int playerUplinkTime = plugin.getCooldownManager().stillHasCooldown(playerName, cooldownName);
		
		if(playerUplinkTime > 0){
			if(!triggerButHasUplink(wrapper)){
				triggerButHasRestriction(TraitRestriction.Cooldown, wrapper);
				if(notifyTriggeredUplinkTime(wrapper)){
					//if notices are disabled, we don't need to do anything here.
					if(disableCooldownNotice) return false;
					
					//we still check to not spam players.
					long lastNotified = uplinkNotifyList.containsKey(playerName)
							? uplinkNotifyList.get(playerName)
							: 0;
					
					long maxTime = minUplinkShowTime * 1000;		
							
					if(new Date().after(new Date(lastNotified + maxTime))){
						LanguageAPI.sendTranslatedMessage(player.getPlayer(), trait_cooldown, 
								"seconds", String.valueOf(playerUplinkTime),
								"name", getDisplayName());
						uplinkNotifyList.put(playerName, new Date().getTime());
					}
				}
			}
			
			return false;
		}
		
		//Only check if we really need. Otherwise we would use resources we don't need
		if(onlyOnDay || onlyInNight){
			//Daytime check
			int hour = ((int) (wrapper.getWorld().getTime() / 1000l) + 8) % 24;
			boolean isNight = hour > 18 || hour < 6;
			boolean isDay = hour > 6 && hour < 18;
			
			//Check day
			if(onlyOnDay && isNight && !onlyInNight){
				triggerButHasRestriction(TraitRestriction.OnlyOnDay, wrapper);
				return false;
			}
			
			//Check night
			if(onlyInNight && isDay && !onlyOnDay){
				triggerButHasRestriction(TraitRestriction.OnlyInNight, wrapper);
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public boolean isStackable(){
		return true;
	}

	@Override
	public int getMaxUplinkTime() {
		return cooldownTime;
	}

	@Override
	public boolean triggerButHasUplink(EventWrapper wrapper) {
		return false;
	}
	
	@Override
	public String toString(){
		return getDisplayName();
	}

	
	/**
	 * Can and should be overriden.
	 */
	@Override
	public boolean notifyTriggeredUplinkTime(EventWrapper wrapper) {
		return true;
	}

	@Override
	public String getDisplayName() {
		return displayName == null ? getName() : displayName;
	}

	@Override
	public boolean isInLevelRange(int level) {
		if(level < minimumLevel) return false;
		if(level > maximumLevel) return false;
		
		return true;
	}
	
	@Override
	public boolean isVisible(){
		return visible;
	}
	
	@Override
	public void triggerButHasRestriction(TraitRestriction restriction, EventWrapper wrapper){
	}
	
}
