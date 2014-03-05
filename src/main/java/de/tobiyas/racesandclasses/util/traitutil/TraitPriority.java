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
package de.tobiyas.racesandclasses.util.traitutil;

public class TraitPriority {

	public static final int last = 0;
	public static final int lowest = 1;
	public static final int low = 2;
	public static final int middle = 3;
	public static final int high = 4;
	public static final int highest = 5;
	public static final int first = 6;
	
	
	private int prio;
	private TraitPriority(int prio){
		this.prio = prio;
	}
	
	public int getPrio(){
		return prio;
	}
	
	public static TraitPriority createPriority(int prio){
		switch(prio){
			case 0: return last();
			case 1: return lowest();
			case 2: return low();
			case 3: return middle();
			case 4: return high();
			case 5: return highest();
			case 6: return first();
			
			default: return middle();
		}
	}
	
	public boolean isHigherThan(TraitPriority second){
		return prio > second.getPrio();
	}
	
	public boolean isLowerThan(TraitPriority second){
		return prio < second.getPrio();
	}
	
	public static TraitPriority last(){
		return new TraitPriority(0);
	}
	
	public static TraitPriority lowest(){
		return new TraitPriority(1);
	}
	
	public static TraitPriority low(){
		return new TraitPriority(2);
	}
	
	public static TraitPriority middle(){
		return new TraitPriority(3);
	}
	
	public static TraitPriority high(){
		return new TraitPriority(4);
	}
	
	public static TraitPriority highest(){
		return new TraitPriority(5);
	}
	
	public static TraitPriority first(){
		return new TraitPriority(6);
	}
}
