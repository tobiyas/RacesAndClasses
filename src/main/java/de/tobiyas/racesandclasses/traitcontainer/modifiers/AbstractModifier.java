package de.tobiyas.racesandclasses.traitcontainer.modifiers;


public abstract class AbstractModifier implements TraitSituationModifier {	

	/**
	 * The Modifier to use.
	 */
	protected final double modifier;
	
	
	public AbstractModifier(double modifier) {
		this.modifier = modifier;
	}
	
	
	
	@Override
	public double apply(double value) {
		return modifier * value;
	}
	
	@Override
	public int apply(int value) {
		return (int) Math.round(apply((double) value));
	}

}