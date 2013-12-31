package de.tobiyas.racesandclasses.traitcontainer.traits.arrows;

import static de.tobiyas.racesandclasses.translation.languages.Keys.arrow_change;

import java.util.List;
import java.util.concurrent.Callable;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
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
import de.tobiyas.racesandclasses.traitcontainer.interfaces.AbstractBasicTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;


public abstract class AbstractArrow extends AbstractBasicTrait {
	
	protected RacesAndClasses plugin = RacesAndClasses.getPlugin();
	
	protected int duration;
	protected double totalDamage;
	
	protected int uplinkTime = 0;
	
	
	@Override
	public boolean canBeTriggered(Event event){
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
			
			if(!TraitHolderCombinder.checkContainer(player.getName(), this)) return false;
			if(player.getItemInHand().getType() != Material.BOW) return false;
	
			return true;
		}
			
		//Projectile launch
		if(event instanceof EntityShootBowEvent){
			EntityShootBowEvent Eevent = (EntityShootBowEvent) event;
			if(Eevent.getEntity().getType() != EntityType.PLAYER) return false;
			Player player = (Player) Eevent.getEntity();
			if(!TraitHolderCombinder.checkContainer(player.getName(), this)) return false;			
			if(!isThisArrow(player)) return false;
			
			return true;
		}
		
		//Arrow Hit Location
		if(event instanceof ProjectileHitEvent){
			ProjectileHitEvent Eevent = (ProjectileHitEvent) event;
			if(Eevent.getEntityType() != EntityType.ARROW) return false;
			if(Eevent.getEntity().getShooter() == null) return false;
			
			Arrow arrow = (Arrow) Eevent.getEntity();
			if(arrow.getShooter().getType() != EntityType.PLAYER) return false;
			
			Player player = (Player) arrow.getShooter();
			if(!TraitHolderCombinder.checkContainer(player.getName(), this)) return false;

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
			Entity shooter = realArrow.getShooter();
			
			if(shooter == null || realArrow == null || realArrow.isDead()) return false;
			if(shooter.getType() != EntityType.PLAYER) return false;
			
			if(Eevent.getEntity() == shooter && realArrow.getTicksLived() < 10)
				return false;

			Player player = (Player) shooter;
			if(!TraitHolderCombinder.checkContainer(player.getName(), this)) return false;
			
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
		ArrowManager arrowManager = plugin.getPlayerManager().getArrowManagerOfPlayer(player.getName());
		AbstractArrow arrow = arrowManager.getCurrentArrow();
		if(arrow == null || arrow != this) return false;
		return true;
	}
	
	/**
	 * The Meta Key for the Arrow to search for.
	 */
	private static final String ARROW_META_KEY = "arrowType";
	
	@Override	
	public boolean trigger(Event event) {
		//Change ArrowType
		if(event instanceof PlayerInteractEvent){
			PlayerInteractEvent Eevent = (PlayerInteractEvent) event;
			changeArrowType(Eevent.getPlayer());
			return false;
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
			
			return onShoot(Eevent);
		}
		
		//Arrow Hit Location
		if(event instanceof ProjectileHitEvent){
			ProjectileHitEvent Eevent = (ProjectileHitEvent) event;
			boolean change = onHitLocation(Eevent);
			return change;
		}
		
		//Arrow Hits target
		if(event instanceof EntityDamageByEntityEvent){
			EntityDamageByEntityEvent Eevent = (EntityDamageByEntityEvent) event;
			boolean change = onHitEntity(Eevent);
			Eevent.getDamager().remove();
			return change;
		}
		
		return false;
	}
	
	/**
	 * Changes to the next arrow.
	 */
	protected void changeArrowType(Player player){
		ArrowManager arrowManager = plugin.getPlayerManager().getArrowManagerOfPlayer(player.getName());
		AbstractArrow arrow = arrowManager.getCurrentArrow();
		if(arrow == null || arrow != this) return;
		
		AbstractArrow newArrow = arrowManager.nextArrow();
		if(newArrow != null && newArrow != arrow){
			LanguageAPI.sendTranslatedMessage(player, arrow_change, "trait_name", this.getDisplayName());
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
	public boolean triggerButHasUplink(Event event) {
		if(event instanceof PlayerInteractEvent){
			changeArrowType(((PlayerInteractEvent) event).getPlayer());
			return true;
		}
		
		if(event instanceof ProjectileHitEvent){
			return true;
		}
		
		if(event instanceof EntityDamageByEntityEvent){
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
