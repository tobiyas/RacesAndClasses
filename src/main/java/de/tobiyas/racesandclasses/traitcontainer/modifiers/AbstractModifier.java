package de.tobiyas.racesandclasses.traitcontainer.modifiers;

import java.util.regex.Pattern;

import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;

public abstract class AbstractModifier implements TraitSituationModifier {	

	/**
	 * The Modifier to use.
	 */
	protected final double modifier;
	
	/**
	 * The values to modify.
	 */
	protected final String[] toModify;
	
	
	public AbstractModifier(double modifier, String toModify) {
		this.modifier = modifier;
		this.toModify = toModify.split(Pattern.quote(","));
	}
	
	@Override
	public boolean canBeApplied(String toModify, RaCPlayer player) {
		for(String allowed : this.toModify){
			if(allowed.equals("*")) return true;
			if(allowed.equalsIgnoreCase(toModify)) return true;
		}
		
		return false;
	}
	
	
	@Override
	public double apply(RaCPlayer player, double value) {
		return modifier * value;
	}
	
	@Override
	public int apply(RaCPlayer player, int value) {
		return (int) Math.round(apply(player, (double) value));
	}

}