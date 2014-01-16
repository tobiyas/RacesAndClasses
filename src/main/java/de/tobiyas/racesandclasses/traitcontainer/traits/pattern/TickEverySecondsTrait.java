package de.tobiyas.racesandclasses.traitcontainer.traits.pattern;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.plugin.java.JavaPlugin;

import de.tobiyas.racesandclasses.traitcontainer.interfaces.AbstractBasicTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public abstract class TickEverySecondsTrait extends AbstractBasicTrait {

	
private int schedulerTaskId = -1;
	
	/**
	 * The Seconds when this is fired.
	 */
	protected int seconds = 1;
	
	
	@TraitEventsUsed(registerdClasses = {  })
	@Override
	public void generalInit() {
		schedulerTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask((JavaPlugin)plugin, new Runnable() {
			
			@Override
			public void run() {
				for(String playerName : holder.getHolderManager().getAllPlayersOfHolder(holder)){
					Player player = Bukkit.getPlayer(playerName);
					if(player != null 
							&& !checkRestrictions(player, null) 
							&& canBeTriggered(new PlayerBedEnterEvent(player, null))){
						
						
						if(tickDoneForPlayer(player)){
							plugin.getStatistics().traitTriggered(TickEverySecondsTrait.this);
						}
					}
				}
				
				
			}
		}, seconds * 20, seconds * 20);
	}
	
	/**
	 * Is called when the Tick for the Player is on it's way.
	 * 
	 * @param player The tick for the Player is done.
	 * 
	 * @return true if it triggered, false otherwise.
	 */
	protected abstract boolean tickDoneForPlayer(Player player);
	
	
	@Override
	public void deInit(){
		Bukkit.getScheduler().cancelTask(schedulerTaskId);
	}


	@Override
	public String getPrettyConfiguration() {
		String reason = "Nothing";
		if(onlyInLava){
			reason = "in Lava";
		}

		if(onlyInWater){
			reason = "in Water";
		}

		if(onlyOnLand){
			reason = "on Land";
		}

		if(onlyOnDay && !onlyInNight){
			reason = "in NightShine";
		}
		
		if(onlyInNight && !onlyOnDay){
			reason = "on DayLight";
		}
		
		return " every: " + seconds + " sec for " + reason;
	}
	
	/**
	 * Returns the Pre before the restrictions come.
	 * @return
	 */
	protected abstract String getPrettyConfigurationPre();

	@TraitConfigurationNeeded( fields = {
			@TraitConfigurationField(fieldName = "seconds", classToExpect = Integer.class, optional = false)
		})
	@Override
	public void setConfiguration(Map<String, Object> configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		seconds = (Integer) configMap.get("seconds");
	}

	@Override
	public boolean trigger(Event event) {
		//Not needed
		return true;
	}

	
	
	@Override
	public boolean isBetterThan(Trait trait) {
		if (!(trait instanceof TickEverySecondsTrait))
			return false;
		TickEverySecondsTrait otherTrait = (TickEverySecondsTrait) trait;

		return seconds >= otherTrait.seconds;
	}
	
	@Override
	public boolean isStackable(){
		return true;
	}


	@Override
	public boolean canBeTriggered(Event event) {
		if(event instanceof PlayerEvent){
			Player player = ((PlayerEvent) event).getPlayer();
			
			int lightFromSky = player.getLocation().getBlock().getLightFromSky();
			if(onlyOnDay){ //TODO fixme
				if(lightFromSky > 2){
					return false;
				}
			}

			return true;
		}
		
		return false;
	}
}
