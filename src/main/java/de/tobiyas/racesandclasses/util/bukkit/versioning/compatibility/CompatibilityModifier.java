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
package de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.GameMode;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.eventprocessing.events.entitydamage.EntityHealEvent;
import de.tobiyas.racesandclasses.util.bukkit.versioning.CertainVersionChecker;

public class CompatibilityModifier {

	public static class EntityDamage{
		
		/**
		 * Returns a safe value of the damage from an {@link EntityDamageEvent}.
		 * 
		 * @param event
		 * @return
		 */
		public static double safeGetDamage(EntityDamageEvent event){
			if(CertainVersionChecker.isAbove1_6()){
				return event.getDamage();
			}else{
				try{
					Method method = EntityDamageEvent.class.getMethod("getDamage");
					int value = (Integer) method.invoke(event);
					
					return value;
				}catch(Exception exp){
					return 1;
				}
			}
		}
		
		
		/**
		 * Safely sets the damage to the {@link EntityDamageEvent}
		 * 
		 * @param damage to set
		 * @param event to set in
		 */
		public static void safeSetDamage(double damage, EntityDamageEvent event){
			if(CertainVersionChecker.isAbove1_6()){
				event.setDamage(damage);
			}else{
				try{
					Method method = EntityDamageEvent.class.getMethod("setDamage", int.class);
					int intValue = (int) damage;
					method.invoke(event, intValue);
				}catch(Exception exp){
				}
			}
		}
		
		
		/**
		 * Creates an Entity Damage by entity event safely.
		 * 
		 * @param entity
		 * @param cause
		 * @param damage
		 * @return
		 */
		@SuppressWarnings("deprecation")
		public static EntityDamageEvent safeCreateEvent(Entity entity, DamageCause cause, double damage){
			if(CertainVersionChecker.isAbove1_6()){
				return new EntityDamageEvent(entity, cause, damage);
			}else{
				int roundedDamage = Math.round((float) damage);
				return new EntityDamageEvent(entity, cause, roundedDamage);
			}
		}
		
	}
	
	public static class EntityDamageByEntity{
		
		/**
		 * Creates an Entity Damage by entity event safely.
		 * 
		 * @param entity
		 * @param cause
		 * @param damage
		 * @return
		 */
		@SuppressWarnings("deprecation")
		public static EntityDamageByEntityEvent safeCreateEvent(Entity damager, Entity target, DamageCause cause, double damage){
			if(CertainVersionChecker.isAbove1_6()){
				return new EntityDamageByEntityEvent(damager, target, cause, damage);
			}else{
				try{
					int roundedDamage = Math.round((float) damage);
					return new EntityDamageByEntityEvent(damager, target, cause, roundedDamage);
				}catch(Exception exp){
					return null;
				}
			}
		}

	}
	
	public static class BukkitPlayer{
		
		
		/**
		 * Checks if the Player is fully healed.
		 * 
		 * @param player to check
		 * 
		 * @return true if the player has full Health, false otherwise.
		 */
		public static boolean isFullyHealed(Player player){
			if(player == null) return false;
			
			return Math.abs(safeGetMaxHealth(player) - safeGetHealth(player)) < 0.01;
		}
		
		/**
		 * Sets the max life of a Player safely to the correct value
		 * 
		 * @param maxLife
		 * @param player
		 */
		public static void safeSetMaxHealth(double maxHealth, Player player){
			if(maxHealth <= 0) return;
			if(player == null) return;
			
			if(safeGetHealth(player) < maxHealth){
				safeSetHealth(maxHealth, player);
			}
			
			
			if(CertainVersionChecker.isAbove1_6()){
				try{ player.setMaxHealth(maxHealth); }catch(Throwable exp){}
			}else{
				try{
					Method method = Damageable.class.getMethod("setMaxHealth", int.class);
					
					int intHealthValue = (int) maxHealth;
					method.invoke(player, intHealthValue);
				}catch(Exception exp){
				}
			}
		}
		
		
		/**
		 * Returns the Player's max health.
		 * 
		 * @param player
		 */
		public static double safeGetMaxHealth(Player player){
			if(player == null) return 0;
			
			if(CertainVersionChecker.isAbove1_6()){
				return player.getMaxHealth();
			}else{
				try{
					Method method = Damageable.class.getMethod("getMaxHealth");
					
					int maxHealth = (Integer) method.invoke(player);
					return maxHealth;
				}catch(Exception exp){
					return 20;
				}
			}
		}
		
		/**
		 * Returns the Player's current health.
		 * 
		 * @param player
		 */
		public static double safeGetHealth(Player player){
			if(player == null) return 0;
			
			if(CertainVersionChecker.isAbove1_6()){
				return player.getHealth();
			}else{
				try{
					Method method = Damageable.class.getMethod("getHealth");
					
					int maxHealth = (Integer) method.invoke(player);
					return maxHealth;
				}catch(Exception exp){
					return 20;
				}
			}
		}

		/**
		 * Sets the current health of a Player to the correct value
		 * <br>The health is set is MAX the max health of a Player.
		 * 
		 * @param newHealth the health to set to
		 * @param player the player to set the health
		 */
		public static void safeSetHealth(double newHealth, Player player) {
			if(player == null) return;
			
			if(newHealth > safeGetMaxHealth(player)){
				newHealth = safeGetMaxHealth(player);
			}
			
			if(CertainVersionChecker.isAbove1_6()){
				try { player.setHealth(newHealth);  }catch(Throwable exp){}
			}else{
				try{
					Method method = Damageable.class.getMethod("setHealth", int.class);
					
					int intNewHealth = (int) newHealth;
					method.invoke(player, intNewHealth);
				}catch(Exception exp){}
			}
			
		}

		
		/**
		 * Damages a Player by a certain Value
		 *
		 * @param damage the damage to do
		 * @param player the player to damage
		 */
		public static void safeDamage(double damage, Player player) {
			if(player == null) return;
			
			//if in creative, there is no 
			if(player.getGameMode() == GameMode.CREATIVE) return;
			
			double oldHealth = safeGetHealth(player);
			double newHealth = oldHealth - damage;
			
			safeSetHealth(newHealth, player);
		}
		
		/**
		 * Damages a Player by a certain Value.
		 * <br>This calls player.damage().
		 * <br>This triggers a Damage event.
		 *
		 * @param damage the damage to do
		 * @param player the player to damage
		 */
		public static void safeDamageWithEvent(double damage, Player player) {
			if(player == null) return;
			
			//if in creative, there is no 
			if(player.getGameMode() == GameMode.CREATIVE) return;
			

			if(CertainVersionChecker.isAbove1_6()){
				try { player.damage(damage);  }catch(Throwable exp){}
			}else{
				try{
					Method method = Damageable.class.getMethod("damage", int.class);
					
					int intNewHealth = (int) damage;
					method.invoke(player, intNewHealth);
				}catch(Exception exp){}
			}
		}

		
		/**
		 * Heals a Player by a certain Value
		 *
		 * @param healAmount the healing to do
		 * @param player the player to heal
		 */
		public static void safeHeal(double healAmount, Player player) {
			if(player == null) return;
			if(healAmount <= 0) return;
			
			double oldHealth = safeGetHealth(player);
			double newHealth = oldHealth + healAmount;
			
			safeSetHealth(newHealth, player);
		}
	}
	
	public static class EntityHeal{
		
		
		/**
		 * Generates a EntityHealEvent regardless of MC version. (1.5 or 1.6)
		 * 
		 * @param target entity
		 * @param amount of healing
		 * @param reason of healing
		 * @return the generated Event
		 */
		@SuppressWarnings("deprecation")
		public static EntityHealEvent safeGenerate(Entity target, double amount, RegainReason reason){
			if(target == null) return null;
			
			if(CertainVersionChecker.isAbove1_6()){
				return new EntityHealEvent(target, amount, reason);
			}else{
				int intAmount = (int) amount;
				return new EntityHealEvent(target, intAmount, reason);
			}
		}
		
		
		/**
		 * Sets the Amount to the corresponding value
		 * 
		 * @param event
		 * @param amount
		 */
		public static void safeSetAmount(EntityHealEvent event, double amount){
			if(event == null) return;
			
			if(CertainVersionChecker.isAbove1_6()){
				event.setAmount(amount);
			}else{
				try{
					Method method = EntityRegainHealthEvent.class.getMethod("setAmount", int.class);
					
					int intAmount = (int) amount;
					method.invoke(event, intAmount);
				}catch(Exception exp){}
			}
		}
		
		
		/**
		 * Gets the amount safely of an {@link EntityHealEvent}
		 * 
		 * @param event
		 * @return
		 */
		public static double safeGetAmount(EntityHealEvent event){
			if(event == null) return 0;
			
			if(CertainVersionChecker.isAbove1_6()){
				return event.getAmount();
			}else{
				try{
					Method method = EntityRegainHealthEvent.class.getMethod("getAmount");					
					return (Double) method.invoke(event);
				}catch(Exception exp){
					return 0;
				}
			}
		}
	}
	
	public static class EntityRegainHealth{
		
		
		/**
		 * Sets the Amount to the corresponding value
		 * 
		 * @param event
		 * @param amount
		 */
		public static void safeSetAmount(EntityRegainHealthEvent event, double amount){
			if(event == null) return;
			
			if(CertainVersionChecker.isAbove1_6()){
				event.setAmount(amount);
			}else{
				try{
					Method method = EntityRegainHealthEvent.class.getMethod("setAmount", int.class);
					
					int intAmount = (int) amount;
					method.invoke(event, intAmount);
				}catch(Exception exp){}
			}
		}
		
		
		/**
		 * Gets the amount safely of an {@link EntityRegainHealthEvent}
		 * 
		 * @param event
		 * @return
		 */
		public static double safeGetAmount(EntityRegainHealthEvent event){
			if(event == null) return 0;
			if(CertainVersionChecker.isAbove1_6()){
				return event.getAmount();
			}else{
				try{
					Method method = EntityRegainHealthEvent.class.getMethod("getAmount");					
					return (Double) method.invoke(event);
				}catch(Exception exp){
					return 0;
				}
			}
		}
	}
	
	
	public static class LivingEntity{
		
		/**
		 * Does damage to an entity safely to healthVersions.
		 * 
		 * @param entity to damage
		 * @param value to do damage
		 * 
		 * @deprecated try to use {@link #safeDamageEntityByEntity(org.bukkit.entity.LivingEntity, org.bukkit.entity.LivingEntity, double)}
		 */
		public static void safeDamageEntity(org.bukkit.entity.LivingEntity entity, double value){
			if(entity == null) return;
			if(CertainVersionChecker.isAbove1_6()){
				entity.damage(value);
			}else{
				int damage = Math.round((float) value);
				
				try{
					Method method = org.bukkit.entity.LivingEntity.class.getMethod("damage", Integer.class);					
					method.invoke(entity, damage);
				}catch(Exception exp){}//silent fail
			}
		}
		
		/**
		 * Does damage to an entity safely to healthVersions.
		 * 
		 * @param entity to damage
		 * @param value to do damage
		 */
		public static void safeDamageEntityByEntity(org.bukkit.entity.LivingEntity entity, 
				org.bukkit.entity.LivingEntity damager, double value){
			
			if(entity == null) return;
			if(CertainVersionChecker.isAbove1_6()){
				entity.damage(value, damager);
			}else{
				int damage = Math.round((float) value);
				
				try{
					Method method = org.bukkit.entity.LivingEntity.class.getMethod("damage", Integer.class, Entity.class);					
					method.invoke(entity, damage, damager);
				}catch(Exception exp){}//silent fail
			}
		}
		
		
		/**
		 * Does damage to an entity safely to healthVersions.
		 * 
		 * @param entity to damage
		 * @param value to do damage
		 */
		public static void safeDamageEntityByEntity(org.bukkit.entity.LivingEntity entity, 
				org.bukkit.entity.LivingEntity damager, double value, DamageCause cause){
			
			if(entity == null) return;
			if(CertainVersionChecker.isAbove1_6()){
				//This will be auto-removed later!
				new DamageEventChangerClass(cause);
				entity.damage(value, damager);
			}else{
				int damage = Math.round((float) value);
				
				try{
					Method method = org.bukkit.entity.LivingEntity.class.getMethod("damage", Integer.class, Entity.class);					
					method.invoke(entity, damage, damager);
				}catch(Exception exp){}//silent fail
			}
		}
		
		
		private static class DamageEventChangerClass implements Listener{
			private final DamageCause cause;			
			private DamageEventChangerClass(DamageCause cause) {
				this.cause = cause;
				RacesAndClasses.getPlugin().registerEvents(this);
			}
			
			@EventHandler(priority=EventPriority.LOWEST)
			public void damage(EntityDamageByEntityEvent event){
				try{
					Field field = EntityDamageEvent.class.getDeclaredField("cause");
					field.setAccessible(true);
					field.set(event, cause);
				}catch(Throwable exp){ exp.printStackTrace(); }
				
				HandlerList.unregisterAll(this);
			}
			
		}
		
		
		/**
		 * Does damage to an entity safely to healthVersions.
		 * 
		 * @param entity to damage
		 * @param value to do damage
		 */
		public static void safeHealEntity(org.bukkit.entity.LivingEntity entity, double value){
			if(entity == null) return;
			if(value < 0 )return;
			
			double maxHealth = safeGetEntityMaxHealth(entity);
			double newHealth = safeGetEntityHealth(entity) + value;
			
			safeSetEntityHealth(entity, Math.min(maxHealth, newHealth));
		}

		/**
		 * Gets the Current Health of the Entity..
		 * 
		 * @param entity to check
		 */
		public static double safeGetEntityHealth(org.bukkit.entity.LivingEntity entity){
			if(entity == null) return 0;
			if(CertainVersionChecker.isAbove1_6()){
				return entity.getHealth();
			}else{
				try{
					Method method = org.bukkit.entity.LivingEntity.class.getMethod("getHealth");					
					return (Double) method.invoke(entity);
				}catch(Exception exp){ return 0; }//silent fail
			}
		}
		
		/**
		 * Gets the Current Health of the Entity..
		 * 
		 * @param entity to check
		 */
		public static double safeGetEntityMaxHealth(org.bukkit.entity.LivingEntity entity){
			if(entity == null) return 0;
			if(CertainVersionChecker.isAbove1_6()){
				return entity.getMaxHealth();
			}else{
				try{
					Method method = org.bukkit.entity.LivingEntity.class.getMethod("getMaxHealth");					
					return (Double) method.invoke(entity);
				}catch(Exception exp){ return 0; }//silent fail
			}
		}
		
		/**
		 * Gets the Current Health of the Entity..
		 * 
		 * @param entity to check
		 */
		public static void safeSetEntityMaxHealth(org.bukkit.entity.LivingEntity entity, double value){
			if(entity == null) return;
			
			double maxHealth = safeGetEntityMaxHealth(entity);
			if(CertainVersionChecker.isAbove1_6()){
				entity.setMaxHealth(maxHealth);
			}else{
				try{
					Method method = org.bukkit.entity.LivingEntity.class.getMethod("setHealth", Integer.class);					
					method.invoke(entity, (int) maxHealth);
				}catch(Exception exp){}//silent fail
			}
		}
		
		/**
		 * Gets the Current Health of the Entity..
		 * 
		 * @param entity to check
		 */
		public static void safeSetEntityHealth(org.bukkit.entity.LivingEntity entity, double value){
			if(entity == null) return;
			if(value <= 0) return;
			
			if(CertainVersionChecker.isAbove1_6()){
				entity.setHealth(value);
			}else{
				try{
					Method method = org.bukkit.entity.LivingEntity.class.getMethod("setHealth", Integer.class);					
					method.invoke(entity, (int) value);
				}catch(Exception exp){}//silent fail
			}
		}
	}
	
	public static class Shooter{
		
		
		/**
		 * Finds the shooter of an arrow if any present.
		 * 
		 * @param arrow the arrow to get the shooter from.
		 * @return the found Living entity the arrow belongs to or NULL if not found.
		 */
		public static org.bukkit.entity.LivingEntity getShooter(Projectile projectile){
			if(projectile == null) return null;
			
			try{
				return (org.bukkit.entity.LivingEntity)projectile.getShooter();
			}catch(Throwable exp){
				//lets do good old reflections on this.
			}
			
			try{
				Method method = projectile.getClass().getMethod("getShooter");
				return (org.bukkit.entity.LivingEntity) method.invoke(projectile, method);
			}catch(Throwable exp){
				//well we don't know how to get to the shooter.
			}
			
			//nothing worked...
			return null;
		}
	}
	
}
