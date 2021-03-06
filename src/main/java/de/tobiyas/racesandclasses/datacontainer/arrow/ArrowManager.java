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
import java.util.Collections;
import java.util.List;
import java.util.Set;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.resolvers.WorldResolver;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.arrows.AbstractArrow;
import de.tobiyas.racesandclasses.traitcontainer.traits.statictraits.NormalArrow;

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
	
	public void rescanPlayer(){
		arrows.clear();
		
		Set<Trait> traits = TraitHolderCombinder.getSkillTreeReducedTraitsOfPlayer(player);
		for(Trait arrow : traits){
			if(arrow instanceof AbstractArrow && !arrow.isBindable()){
				arrows.add((AbstractArrow) arrow);
			}
		}
		
		Collections.sort(arrows);
		if(arrows.size() < currentPointer) currentPointer = 0;
	}
	
	public AbstractArrow nextArrow(){
		if(System.currentTimeMillis() - eventTime < 100) return null;
		if(arrows.size() <= 0) return null;
		if(WorldResolver.isOnDisabledWorld(player))	return null;
		
		currentPointer ++;
		if(currentPointer >= arrows.size()) currentPointer = 0;
		
		AbstractArrow arrow = arrows.get(currentPointer);

		eventTime = System.currentTimeMillis();
		return arrow;
	}
	
	
	
	public AbstractArrow previousArrow() {
		if(System.currentTimeMillis() - eventTime < 100) return null;
		if(WorldResolver.isOnDisabledWorld(player))	return null;
		if(arrows.size() <= 0) return null;
		
		currentPointer --;
		if(currentPointer < 0) currentPointer = arrows.size() - 1;
		
		AbstractArrow arrow = arrows.get(currentPointer);
		eventTime = System.currentTimeMillis();
		return arrow;
	}
	
	
	public AbstractArrow getCurrentArrow(){
		if(WorldResolver.isOnDisabledWorld(player))	return null;
		if(arrows.size() <= 0) rescanPlayer();
		if(arrows.size() <= 0) return null;
		
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
		return WorldResolver.isOnDisabledWorld(player) ? 0 : arrows.size() - 1;
	}
	
	
	/**
	 * If the player has any arrow (except for default arrow).
	 * @return true if has any.
	 */
	public boolean hasAnyArrow(){
		int size = arrows.size();
		switch(size){
			case 0 : return false;
			case 1 : return !(arrows.get(0) instanceof NormalArrow);
			default : return true;
		}
	}
	

	/**
	 * Returns all available Arrows.
	 * @return arrows.
	 */
	public List<AbstractArrow> getAllArrows() {
		if(WorldResolver.isOnDisabledWorld(player)) return new ArrayList<AbstractArrow>();
		else return new ArrayList<AbstractArrow>(this.arrows);
	}
}
