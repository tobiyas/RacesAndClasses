package de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces;
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
	