package de.tobiyas.racesandclasses.traitcontainer.traits.arrows;

import java.util.Map;

import org.bukkit.ChatColor;
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

import de.tobiyas.racesandclasses.datacontainer.arrow.ArrowManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.racesandclasses.healthmanagement.HealthManager;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitWithUplink;


public abstract class AbstractArrow implements TraitWithUplink{
	
	protected AbstractTraitHolder traitHolder;
	
	protected int duration;
	protected double totalDamage;
	
	
	@Override
	public void setTraitHolder(AbstractTraitHolder abstractTraitHolder){
		this.traitHolder = abstractTraitHolder;
	}
	
	@Override
	public AbstractTraitHolder getTraitHolder(){
		return traitHolder;
	}
	
	@Override
	public abstract String getName();


	@Override
	public abstract String getPrettyConfiguration();
	
	
	@Override
	public abstract void setConfiguration(Map<String, String> configMap);

	@Override	
	public boolean modify(Event event) {
		if(!(event instanceof PlayerInteractEvent || 
			event instanceof EntityShootBowEvent || 
			event instanceof ProjectileHitEvent ||
			event instanceof EntityDamageByEntityEvent)) return false;
		
		//Change ArrowType
		if(event instanceof PlayerInteractEvent){
			PlayerInteractEvent Eevent = (PlayerInteractEvent) event;
			if(!(Eevent.getAction() == Action.LEFT_CLICK_AIR || Eevent.getAction() == Action.LEFT_CLICK_BLOCK)) return false;

			Player player = Eevent.getPlayer();
			if(!TraitHolderCombinder.checkContainer(player.getName(), this)) return false;
			if(player.getItemInHand().getType() != Material.BOW) return false;
	
			if(changeArrowType(player)) return true;
		}
			
		//Projectile launch
		if(event instanceof EntityShootBowEvent){
			EntityShootBowEvent Eevent = (EntityShootBowEvent) event;
			if(Eevent.getEntity().getType() != EntityType.PLAYER) return false;
			Player player = (Player) Eevent.getEntity();
			if(!TraitHolderCombinder.checkContainer(player.getName(), this)) return false;
			
			ArrowManager arrowManager = HealthManager.getHealthManager().getArrowManagerOfPlayer(player.getName());
			AbstractArrow arrow = arrowManager.getCurrentArrow();
			if(arrow == null || arrow != this) return false;
			
			return onShoot(Eevent);
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
			
			ArrowManager arrowManager = HealthManager.getHealthManager().getArrowManagerOfPlayer(player.getName());
			AbstractArrow currentArrow = arrowManager.getCurrentArrow();
			if(currentArrow == null) return false;
			
			boolean change = onHitLocation(Eevent);
			return change;
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

			ArrowManager arrowManager = HealthManager.getHealthManager().getArrowManagerOfPlayer(player.getName());
			AbstractArrow arrow = arrowManager.getCurrentArrow();
			if(arrow == null || arrow != this) return false;

			boolean change = onHitEntity(Eevent);
			Eevent.getDamager().remove();
			return change;
		}
		
		return false;
	}
	
	/**
	 * Changes to the next arrow.
	 * 
	 * 
	 * @return
	 */
	private boolean changeArrowType(Player player){
		ArrowManager arrowManager = HealthManager.getHealthManager().getArrowManagerOfPlayer(player.getName());
		AbstractArrow arrow = arrowManager.getCurrentArrow();
		if(arrow == null || arrow != this) return false;
		
		AbstractArrow newArrow = arrowManager.nextArrow();
		if(newArrow != null && newArrow != arrow){
			player.sendMessage(ChatColor.GREEN + "Switched arrows to: " + ChatColor.LIGHT_PURPLE + newArrow.getArrowName());
		}
		
		return true;
	}
	
	protected abstract boolean onShoot(EntityShootBowEvent event);
	
	protected abstract boolean onHitEntity(EntityDamageByEntityEvent event);
	
	protected abstract boolean onHitLocation(ProjectileHitEvent event);
	
	protected abstract String getArrowName();
	
	
	@Override
	public final String getUplinkIndicatorName(){
		return "trait." + getName();
	}
	
	@Override
	public boolean isBetterThan(Trait trait){
		if(trait.getClass() != this.getClass()) return false;
		
		//TODO Not sure about this...
		return false;
	}
	
	@Override
	public String toString(){
		return getName();
	}
}
