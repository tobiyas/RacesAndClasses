package de.tobiyas.racesandclasses.vollotile;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public abstract class VollotileCode {
	
	/**
	 * The relocation string for the Vollotile code.
	 */
	protected final String CB_RELOCATION;
	
	
	public VollotileCode(String relocationString) {
		this.CB_RELOCATION = relocationString;
	}
	
	/**
	 * Plays a Critical hit effect on the Player.
	 * 
	 * @param player to play on.
	 */
	public abstract void playCriticalHitEffect(Player toSendTo, Entity toPlayEffect);
	
	
	/**
	 * Removes the Particle Effect off the Entity.
	 * 
	 * @param entity to remove the effect.
	 */
	public void removeParticleEffect(LivingEntity entity){
		Object mcPlayer = getMCEntityFromBukkitEntity(entity);
		try{
			Field dataWatcher = mcPlayer.getClass().getField("datawatcher");
			Method method = dataWatcher.getClass().getMethod("watch", Integer.class, Object.class);
			
			method.invoke(dataWatcher, 8, (byte) 0);
		}catch(Exception exp){
			//didn't work.... but well who cares. :D
		}
	}
	
	
	/**
	 * Checks if this version is the Correct Version for the Vollotile Code.
	 * 
	 * @return true if is the correct version, false if not.
	 */
	public boolean isCorrectVersion(){
		try{
			Class<?> clazz = Class.forName("net.minecraft.server." + CB_RELOCATION + ".Entity");
			return clazz != null;
		}catch(ClassNotFoundException exp){
			return false;
		}
	}
	
	
	@Override
	public String toString(){
		return "Vollotile: " + CB_RELOCATION;
	}

	
	////////////////////////
	//Helper Methods Below//
	////////////////////////
	
	
	/**
	 * Gets the MC entity from the Bukkit entity.
	 * 
	 * @param entity to get from
	 * 
	 * @return the MC entity.
	 */
	protected Object getMCEntityFromBukkitEntity(Entity entity){
		try{
			Method getHandle = entity.getClass().getDeclaredMethod("getHandle");
			Object mcEntity = getHandle.invoke(entity);
			
			return mcEntity;
		}catch(Exception exp){
			exp.printStackTrace();
			return null;
		}
	}
}
