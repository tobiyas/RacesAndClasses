package de.tobiyas.races.datacontainer.traitcontainer.traits.arrows;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import de.tobiyas.races.datacontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitcontainer.TraitEventManager;

public class FireArrowTrait extends AbstractArrow {
	
	public FireArrowTrait(RaceContainer raceContainer){
		this.raceContainer = raceContainer;
		TraitEventManager.getTraitEventManager().registerTrait(this);
	}
	
	@Override
	public String getName() {
		return "FireArrowTrait";
	}

	@Override
	public String getValueString() {
		return this.totalDamage + " Fire-Damage over " + this.duration + " seconds.";
	}

	@Override
	public void setValue(Object obj) {
		String preValue = (String) obj;
		
		String[] split = preValue.split("#");
		if(split.length != 2)
			split = new String[]{"20", "7"};
		
		duration = Integer.valueOf(split[0]);
		this.totalDamage = Double.valueOf(split[0]);
	}

	
	@Override
	protected boolean onShoot(EntityShootBowEvent event){
		Location loc = event.getEntity().getLocation();
		loc.getWorld().playEffect(loc, Effect.MOBSPAWNER_FLAMES, 0);
		return false;
	}
	
	@Override
	protected boolean onHitEntity(EntityDamageByEntityEvent event){
		Entity hitTarget = event.getEntity();
		hitTarget.setFireTicks(duration * 20);
		return true;
	}

	@Override
	public boolean isVisible(){
		return true;
	}

	@Override
	protected String getArrowName(){
		return "Fire Arrow";
	}

	@Override
	protected boolean onHitLocation(ProjectileHitEvent event) {
		//Not needed
		return false;
	}

	public static void pasteHelpForTrait(CommandSender sender) {
		sender.sendMessage(ChatColor.YELLOW + "If you hit an enemy with an arrow and choosen the Fire Arrow as current arrow,");
		sender.sendMessage(ChatColor.YELLOW + "He will start burning.");
	}
}
