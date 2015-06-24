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
package de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces;

import org.bukkit.Material;

import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;

/**
 * This interface indicates that the Trait is a magic Spell.
 * It indicates that it needs magic points to cast.
 * 
 * @author Tobiyas
 */
public interface MagicSpellTrait extends TraitWithRestrictions {
	
	/**
	 * Returns the cost of the Spell.
	 * The Cost can vary on which Cost Type used.
	 * 
	 * @return the costs of the spell
	 */
	public double getCost();
	
	
	/**
	 * Returns the {@link CostType} of the Spell.
	 * To see the different type of costs, see {@link CostType}
	 * 
	 * @return the CostType of the Spell.
	 * 
	 * @see CostType
	 */
	public CostType getCostType();
	
	
	/**
	 * returns the Material Type of CostType.
	 * <br> Returns null if no Material costType is needed
	 * 
	 * @return
	 */
	public Material getCastMaterialType();
	
	
	/**
	 * triggered when the spell should be triggered, but no CostType is present.
	 * 
	 * @param event that triggered
	 * @param player that triggered the spell
	 */
	public void triggerButDoesNotHaveEnoghCostType(EventWrapper wrapper);
	
	
	/**
	 * The Cost type of the Spells.
	*/
	public enum CostType{
		/**
		 * Mana is used to cast this spell.
		 */
		MANA,
		
		/**
		 * Health is used to cast this spell.
		 */
		HEALTH,
		
		/**
		 * An specific Item is used to cast this spell.
		 */
		ITEM,
		
		/**
		 * This costs Experience to cost.
		 */
		EXP,
		
		/**
		 * The Hunger bar is drained to cast the spell.
		 */
		HUNGER;
		
		
		
		@Override
		public String toString(){
			String name = this.name();
			String pre = name.substring(0, 1).toUpperCase();
			String rest = name.substring(1, name.length()).toLowerCase();
			
			return pre + rest;
		}
		
		
		/**
		 * Tries to parse the CostType by name.
		 * Returns null if not parsable.
		 * 
		 * @param costTypeName to parse
		 * @return CostType parsed
		 */
		public static CostType tryParse(String costTypeName){
			for(CostType type : values()){
				if(type.name().equalsIgnoreCase(costTypeName)){
					return type;
				}
			}
			
			if(costTypeName.equalsIgnoreCase("leben")){
				return HEALTH;
			}
			
			
			//parse Hunger stuff
			if(costTypeName.equalsIgnoreCase("foodlevel")){
				return HUNGER;
			}
			
			if(costTypeName.equalsIgnoreCase("food")){
				return HUNGER;
			}
			
			if(costTypeName.equalsIgnoreCase("essen")){
				return HUNGER;
			}
			
			if(costTypeName.equalsIgnoreCase("experience")){
				return EXP;
			}
			
			if(costTypeName.equalsIgnoreCase("xp")){
				return EXP;
			}
			
			return null;
		}
	}
	
	
	/**
	 * Returns true if a CostCheck is needed for this event.
	 * 
	 * @param event the Event to check
	 * 
	 * @return true if costCheck needed, false otherwise.
	 */
	public boolean needsCostCheck(EventWrapper wrapper);
	
}
