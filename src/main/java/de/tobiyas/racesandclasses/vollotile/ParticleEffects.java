package de.tobiyas.racesandclasses.vollotile;

import org.bukkit.Effect;

public enum ParticleEffects {

	//Packet based Effects
    HUGE_EXPLOSION,
    LARGE_EXPLODE,
    FIREWORKS_SPARK,
    BUBBLE,
    SUSPEND,
    DEPTH_SUSPEND,
    TOWN_AURA,
    CRIT,
    MAGIC_CRIT,
    MOB_SPELL,
    MOB_SPELL_AMBIENT,
    SPELL,
    INSTANT_SPELL,
    WITCH_MAGIC,
    NOTE,
    PORTAL,
    ENCHANTMENT_TABLE,
    EXPLODE,
    FLAME,
    LAVA,
    FOOTSTEP,
    SPLASH,
    LARGE_SMOKE,
    CLOUD,
    RED_DUST,
    SNOWBALL_POOF,
    DRIP_WATER,
    DRIP_LAVA,
    SNOW_SHOVEL,
    SLIME,
    HEART,
    ANGRY_VILLAGER,
    HAPPY_VILLAGER,
    
    //Seperator for Bukkit Effects
	CLICK2,
	CLICK1,
	BOW_FIRE,
	DOOR_TOGGLE,
	EXTINGUISH,
	RECORD_PLAY,
	GHAST_SHRIEK,
	GHAST_SHOOT,
	BLAZE_SHOOT,
	ZOMBIE_CHEW_WOODEN_DOOR,
	ZOMBIE_CHEW_IRON_DOOR,
	ZOMBIE_DESTROY_DOOR,
	SMOKE,
	STEP_SOUND,
	POTION_BREAK,
	ENDER_SIGNAL,
	MOBSPAWNER_FLAMES;

   
    public de.tobiyas.util.vollotile.ParticleEffects asMirror(){
    	try{
    		return de.tobiyas.util.vollotile.ParticleEffects.valueOf(this.name());
    	}catch(IllegalArgumentException exp){ return null; }
    }
    
    
    public Effect asBukkit(){
    	try{
    		return Effect.valueOf(this.name());
	    }catch(IllegalArgumentException exp){ return null; }
    }
    
}
