package de.tobiyas.racesandclasses.traitcontainer.traits.statictraits;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.configuration.traits.TraitConfig;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitInfos;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier;

public class STDAxeDamageTrait implements Trait {
	
	/**
	 * The Plugin to get the Managers from.
	 */
	private RacesAndClasses plugin = RacesAndClasses.getPlugin();
	
	private double woodDmg;
	private double stoneDmg;
	private double goldDmg;
	private double ironDmg;
	private double diamondDmg;
	
	private AbstractTraitHolder traitHolder;
	
	public STDAxeDamageTrait(){
	}
	
	@Override
	public void setTraitHolder(AbstractTraitHolder abstractTraitHolder){
		this.traitHolder = abstractTraitHolder;
	}
	
	@Override
	public AbstractTraitHolder getTraitHolder(){
		return traitHolder;
	}

	@TraitEventsUsed(registerdClasses = {EntityDamageByEntityEvent.class})
	@Override
	public void generalInit() {		
		TraitConfig config = plugin.getConfigManager().getTraitConfigManager().getConfigOfTrait(getName());
		if(config != null){
			woodDmg =  (Integer) config.getValue("trait.damage.wood", 4.0);
			stoneDmg = (Integer) config.getValue("trait.damage.stone", 5.0);
			goldDmg = (Integer) config.getValue("trait.damage.gold", 4.0);
			ironDmg = (Integer) config.getValue("trait.damage.iron", 6.0);
			diamondDmg = (Integer) config.getValue("trait.damage.diamond", 7.0);
		}	
	}

	@Override
	public String getName() {
		return "STDAxeDamageTrait";
	}


	@Override
	public String getPrettyConfiguration() {
		return "";
	}

	@TraitConfigurationNeeded
	@Override
	public void setConfiguration(Map<String, String> configMap) {
	}

	@Override
	public boolean modify(Event event) {
		if(!(event instanceof EntityDamageByEntityEvent)) return false;
		EntityDamageByEntityEvent Eevent = (EntityDamageByEntityEvent) event;
		
		if(Eevent.getDamager() instanceof Player){
			Player player = (Player) Eevent.getDamager();
			double newDmg = getDamageOfAxe(player.getItemInHand().getType());
			
			if(newDmg != -1){
				CompatibilityModifier.EntityDamage.safeSetDamage(newDmg, Eevent);
				return true;
			}
		}
		
		return false;
	}

	
	private double getDamageOfAxe(Material material){
		if(material == null) return -1;
		
		switch(material){
			case WOOD_AXE: return woodDmg;
			case STONE_AXE: return stoneDmg;
			case GOLD_AXE: return goldDmg;
			case IRON_AXE: return ironDmg;
			case DIAMOND_AXE: return diamondDmg;
			default: return -1;
		}
	}
	
	@Override
	public boolean isBetterThan(Trait trait) {
		//STD and no difference
		return true;
	}

	@TraitInfos(category="static", traitName="STDAxeDamageTrait", visible=false)
	@Override
	public void importTrait() {
	}

}
