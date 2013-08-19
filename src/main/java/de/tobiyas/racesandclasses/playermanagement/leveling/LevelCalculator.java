package de.tobiyas.racesandclasses.playermanagement.leveling;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import de.tobiyas.racesandclasses.RacesAndClasses;

public class LevelCalculator {
	
	
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
		ScriptEngineManager mgr = new ScriptEngineManager();
	    ScriptEngine engine = mgr.getEngineByName("JavaScript");
	    String maxExpGeneratorString = RacesAndClasses.getPlugin()
	    		.getConfigManager().getGeneralConfig().getConfig_mapExpPerLevelCalculationString();
	    
	    maxExpGeneratorString = maxExpGeneratorString.replace("{level}", String.valueOf(level));
		
	    try{
	    	String parsedValue = (String) engine.eval(maxExpGeneratorString).toString();	
	    	double doubleValue = Double.parseDouble(parsedValue);
	    	int intValue = (int) doubleValue;
	    	
	    	return intValue;
	    }catch(Exception exp){
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
			ScriptEngineManager mgr = new ScriptEngineManager();
		    ScriptEngine engine = mgr.getEngineByName("JavaScript");
		    
		    generatorString = generatorString.replace("{level}", String.valueOf(42));
		
	    	String parsedValue = (String) engine.eval(generatorString).toString();	    	
	    	double doubleValue = Double.parseDouble(parsedValue);
	    	Integer intValue = (int) doubleValue;
	    	
	    	return intValue != null;
	    }catch(Exception exp){
	    	return false;
	    }
	}
}
