package de.tobiyas.races.datacontainer.traitcontainer.traits.activate;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import de.tobiyas.races.Races;
import de.tobiyas.races.configuration.member.MemberConfig;
import de.tobiyas.races.configuration.member.MemberConfigManager;
import de.tobiyas.races.configuration.traits.TraitConfig;
import de.tobiyas.races.configuration.traits.TraitConfigManager;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.TraitEventManager;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.events.EntityHealEvent;
import de.tobiyas.races.datacontainer.traitcontainer.traits.Trait;
import de.tobiyas.races.datacontainer.traitcontainer.traits.TraitsWithUplink;
import de.tobiyas.races.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;

public class HealOthersTrait implements TraitsWithUplink{
	
	private double value;
	
	private RaceContainer raceContainer;
	private ClassContainer classContainer;
	
	private HashMap<String, Integer> uplinkMap = new HashMap<String, Integer>();
	
	private static int uplinkTime = 60*20;
	private static int itemIDInHand = Material.STRING.getId();

	public HealOthersTrait(RaceContainer raceContainer){
		this.raceContainer = raceContainer;
	}
	
	public HealOthersTrait(ClassContainer classContainer){
		this.classContainer = classContainer;
	}

	@TraitInfo(registerdClasses = {PlayerInteractEntityEvent.class, PlayerInteractEvent.class})
	@Override
	public void generalInit() {		
		TraitConfig config = TraitConfigManager.getInstance().getConfigOfTrait(getName());
		if(config != null){
			uplinkTime = (int) config.getValue("trait.uplink", 60) * 20;
			itemIDInHand = (int) config.getValue("trait.iteminhand", Material.STRING.getId());
		}
	}

	@Override
	public String getName() {
		return "HealOthersTrait";
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
		return "heals: " + value;
	}

	@Override
	public void setValue(Object obj) {
		String valueString = (String) obj;
		
		try{
			value = Double.valueOf(valueString);
		}catch(NumberFormatException e){
			value = 3;
		}
	}

	@Override
	public boolean modify(Event event) {
		if(event instanceof PlayerInteractEntityEvent || event instanceof PlayerInteractEvent){
			if(event instanceof PlayerInteractEntityEvent){
				PlayerInteractEntityEvent Eevent = (PlayerInteractEntityEvent) event;
				
				Entity target = Eevent.getRightClicked();
				if(target != null && target instanceof Player){
					Player player = Eevent.getPlayer();
					if(player.getItemInHand().getTypeId() != itemIDInHand) return false;
					if(!TraitHolderCombinder.checkContainer(player.getName(), this)) return false;
					if(!checkUplink(player)) return false;
					
					Player targetPlayer = (Player) target;
					EntityHealEvent entityHealEvent = new EntityHealEvent(targetPlayer, value, player, RegainReason.MAGIC);
					TraitEventManager.fireEvent(entityHealEvent);
					
					if(!entityHealEvent.isCancelled() && entityHealEvent.getDoubleValueAmount() != 0){
						Location loc = entityHealEvent.getEntity().getLocation();
						loc.getWorld().playEffect(loc, Effect.POTION_BREAK, 1);
						player.sendMessage(ChatColor.GREEN + "You healed " + ChatColor.LIGHT_PURPLE + targetPlayer.getDisplayName() + ChatColor.GREEN + " for: " + 
											ChatColor.AQUA + entityHealEvent.getDoubleValueAmount() + ChatColor.GREEN + " HP.");
						
						targetPlayer.sendMessage(ChatColor.LIGHT_PURPLE + player.getDisplayName() + ChatColor.GREEN + " healed you for " + ChatColor.AQUA + 
												entityHealEvent.getDoubleValueAmount() + ChatColor.GREEN + " HP.");
						uplinkMap.put(player.getName(), uplinkTime);
						return true;
					}
				}
				return false;
			}
			
			if(event instanceof PlayerInteractEvent){
				PlayerInteractEvent Eevent = (PlayerInteractEvent) event;
				Action action = Eevent.getAction();
				
				if(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK){
					Player player = Eevent.getPlayer();
					if(!player.isSneaking()) return false;
					
					if(player.getItemInHand().getTypeId() != itemIDInHand) return false;
					if(!TraitHolderCombinder.checkContainer(player.getName(), this)) return false;
					if(checkUplink(player)) return false;
					
					EntityHealEvent entityHealEvent = new EntityHealEvent(player, value, player, RegainReason.MAGIC);
					TraitEventManager.fireEvent(entityHealEvent);

					if(!entityHealEvent.isCancelled() && entityHealEvent.getDoubleValueAmount() != 0){
						Location loc = entityHealEvent.getEntity().getLocation();
						loc.getWorld().playEffect(loc, Effect.POTION_BREAK, 1);
						player.sendMessage(ChatColor.GREEN + "You healed " + ChatColor.LIGHT_PURPLE + "yourself" + ChatColor.GREEN + " for: " + 
											ChatColor.AQUA + entityHealEvent.getDoubleValueAmount() + ChatColor.GREEN + " HP.");
						uplinkMap.put(player.getName(), uplinkTime);
						return true;
					}
				}
				return false;
			}
		}
		return false;
	}
	
	private boolean checkUplink(Player player){
		if(uplinkMap.containsKey(player.getName())){
			int remainingTime = (int) Math.ceil(uplinkMap.get(player.getName()) / 20);
			player.sendMessage(ChatColor.RED + "You still have " + ChatColor.LIGHT_PURPLE + remainingTime + 
								ChatColor.RED + " seconds uplink on " + ChatColor.LIGHT_PURPLE + getName());
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
		int precision = Races.getPlugin().interactConfig().getconfig_globalUplinkTickPresition();
		if(precision <= 0){
			Races.getPlugin().getDebugLogger().logWarning("Trait: " + getName() + " could not reduce cooldown, because precision = " + precision);
		}
		
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

	@Override
	public boolean isBetterThan(Trait trait) {
		if(!(trait instanceof HealOthersTrait)) return false;
		
		return value >= (double) trait.getValue();
	}

}
