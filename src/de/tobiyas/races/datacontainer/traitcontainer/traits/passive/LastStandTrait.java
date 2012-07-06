package de.tobiyas.races.datacontainer.traitcontainer.traits.passive;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

import de.tobiyas.races.Races;
import de.tobiyas.races.configuration.member.MemberConfig;
import de.tobiyas.races.configuration.member.MemberConfigManager;
import de.tobiyas.races.configuration.traits.TraitConfig;
import de.tobiyas.races.configuration.traits.TraitConfigManager;
import de.tobiyas.races.datacontainer.health.HealthManager;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.TraitEventManager;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.events.EntityDamageDoubleEvent;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.events.EntityHealEvent;
import de.tobiyas.races.datacontainer.traitcontainer.traits.Trait;
import de.tobiyas.races.datacontainer.traitcontainer.traits.TraitsWithUplink;
import de.tobiyas.races.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.races.util.traitutil.TraitPriority;

public class LastStandTrait implements TraitsWithUplink {
	
	private RaceContainer raceContainer = null;
	private ClassContainer classContainer = null;

	private HashMap<String, Integer> uplinkMap = new HashMap<String, Integer>();
	
	private static int uplinkTime = 60*20;
	private static double activationLimit = 30;
	private double value;
	
	public LastStandTrait(RaceContainer container){
		this.raceContainer = container;
	}
	
	public LastStandTrait(ClassContainer container){
		this.classContainer = container;
	}
	
	
	@TraitInfo(registerdClasses = {EntityDamageDoubleEvent.class}, traitPriority = TraitPriority.lowest)
	@Override
	public void generalInit() {
		TraitConfig config = TraitConfigManager.getInstance().getConfigOfTrait(getName());
		if(config != null){
			uplinkTime = (int) config.getValue("trait.uplink", 60) * 20;
			activationLimit = config.getDouble("trait.activationLimit", 30);
		}	
	}

	@Override
	public String getName() {
		return "LastStandTrait";
	}

	@Override
	public RaceContainer getRace() {
		return raceContainer;
	}

	@Override
	public ClassContainer getClazz() {
		return classContainer;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public String getValueString() {
		return " uplink-Time: " + (int) uplinkTime/20 + " secs, heals: " + (int) value;
	}

	@Override
	public void setValue(Object obj) {
		try{
			value = Double.valueOf((String) obj);
		}catch(NumberFormatException e){
			value = 30;
		}
	}

	@Override
	public boolean modify(Event event) {
		if(!(event instanceof EntityDamageDoubleEvent)) return false;
		EntityDamageDoubleEvent Eevent = (EntityDamageDoubleEvent) event;
		Entity entity = Eevent.getEntity();
		
		if(!(entity instanceof Player)) return false;
		Player player = (Player) entity;
		
		if(!TraitHolderCombinder.checkContainer(player.getName(), this)) return false;
		if(checkUplink(player)) return false;
		
		double health = HealthManager.getHealthManager().getHealthOfPlayer(player.getName()) - Eevent.getDoubleValueDamage();
		double maxHealth = HealthManager.getHealthManager().getMaxHealthOfPlayer(player.getName());
		double percent = 100 * health / maxHealth;
		
		if(percent <= activationLimit){
			EntityHealEvent ehEvent = new EntityHealEvent(player, value, player, RegainReason.MAGIC);
			TraitEventManager.fireEvent(ehEvent);
			
			if(!ehEvent.isCancelled() && ehEvent.getDoubleValueAmount() != 0){
				System.out.println("test6");
				player.sendMessage(ChatColor.LIGHT_PURPLE + getName() + ChatColor.GREEN + " toggled. You were healed.");
				Location loc = player.getLocation();
				player.getWorld().playEffect(loc, Effect.POTION_BREAK, 1);
				synchronized(uplinkMap){
					uplinkMap.put(player.getName(), uplinkTime);
				}
				return true;
			}
		}
		return false;
	}
	
	
	private boolean checkUplink(Player player){
		if(uplinkMap.containsKey(player.getName())){
			return true;
		}
		return false;
	}
	

	@Override
	public boolean isVisible() {
		return true;
	}

	@Override
	public void tickReduceUplink() {
		int precision = Races.getPlugin().getGeneralConfig().getconfig_globalUplinkTickPresition();
		if(precision <= 0){
			Races.getPlugin().getDebugLogger().logWarning("Trait: " + getName() + " could not reduce cooldown, because precision = " + precision);
		}
		
		synchronized(uplinkMap){
			for(String player : uplinkMap.keySet()){
				int remainingTime = uplinkMap.get(player);
				remainingTime -= precision;
				
				Player tempPlayer = Bukkit.getPlayer(player);
				if(remainingTime == uplinkTime){
					if(tempPlayer != null)
						tempPlayer.sendMessage(ChatColor.LIGHT_PURPLE + getName() + ChatColor.RED + " has faded.");
				}
					
				if(remainingTime <= 0){
					uplinkMap.remove(player);
					if(tempPlayer != null){
						MemberConfig config = MemberConfigManager.getInstance().getConfigOfPlayer(player);
						if(config != null){
							if(config.getInformCooldownReady())
								tempPlayer.sendMessage(ChatColor.GREEN + "The trait: " + ChatColor.LIGHT_PURPLE + getName() + ChatColor.GREEN +
														" is now ready again to use again.");
						}
					}
				}
				else
					uplinkMap.put(player, remainingTime);
			}
		}
	}

	@Override
	public boolean isBetterThan(Trait trait) {
		if(!(trait instanceof LastStandTrait)) return false;
		return value >= (double) trait.getValue();
	}

}
