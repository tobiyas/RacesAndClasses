package de.tobiyas.racesandclasses.pets;

import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;

public class PetListener implements Listener {

	private final RacesAndClasses plugin;
	
	
	public PetListener(RacesAndClasses plugin) {
		this.plugin = plugin;
		this.plugin.registerEvents(this);
	}
	
	
	@EventHandler
	public void onPetDamage(EntityDamageEvent event){
		Entity entity = event.getEntity();
		if(!entity.hasMetadata("pet")) return;
		
		SpawnedPet petContainer = (SpawnedPet) entity.getMetadata("pet").get(0).value();
		if(petContainer.getPet().isInvincible()) event.setCancelled(true);
	}
	
	

	@EventHandler
	public void passivePetsDontTarget(EntityTargetEvent event){
		//First check targeting.
		Entity entity = event.getEntity();
		if(entity != null){
			if(!entity.hasMetadata("pet")) return;
			
			SpawnedPet petContainer = (SpawnedPet) entity.getMetadata("pet").get(0).value();
			if(petContainer.getPet().isPassive()) event.setCancelled(true);
		}
		
		
		//Now check getting targeted.
		entity = event.getTarget();
		if(entity != null){
			if(!entity.hasMetadata("pet")) return;
			
			SpawnedPet petContainer = (SpawnedPet) entity.getMetadata("pet").get(0).value();
			if(petContainer.getPet().isPassive()) event.setCancelled(true);
		}
	}
	

	@EventHandler
	public void onPetDealingDamage(EntityDamageByEntityEvent event){
		Entity damagee = event.getEntity();
		UUID entityID = damagee.getUniqueId();
		
		//First check targeting.
		Entity damager = event.getDamager();
		if(damager != null){
			if(!damager.hasMetadata("pet")) return;
			
			SpawnedPet petContainer = (SpawnedPet) damager.getMetadata("pet").get(0).value();
			//Pets don't hurt owners!
			if(petContainer.getOwner().getUniqueId().equals(entityID)){
				event.setCancelled(true);
				return;
			}
			
			//Pets do damage to others.
			if(!petContainer.getPet().isPassive()){
				event.setDamage(petContainer.getPet().getPetDamage());
				return;
			}
		}
	}
	
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void ownerIsGettingTargeted(EntityTargetEvent event){
		if(event.isCancelled()) return;
		if(!(event.getEntity() instanceof LivingEntity)) return;
		
		Entity target = event.getTarget();
		if(target == null) return;
		
		if(target.getType() != EntityType.PLAYER) return;
		RaCPlayer player = RaCPlayerManager.get().getPlayer((Player)target);
		PlayerPetManager petManager = player.getPlayerPetManager();
		petManager.playerGotTargeted((LivingEntity)event.getEntity());
	}
	
	
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void cancelPetTargetOwner(EntityTargetEvent event){
		if(event.isCancelled()) return;
		if(!(event.getEntity() instanceof LivingEntity)) return;
		
		Entity target = event.getTarget();
		if(target == null) return;
		if(target.getType() != EntityType.PLAYER) return;
		if(!event.getEntity().hasMetadata("pet")) return;
		
		SpawnedPet pet = (SpawnedPet) event.getEntity().getMetadata("pet").get(0).value();
		if(target.getUniqueId().equals(pet.getOwner().getUniqueId())) event.setCancelled(true);
	}
	
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void ownerIsGettingAttacked(EntityDamageByEntityEvent event){
		if(event.isCancelled()) return;

		Entity damagee = event.getEntity();
		if(damagee == null) return;
		if(damagee.getType() != EntityType.PLAYER) return;
		
		Entity damager = event.getDamager();
		if(damager == null) return;
		if(!(damager instanceof LivingEntity)) return;
		
		RaCPlayer player = RaCPlayerManager.get().getPlayer((Player)damagee);
		PlayerPetManager petManager = player.getPlayerPetManager();
		petManager.playerGotDamaged((LivingEntity)event.getEntity());
	}
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPetDied(EntityDeathEvent event){
		Entity entity = event.getEntity();
		if(entity == null) return;
		if(!entity.hasMetadata("pet")) return;
		
		entity.removeMetadata("pet", plugin);
		event.getDrops().clear();
		event.setDroppedExp(0);
	}
	
	
	
	@EventHandler
	public void onOnwerAttacks(EntityDamageByEntityEvent event){
		Entity damager = event.getDamager();
		Entity damagee = event.getEntity();
		
		if(damager == null || damagee == null) return;
		if(!(damagee instanceof LivingEntity)) return;
		if(damager.getType() != EntityType.PLAYER) return;
		
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(damager.getUniqueId());
		racPlayer.getPlayerPetManager().playerAttacks((LivingEntity)damagee);
	}
	
	
	@EventHandler
	public void petsDoNotBurn(EntityCombustEvent event){
		if(event.getEntity().hasMetadata("pet")) event.setCancelled(true);
	}
	
	
}
