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
package de.tobiyas.racesandclasses.datacontainer.arrow;

import java.util.ArrayList;
import java.util.Set;

import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.resolvers.WorldResolver;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.arrows.AbstractArrow;

public class ArrowManager {
	
	private ArrayList<AbstractArrow> arrows;
	private int currentPointer;
	private RaCPlayer player;
	
	private long eventTime;
	
	public ArrowManager(RaCPlayer player){
		this.player = player;
		arrows = new ArrayList<AbstractArrow>();
		currentPointer = 0;
		eventTime = 0;
	}
	
	public void rescanClass(){
		arrows.clear();
		
		if(WorldResolver.isOnDisabledWorld(player)){
			return;
		}
		
		Set<Trait> traits = TraitHolderCombinder.getReducedTraitsOfPlayer(player);
		
		for(Trait arrow : traits){
			if(arrow instanceof AbstractArrow && !arrow.isBindable()){
				arrows.add((AbstractArrow) arrow);
			}
		}
		
		if(arrows.size() < currentPointer)
			currentPointer = 0;
	}
	
	public AbstractArrow nextArrow(){
		if(System.currentTimeMillis() - eventTime < 100) return null;
		currentPointer ++;
		if(currentPointer >= arrows.size())
			currentPointer = 0;
		AbstractArrow arrow = null;
		try{
			arrow = arrows.get(currentPointer);
			}catch(IndexOutOfBoundsException e){
				return null;
			}
		eventTime = System.currentTimeMillis();
		return arrow;
	}
	
	public AbstractArrow getCurrentArrow(){
		AbstractArrow arrow = arrows.get(currentPointer);
		return arrow;
	}
	
	
	/**
	 * Returns the amounts of Arrows Types the Player has.
	 * <br>This EXCLUDES the NormalArrow!
	 * 
	 * @return number of different arrow types
	 */
	public int getNumberOfArrowTypes(){
		return arrows.size() - 1;
	}
}
