package de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces;

import static de.tobiyas.racesandclasses.translation.languages.Keys.*;


public enum TraitRestriction {

	MinimumLevel(restrictions_not_met_MinimumLevel),
	MaximumLevel(restrictions_not_met_MaximumLevel),
	Biomes(restrictions_not_met_Biomes),
	Wearing(restrictions_not_met_Wearing),
	OnlyInWater(restrictions_not_met_OnlyInWater),
	OnlyOnLand(restrictions_not_met_OnlyOnLand),
	OnlyInLava(restrictions_not_met_OnlyInLava),
	OnlyOnSnow(restrictions_not_met_OnlyOnSnow),
	OnlyInNight(restrictions_not_met_OnlyInNight),
	OnlyOnDay(restrictions_not_met_OnlyOnDay),
	Cooldown(restrictions_not_met_Cooldown),
	AboveLevitation(restrictions_not_met_AboveLevitation),
	BelowLevitation(restrictions_not_met_BelowLevitation),
	OnlyInRain(restrictions_not_met_OnlyInRain),
	OnlyAfterDamage(restrictions_not_met_OnlyAfterDamage),
	OnlyAfterNotDamage(restrictions_not_met_OnlyAfterNotDamage),
	OnlyOnBlock(restrictions_not_met_OnlyOnBlock),
	OnlyWhileSneaking(restrictions_not_met_OnlyWhileSneaking),
	OnlyWhileNotSneaking(restrictions_not_met_OnlyWhileNotSneaking), 
	NotOnBlock(restrictions_not_met_NotOnBlock),
	NeededPermission(restrictions_not_met_NeededPermission),
	Costs(restrictions_not_met_Costs),
	NotSkilled(restrictions_not_met_Skilled),
	Silenced(restrictions_not_met_Silence),
	Unknown(restrictions_not_met_Unknown),
	
	NoTarget(restrictions_not_met_NoTarget),
	OutOfRange(restrictions_not_met_OutOfRange),
	TargetFriendly(restrictions_not_met_TargetFriendly),
	
	
	
	None("");
	
	
	/**
	 * The Key for the Language
	 */
	private final String langKey;
	
	
	private TraitRestriction(String key) {
		this.langKey = key;
	}
	
	/**
	 * Returns the Language Key for translation.
	 * 
	 * @return the key for translation.
	 */
	public String translation(){
		return langKey;
	}
	
}
