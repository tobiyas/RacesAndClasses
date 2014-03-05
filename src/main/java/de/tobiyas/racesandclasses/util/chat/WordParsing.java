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
package de.tobiyas.racesandclasses.util.chat;

import de.tobiyas.racesandclasses.configuration.member.file.ConfigOption.SaveFormat;

public class WordParsing {


	/**
	 * Checks if the passed value is parsable to an int.
	 * True if parsable, false if not.
	 * 
	 * RESTRICTION: false positive on {@link Integer#MIN_VALUE}
	 * 
	 * @param intValue to try parsing
	 * @return true if parsable, false otherwise.
	 */
	public static boolean isInt(Object intValue){
		return convertToInt(intValue) != Integer.MIN_VALUE;
	}
	
	/**
	 * Parses the passed String to an int.
	 * If it can not parse, {@link Integer#MIN_VALUE} is returned.
	 * 
	 * @return
	 */
	public static int convertToInt(Object intValue){
		if(intValue instanceof Integer){
			return (Integer) intValue;
		}
		
		if(intValue instanceof String){
			try{
				int newInt = Integer.valueOf((String) intValue);
				return newInt;
			}catch(NumberFormatException e){
				return Integer.MIN_VALUE;
			}
		}
		
		return Integer.MIN_VALUE;
	}
	
	
	/**
	 * Converts the most used terms of boolean as String to an Boolean.
	 * Returns false if none could be resolved.
	 * 
	 * @param bool
	 * @return
	 */
	public static boolean convertToBool(Object object){
		if(object instanceof Boolean){
			return (Boolean) object;
		}
		
		if(object instanceof String){
			String bool = (String) object;
			bool = bool.toLowerCase();
			if(bool.equalsIgnoreCase("on") || bool.equalsIgnoreCase("true") || bool.equalsIgnoreCase("1") || bool.equalsIgnoreCase("yes"))
				return true;
			if(bool.equalsIgnoreCase("off") || bool.equalsIgnoreCase("false") || bool.equalsIgnoreCase("0") || bool.equalsIgnoreCase("no"))
				return false;
			
			return false;
		}
		
		return false;
	}
	
	/**
	 * Checks if the value passes is a legit boolean expression.
	 * Returns true if it can be parsed correctly.
	 * False otherwise.
	 * 
	 * @param bool to check
	 * @return
	 */
	public static boolean isBool(Object object){
		if(object instanceof Boolean){
			return true;
		}
		
		if(object instanceof String){
			String bool = (String) object;
			
			bool = bool.toLowerCase();
			if(bool.equalsIgnoreCase("on") || bool.equalsIgnoreCase("true") || bool.equalsIgnoreCase("1") || bool.equalsIgnoreCase("yes"))
				return true;
			if(bool.equalsIgnoreCase("off") || bool.equalsIgnoreCase("false") || bool.equalsIgnoreCase("0") || bool.equalsIgnoreCase("no"))
				return true;
			
			return false;			
		}
		
		return false;
	}
	
	
	/**
	 * Checks if the value passed is parsable to double.
	 * True if it is parsable, false otherwise.
	 * 
	 * @param doubleValue the value to check
	 * @return true if parsable
	 */
	public static boolean isDouble(Object doubleValue){
		return convertToDouble(doubleValue) != Double.MIN_VALUE;
	}
	
	
	/**
	 * Gets the Double value of a String.
	 * If not parsable, {@link Double#MIN_VALUE} is returned
	 * 
	 * @param doubleValue to parse
	 * @return the parsed value or MIN_VALUE
	 */
	public static double convertToDouble(Object doubleValue){
		if(doubleValue instanceof Double){
			return (Double) doubleValue;
		}
		
		if(doubleValue instanceof String){
			try{
				return Double.parseDouble((String) doubleValue);
			}catch(NumberFormatException exp){
				return Double.MIN_VALUE;
			}
		}
		
		return Double.MIN_VALUE;
	}


	/**
	 * Parses the passed Value into the wanted format.
	 * If parsing is not possible, null is returned.
	 * 
	 * @param value to parse.
	 * @param toParseInto to parse into.
	 * 
	 * @return the wanted converted value or null if not possible
	 */
	public static Object parseToSaveFormat(Object value, SaveFormat toParseInto){
		switch (toParseInto) {
		case BOOLEAN:
			return isBool(value) ? convertToBool(value) : null;
		case DOUBLE:
			return isDouble(value) ? convertToDouble(value) : null;
		case INT:
			return isInt(value) ? convertToInt(value) : null;
		case STRING:
			return value;
		case UNKNOWN:
			return null;
		}
		
		//should not get here
		return null;
	}
}
