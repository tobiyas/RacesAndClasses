package de.tobiyas.racesandclasses.util.friend;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.GroupAPI;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier.Shooter;

public class EnemyChecker implements Listener {

	
	private static EnemyChecker checker;
	public EnemyChecker() {
		Bukkit.getPluginManager().registerEvents(this, RacesAndClasses.getPlugin());
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void entityDamage(EntityDamageByEntityEvent event){
		if(event instanceof FriendDetectEvent) {
			((FriendDetectEvent) event).realCancle = event.isCancelled();
			event.setCancelled(true);
		}
	}
	
	public static class FriendDetectEvent extends EntityDamageByEntityEvent{
		boolean realCancle = false;
		@SuppressWarnings("deprecation") //still safe.
		public FriendDetectEvent(Entity damager, Entity damagee) {
			super(damager, damagee, DamageCause.CUSTOM, 0.1);
		}
	}
	
	
	
	/**
	 * Checks if the these 2 are Allies.
	 * 
	 * @param attacker to check
 	 * @param receiver to check
 	 * 
	 * @return true if they are allies.
	 */
	public static boolean areAllies(Entity attacker, Entity receiver){
		if(checker == null) checker = new EnemyChecker();
		
		if(attacker == null || receiver == null) return false;
		if(attacker instanceof Projectile){
			attacker = Shooter.getShooter((Projectile) attacker);
			if(attacker == null) return false;
		}
		
		
		//Check if is in Group:
		if(attacker instanceof Player && receiver instanceof Player){
			Player p1 = (Player) attacker;
			Player p2 = (Player) receiver;
			
			//Check if in same Group!
			if(GroupAPI.isInSameGroup(p1, p2)) return true;
		}
		
		
		FriendDetectEvent event = new FriendDetectEvent(attacker, receiver);
		RacesAndClasses.getPlugin().fireEventToBukkit(event);
		
		//only check cancles!
		return event.realCancle;
	}
	
	/**
	 * Checks if the these 2 are enemies.
	 * 
	 * @param attacker to check
	 * @param receiver to check
	 * 
	 * @return true if they are enemies.
	 */
	public static boolean areEnemies(Entity attacker, Entity receiver){
		return !areAllies(attacker, receiver);
	}
	
	/**
	 * Checks if the these 2 are enemies.
	 * 
	 * @param attacker to check
	 * @param receiver to check
	 * 
	 * @return true if they are enemies.
	 */
	public static boolean isApplyable(Entity attacker, Entity receiver, TargetType type){
		switch(type){
			case ENEMY:  return areEnemies(attacker, receiver);
			case FRIEND: return areAllies(attacker, receiver);
			case ALL: return true;
			
			default: return false;
		}
	}
}
