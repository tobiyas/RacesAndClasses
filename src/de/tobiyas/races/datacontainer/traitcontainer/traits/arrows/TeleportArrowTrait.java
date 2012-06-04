package de.tobiyas.races.datacontainer.traitcontainer.traits.arrows;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import de.tobiyas.races.Races;
import de.tobiyas.races.configuration.traits.TraitConfig;
import de.tobiyas.races.configuration.traits.TraitConfigManager;
import de.tobiyas.races.datacontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitcontainer.TraitEventManager;

public class TeleportArrowTrait extends AbstractArrow {
	
	private static int uplinkTime = 60 * 20;
	private static HashMap<String, Integer> uplinkMap = new HashMap<String, Integer>();

	public TeleportArrowTrait(RaceContainer raceContainer){
		this.raceContainer = raceContainer;
		TraitEventManager.getTraitEventManager().registerTrait(this);
		TraitConfig config = TraitConfigManager.getInstance().getConfigOfTrait(getName());
		if(config != null){
			uplinkTime = (int) config.getValue("trait.uplink", 60) * 20;
		}
	}
	
	@Override
	public boolean isVisible() {
		return true;
	}

	@Override
	public String getName() {
		return "TeleportArrowTrait";
	}

	@Override
	public String getValueString() {
		return "uplink: " + (int) (uplinkTime / 20) + " seconds";
	}

	@Override
	public void setValue(Object obj) {
		//Not needed
	}

	@Override
	protected boolean onShoot(EntityShootBowEvent event) {
		//Not needed
		return false;
	}

	@Override
	protected boolean onHitEntity(EntityDamageByEntityEvent event) {
		//Not needed
		return false;
	}
	
	@Override
	protected boolean onHitLocation(ProjectileHitEvent event){
		LivingEntity player = event.getEntity().getShooter();
		Location location = event.getEntity().getLocation();
		
		if(player instanceof Player)
			if(checkUplink((Player) player)) 
				return false;
		
		Location oldLocation = player.getLocation();
		enderEffectOnLocation(oldLocation);
		
		player.teleport(location);
		uplinkMap.put(((Player) player).getName(), uplinkTime);
		return false;
	}
	
	private void enderEffectOnLocation(Location loc){
		loc.getWorld().playEffect(loc, Effect.ENDER_SIGNAL, 0);
		
		loc.setY(loc.getY() + 1);
		loc.getWorld().playEffect(loc, Effect.ENDER_SIGNAL, 0);
		
		loc.setY(loc.getY() + 1);
		loc.getWorld().playEffect(loc, Effect.ENDER_SIGNAL, 0);
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
	protected String getArrowName() {
		return "Teleport Arrow";
	}
	
	@Override
	public void tickReduceUplink() {
		for(String player : uplinkMap.keySet()){
			int remainingTime = uplinkMap.get(player);
			remainingTime -= Races.getPlugin().interactConfig().getconfig_globalUplinkTickPresition();
			
			if(remainingTime == uplinkTime){
				Player tempPlayer = Bukkit.getPlayer(player);
				if(tempPlayer != null)
					tempPlayer.sendMessage(ChatColor.LIGHT_PURPLE + getName() + ChatColor.RED + " has faded.");
			}
				
			if(remainingTime <= 0)
				uplinkMap.remove(player);
			else
				uplinkMap.put(player, remainingTime);
		}
	}
	
	public static void pasteHelpForTrait(CommandSender sender) {
		sender.sendMessage(ChatColor.YELLOW + "When this arrow hits a target/block,");
		sender.sendMessage(ChatColor.YELLOW + "the player will be teleportet there.");
		sender.sendMessage(ChatColor.YELLOW + "Don't forget, that this trait has uplink.");
	}

}
