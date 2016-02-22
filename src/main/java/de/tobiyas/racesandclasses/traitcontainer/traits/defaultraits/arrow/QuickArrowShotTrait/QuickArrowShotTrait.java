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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.arrow.QuickArrowShotTrait;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.traits.arrows.AbstractArrow;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class QuickArrowShotTrait extends AbstractArrow {
	
	/**
	 * The amounts of arrows fired
	 */
	private int amountArrows = 4;
	
	/**
	 * every how many ticks an arrow is fired.
	 */
	private int every = 5;
	
	/**
	 * If the Arrows should be consumed
	 */
	private boolean useArrow = true;
	
	
	
	public QuickArrowShotTrait(){
	}

	
	@TraitEventsUsed(registerdClasses = {})
	@Override
	public void generalInit(){
	}
	
	@Override
	public String getName() {
		return "QuickArrowShotTrait";
	}

	@Override
	protected String getPrettyConfigIntern(){
		return "fires " + amountArrows + " arrows";
	}

	
	
	@TraitConfigurationNeeded(fields = {
			@TraitConfigurationField(fieldName = "amount", classToExpect = Integer.class, optional = true),
			@TraitConfigurationField(fieldName = "useArrow", classToExpect = Boolean.class, optional = true),
			@TraitConfigurationField(fieldName = "every", classToExpect = Integer.class, optional = true),
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		amountArrows = configMap.getAsInt("amount", 4);			
		every = configMap.getAsInt("every", 5);
		useArrow = configMap.getAsBool("useArrow", true);
	}

	
	@Override
	protected boolean onShoot(EntityShootBowEvent event){
		final Player shooter = (Player) event.getEntity();
		
		final double speed = event.getProjectile().getVelocity().length();
		
		int modAmount = modifyToPlayer(RaCPlayerManager.get().getPlayer(shooter), amountArrows, "amount");
		for(int i = 1; i < modAmount; i++){
			new BukkitRunnable(){
				@Override
				public void run() {
					if(useArrow && !removeArrow(shooter)){
						this.cancel();
						return;
					}
					
					Arrow arrow = shooter.launchProjectile(Arrow.class);
					Vector vec = arrow.getVelocity().normalize().multiply(speed);
					arrow.setVelocity(vec);
				}
			}.runTaskLater((Plugin)RacesAndClasses.getPlugin(), i * every);
		}
		
		return true;
	}
	
	/**
	 * Removes an Arrow from the Player.
	 * 
	 * @param shooter to remove from
	 * 
	 * @return true if worked, false otherwise.
	 */
	private boolean removeArrow(Player shooter) {
		for(int i = 0; i < shooter.getInventory().getSize(); i++){
			ItemStack item = shooter.getInventory().getItem(i);
			if(item == null || item.getType() != Material.ARROW) continue;
			
			int newValue = item.getAmount() - 1;
			item.setAmount(newValue);
			if(newValue > 0) shooter.getInventory().setItem(i, item);
			else shooter.getInventory().setItem(i, null);

			return true;
		}

		return false;
	}

	@Override
	protected boolean onHitEntity(EntityDamageByEntityEvent event){
		return false;
	}

	@Override
	protected String getArrowName(){
		return "QuickArrowShotTrait";
	}

	@Override
	protected boolean onHitLocation(ProjectileHitEvent event) {
		//Not needed
		return false;
	}

	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "You can shoot your arrows with more force.");
		return helpList;
	}
	
	
	@TraitInfos(category="arrow", traitName="QuickArrowShotTrait", visible=true)
	@Override
	public void importTrait() {
	}

}
