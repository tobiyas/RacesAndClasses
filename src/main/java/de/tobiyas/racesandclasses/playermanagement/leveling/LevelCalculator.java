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
package de.tobiyas.racesandclasses.playermanagement.leveling;

import java.util.HashMap;
import java.util.Map;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.util.evaluations.EvalEvaluator;
import de.tobiyas.util.evaluations.parts.Calculation;

public class LevelCalculator {
	
	/**
	 * The Calculator to use.
	 */
	private static Calculation calc;
	
	
	
	/**
	 * Returns the LevelPackage for the passed Level.
	 * If the Level is < 1, the Level of 1 is returned.
	 * 
	 * @param level to calculate for
	 * 
	 * @return the Level to calculate for
	 */
	public static LevelPackage calculateLevelPackage(int level){
		if(level < 1) return calculateLevelPackage(1);
		
		return new LevelPackage(level, calcMaxExpForLevel(level));
	}
	
	
	/**
	 * Calculates the Max EXP needed to level up.
	 * 
	 * WARNING: This method will NEVER return a value < 1
	 * 
	 * @param level to calculate for
	 * 
	 * @return
	 */
	protected static int calcMaxExpForLevel(int level){
		try{			
			if(calc == null){
				String maxExpGeneratorString = RacesAndClasses.getPlugin()
						.getConfigManager().getGeneralConfig().getConfig_mapExpPerLevelCalculationString();
				
				calc = EvalEvaluator.parse(maxExpGeneratorString);
			}
		    
		    Map<String,Double> vars = new HashMap<>();
		    vars.put("level", (double)level);
		    
	    	return (int) calc.calculate(vars);
	    }catch(Throwable exp){
	    	return level * level * 1000;
	    }
	    
	}
	
	
	
	/**
	 * Returns the Percentage of the current Level.
	 * If the Value is > 100, you should consider giving the Player a level.
	 * 
	 * @param level to check
	 * @param currentEXP of the Level
	 * 
	 * @return the Percentage of EXP of the current Level.
	 */
	public static double calculatePercentageOfLevel(int level, int currentEXP){
		LevelPackage levelPackage = calculateLevelPackage(level);
		return 100d * ((double)currentEXP / (double)levelPackage.getMaxEXP());
	}

	
	
	/**
	 * Tries to generate a value with the String passed.
	 * If it works, true is returned.
	 * If anything fails, false is returned.
	 * 
	 * @param generatorString
	 * @return
	 */
	public static boolean verifyGeneratorStringWorks(String generatorString){
		try{
			Calculation calc = EvalEvaluator.parse(generatorString);
			if(calc == null) return false;
			
			return calc.calculate(new HashMap<String,Double>()) != Double.NaN;
	    }catch(Throwable exp){
	    	return false;
	    }
	}
}
