package de.tobiyas.racesandclasses.traitcontainer.modifiers.specific;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;

import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.modifiers.AbstractModifier;
import de.tobiyas.racesandclasses.traitcontainer.modifiers.exceptions.ModifierConfigurationException;
import de.tobiyas.util.evaluations.EvalEvaluator;
import de.tobiyas.util.evaluations.parts.Calculation;

public class EvaluationModifiers extends AbstractModifier {
	
	/**
	 * The calculation to use.
	 */
	private Calculation calculation;
	
	
	private EvaluationModifiers(String evalString, String toModify) throws ModifierConfigurationException {
		super(0, toModify);
		
		try{
			this.calculation = EvalEvaluator.parse(evalString);
		}catch(Throwable exp){
			throw new EvaluationNotParseableException("eval:"+evalString+":0:"+toModify, "eval", evalString, 0, toModify, exp.getMessage());
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
		Location loc = player.getLocation();
		
		variables.put("mana", player.getCurrentMana());
		variables.put("maxmana", player.getMaxMana());
		variables.put("level", (double) player.getCurrentLevel());
		variables.put("health", player.getHealth());
		variables.put("maxhealth", player.getMaxHealth());
		variables.put("skilllevel", (double) player.getSkillTreeManager().getLevel(trait));
		variables.put("playery", loc.getY() );
		
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
	public static EvaluationModifiers generate(String descriptor, double modifier, String toModify) throws ModifierConfigurationException {
		return new EvaluationModifiers(descriptor, toModify);
	}
	
	
	
	public static class EvaluationNotParseableException extends ModifierConfigurationException {
		private static final long serialVersionUID = 5502663814233447085L;
		
		private final String text;

		public EvaluationNotParseableException(String total, String type, String descriptor, double value, String appliedOn, String text) {
			super(total, type, descriptor, value, appliedOn);
			
			this.text = text;
		}

		@Override
		protected String formatErrorMSG() {
			return "Could not parse Evaluation: " + descriptor + " Error: " + text;
		}
		
	}

}
