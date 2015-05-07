package de.tobiyas.racesandclasses.listeners.generallisteners;

import java.util.Set;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.DebuffAPI;
import de.tobiyas.racesandclasses.entitystatusmanager.debuff.Debuff;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier;

public class Listener_Debuff implements Listener {

	
	public Listener_Debuff() {
		RacesAndClasses.getPlugin().registerEvents(this);
	}
	
	
	
	@EventHandler(priority = EventPriority.LOW)
	public void outgoingDamage(EntityDamageByEntityEvent event){
		Entity damager = event.getDamager();
		
		Set<Debuff> debuffs = DebuffAPI.getAllDebuffs(damager.getUniqueId());
		double damage = CompatibilityModifier.EntityDamage.safeGetDamage(event);
		for(Debuff buff : debuffs){
			damage = buff.modifyOutgoingDamage(damage);
		}
		
		CompatibilityModifier.EntityDamage.safeSetDamage(damage, event);
	}
	

	@EventHandler(priority = EventPriority.LOW)
	public void incomingDamage(EntityDamageEvent event){
		Entity damagee = event.getEntity();
		
		Set<Debuff> debuffs = DebuffAPI.getAllDebuffs(damagee.getUniqueId());
		double damage = CompatibilityModifier.EntityDamage.safeGetDamage(event);
		for(Debuff buff : debuffs){
			damage = buff.modifyIncomingDamage(damage);
		}
		
		CompatibilityModifier.EntityDamage.safeSetDamage(damage, event);
	}
	
	
}
