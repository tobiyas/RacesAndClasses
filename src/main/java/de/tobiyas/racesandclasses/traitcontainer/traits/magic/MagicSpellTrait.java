package de.tobiyas.racesandclasses.traitcontainer.traits.magic;

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
	}
	
}
