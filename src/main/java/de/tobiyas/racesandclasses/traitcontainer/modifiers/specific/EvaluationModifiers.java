package de.tobiyas.racesandclasses.traitcontainer.modifiers.specific;

import java.util.HashMap;
import java.util.Map;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.HealthAPI;
import de.tobiyas.racesandclasses.APIs.LevelAPI;
import de.tobiyas.racesandclasses.APIs.ManaAPI;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.modifiers.AbstractModifier;
import de.tobiyas.util.evaluations.EvalEvaluator;
import de.tobiyas.util.evaluations.parts.Calculation;

public class EvaluationModifiers extends AbstractModifier {
	
	/**
	 * the calculation to use.
	 */
	private Calculation calculation;
	
	
	public EvaluationModifiers(String evalString, String toModify) {
		super(0, toModify);
		
		try{
			this.calculation = EvalEvaluator.parse(evalString);
		}catch(Throwable exp){
			RacesAndClasses.getPlugin().logError("Could not parse Expression: " + evalString + " because: " 
					+ exp.getCause().getClass().getSimpleName() + " - " + exp.getLocalizedMessage());
		}
	}
	
	
	@Override
	public double apply(RaCPlayer player, double value) {
		if(calculation == null) return value;
		
		Map<String,Double> vars = generateVariables(player);
		vars.put("pre", value);
		
		return calculation.calculate(vars);
	}
	
	
	private Map<String,Double> generateVariables(RaCPlayer player){
		Map<String,Double> variables = new HashMap<String,Double>();
		variables.put("mana", ManaAPI.getCurrentMana(player));
		variables.put("level", (double) LevelAPI.getCurrentLevel(player));
		variables.put("health", (double) HealthAPI.getCurrentHealth(player));
		
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
	public static EvaluationModifiers generate(String descriptor, double modifier, String toModify){
		return new EvaluationModifiers(descriptor, toModify);
	}

}
