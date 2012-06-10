package de.tobiyas.races.datacontainer.traitcontainer.traits.passive;

import java.util.HashSet;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import de.tobiyas.races.configuration.traits.TraitConfig;
import de.tobiyas.races.configuration.traits.TraitConfigManager;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.TraitEventManager;
import de.tobiyas.races.datacontainer.traitcontainer.traits.Trait;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassManager;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceManager;

public class STDAxeDamageTrait implements Trait {
	
	private int woodDmg;
	private int stoneDmg;
	private int goldDmg;
	private int ironDmg;
	private int diamondDmg;
	
	private RaceContainer raceContainer = null;
	private ClassContainer classContainer = null;
	
	public STDAxeDamageTrait(RaceContainer raceContainer){
		this.raceContainer = raceContainer;
	}

	@Override
	public void generalInit() {
		HashSet<Class<?>> listenedEvents = new HashSet<Class<?>>();
		listenedEvents.add(EntityDamageByEntityEvent.class);
		TraitEventManager.getInstance().registerTrait(this, listenedEvents);
		
		TraitConfig config = TraitConfigManager.getInstance().getConfigOfTrait(getName());
		if(config != null){
			woodDmg = (int) config.getValue("trait.damage.wood", 4);
			stoneDmg = (int) config.getValue("trait.damage.stone", 5);
			goldDmg = (int) config.getValue("trait.damage.gold", 4);
			ironDmg = (int) config.getValue("trait.damage.iron", 6);
			diamondDmg = (int) config.getValue("trait.damage.diamond", 7);
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
		if(!(event instanceof EntityDamageByEntityEvent)) return false;
		EntityDamageByEntityEvent Eevent = (EntityDamageByEntityEvent) event;
		
		if(Eevent.getDamager() instanceof Player){
			Player player = (Player) Eevent.getDamager();
			if(!checkContainer(player.getName())) return false;
			int newDmg = getDamageOfAxe(player.getItemInHand().getType());
			if(newDmg != -1){
				Eevent.setDamage(newDmg);
				return true;
			}
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

	@Override
	public boolean isVisible() {
		return false;
	}
	
	private int getDamageOfAxe(Material material){
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

}
