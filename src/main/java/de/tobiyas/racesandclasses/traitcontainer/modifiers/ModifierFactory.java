package de.tobiyas.racesandclasses.traitcontainer.modifiers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import de.tobiyas.racesandclasses.traitcontainer.modifiers.exceptions.GeneratorNotFoundException;
import de.tobiyas.racesandclasses.traitcontainer.modifiers.exceptions.ModifierConfigurationException;
import de.tobiyas.racesandclasses.traitcontainer.modifiers.exceptions.ModifierGenericException;
import de.tobiyas.racesandclasses.traitcontainer.modifiers.exceptions.NotANumberAtArg3Exception;
import de.tobiyas.racesandclasses.traitcontainer.modifiers.exceptions.NotEnoughArgumentsForModifierException;
import de.tobiyas.racesandclasses.traitcontainer.modifiers.specific.BiomeModifier;
import de.tobiyas.racesandclasses.traitcontainer.modifiers.specific.EvaluationModifiers;
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
		modifierMap.put("eval", EvaluationModifiers.class);
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
	public static TraitSituationModifier generate(String toParse) throws ModifierConfigurationException {
		String[] split = toParse.split(":");
		
		if(split.length < 3) throw new NotEnoughArgumentsForModifierException( toParse );
		
		String type = split[0].toLowerCase();
		String descriptor = split[1];
		String mod = split[2];
		String toUseOn = split.length <=3 ? "*" : split[3];
		
		double parsedMod = 0;
		try{ parsedMod = Double.parseDouble(mod); }catch( IllegalArgumentException exp ){ throw new NotANumberAtArg3Exception(toParse, type, descriptor, mod, toUseOn); }
		
		Class<? extends AbstractModifier> generator = modifierMap.get(type);
		if(generator == null) {
			throw new GeneratorNotFoundException( toParse, type, descriptor, parsedMod, toUseOn );
		}
		
		try{
			Method method = generator.getMethod("generate", String.class, double.class, String.class);
			return (TraitSituationModifier) method.invoke(null, descriptor, parsedMod, toUseOn);
		}catch(Throwable exp){
			//Proxy if we have a problem with Reflections:
			if( exp instanceof InvocationTargetException ) {
				Throwable inner = ((InvocationTargetException) exp).getCause();
				if( inner != null ) exp = inner;
			}
			
			//Do Exception Stuff we know went wrong:
			if( exp instanceof ModifierConfigurationException ) throw (ModifierConfigurationException) exp;
			
			//TODO: Remove line below. Only for Testing
			exp.printStackTrace();
			
			//Something went completely wrong -> Pack it in an own exception!
			throw new ModifierGenericException( toParse, type, descriptor, parsedMod, toUseOn, exp );
		}
	}
	
}
