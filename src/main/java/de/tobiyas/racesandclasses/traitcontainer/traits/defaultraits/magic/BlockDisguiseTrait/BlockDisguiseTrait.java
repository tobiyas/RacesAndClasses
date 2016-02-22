/*******************************************************************************
 * Copyright 2014 Tob
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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.BlockDisguiseTrait;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.scheduler.BukkitTask;

import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.magic.AbstractContinousCostMagicSpellTrait;
import de.tobiyas.racesandclasses.translation.languages.Keys;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class BlockDisguiseTrait extends AbstractContinousCostMagicSpellTrait  {

	/**
	 * The set of currently disguised data.
	 */
	private final Set<DisguiseData> data = new HashSet<>();
	
	/**
	 * The Material to use.
	 */
	private Material disguiseMat = Material.HAY_BLOCK;
	
	/**
	 * the disguise data to use.
	 */
	private byte disguiseData = 0;
	
	/**
	 * The task used for ticking.
	 */
	private BukkitTask tickTask;
	
	
	
	@TraitEventsUsed(registerdClasses = {})
	@Override
	public void generalInit() {
		super.generalInit();
		
		tickTask = new de.tobiyas.util.schedule.DebugBukkitRunnable("DisguiseTraitTick") {
			@Override
			protected void runIntern() {
				Iterator<DisguiseData> it = data.iterator();
				while(it.hasNext()){
					DisguiseData data = it.next();
					data.tick();
					
					//if not valid any more, restore + remove!
					if(!data.isStillValid()){
						data.restoreOld();
						it.remove();
						
						//Remove!
						BlockDisguiseTrait.this.deactivate(data.getPlayer());
					}
				}
			}
		}.runTaskTimer(plugin, 10, 2);
	}


	@Override
	public String getName() {
		return "BlockDisguiseTrait";
	}

	
	@Override
	protected String getPrettyConfigIntern(){
		int time = everyXSeconds <= 0 ? durationInSeconds : everyXSeconds;
		return "duration: " + time + " seconds. Mana: " + cost;
	}

	
	@TraitInfos(category="magic", traitName="BlockDisguiseTrait", visible=true)
	@Override
	public void importTrait() {}

	
	@Override
	public void deInit() {
		super.deInit();
		
		//Restore the old data before! otherwise we lose stuff!
		for(DisguiseData data : this.data) data.restoreOld();
		this.data.clear();
		
		if(tickTask != null) tickTask.cancel();
	}
	
	
	@TraitConfigurationNeeded(fields = {
			@TraitConfigurationField(classToExpect=String.class, fieldName = "material", optional=true),
			@TraitConfigurationField(classToExpect=Integer.class, fieldName = "data", optional=true)
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		this.disguiseMat = configMap.getAsMaterial("material", Material.HAY_BLOCK);
		this.disguiseData = (byte) configMap.getAsInt("data", 0);
	}
	
	
	@Override
	public boolean isBetterThan(Trait trait) {
		if(!(trait instanceof BlockDisguiseTrait)) return false;
		
		BlockDisguiseTrait otherTrait = (BlockDisguiseTrait) trait;
		int time = everyXSeconds <= 0 ? durationInSeconds : everyXSeconds;
		int othertime = otherTrait.everyXSeconds <= 0 ? otherTrait.durationInSeconds : otherTrait.everyXSeconds;
		return time > othertime;
	}
	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "This trait disquises you as block.");
		return helpList;
	}


	@Override
	protected boolean activateIntern(RaCPlayer player) {
		if( getData(player) != null )return false;
		
		Block block = player.getLocation().getBlock();
		BlockState state = block.getState();
		if(!state.getClass().getSimpleName().equals("CraftBlockState")){
			//TODO send error message!
			return false;
		}
		
		//Generate data + tick once to be invis.
		DisguiseData data = new DisguiseData(player, player.getLocation().getBlock(), disguiseMat, disguiseData);
		this.data.add(data);
		
		LanguageAPI.sendTranslatedMessage(player, Keys.trait_toggled, "name", getDisplayName());
		return true;
	}
	
	
	
	/**
	 * If the player is currently using.
	 * @param player to check
	 * @return true if currently using.
	 */
	private DisguiseData getData(RaCPlayer player){
		for(DisguiseData data : this.data){
			if(data.getPlayer() == player) return data;
		}
		
		return null;
	}
	
	
	@Override
	protected boolean deactivateIntern(RaCPlayer player){
		DisguiseData data = getData(player);
		if(data != null) this.data.remove(data);
		
		LanguageAPI.sendTranslatedMessage(player, Keys.trait_faded, "name", getDisplayName());
		return true;
	}


	@Override
	protected boolean tickInternal(RaCPlayer player) { return true; } //not needed.

}
