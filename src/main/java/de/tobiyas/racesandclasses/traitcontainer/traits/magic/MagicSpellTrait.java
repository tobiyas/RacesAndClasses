package de.tobiyas.racesandclasses.traitcontainer.traits.magic;

import org.bukkit.Material;
import org.bukkit.event.Event;

/**
 * This interface indicates that the Trait is a magic Spell.
 * It indicates that it needs magic points to cast.
 * 
 * @author Tobiyas
 */
public interface MagicSpellTrait {
	
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
	 * 
	 * @return true if no message should be sent
	 */
	public boolean triggerButDoesNotHaveEnoghCostType(Event event);
	
	
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
		ITEM;
		
		
		
		@Override
		public String toString(){
			String name = this.name();
			String pre = name.substring(0, 1).toUpperCase();
			String rest = name.substring(1, name.length() - 1).toLowerCase();
			
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
	public boolean needsCostCheck(Event event);
	
}
