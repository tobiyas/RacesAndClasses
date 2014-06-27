/*******************************************************************************
 * Copyright 2014 Tobias Welther
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.tobiyas.racesandclasses.traitcontainer.traits.statictraits;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.configuration.traits.TraitConfig;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.AbstractBasicTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;

public class STDAxeDamageTrait extends AbstractBasicTrait {
	
	/**
	 * The Plugin to get the Managers from.
	 */
	private RacesAndClasses plugin = RacesAndClasses.getPlugin();
	
	private double woodDmg;
	private double stoneDmg;
	private double goldDmg;
	private double ironDmg;
	private double diamondDmg;
	
	public STDAxeDamageTrait(){
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
	protected String getPrettyConfigIntern() {
		return "";
	}

	@TraitConfigurationNeeded
	@Override
	public void setConfiguration(TraitConfiguration configMap) {
	}

	@Override	
	public TraitResults trigger(EventWrapper eventWrapper) {
		Event event = eventWrapper.getEvent();
		
		if(!(event instanceof EntityDamageByEntityEvent)) return TraitResults.False();
		EntityDamageByEntityEvent Eevent = (EntityDamageByEntityEvent) event;
		
		if(Eevent.getDamager() instanceof Player){
			Player player = (Player) Eevent.getDamager();
			double newDmg = getDamageOfAxe(player.getItemInHand().getType());
			
			if(newDmg != -1){
				CompatibilityModifier.EntityDamage.safeSetDamage(newDmg, Eevent);
				return TraitResults.True();
			}
		}
		
		return TraitResults.False();
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

	@Override
	public boolean canBeTriggered(EventWrapper wrapper) {
		return true;
	}
	
	@Override
	public boolean isStackable(){
		return false;
	}
	

	@Override
	public boolean isVisible() {
		return false;
	}
}
