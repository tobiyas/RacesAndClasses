package de.tobiyas.racesandclasses.util.traitutil;

public class TraitStringUtils {

	public static double getNewValue(double oldDmg, String operation, double value){
		double newDmg = 0;
		if(operation.equals("+")){
			newDmg = oldDmg + value;
		}else
		
		if(operation.equals("-")){
			newDmg = oldDmg - value;
		}else
		
		if(operation.equals("*")){
			newDmg = oldDmg * value;
		}else{
			newDmg = oldDmg * value;
		}
		
		
		if(newDmg < 0) newDmg = 0;
		return newDmg;
	}
	
}
