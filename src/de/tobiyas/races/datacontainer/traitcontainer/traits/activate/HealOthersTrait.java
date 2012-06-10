package de.tobiyas.races.datacontainer.traitcontainer.traits.activate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Observable;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import de.tobiyas.races.Races;
import de.tobiyas.races.configuration.traits.TraitConfig;
import de.tobiyas.races.configuration.traits.TraitConfigManager;
import de.tobiyas.races.datacontainer.health.HealthManager;
import de.tobiyas.races.datacontainer.health.HealthModifyContainer;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.TraitEventManager;
import de.tobiyas.races.datacontainer.traitcontainer.traits.TraitsWithUplink;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassManager;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceManager;

public class HealOthersTrait extends Observable implements TraitsWithUplink{
	
	private double value;
	
	private RaceContainer raceContainer;
	private ClassContainer classContainer;
	
	private static HashMap<String, Integer> uplinkMap = new HashMap<String, Integer>();
	
	private static int uplinkTime = 60*20;
	private static int itemIDInHand = Material.STRING.getId();

	public HealOthersTrait(RaceContainer raceContainer){
		this.raceContainer = raceContainer;
	}
	
	public HealOthersTrait(ClassContainer classContainer){
		this.classContainer = classContainer;
	}

	@Override
	public void generalInit() {
		HashSet<Class<?>> listenedEvents = new HashSet<Class<?>>();
		listenedEvents.add(PlayerInteractEntityEvent.class);
		TraitEventManager.getInstance().registerTrait(this, listenedEvents);
		addObserver(HealthManager.getHealthManager());
		
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
		if(!(event instanceof PlayerInteractEntityEvent)) return false;
		PlayerInteractEntityEvent Eevent = (PlayerInteractEntityEvent) event;
		
		Entity target = Eevent.getRightClicked();
		if(target != null && target instanceof Player){
			Player player = Eevent.getPlayer();
			if(player.getItemInHand().getTypeId() != itemIDInHand) return false;
			if(!checkContainer(player.getName())) return false;
			if(!checkUplink(player)) return false;
			
			Player targetPlayer = (Player) target;
			notifyObservers(new HealthModifyContainer(targetPlayer.getName(), value, "heal"));
			setChanged();
			uplinkMap.put(player.getName(), uplinkTime);
		}
		return false;
	}
	
	private boolean checkContainer(String playerName){
		if(raceContainer != null){
			RaceContainer container = RaceManager.getManager().getRaceOfPlayer(playerName);
			if(container == null) return true;
			return raceContainer == container;
		}
		if(classContainer != null){
			ClassContainer container = ClassManager.getInstance().getClassOfPlayer(playerName);
			if(container == null) return true;
			return classContainer == container;
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
		for(String player : uplinkMap.keySet()){
			int remainingTime = uplinkMap.get(player);
			remainingTime -= Races.getPlugin().interactConfig().getconfig_globalUplinkTickPresition();
				
			if(remainingTime <= 0)
				uplinkMap.remove(player);
			else
				uplinkMap.put(player, remainingTime);
		}
	}

}
