package de.tobiyas.racesandclasses.traitcontainer.traits.statictraits;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.LazyMetadataValue;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.configuration.traits.TraitConfig;
import de.tobiyas.racesandclasses.playermanagement.leveling.PlayerLevelManager;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.AbstractBasicTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.bypasses.StaticTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.util.consts.Consts;
import de.tobiyas.racesandclasses.util.items.CreateDropContainer;
import de.tobiyas.racesandclasses.util.items.DropContainer;

public class DeathCheckerTrait extends AbstractBasicTrait implements StaticTrait{
	
	private HashMap<EntityType, DropContainer> dropMap;
	private RacesAndClasses plugin;
	

	@TraitEventsUsed(registerdClasses = {EntityDeathEvent.class, CreatureSpawnEvent.class})
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
		return "";
	}

	@TraitConfigurationNeeded
	@Override
	public void setConfiguration(Map<String, Object> configMap) {
	}
	
	

	@Override
	public boolean checkRestrictions(Player player, Event event) {
		return true;
	}

	
	/**
	 * This key indicates that the Monster has been spawned by a Spawner.
	 */
	private final static String SPAWNER_KEY = "spawner_spawned";

	@Override
	public boolean trigger(Event event) {
		if(event instanceof CreatureSpawnEvent){
			if(((CreatureSpawnEvent) event).getSpawnReason() == SpawnReason.SPAWNER){
				((CreatureSpawnEvent) event).getEntity().setMetadata(SPAWNER_KEY, new LazyMetadataValue(plugin, new Callable<Object>() {
					
					@Override
					public Object call() throws Exception {
						return true;
					}
				}));
				
				return true;
			}
			
			return false;
		}
		
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_enable_expDropBonus()) return false;
		
		if(!(event instanceof EntityDeathEvent)) return false;
		EntityDeathEvent Eevent = (EntityDeathEvent) event;
		
		LivingEntity entity = Eevent.getEntity();
		if(entity == null || entity instanceof Player) return false;
		
		int exp = Eevent.getDroppedExp();
		List<ItemStack> items = Eevent.getDrops();
		
		Eevent.setDroppedExp(modifyEXP(exp, entity));
		modifyItems(items, entity);
		
		if(Eevent.getEntity().getMetadata(SPAWNER_KEY).isEmpty()) return false;
		Player killer = entity.getKiller();
		if(killer != null){
			int expForLevel = (int) (Eevent.getDroppedExp() / 10d);
			
			PlayerLevelManager manager = plugin.getPlayerManager().getPlayerLevelManager(killer.getName());
			manager.addExp(expForLevel);
		}
		
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
	}

	@Override
	public boolean canBeTriggered(Event event) {
		return true;
	}

	@Override
	public boolean isStackable(){
		return false;
	}
}