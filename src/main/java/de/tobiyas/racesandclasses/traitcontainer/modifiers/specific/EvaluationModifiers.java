package de.tobiyas.racesandclasses.traitcontainer.modifiers.specific;

import java.util.HashMap;
import java.util.Map;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.modifiers.AbstractModifier;
import de.tobiyas.util.evaluations.EvalEvaluator;
import de.tobiyas.util.evaluations.parts.Calculation;

public class EvaluationModifiers extends AbstractModifier {
	
	/**
	 * The calculation to use.
	 */
	private Calculation calculation;
	
	
	private EvaluationModifiers(String evalString, String toModify) {
		super(0, toModify);
		
		try{
			this.calculation = EvalEvaluator.parse(evalString);
		}catch(Throwable exp){
			RacesAndClasses.getPlugin().logError("Could not parse Expression: " 
					+ evalString + " because: " + exp.getLocalizedMessage());
		}
	}
	
	
	@Override
	public double apply(RaCPlayer player, double value, Trait trait) {
		if(calculation == null) return value;
		
		Map<String,Double> vars = generateVariables(player, trait);
		vars.put("old", value);
		
		return calculation.calculate(vars);
	}
	
	
	private Map<String,Double> generateVariables(RaCPlayer player, Trait trait){
		Map<String,Double> variables = new HashMap<String,Double>();
		variables.put("mana", player.getCurrentMana());
		variables.put("maxmana", player.getMaxMana());
		variables.put("level", (double) player.getCurrentLevel());
		variables.put("maxhealth", player.getMaxHealth());
		variables.put("health", player.getHealth());
		variables.put("skilllevel", (double) player.getSkillTreeManager().getLevel(trait));
		
		return variables;
	}
	
	
	
	/**
	 * Generates the Modifier by the values Passed.
	 * 
	 * @param descriptor the descriptor to parse
	 * @param modifier the modifier to parse.
	 * 
	 * @return the Generated Modifier or Null if not possible.
	 */
	public static EvaluationModifiers generate(String descriptor, Double modifier, String toModify){
		return new EvaluationModifiers(descriptor, toModify);
	}

}
