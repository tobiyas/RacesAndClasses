package de.tobiyas.races.datacontainer.traitcontainer.eventmanagement;

import java.util.HashSet;

import org.bukkit.entity.LivingEntity;

public class EntityDeathManager {

	private static EntityDeathManager entityDeathManager;
	private HashSet<LivingEntity> toDie;
	
	public EntityDeathManager(){
		toDie = new HashSet<LivingEntity>();
		entityDeathManager = this;
	}
	
	public void init(){
		toDie.clear();
	}
	
	public boolean contains(LivingEntity entity){
		return toDie.remove(entity); 
	}
	
	public void addToDie(LivingEntity entity){
		toDie.add(entity);
	}
	
	public static EntityDeathManager getManager(){
		return entityDeathManager;
	}
	
}
