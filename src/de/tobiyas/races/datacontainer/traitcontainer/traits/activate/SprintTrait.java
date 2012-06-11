package de.tobiyas.races.datacontainer.traitcontainer.traits.activate;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.potion.PotionEffectTypeWrapper;

import de.tobiyas.races.Races;
import de.tobiyas.races.configuration.traits.TraitConfig;
import de.tobiyas.races.configuration.traits.TraitConfigManager;
import de.tobiyas.races.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.TraitEventManager;
import de.tobiyas.races.datacontainer.traitcontainer.traits.TraitsWithUplink;

public class SprintTrait implements TraitsWithUplink {

	private int value;
	private int duration;
	private RaceContainer raceContainer;
	private ClassContainer classContainer;
	
	private static HashMap<String, Integer> uplinkMap = new HashMap<String, Integer>();
	
	private static int uplinkTime = 60*20;
	private static int itemIDInHand = Material.APPLE.getId();

	public SprintTrait(RaceContainer raceContainer){
		this.raceContainer = raceContainer;
	}
	
	public SprintTrait(ClassContainer classContainer){
		this.classContainer = classContainer;
	}
	
	@Override
	public void generalInit() {
		HashSet<Class<?>> listenedEvents = new HashSet<Class<?>>();
		listenedEvents.add(PlayerToggleSprintEvent.class);
		TraitEventManager.getInstance().registerTrait(this, listenedEvents);
		
		TraitConfig config = TraitConfigManager.getInstance().getConfigOfTrait(getName());
		if(config != null){
			uplinkTime = (int) config.getValue("trait.uplink", 60) * 20;
			itemIDInHand = (int) config.getValue("trait.iteminhand", Material.APPLE.getId());
		}	
	}
	
	@Override
	public String getName(){
		return "SprintTrait";
	}

	@Override
	public RaceContainer getRace(){
		return raceContainer;
	}
	
	@Override
	public ClassContainer getClazz() {
		return classContainer;
	}

	@Override
	public Object getValue(){
		return value;
	}

	@Override
	public String getValueString() {
		return "level(" + String.valueOf(value) + ") for " + (int) Math.ceil(duration/20) + "seconds";
	}

	@Override
	public void setValue(Object obj) {
		String combinedString = (String) obj;
		
		String[] splits = combinedString.split("#");
		
		if(splits.length == 1){
			splits = new String[]{"10" ,splits[0]};
		}
		
		if(splits.length > 2){
			splits = new String[]{"10" ,"2"};
		}
		
		duration = Integer.valueOf(splits[0]) * 20;
		value = Integer.valueOf(splits[1]);
	}

	@Override
	public boolean modify(Event event) {
		if(!(event instanceof PlayerToggleSprintEvent)) return false;
		PlayerToggleSprintEvent Eevent = (PlayerToggleSprintEvent) event;
		if(!Eevent.isSprinting())
			return false;
		
		Player player = Eevent.getPlayer();
		if(player.getItemInHand().getType().getId() != itemIDInHand) return false;
		
		if(TraitHolderCombinder.checkContainer(player.getName(), this)){
			if(checkUplink(player)) return false;			
			
			uplinkMap.put(player.getName(), uplinkTime + duration);
			player.sendMessage(ChatColor.LIGHT_PURPLE + "SprintTrait " + ChatColor.GREEN + " toggled.");
			player.addPotionEffect(PotionEffectTypeWrapper.SPEED.createEffect(duration, value - 1), true);
			return true;
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
		sender.sendMessage(ChatColor.YELLOW + "The trait lets you sprint (move faster) for a short time.");
		sender.sendMessage(ChatColor.YELLOW + "It can be used by toggleing sprint with a " + ChatColor.LIGHT_PURPLE + Material.getMaterial(itemIDInHand).name() + ChatColor.YELLOW + " in hands.");
	}

}
