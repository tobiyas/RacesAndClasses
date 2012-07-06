package de.tobiyas.races.datacontainer.traitcontainer.traits.statictraits;

import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.races.Races;
import de.tobiyas.races.configuration.traits.TraitConfig;
import de.tobiyas.races.configuration.traits.TraitConfigManager;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.EntityDeathManager;
import de.tobiyas.races.datacontainer.traitcontainer.traits.Trait;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.races.util.items.CreateDropContainer;
import de.tobiyas.races.util.items.DropContainer;

public class DeathCheckerTrait implements Trait {
	
	private HashMap<EntityType, DropContainer> dropMap;
	private Races plugin;
	
	public DeathCheckerTrait(){
		plugin = Races.getPlugin();
		dropMap = new HashMap<EntityType, DropContainer>();
	}

	@TraitInfo(registerdClasses = {EntityDeathEvent.class})
	@Override
	public void generalInit() {
		dropMap.clear();
		
		TraitConfig config = TraitConfigManager.getInstance().getConfigOfTrait(getName());
		if(config == null){
			initListsDefault();
		}
		
		readListFromConfig(config);
	}
	
	private void readListFromConfig(TraitConfig config){
		for(EntityType type : EntityType.values()){
			DropContainer container = CreateDropContainer.loadDropContainer(type);
			if(container != null)
				dropMap.put(type, container);
		}
	}
	
	private void initListsDefault(){
		CreateDropContainer.createAllContainers();			
	}

	@Override
	public String getName() {
		return "DropRates";
	}

	@Override
	public RaceContainer getRace() {
		return null;
	}

	@Override
	public ClassContainer getClazz() {
		return null;
	}

	@Override
	public Object getValue() {
		return null;
	}

	@Override
	public String getValueString() {
		return null;
	}

	@Override
	public void setValue(Object obj) {
	}

	@Override
	public boolean modify(Event event) {
		if(!plugin.getGeneralConfig().getConfig_enable_expDropBonus()) return false;
		
		if(!(event instanceof EntityDeathEvent)) return false;
		EntityDeathEvent Eevent = (EntityDeathEvent) event;
		
		LivingEntity entity = Eevent.getEntity();
		if(entity == null || entity instanceof Player) return false;
		
		boolean willDrop = EntityDeathManager.getManager().willDrop(entity);
		if(!willDrop) return false;
		
		
		int exp = Eevent.getDroppedExp();
		List<ItemStack> items = Eevent.getDrops();
		
		Eevent.setDroppedExp(modifyEXP(exp, entity));
		modifyItems(items, entity);
		return true;
	}
	
	private int modifyEXP(int exp, LivingEntity entity){
		DropContainer container = dropMap.get(entity.getType());
		if(container == null) return exp;
		
		return container.getEXP();
	}
	
	private void modifyItems(List<ItemStack> list, LivingEntity entity){
		DropContainer container = dropMap.get(entity.getType());
		if(container == null) return;
		
		for(ItemStack stack : container.getItems())
			list.add(stack);
	}

	@Override
	public boolean isVisible() {
		return false;
	}

	@Override
	public boolean isBetterThan(Trait trait) {
		//Singleton, Not needed.
		return true;
	}

}
