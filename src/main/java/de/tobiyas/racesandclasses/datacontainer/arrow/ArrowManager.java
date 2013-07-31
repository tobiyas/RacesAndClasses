package de.tobiyas.racesandclasses.datacontainer.arrow;

import java.util.ArrayList;
import java.util.Set;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.traits.arrows.AbstractArrow;

public class ArrowManager {
	
	private ArrayList<AbstractArrow> arrows;
	private int currentPointer;
	private String player;
	
	private long eventTime;
	
	public ArrowManager(String player){
		this.player = player;
		arrows = new ArrayList<AbstractArrow>();
		currentPointer = 0;
		eventTime = 0;
		
		rescanClass();
	}
	
	public void rescanClass(){
		arrows = new ArrayList<AbstractArrow>();
		Set<Trait> traits = TraitHolderCombinder.getReducedTraitsOfPlayer(player);
		
		for(Trait arrow : traits){
			if(arrow instanceof AbstractArrow)
				arrows.add((AbstractArrow) arrow);
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
}
