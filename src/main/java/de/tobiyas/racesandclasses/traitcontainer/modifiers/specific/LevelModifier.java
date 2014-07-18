package de.tobiyas.racesandclasses.traitcontainer.modifiers.specific;

import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.modifiers.AbstractModifier;

public class LevelModifier extends AbstractModifier {

	private final int minLevel;
	private final int maxLevel;
	
	
	public LevelModifier(int minLevel, int maxLevel, double modifier) {
		super(modifier);
		
		this.minLevel = minLevel;
		this.maxLevel = maxLevel;
	}
	
	@Override
	public boolean canBeApplied(RaCPlayer player) {
		int current = player.getLevelManager().getCurrentLevel();
		return current >= minLevel && current <= maxLevel;
	}
	
	
	/**
	 * Generates the Modifier.
	 * 
	 * @param desriptor
	 * @param modifier
	 * 
	 * @return
	 */
	public static LevelModifier generate(String desriptor, Double modifier){
		String[] split = desriptor.split("-");
		if(split.length != 2) return null;
		
		try{
			int minLevel = Integer.parseInt(split[0]);
			int maxLevel = Integer.parseInt(split[1]);
			
			return new LevelModifier(minLevel, maxLevel, modifier);
		}catch(NumberFormatException exp){
			//not parseable.
			return null;
		}
	}

}
