package de.tobiyas.races.datacontainer.traitcontainer.traits.statictraits;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import de.tobiyas.races.configuration.traits.TraitConfig;
import de.tobiyas.races.configuration.traits.TraitConfigManager;
import de.tobiyas.races.datacontainer.eventmanagement.events.EntityDamageByEntityDoubleEvent;
import de.tobiyas.races.datacontainer.traitcontainer.traits.Trait;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;

public class STDAxeDamageTrait implements Trait {
	
	private double woodDmg;
	private double stoneDmg;
	private double goldDmg;
	private double ironDmg;
	private double diamondDmg;
	
	private RaceContainer raceContainer = null;
	private ClassContainer classContainer = null;
	
	public STDAxeDamageTrait(){
	}
	
	@Override
	public void setRace(RaceContainer container) {
		this.raceContainer = container;
	}

	@Override
	public void setClazz(ClassContainer container) {
		this.classContainer = container;
	}

	@TraitInfo(registerdClasses = {EntityDamageByEntityDoubleEvent.class})
	@Override
	public void generalInit() {		
		TraitConfig config = TraitConfigManager.getInstance().getConfigOfTrait(getName());
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
	public RaceContainer getRace() {
		return raceContainer;
	}

	@Override
	public ClassContainer getClazz() {
		return classContainer;
	}

	@Override
	public Object getValue() {
		return 1;
	}

	@Override
	public String getValueString() {
		return "";
	}

	@Override
	public void setValue(Object obj) {
		

	}

	@Override
	public boolean modify(Event event) {
		if(!(event instanceof EntityDamageByEntityDoubleEvent)) return false;
		EntityDamageByEntityDoubleEvent Eevent = (EntityDamageByEntityDoubleEvent) event;
		
		if(Eevent.getDamager() instanceof Player){
			Player player = (Player) Eevent.getDamager();
			double newDmg = getDamageOfAxe(player.getItemInHand().getType());
			if(newDmg != -1){
				Eevent.setDoubleValueDamage(newDmg);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isVisible() {
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

}
