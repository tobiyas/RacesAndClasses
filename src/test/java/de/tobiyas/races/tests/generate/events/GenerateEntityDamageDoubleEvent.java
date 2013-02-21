package de.tobiyas.races.tests.generate.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import de.tobiyas.races.datacontainer.eventmanagement.events.EntityDamageDoubleEvent;
import de.tobiyas.utils.tests.generate.monster.GenerateMonster;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Skeleton.class)
public class GenerateEntityDamageDoubleEvent {

	public static EntityDamageDoubleEvent vsMonster(double damage, DamageCause cause){	
		EntityDamageDoubleEvent event = new EntityDamageDoubleEvent(GenerateMonster.generateSkeleton(), cause, damage);
		return event;
	}
	
	public static EntityDamageDoubleEvent vsMonster(double damage, DamageCause cause, EntityType type){	
		EntityDamageDoubleEvent event = new EntityDamageDoubleEvent(GenerateMonster.generateMonster(type), cause, damage);
		return event;
	}
	
	public static EntityDamageDoubleEvent vsPlayer(double damage, DamageCause cause, String playerName){
		Player player = Bukkit.getPlayer(playerName);
		
		EntityDamageDoubleEvent event = new EntityDamageDoubleEvent(player, cause, damage);
		return event;
	}
}
