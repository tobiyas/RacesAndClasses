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
package de.tobiyas.racesandclasses.traitcontainer.traits.arrows;

import static de.tobiyas.racesandclasses.translation.languages.Keys.arrow_change;

import java.util.List;
import java.util.concurrent.Callable;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.LazyMetadataValue;
import org.bukkit.metadata.MetadataValue;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.datacontainer.arrow.ArrowManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.PlayerAction;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.AbstractBasicTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier;


public abstract class AbstractArrow extends AbstractBasicTrait {
	
	protected RacesAndClasses plugin = RacesAndClasses.getPlugin();
	
	protected int duration;
	protected double totalDamage;
	
	protected int uplinkTime = 0;
	
	
	@Override
	public boolean canBeTriggered(EventWrapper wrapper){
		Event event = wrapper.getEvent();
		if(!(event instanceof PlayerInteractEvent || 
				event instanceof EntityShootBowEvent || 
				event instanceof ProjectileHitEvent ||
				event instanceof EntityDamageByEntityEvent)) return false;
		
		//Change ArrowType
		if(event instanceof PlayerInteractEvent){
			PlayerInteractEvent Eevent = (PlayerInteractEvent) event;
			if(!(Eevent.getAction() == Action.LEFT_CLICK_AIR || Eevent.getAction() == Action.LEFT_CLICK_BLOCK)) return false;

			Player player = Eevent.getPlayer();
			if(!isThisArrow(player)) return false;
			
			if(!TraitHolderCombinder.checkContainer(player.getUniqueId(), this)) return false;
			if(player.getItemInHand().getType() != Material.BOW) return false;
	
			return true;
		}
			
		//Projectile launch
		if(event instanceof EntityShootBowEvent){
			EntityShootBowEvent Eevent = (EntityShootBowEvent) event;
			if(Eevent.getEntity().getType() != EntityType.PLAYER) return false;
			Player player = (Player) Eevent.getEntity();
			if(!TraitHolderCombinder.checkContainer(player.getUniqueId(), this)) return false;			
			if(!isThisArrow(player)) return false;
			
			return true;
		}
		
		//Arrow Hit Location
		if(event instanceof ProjectileHitEvent){
			ProjectileHitEvent Eevent = (ProjectileHitEvent) event;
			if(Eevent.getEntityType() != EntityType.ARROW) return false;
			if(CompatibilityModifier.Shooter.getShooter(Eevent.getEntity()) == null) return false;
			
			Arrow arrow = (Arrow) Eevent.getEntity();
			if(CompatibilityModifier.Shooter.getShooter(arrow).getType() != EntityType.PLAYER) return false;
			
			Player player = (Player) CompatibilityModifier.Shooter.getShooter(arrow);
			if(!TraitHolderCombinder.checkContainer(player.getUniqueId(), this)) return false;

			if(arrow.getMetadata(ARROW_META_KEY).isEmpty()) return false;
			List<MetadataValue> metaValues = arrow.getMetadata(ARROW_META_KEY);
			
			boolean found = false;
			for(MetadataValue value : metaValues){
				if(getName().equals(value.value())){
					found = true;
					break;
				}
			}
			
			if(!found) return false;
			
			if(!isThisArrow(player)) return false;
			
			return true;
		}
		
		//Arrow Hits target
		if(event instanceof EntityDamageByEntityEvent){
			EntityDamageByEntityEvent Eevent = (EntityDamageByEntityEvent) event;
			if(Eevent.getDamager().getType() != EntityType.ARROW) return false;
			
			Arrow realArrow = (Arrow) Eevent.getDamager();
			LivingEntity shooter = CompatibilityModifier.Shooter.getShooter(realArrow);
			
			if(shooter == null || realArrow == null || realArrow.isDead()) return false;
			if(((org.bukkit.entity.LivingEntity)shooter).getType() != EntityType.PLAYER) return false;
			
			if(Eevent.getEntity() == shooter && realArrow.getTicksLived() < 10)
				return false;

			Player player = (Player) shooter;
			if(!TraitHolderCombinder.checkContainer(player.getUniqueId(), this)) return false;
			
			if(realArrow.getMetadata(ARROW_META_KEY).isEmpty()) return false;
			List<MetadataValue> metaValues = realArrow.getMetadata(ARROW_META_KEY);
			
			boolean found = false;
			for(MetadataValue value : metaValues){
				if(getName().equals(value.value())){
					found = true;
					break;
				}
			}
			
			if(!found) return false;
			
			if(!isThisArrow(player)) return false;

			return true;
		}
		
		return false;
	}
	
	/**
	 * Checks if the Arrow is active at the moment from the passed player
	 * 
	 * @param player to check
	 * @return true if active, false if not.
	 */
	private boolean isThisArrow(Player player){
		ArrowManager arrowManager = plugin.getPlayerManager().getArrowManagerOfPlayer(player.getUniqueId());
		AbstractArrow arrow = arrowManager.getCurrentArrow();
		if(arrow == null || arrow != this) return false;
		return true;
	}
	
	/**
	 * The Meta Key for the Arrow to search for.
	 */
	private static final String ARROW_META_KEY = "arrowType";
	
	@Override	
	public TraitResults trigger(EventWrapper eventWrapper) {
		Event event = eventWrapper.getEvent();
		
		TraitResults result = new TraitResults();
		//Change ArrowType
		if(event instanceof PlayerInteractEvent){
			PlayerInteractEvent Eevent = (PlayerInteractEvent) event;
			changeArrowType(Eevent.getPlayer());
			return result.setTriggered(false);
		}
			
		//Projectile launch
		if(event instanceof EntityShootBowEvent){
			EntityShootBowEvent Eevent = (EntityShootBowEvent) event;
			
			
			Arrow arrow = (Arrow) Eevent.getProjectile();
			arrow.setMetadata(ARROW_META_KEY , new LazyMetadataValue(plugin, new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					return getName();
				}
			}));
			
			return result.setTriggered(onShoot(Eevent));
		}
		
		//Arrow Hit Location
		if(event instanceof ProjectileHitEvent){
			ProjectileHitEvent Eevent = (ProjectileHitEvent) event;
			boolean change = onHitLocation(Eevent);
			return result.setTriggered(change);
		}
		
		//Arrow Hits target
		if(event instanceof EntityDamageByEntityEvent){
			EntityDamageByEntityEvent Eevent = (EntityDamageByEntityEvent) event;
			boolean change = onHitEntity(Eevent);
			Eevent.getDamager().remove();
			return result.setTriggered(change);
		}
		
		return result.setTriggered(false);
	}
	
	/**
	 * Changes to the next arrow.
	 */
	protected void changeArrowType(Player player){
		ArrowManager arrowManager = plugin.getPlayerManager().getArrowManagerOfPlayer(player.getUniqueId());
		AbstractArrow arrow = arrowManager.getCurrentArrow();
		if(arrow == null || arrow != this) return;
		
		AbstractArrow newArrow = arrowManager.nextArrow();
		if(newArrow != null && newArrow != arrow){
			LanguageAPI.sendTranslatedMessage(player, arrow_change, "trait_name", newArrow.getDisplayName());
		}
		
	}

	/**
	 * This is called when a Player shoots an Arrow with this ArrowTrait present
	 * 
	 * @param event that was triggered
	 * @return true if a cooldown should be triggered
	 */
	protected abstract boolean onShoot(EntityShootBowEvent event);
	
	/**
	 * This is triggered when the Player Hits an Entity with it's arrow
	 * 
	 * @param event that triggered the event
	 * @return true if an Cooldown should be triggered
	 */
	protected abstract boolean onHitEntity(EntityDamageByEntityEvent event);
	
	/**
	 * This is triggered when the Player hits an Location
	 * 
	 * @param event that triggered the event
	 * @return true if an Cooldown should be triggered
	 */
	protected abstract boolean onHitLocation(ProjectileHitEvent event);
	
	/**
	 * Returns the name of the Arrow type
	 * 
	 * @return
	 */
	protected abstract String getArrowName();
	
	
	@Override
	public String getDisplayName() {
		String superDisplayName = super.getDisplayName();
		if(superDisplayName.equals(getName())){
			return getArrowName();
		}
		
		return superDisplayName;
	}

	@Override
	public boolean isBetterThan(Trait trait){
		if(trait.getClass() != this.getClass()) return false;
		
		//TODO Not sure about this...
		return false;
	}
	

	@Override
	public int getMaxUplinkTime() {
		return uplinkTime;
	}

	@Override
	public boolean triggerButHasUplink(EventWrapper wrapper) {
		if(wrapper.getPlayerAction() == PlayerAction.INTERACT_BLOCK 
				|| wrapper.getPlayerAction() == PlayerAction.INTERACT_BLOCK){
			changeArrowType(wrapper.getPlayer());
			return true;
		}
		
		if(wrapper.getEvent() instanceof ProjectileHitEvent){
			return true;
		}
		
		if(wrapper.getPlayerAction() == PlayerAction.DO_DAMAGE){
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean isStackable(){
		return false;
	}

	@TraitEventsUsed(registerdClasses = {
			EntityDamageByEntityEvent.class, 
			PlayerInteractEvent.class, 
			EntityShootBowEvent.class,
			ProjectileHitEvent.class
		})
	@Override
	public void generalInit() {
	}
	
	
	
}
