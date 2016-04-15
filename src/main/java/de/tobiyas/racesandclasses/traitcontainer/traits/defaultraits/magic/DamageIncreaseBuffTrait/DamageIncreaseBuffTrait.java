package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.DamageIncreaseBuffTrait;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.pattern.AbstractBuffTrait;
import de.tobiyas.racesandclasses.util.friend.EnemyChecker.FriendDetectEvent;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class DamageIncreaseBuffTrait extends AbstractBuffTrait {

	/**
	 * The amount to increase.
	 */
	private double percent = 0.1;
	
	
	@TraitInfos(category="magic", traitName="DamageIncreaseBuffTrait", visible=true)
	@Override
	public void importTrait() {}
	
	
	@TraitEventsUsed()
	@Override
	public void generalInit() { super.generalInit(); }
	
	
	@TraitConfigurationNeeded( fields = {
			@TraitConfigurationField(fieldName = "value", classToExpect = Double.class, optional = true), 
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		this.percent = configMap.getAsDouble("value", 0.1);
	}
	

	@Override
	public String getName() {
		return "DamageIncreaseBuffTrait";
	}
	

	@Override
	public boolean isBetterThan(Trait trait) {
		return percent > ((DamageIncreaseBuffTrait)trait).percent;
	}
	

	@Override
	protected void buffActivated(RaCPlayer player) {}
	

	@Override
	protected void buffTimeouted(RaCPlayer player) {}
	
	
	@EventHandler(priority=EventPriority.HIGH)
	public void entityDamage(EntityDamageByEntityEvent event){
		Entity damager = event.getDamager();
		if(damager.getType() != EntityType.PLAYER) return;
		if(event.isCancelled()) return;
		if(event instanceof FriendDetectEvent) return;
		
		RaCPlayer player = RaCPlayerManager.get().getPlayer(damager.getUniqueId());
		if(!TraitHolderCombinder.checkContainer(player, this)) return;
		
		double value = (modifyToPlayer(player, percent, "value") + 1 ) * event.getDamage();
		event.setDamage(value);
		
		buffUsed(player);
	}
	

	@Override
	protected String getPrettyConfigIntern() {
		return "does " + (int)(percent*100) + "% more melee damage for " + duration + " seconds";
	}

}
