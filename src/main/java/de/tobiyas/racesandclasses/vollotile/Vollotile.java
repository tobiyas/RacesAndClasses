package de.tobiyas.racesandclasses.vollotile;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import de.tobiyas.util.vollotile.VollotileCodeManager;

public class Vollotile {

	private static Vollotile instance;
	
	public Vollotile() {
	}
	
	
	/**
	 * Returns the Vollotile Code.
	 * @return 
	 */
	public static Vollotile get(){
		if(instance == null) instance = new Vollotile();
		return instance;
	}


	
	public void playCriticalHitEffect(Player toSendTo, Entity toPlayEffect) {
		VollotileCodeManager.getVollotileCode().playCriticalHitEffect(toSendTo, toPlayEffect);
	}


	
	public void playCriticalHitEffect(Entity toPlayEffect) {
		VollotileCodeManager.getVollotileCode().playCriticalHitEffect(toPlayEffect);
	}


	
	public void sendRawMessage(Player player, String rawMessage) {
		VollotileCodeManager.getVollotileCode().sendRawMessage(player, rawMessage);
	}


	
	public void removeParticleEffect(LivingEntity entity) {
		VollotileCodeManager.getVollotileCode().removeParticleEffect(entity);
	}


	
	/**
	 * use THIS for RaC Traits.
	 * 
	 * @param effect
	 * @param loc
	 * @param width
	 * @param data
	 * @param amount
	 * @param player
	 */
	public void sendOwnParticleEffect(ParticleEffects effect, Location loc,
			Vector width, float data, int amount, Player player) {
		if(effect == null) return;
		
		Effect bukkitEffect = effect.asBukkit();
		if(bukkitEffect != null){
			loc.getWorld().playEffect(loc, bukkitEffect, (int)data);
		}else{
			VollotileCodeManager.getVollotileCode().sendParticleEffect(effect.asMirror(), loc, width, data, amount, player);			
		}
		
	}


	
	/**
	 * Use THIS from RaC Traits!
	 * 
	 * @param effect
	 * @param loc
	 * @param data
	 * @param amount
	 */
	public void sendOwnParticleEffectToAll(ParticleEffects effect, Location loc,
			float data, int amount) {
		if(effect == null) return;
		
		sendOwnParticleEffectToAll(effect, loc, new Vector(0.1,0.1,0.1), data, amount);
	}
	
	
	
	/**
	 * Use THIS from RaC Traits!
	 * 
	 * @param effect
	 * @param loc
	 * @param data
	 * @param amount
	 */
	public void sendOwnParticleEffectToAll(ParticleContainer container, Location loc) {
		if(container == null) return;
		sendOwnParticleEffectToAll(container.getEffect(), loc, container.getVec(), container.getData(), container.getAmount());
	}
	
	
	/**
	 * Use THIS from RaC Traits!
	 * 
	 * @param effect
	 * @param loc
	 * @param data
	 * @param amount
	 */
	public void sendOwnParticleEffectToAll(ParticleEffects effect, Location loc,
			Vector width, float data, int amount) {
		if(effect == null) return;
		
		for(Player pl : loc.getWorld().getPlayers()){
			sendOwnParticleEffect(effect, loc, width, data, amount, pl);
		}
	}
	
	
	/**
	 * Froma RaC Traits, use {@link #sendParticleEffect(ParticleEffects, Location, Vector, float, int, Player)
	 * @param effect
	 * @param loc
	 * @param width
	 * @param data
	 * @param amount
	 * @param player
	 */
	public void sendParticleEffect(de.tobiyas.util.vollotile.ParticleEffects effect, Location loc,
			Vector width, float data, int amount, Player player) {
		VollotileCodeManager.getVollotileCode().sendParticleEffect(effect, loc, width, data, amount, player);
	}
	
	
	
	/**
	 * Use {@link #sendParticleEffectToAll(ParticleEffects, Location, float, int)} for RaC Traits.
	 * 
	 * @param effect
	 * @param loc
	 * @param data
	 * @param amount
	 */
	public void sendParticleEffectToAll(de.tobiyas.util.vollotile.ParticleEffects effect, Location loc,
			float data, int amount) {
		VollotileCodeManager.getVollotileCode().sendParticleEffectToAll(effect, loc, data, amount);
	}


	
	public boolean isCorrectVersion() {
		return VollotileCodeManager.getVollotileCode().isCorrectVersion();
	}


	
	public String toString() {
		return VollotileCodeManager.getVollotileCode().toString();
	}
	
	
	
}
