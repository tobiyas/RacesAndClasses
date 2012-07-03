package de.tobiyas.races.datacontainer.health.damagetickers;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffect;

import de.tobiyas.races.Races;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.TraitEventManager;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.events.EntityDamageDoubleEvent;

public class DamageTicker implements Runnable{

	private LivingEntity target;
	private int duration;
	private double damagePerTick;
	private DamageCause cause;
	
	private int taskID;
	private Races plugin;
	
	private Effect effect = null;
	private int effectAmount;
	
	private static HashSet<DamageTicker> tickers = new HashSet<DamageTicker>();
	
	public DamageTicker(LivingEntity target, int duration, double damagePerTick, DamageCause cause){
		this.plugin = Races.getPlugin();
		this.target = target;
		this.duration = duration;
		this.damagePerTick = damagePerTick;
		this.cause = cause;
		
		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 20, 20);
		tickers.add(this);
	}
	
	public void playEffectOnDmg(Effect effect, int amount){
		this.effect = effect;
		this.effectAmount = amount;
	}
	
	public void linkPotionEffect(PotionEffect potionEffect){
		target.addPotionEffect(potionEffect);
	}
	
	private boolean cancleIfFit(LivingEntity entity, DamageCause cause){
		if(entity != this.target) return false;
		if(cause != this.cause) return false;
		
		Bukkit.getScheduler().cancelTask(taskID);
		return true;
	}
	
	private void stopTask(){
		Bukkit.getScheduler().cancelTask(taskID);
		tickers.remove(this);
	}

	@Override
	public void run() {
		if(duration == 0 || target == null || target.isDead()){
			stopTask();
			return;
		}
		
		duration --;
		if(cause == DamageCause.FIRE){
			if(target.getFireTicks() <= 1){
				stopTask();
				return;
			}
		}
		
		if(effect != null){
			for(int i = 0; i < effectAmount; i++)
				target.getLocation().getWorld().playEffect(target.getLocation(), effect, 0);
		}
		
		EntityDamageDoubleEvent event = new EntityDamageDoubleEvent(target, cause, damagePerTick, true);
		TraitEventManager.fireEvent(event);
	}
	
	public static int cancleEffects(LivingEntity entity, DamageCause cause){
		HashSet<DamageTicker> removeTickers = new HashSet<DamageTicker>();
		for(DamageTicker ticker : tickers){
			if(ticker.cancleIfFit(entity, cause))
				removeTickers.add(ticker);
		}
		
		for(DamageTicker ticker : removeTickers)
			tickers.remove(ticker);
		
		return removeTickers.size();
	}
	
	
	public static int hasEffect(Entity entity, DamageCause cause){
		int i = 0;
		for(DamageTicker ticker : tickers){
			if(ticker.cause == cause)
				i++;
		}
		
		return i;
	}
}
