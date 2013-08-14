package de.tobiyas.racesandclasses.traitcontainer.traits.statictraits;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.configuration.traits.TraitConfig;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitInfos;
import de.tobiyas.racesandclasses.util.consts.Consts;
import de.tobiyas.racesandclasses.util.items.CreateDropContainer;
import de.tobiyas.racesandclasses.util.items.DropContainer;

public class DeathCheckerTrait implements Trait {
	
	private AbstractTraitHolder traitHolder;
	
	private HashMap<EntityType, DropContainer> dropMap;
	private RacesAndClasses plugin;
	
	public DeathCheckerTrait(){
	}
	
	@Override
	public void setTraitHolder(AbstractTraitHolder abstractTraitHolder){
		this.traitHolder = abstractTraitHolder;
	}
	
	@Override
	public AbstractTraitHolder getTraitHolder(){
		return traitHolder;
	}

	@TraitEventsUsed(registerdClasses = {EntityDeathEvent.class})
	@Override
	public void generalInit() {
		plugin = RacesAndClasses.getPlugin();
		dropMap = new HashMap<EntityType, DropContainer>();
		
		TraitConfig config = plugin.getConfigManager().getTraitConfigManager().getConfigOfTrait(getName());
		if(config == null){
			initListsDefault();
		}
		
		readListFromConfig(config);
	}
	
	private void readListFromConfig(TraitConfig config){
		for(EntityType type : EntityType.values()){
			String path = Consts.traitConfigDir + "DropRates.yml";
			DropContainer container = CreateDropContainer.loadDropContainer(path, type);
			if(container != null)
				dropMap.put(type, container);
		}
	}
	
	private void initListsDefault(){
		String path = Consts.traitConfigDir + "DropRates.yml";
		CreateDropContainer.createAllContainers(path);			
	}

	@Override
	public String getName() {
		return "DropRates";
	}


	@Override
	public String getPrettyConfiguration() {
		return null;
	}

	@TraitConfigurationNeeded
	@Override
	public void setConfiguration(Map<String, String> configMap) {
	}

	@Override
	public boolean modify(Event event) {
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_enable_expDropBonus()) return false;
		
		if(!(event instanceof EntityDeathEvent)) return false;
		EntityDeathEvent Eevent = (EntityDeathEvent) event;
		
		LivingEntity entity = Eevent.getEntity();
		if(entity == null || entity instanceof Player) return false;
		
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
	public boolean isBetterThan(Trait trait) {
		//Singleton, Not needed.
		return true;
	}

	@TraitInfos(category="static", traitName="DropTrait", visible=false)
	@Override
	public void importTrait() {
		// TODO Auto-generated method stub
		
	}

}
