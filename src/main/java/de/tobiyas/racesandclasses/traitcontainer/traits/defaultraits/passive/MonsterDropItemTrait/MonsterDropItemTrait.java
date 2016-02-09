/*******************************************************************************
 * Copyright 2014 Tobiyas
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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.MonsterDropItemTrait;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapperFactory;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.AbstractBasicTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.TraitRestriction;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;
import de.tobiyas.util.collections.CollectionUtils;
import de.tobiyas.util.items.ItemMetaUtils;

public class MonsterDropItemTrait extends AbstractBasicTrait {
	
	/**
	 * The item to give.
	 */
	private ItemStack item;
	
	/**
	 * The Needed name of the Entity.
	 */
	private String neededName = "";
	
	/**
	 * The Types to drop on for entity -> chance.
	 */
	private final Map<EntityType,Double> dropChances = new HashMap<EntityType,Double>();
	
	

	@Override
	public String getName() {
		return "MonsterDropItemTrait";
	}


	@TraitInfos(category="passive", traitName="MonsterDropItemTrait", visible=true)
	@Override
	public void importTrait() {}
	
	
	@TraitConfigurationNeeded( fields = {
				@TraitConfigurationField(classToExpect = List.class, fieldName = "drops", optional = false),
				@TraitConfigurationField(classToExpect = String.class, fieldName = "monstername", optional = false),
				
				@TraitConfigurationField(classToExpect = Material.class, fieldName = "material", optional = false),
				@TraitConfigurationField(classToExpect = Integer.class, fieldName = "damage", optional = true),
				@TraitConfigurationField(classToExpect = String.class, fieldName = "name", optional = true),
				@TraitConfigurationField(classToExpect = List.class, fieldName = "lore", optional = true),
			})
	@Override
	public void setConfiguration(TraitConfiguration configMap)
			throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		Material mat = configMap.getAsMaterial("material", Material.STONE);
		this.item = new ItemStack(mat);
		
		//Add item damage:
		if(configMap.containsKey("damage")){
			this.item.setDurability((short) configMap.getAsInt("damage",0));
		}
		
		//Add item Name:
		if(configMap.containsKey("name")){
			String itemName = ChatColor.translateAlternateColorCodes('&', configMap.getAsString("name"));
			ItemMetaUtils.setDisplayNameOfItem(item, itemName);
		}
		
		//Add item Lore:
		if(configMap.containsKey("lore")){
			List<String> lore = CollectionUtils.translateChatColors(configMap.getAsStringList("lore"));
			ItemMeta meta = item.getItemMeta(); meta.setLore(lore); item.setItemMeta(meta);
		}
		
		
		//Parse monster stuff:
		if(configMap.containsKey("drops")){
			List<String> drops = configMap.getAsStringList("drops");
			this.dropChances.clear();
			this.dropChances.putAll(convertToDropMap(drops));
		}
		
		//Set the monster name.
		if(configMap.containsKey("monstername")){
			this.neededName = configMap.getAsString("monstername","");
		}
	}
	
	
	private static Map<EntityType,Double> convertToDropMap(List<String> toParse){
		Map<EntityType,Double> parsed = new HashMap<EntityType,Double>();
		for(String line : toParse){
			String[] split = line.split(Pattern.quote(";"));
			if(split.length != 2) continue;
			
			try{
				EntityType type = EntityType.valueOf(split[0].replace(" ", "_").toUpperCase());
				double percent = Double.parseDouble(split[1]);
				
				parsed.put(type, percent);
			}catch(Throwable exp){}
		}
		
		return parsed;
	}
	
	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "The trait drops stuff at the kill of monsters.");
		return helpList;
	}


	@Override
	public boolean canBeTriggered(EventWrapper arg0) {
		return false;
	}


	@Override
	public void generalInit() {
		plugin.registerEvents(this);
	}


	@Override
	public boolean isBetterThan(Trait arg) {
		return false;
	}


	@Override
	public TraitResults trigger(EventWrapper arg) {
		return TraitResults.False();
	}


	@Override
	protected String getPrettyConfigIntern() {
		return "Drops an item when killing monsters.";
	}
	
	
	private ItemStack playerKilled(EntityType type){
		if(!dropChances.containsKey(type)) return null;
		double percent = dropChances.get(type);
		if(Math.random()*100d > percent) return null;
		
		return item.clone();
	}
	
	
	@EventHandler(priority = EventPriority.LOW)
	public void monsterDied(EntityDeathEvent event){
		if(item == null) return;
		
		LivingEntity died = event.getEntity();
		if(died == null) return;
		
		Player killer = died.getKiller();
		if(killer == null) return;
		
		RaCPlayer racKiller = RaCPlayerManager.get().getPlayer(killer);
		if(racKiller == null) return;
		
		//Check for the name.
		if(!neededName.isEmpty()){
			String monsterName = ChatColor.stripColor(died.getCustomName());
			if(!neededName.equalsIgnoreCase(monsterName)) return;
		}
		
		//If not in this trait -> break.
		if(!TraitHolderCombinder.checkContainer(racKiller, this)) return;
		
		EventWrapper wrapper = EventWrapperFactory.buildOnlyWithplayer(racKiller);
		boolean canBeTriggered = super.checkRestrictions(wrapper) == TraitRestriction.None;
		if(!canBeTriggered) return;
		
		ItemStack drop = playerKilled(died.getType());
		event.getDrops().add(drop);
	}
	
}
