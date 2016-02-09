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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.passive.NoHungerTrait;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.scheduler.BukkitTask;

import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.AbstractBasicTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;
import de.tobiyas.util.schedule.DebugBukkitRunnable;

public class NoHungerTrait extends AbstractBasicTrait {
	
	/**
	 * If we should always fill the Hunger-bar.
	 */
	private boolean alwaysFill = false;
	
	/**
	 * The Refiller to use, or not.
	 */
	private BukkitTask refiller;
	
	
	public NoHungerTrait(){
		super();
	}
	

	@Override
	public String getName() {
		return "NoHungerTrait";
	}


	@TraitInfos(category="passive", traitName="NoHungerTrait", visible=true)
	@Override
	public void importTrait() {}
	
	
	@TraitConfigurationNeeded( fields = {
				@TraitConfigurationField(classToExpect = Boolean.class, fieldName = "alwaysFill", optional = true)
			})
	@Override
	public void setConfiguration(TraitConfiguration configMap)
			throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		if(configMap.containsKey("alwaysFill")){
			this.alwaysFill = configMap.getAsBool("alwaysFill");
			
			if(alwaysFill){
				if(refiller != null) refiller.cancel();
				refiller = new Refiller().runTaskTimer(plugin, 20, 20);
			}
		}
		
		
	}
	
	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "The trait removes Hunger.");
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
		return "no hunger";
	}
	
	
	@EventHandler
	public void removeHungerReduce(FoodLevelChangeEvent event){
		HumanEntity humanEntity = event.getEntity();
		if(humanEntity instanceof Player){
			RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(humanEntity.getUniqueId());
			
			if(racPlayer != null && TraitHolderCombinder.checkContainer(racPlayer, this)){
				event.setCancelled(true);
				racPlayer.setFoodLevel(20);
			}
		}
	}
	
	
	
	private class Refiller extends DebugBukkitRunnable {
		
		public Refiller() {
			super("HungerRefiller");
		}
		
		@Override
		public void runIntern(){
			for(AbstractTraitHolder holder : getTraitHolders()){
				for(RaCPlayer player : holder.getHolderManager().getAllPlayersOfHolder(holder)){
					try{
						player.setFoodLevel(20);
						player.setSaturation(20f);						
					}catch(Throwable exp){}
				}
				
			}
		}
		
	}
	
}
