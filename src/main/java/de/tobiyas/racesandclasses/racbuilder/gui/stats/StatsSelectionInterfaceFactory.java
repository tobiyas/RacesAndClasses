package de.tobiyas.racesandclasses.racbuilder.gui.stats;

import java.util.Map;

import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.racbuilder.gui.BasicSelectionInterface;

public class StatsSelectionInterfaceFactory {

	
	public static AbstractStatSelectionInterface buildInterface(StatType type, Player player, BasicSelectionInterface parent,
			Map<String, Object> config, String key){
		
		switch(type){
			case BOOLEAN: return new BooleanSelectionInterface(player, parent, config, key);
			case DOUBLE: return new DoubleSelectionInterface(player, parent, config, key);
			case INTEGER: return new IntegerSelectionInterface(player, parent, config, key);
			case OPERATOR: return new OperatorSelectionInterface(player, parent, config, key);
			case STRING: return new StringSelectionInterface(player, parent, config, key);
		}
		
		return null;
	}
}
