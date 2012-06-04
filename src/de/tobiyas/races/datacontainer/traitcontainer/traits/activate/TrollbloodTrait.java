package de.tobiyas.races.datacontainer.traitcontainer.traits.activate;

import java.util.Collection;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import de.tobiyas.races.Races;
import de.tobiyas.races.configuration.traits.TraitConfig;
import de.tobiyas.races.configuration.traits.TraitConfigManager;
import de.tobiyas.races.datacontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.race.RaceManager;
import de.tobiyas.races.datacontainer.traitcontainer.TraitEventManager;
import de.tobiyas.races.datacontainer.traitcontainer.traits.TraitsWithUplink;

public class TrollbloodTrait implements TraitsWithUplink {

	private RaceContainer raceContainer;
	private int duration;
	
	private static HashMap<String, Integer> uplinkMap = new HashMap<String, Integer>();
	private static int uplinkTime = 60 * 20;
	private static int itemIDInHand = Material.APPLE.getId();
	
	public TrollbloodTrait(RaceContainer raceContainer){
		this.raceContainer = raceContainer;
		TraitEventManager.getTraitEventManager().registerTrait(this);
		TraitConfig config = TraitConfigManager.getInstance().getConfigOfTrait(getName());
		if(config != null){
			uplinkTime = (int) config.getValue("trait.uplink", 60) * 20;
			itemIDInHand = (int) config.getValue("trait.iteminhand", Material.APPLE.getId());
		}
	}
	
	@Override
	public String getName() {
		return "TrollbloodTrait";
	}

	@Override
	public RaceContainer getRace() {
		return raceContainer;
	}

	@Override
	public Object getValue() {
		return duration;
	}

	@Override
	public String getValueString() {
		return "duration: " + (int) Math.ceil(duration/20);
	}

	@Override
	public void setValue(Object obj) {
		String tempString = (String) obj;
		duration = Integer.valueOf(tempString);
	}

	@Override
	public boolean modify(Event event) {
		if(!(event instanceof PlayerInteractEvent)) return false;
		PlayerInteractEvent Eevent = (PlayerInteractEvent) event;
		if(!(Eevent.getAction().equals(Action.LEFT_CLICK_BLOCK) || Eevent.getAction().equals(Action.LEFT_CLICK_AIR))) return false;
		Player player = Eevent.getPlayer();
		RaceContainer container = RaceManager.getManager().getRaceOfPlayer(player.getName());

		if(player.getItemInHand().getType().getId() != itemIDInHand) return false;
		if(container == raceContainer){
			if(checkUplink(player))
				return false;

			int i = 0;
			Collection<PotionEffect> effects = player.getActivePotionEffects();
			for(PotionEffect effect : effects)
				if(effect.getType().equals(PotionEffectType.POISON))
					i++;

			player.removePotionEffect(PotionEffectType.POISON);
			uplinkMap.put(player.getName(), uplinkTime + duration);
			player.sendMessage(ChatColor.GREEN + "SprintTrait toggled. " + ChatColor.LIGHT_PURPLE + i + ChatColor.GREEN + " poison effects removed.");
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

	@Override
	public boolean isVisible() {
		return true;
	}
	
	public static void pasteHelpForTrait(CommandSender sender) {
		sender.sendMessage(ChatColor.YELLOW + "The trait removes all poison effects on you.");
		sender.sendMessage(ChatColor.YELLOW + "It can be used by 'left-click' with a " + ChatColor.LIGHT_PURPLE + Material.getMaterial(itemIDInHand).name() + ChatColor.YELLOW + " in hands.");
	}

}
