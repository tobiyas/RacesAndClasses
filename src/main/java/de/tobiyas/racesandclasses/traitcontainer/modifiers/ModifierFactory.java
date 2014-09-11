package de.tobiyas.racesandclasses.traitcontainer.modifiers;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import de.tobiyas.racesandclasses.traitcontainer.modifiers.specific.BiomeModifier;
import de.tobiyas.racesandclasses.traitcontainer.modifiers.specific.LevelModifier;
import de.tobiyas.racesandclasses.traitcontainer.modifiers.specific.TimeModifier;
import de.tobiyas.racesandclasses.traitcontainer.modifiers.specific.WorldModifier;

public class ModifierFactory {

	/**
	 * The Generator Map.
	 */
	private static Map<String,Class<? extends AbstractModifier>> modifierMap = new HashMap<String, Class<? extends AbstractModifier>>();
	
	
	static{
		modifierMap.put("biome", BiomeModifier.class);
		modifierMap.put("level", LevelModifier.class);
		modifierMap.put("time", TimeModifier.class);
		modifierMap.put("world", WorldModifier.class);		
	}
	
	
	/**
	 * This generates an Modifier from the Values passed.
	 * 
	 * @param toParse the value to parse.
	 * 
	 * @return the modifier or null if not parseable.
	 */
	public static TraitSituationModifier generate(String toParse){
		String[] split = toParse.split(":");
		if(split.length < 3) return null;
		
		String type = split[0].toLowerCase();
		String descriptor = split[1];
		String mod = split[2];
		
		double parsedMod = 0;
		try{ parsedMod = Double.parseDouble(mod); }catch(IllegalArgumentException exp){ return null; }
		
		Class<? extends AbstractModifier> generator = modifierMap.get(type);
		if(generator == null) return null;
		
		try{
			Method method = generator.getMethod("generate", String.class, Double.class);
			return (TraitSituationModifier) method.invoke(null, descriptor, parsedMod);
		}catch(Throwable exp){
			//not parseable.
			return null;
		}
	}
	
}
