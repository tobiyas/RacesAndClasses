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
package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.arrow.MultishotArrowTrait;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.traits.arrows.AbstractArrow;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;
import de.tobiyas.racesandclasses.vollotile.Vollotile;

public class MultishotArrowTrait extends AbstractArrow {
	
	/**
	 * The amounts of arrows fired
	 */
	private int amountArrows;
	
	/**
	 * If the Arrows should be consumed
	 */
	private boolean useArrow = true;
	
	
	
	public MultishotArrowTrait(){
	}

	
	@TraitEventsUsed(registerdClasses = {})
	@Override
	public void generalInit(){
	}
	
	@Override
	public String getName() {
		return "MultishotArrowTrait";
	}

	@Override
	protected String getPrettyConfigIntern(){
		return "fires " + amountArrows + " arrows";
	}

	
	
	@TraitConfigurationNeeded(fields = {
			@TraitConfigurationField(fieldName = "amount", classToExpect = Integer.class),
			@TraitConfigurationField(fieldName = "useArrow", classToExpect = Boolean.class, optional = true)			
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		amountArrows = (Integer) configMap.get("amount");
		if(configMap.containsKey("useArrow")){
			useArrow = (Boolean) configMap.get("useArrow");
		}
	}

	
	@Override
	protected boolean onShoot(EntityShootBowEvent event){
		Player shooter = (Player) event.getEntity();
		
		Projectile arrow = (Projectile) event.getProjectile();
		Vector oldVelocity = arrow.getVelocity();
		
		final int angle = 10;
		int currentAngle = angle;
		
		int modAmount = modifyToPlayer(RaCPlayerManager.get().getPlayer(shooter), amountArrows, "amount");
		for(int i = 1; i < modAmount; i++){
			currentAngle = ((i + 1) /2 ) * angle;
			
			if(useArrow && !removeArrow(shooter)) return true;
			
			Vector newVelocity = calcNewVelocity(oldVelocity.clone(), currentAngle);
			newVelocity = calcNewVelocity(oldVelocity.clone(), currentAngle);
			Arrow rightArrow = shooter.launchProjectile(Arrow.class);
			rightArrow.setVelocity(newVelocity);
			rightArrow.setBounce(false);
			
			//if we do not use arrows, don't make it pickupable.
			if(!useArrow) Vollotile.get().makeArrowPickupable((Arrow) rightArrow, useArrow);
			
			i++;
			
			if(i < modAmount){
				if(useArrow && !removeArrow(shooter)) return true;
				newVelocity = calcNewVelocity(oldVelocity.clone(), -currentAngle);
				Arrow leftArrow = shooter.launchProjectile(Arrow.class);
				
				leftArrow.setVelocity(newVelocity);
				leftArrow.setBounce(false);
				
				//if we do not use arrows, don't make it pickupable.
				if(!useArrow) Vollotile.get().makeArrowPickupable((Arrow) leftArrow, useArrow);
			}
			
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


	/**
	 * Calculates a new Vector for the 
	 * 
	 * @param clone to calculate from
	 * @param currentAngle the angle in DEGREE to calculate
	 * 
	 * @return the newly rotated vector.
	 */
	private Vector calcNewVelocity(Vector clone, int currentAngle) {
		double rad = currentAngle * (Math.PI / 180d);
		double x = clone.getX();
		double y = clone.getY();
		
		double addX = (double)(x * Math.cos(rad)  -  y * Math.sin(rad));
		double addZ = (double)(x * Math.sin(rad)  +  y * Math.cos(rad));
		
		if(addX < 0.01 && addZ < 0.01){
			addX = rad; addZ = rad;
		}
		
		Vector additionVector = new Vector(addX, 0, addZ);
		clone.add(additionVector);
		
		return clone;
	}


	@Override
	protected boolean onHitEntity(EntityDamageByEntityEvent event){
		return false;
	}

	@Override
	protected String getArrowName(){
		return "Mulitshot Arrow";
	}

	@Override
	protected boolean onHitLocation(ProjectileHitEvent event) {
		return false;
	}

	
	public static List<String> getHelpForTrait(){
		List<String> helpList = new LinkedList<String>();
		helpList.add(ChatColor.YELLOW + "You can shoot your arrows with more force.");
		return helpList;
	}
	
	
	@TraitInfos(category="arrow", traitName="MultishotArrowTrait", visible=true)
	@Override
	public void importTrait() {
	}

}
