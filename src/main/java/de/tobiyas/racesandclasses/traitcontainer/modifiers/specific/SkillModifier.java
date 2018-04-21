package de.tobiyas.racesandclasses.traitcontainer.modifiers.specific;

import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.modifiers.AbstractModifier;

public class SkillModifier extends AbstractModifier {

	private final int minLevel;
	private final int maxLevel;
	private final Trait trait;
	
	
	public SkillModifier( int minLevel, int maxLevel, double modifier, String toModify, Trait trait ) {
		super( modifier, toModify );
		
		this.minLevel = minLevel;
		this.maxLevel = maxLevel;
		this.trait = trait;
	}
	
	@Override
	public boolean canBeApplied( String toModify, RaCPlayer player ) {
		if( !super.canBeApplied( toModify, player ) ) return false;
		
		int current = player.getSkillTreeManager().getLevel( trait );
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
	public static SkillModifier generate(String desriptor, double modifier, String toModify, Trait trait ){
		String[] split = desriptor.split("-");
		if(split.length != 2) return null;
		
		try{
			int minLevel = Integer.parseInt(split[0]);
			int maxLevel = Integer.parseInt(split[1]);
			
			return new SkillModifier( minLevel, maxLevel, modifier, toModify, trait );
		}catch(NumberFormatException exp){
			//not parseable.
			return null;
		}
	}

}
