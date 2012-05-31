package de.tobiyas.races.datacontainer.arrow;

import java.util.ArrayList;
import java.util.Set;

import de.tobiyas.races.datacontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.race.RaceManager;
import de.tobiyas.races.datacontainer.traitcontainer.Trait;
import de.tobiyas.races.datacontainer.traitcontainer.traits.arrows.AbstractArrow;

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
	
	private void rescanClass(){
		arrows = new ArrayList<AbstractArrow>();
		RaceContainer container = RaceManager.getManager().getRaceOfPlayer(player);
		Set<Trait> traits = container.getTraits();
		for(Trait arrow : traits)
			if(arrow instanceof AbstractArrow)
				arrows.add((AbstractArrow) arrow);
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
