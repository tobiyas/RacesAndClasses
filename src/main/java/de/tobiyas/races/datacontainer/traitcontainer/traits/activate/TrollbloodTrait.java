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
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import de.tobiyas.races.Races;
import de.tobiyas.races.configuration.traits.TraitConfig;
import de.tobiyas.races.configuration.traits.TraitConfigManager;
import de.tobiyas.races.datacontainer.health.damagetickers.DamageTicker;
import de.tobiyas.races.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitcontainer.traits.Trait;
import de.tobiyas.races.datacontainer.traitcontainer.traits.TraitsWithUplink;

public class TrollbloodTrait implements TraitsWithUplink {

	private RaceContainer raceContainer = null;
	private ClassContainer classContainer = null;
	private int duration;
	
	private HashMap<String, Integer> uplinkMap = new HashMap<String, Integer>();
	private static int uplinkTime = 60 * 20;
	private static int itemIDInHand = Material.APPLE.getId();
	
	public TrollbloodTrait(){
	}
	
	@Override
	public void setRace(RaceContainer container) {
		this.raceContainer = container;
	}

	@Override
	public void setClazz(ClassContainer container) {
		this.classContainer = container;
	}
	
	@TraitInfo(registerdClasses = {PlayerInteractEvent.class})
	@Override
	public void generalInit() {
		TraitConfig config = TraitConfigManager.getInstance().getConfigOfTrait(getName());
		if(config != null){
			uplinkTime = (Integer) config.getValue("trait.uplink", 60) * 20;
			itemIDInHand = (Integer) config.getValue("trait.iteminhand", Material.APPLE.getId());
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
	public ClassContainer getClazz() {
		return classContainer;
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

		if(player.getItemInHand().getType().getId() != itemIDInHand) return false;
		if(TraitHolderCombinder.checkContainer(player.getName(), this)){
			if(checkUplink(player))
				return false;

			int i = 0;
			Collection<PotionEffect> effects = player.getActivePotionEffects();
			for(PotionEffect effect : effects)
				if(effect.getType().equals(PotionEffectType.POISON))
					i++;

			player.removePotionEffect(PotionEffectType.POISON);
			i += DamageTicker.cancleEffects(player, DamageCause.POISON);
			
			synchronized(uplinkMap){
				uplinkMap.put(player.getName(), uplinkTime + duration);
			}
			player.sendMessage(ChatColor.LIGHT_PURPLE + getName() + ChatColor.GREEN + " toggled. " + 
								ChatColor.LIGHT_PURPLE + i + ChatColor.GREEN + " poison effects removed.");
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
		synchronized(uplinkMap){
			for(String player : uplinkMap.keySet()){
				int remainingTime = uplinkMap.get(player);
				remainingTime -= Races.getPlugin().getGeneralConfig().getConfig_globalUplinkTickPresition();
				
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
	}

	@Override
	public boolean isVisible() {
		return true;
	}
	
	public static void pasteHelpForTrait(CommandSender sender) {
		sender.sendMessage(ChatColor.YELLOW + "The trait removes all poison effects on you.");
		sender.sendMessage(ChatColor.YELLOW + "It can be used by 'left-click' with a " + ChatColor.LIGHT_PURPLE + Material.getMaterial(itemIDInHand).name() + ChatColor.YELLOW + " in hands.");
	}

	@Override
	public boolean isBetterThan(Trait trait) {
		if(!(trait instanceof TrollbloodTrait)) return false;
		return duration >= (Integer) trait.getValue();
	}

}
