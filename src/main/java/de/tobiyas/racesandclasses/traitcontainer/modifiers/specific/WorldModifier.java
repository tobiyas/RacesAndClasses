package de.tobiyas.racesandclasses.traitcontainer.modifiers.specific;

import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.modifiers.AbstractModifier;

public class WorldModifier extends AbstractModifier {

	protected final String worldName;
	
	public WorldModifier(String worldName, double modifier) {
		super(modifier);
		this.worldName = worldName;
	}
	
	
	@Override
	public boolean canBeApplied(RaCPlayer player) {
		return player.getWorld().getName().equalsIgnoreCase(worldName);
	}
	
	
	/**
	 * Generates the Modifier
	 * 
	 * @param descriptor to create from
	 * @param modifier the modify value
	 * 
	 * @return the modifier.
	 */
	public static WorldModifier generate(String descriptor, double modifier){
		return new WorldModifier(descriptor, modifier);
	}

}
