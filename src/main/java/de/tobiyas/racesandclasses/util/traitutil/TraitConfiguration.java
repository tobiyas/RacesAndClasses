package de.tobiyas.racesandclasses.util.traitutil;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;

import de.tobiyas.racesandclasses.util.friend.TargetType;
import de.tobiyas.racesandclasses.vollotile.ParticleContainer;
import de.tobiyas.racesandclasses.vollotile.ParticleEffects;
import de.tobiyas.util.collections.CaseInsenesitveMap;

public class TraitConfiguration extends CaseInsenesitveMap<Object> {
	private static final long serialVersionUID = 1837146794134038024L;
	
	
	
	/**
	 * Retrieves the Element wanted as String.
	 * 
	 * @param key to retrieve
	 * 
	 * @return the wanted value or null.
	 */
	public String getAsString(String key){
		Object obj = get(key);
		if(obj == null) return null;
		if(obj instanceof String) return (String) obj;
		
		return obj.toString();
	}
	
	/**
	 * Retrieves the Element wanted as int.
	 * 
	 * @param key to retrieve
	 * 
	 * @return the wanted value or  {@value Integer.MIN_VALUE}.
	 */
	public int getAsInt(String key){
		Object obj = get(key);
		int returnValue = Integer.MIN_VALUE;
		if(obj == null) return returnValue;
		
		if(obj instanceof Integer) returnValue = (Integer) obj;
		if(obj instanceof Double) returnValue = ((Double) obj).intValue();
		if(obj instanceof Float) returnValue = ((Float) obj).intValue();
		
		return returnValue;
	}
	
	/**
	 * Retrieves the Element wanted as double.
	 * 
	 * @param key to retrieve
	 * 
	 * @return the wanted value or  {@value Double.MIN_VALUE}.
	 */
	public double getAsDouble(String key){
		Object obj = get(key);
		double returnValue = Integer.MIN_VALUE;
		if(obj == null) return returnValue;
		
		if(obj instanceof Integer) returnValue = (Integer) obj;
		if(obj instanceof Double) returnValue = (Double) obj;
		if(obj instanceof Float) returnValue = (Float) obj;
		
		return returnValue;
	}
	
	/**
	 * Retrieves the Element wanted as Boolean.
	 * 
	 * @param key to retrieve
	 * 
	 * @return the wanted value or False.
	 */
	public boolean getAsBool(String key){
		Object obj = get(key);
		if(obj == null) return false;
		
		if(obj instanceof Boolean) return (Boolean)obj;
		return false;
	}
	
	/**
	 * Retrieves the Element wanted as String List.
	 * 
	 * @param key to retrieve
	 * 
	 * @return the wanted value or an empty list.
	 */
	public List<String> getAsStringList(String key){
		Object obj = get(key);
		List<String> returnValue = new LinkedList<String>();
		if(obj == null) return returnValue;
		
		if(obj instanceof List) {
			List<?> list = (List<?>) obj;
			if(!list.isEmpty()){
				for(Object element : list){
					if(element instanceof String){
						returnValue.add((String) element);
					}
				}
			}
		}
		
		return returnValue;
	}
	
	/**
	 * Retrieves the Element wanted as Material.
	 * 
	 * @param key to retrieve
	 * 
	 * @return the wanted value or null.
	 */
	@SuppressWarnings("deprecation")
	public Material getAsMaterial(String key){
		Object obj = get(key);
		Material returnValue = null;
		if(obj == null) return returnValue;
		
		if(obj instanceof Material) {
			returnValue = (Material) obj;
		}
		
		if(obj instanceof String){
			returnValue = Material.matchMaterial((String) obj);
		}
		
		if(obj instanceof Integer){
			returnValue = Material.getMaterial((Integer) obj);
		}
		
		return returnValue;
	}
	
	/**
	 * Retrieves the Element wanted as Particles.
	 * 
	 * @param key to retrieve
	 * 
	 * @return the wanted value or null.
	 */
	public ParticleEffects getAsParticle(String key){
		Object obj = get(key);
		if(obj instanceof String){
			String stringParticle = (String) obj;
			if("null".equalsIgnoreCase(stringParticle)
					|| "none".equalsIgnoreCase(stringParticle)
					|| stringParticle == null){
				return null;
			}else{
				ParticleEffects realParticle = null;
				try{
					for(ParticleEffects effect : ParticleEffects.values()){
						if(effect.asMirror() != null 
								&& effect.asMirror().getPacketArg().equals(stringParticle)) {
							
							realParticle = effect;
						}
					}
					
					if(realParticle == null) realParticle = ParticleEffects.valueOf(stringParticle.toUpperCase()); 
				}catch(Throwable exp){}
				
				if(realParticle == null) realParticle = ParticleEffects.FIREWORKS_SPARK;
				return realParticle;
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves the Element wanted as ParticleContainer.
	 * 
	 * @param key to retrieve
	 * 
	 * @return the wanted value or null.
	 */
	public ParticleContainer getAsParticleContainer(String key){
		Object obj = get(key);
		if(obj instanceof String){
			return ParticleContainer.generate((String) obj);
		}
		
		return null;
	}
	
	/**
	 * Retrieves the Element wanted as Target Type.
	 * 
	 * @param key to retrieve
	 * 
	 * @return the wanted value or null.
	 */
	public TargetType getAsTargetType(String key){
		Object obj = get(key);
		if(obj instanceof String){
			String stringTarget = (String) obj;
			stringTarget = stringTarget.toLowerCase();
			
			if(stringTarget.startsWith("ally")) return TargetType.FRIEND;
			if(stringTarget.startsWith("alli")) return TargetType.FRIEND;
			if(stringTarget.startsWith("fr")) return TargetType.FRIEND;
			
			if(stringTarget.startsWith("a")) return TargetType.ALL;
			
			if(stringTarget.startsWith("e")) return TargetType.ENEMY;
			if(stringTarget.startsWith("fr")) return TargetType.ENEMY;
			
			return TargetType.ALL;
		}
		
		return null;
	}
	
	
	/**
	 * Retrieves the Element wanted as Potion Type.
	 * 
	 * @param key to retrieve
	 * 
	 * @return the wanted value or null.
	 */
	@SuppressWarnings("deprecation")
	public PotionEffectType getAsPotionEffectType(String key){
		Object obj = get(key);
		if(obj instanceof PotionEffectType) return (PotionEffectType) obj;
		if(obj instanceof String){
			String potionString = (String) obj;
			return PotionEffectType.getByName(potionString);
		}

		if(obj instanceof Integer){
			int potionNumber = (Integer) obj;
			return PotionEffectType.getById(potionNumber);
		}
		
		return null;
	}
	
	
	
	//The following methods are bypassed to get the
	//Be sure the Traits do not have Compilation issues.

	@Override
	public Object get(Object key) {
		return super.get(key);
	}

	@Override
	public Object put(String key, Object value) {
		return super.put(key, value);
	}

	@Override
	public boolean containsKey(Object key) {
		return super.containsKey(key);
	}

}
