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
		return getAsString(key, null);
	}
	
	
	/**
	 * Retrieves the Element wanted as String.
	 * 
	 * @param key to retrieve
	 * @param defaultValue to use if failed.
	 * 
	 * @return the wanted value or null.
	 */
	public String getAsString(String key, String defaultValue){
		Object obj = get(key);
		if(obj == null) return defaultValue;
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
		return getAsInt(key, Integer.MIN_VALUE);
	}
	
	/**
	 * Retrieves the Element wanted as int.
	 * 
	 * @param key to retrieve
	 * @param defaultValue to use if failed.
	 * 
	 * @return the wanted value or the default value.
	 */
	public int getAsInt(String key, int defaultValue){
		Object obj = get(key);
		int returnValue = defaultValue;
		if(obj == null) return returnValue;
		
		if(obj instanceof Integer) returnValue = (Integer) obj;
		if(obj instanceof Double) returnValue = ((Double) obj).intValue();
		if(obj instanceof Float) returnValue = ((Float) obj).intValue();
		if(obj instanceof String) try{returnValue = Integer.parseInt((String) obj);}catch(Throwable exp){}
		
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
		return getAsDouble(key, Double.MIN_VALUE);
	}
	
	/**
	 * Retrieves the Element wanted as double.
	 * 
	 * @param key to retrieve
	 * @param defaultValue to use if failed.
	 * 
	 * @return the wanted value or the default value.
	 */
	public double getAsDouble(String key, double defaultValue){
		Object obj = get(key);
		double returnValue = defaultValue;
		if(obj == null) return returnValue;
		
		if(obj instanceof Integer) returnValue = (Integer) obj;
		if(obj instanceof Double) returnValue = (Double) obj;
		if(obj instanceof Float) returnValue = (Float) obj;
		if(obj instanceof String) try{returnValue = Double.parseDouble((String) obj);}catch(Throwable exp){}
		
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
		return getAsBool(key, false);
	}
	
	/**
	 * Retrieves the Element wanted as Boolean.
	 * 
	 * @param key to retrieve
	 * @param defaultValue to use if failed.
	 * 
	 * @return the wanted value or False.
	 */
	public boolean getAsBool(String key, boolean defaultValue){
		Object obj = get(key);
		if(obj == null) return false;
		
		if(obj instanceof Boolean) return (Boolean)obj;
		return defaultValue;
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
		
		if(obj instanceof String) returnValue.add(obj.toString());
		
		return returnValue;
	}
	
	/**
	 * Retrieves the Element wanted as Material.
	 * 
	 * @param key to retrieve
	 * 
	 * @return the wanted value or null.
	 */
	public Material getAsMaterial(String key){
		return getAsMaterial(key, null);
	}
	
	/**
	 * Retrieves the Element wanted as Material.
	 * 
	 * @param key to retrieve
	 * @param defaultValue to use if failed.
	 * 
	 * @return the wanted value or null.
	 */
	@SuppressWarnings("deprecation")
	public Material getAsMaterial(String key, Material defaultValue){
		Object obj = get(key);
		Material returnValue = defaultValue;
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
		
		return returnValue == null ? defaultValue : returnValue;
	}
	
	/**
	 * Retrieves the Element wanted as Particles.
	 * 
	 * @param key to retrieve
	 * 
	 * @return the wanted value or null.
	 */
	public ParticleEffects getAsParticle(String key){
		return getAsParticle(key, null);
	}
	
	/**
	 * Retrieves the Element wanted as Particles.
	 * 
	 * @param key to retrieve
	 * @param defaultValue to use if failed.
	 * 
	 * @return the wanted value or null.
	 */
	public ParticleEffects getAsParticle(String key, ParticleEffects defaultValue){
		Object obj = get(key);
		if(obj instanceof String){
			String stringParticle = (String) obj;
			if("null".equalsIgnoreCase(stringParticle)
					|| "none".equalsIgnoreCase(stringParticle)
					|| stringParticle == null){
				return null;
			}else{
				ParticleEffects realParticle = defaultValue;
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
		
		return defaultValue;
	}
	
	/**
	 * Retrieves the Element wanted as ParticleContainer.
	 * 
	 * @param key to retrieve
	 * 
	 * @return the wanted value or null.
	 */
	public ParticleContainer getAsParticleContainer(String key){
		return getAsParticleContainer(key, null);
	}
	
	/**
	 * Retrieves the Element wanted as ParticleContainer.
	 * 
	 * @param key to retrieve
	 * @param defaultValue to use if failed.
	 * 
	 * @return the wanted value or null.
	 */
	public ParticleContainer getAsParticleContainer(String key, ParticleContainer defaultValue){
		Object obj = get(key);
		if(obj instanceof String){
			ParticleContainer container = ParticleContainer.generate(obj.toString());
			return container == null ? defaultValue : container;
		}
		
		return defaultValue;
	}
	
	/**
	 * Retrieves the Element wanted as Target Type.
	 * 
	 * @param key to retrieve
	 * 
	 * @return the wanted value or null.
	 */
	public TargetType getAsTargetType(String key){
		return getAsTargetType(key, TargetType.ALL);
	}
	
	/**
	 * Retrieves the Element wanted as Target Type.
	 * 
	 * @param key to retrieve
	 * @param defaultValue to use if failed.
	 * 
	 * @return the wanted value or null.
	 */
	public TargetType getAsTargetType(String key, TargetType defaultValue){
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
		
		return defaultValue;
	}
	
	
	/**
	 * Retrieves the Element wanted as Potion Type.
	 * 
	 * @param key to retrieve
	 * 
	 * @return the wanted value or null.
	 */
	public PotionEffectType getAsPotionEffectType(String key){
		return getAsPotionEffectType(key, null);
	}
	
	/**
	 * Retrieves the Element wanted as Potion Type.
	 * 
	 * @param key to retrieve
	 * @param defaultValue to use if failed.
	 * 
	 * @return the wanted value or null.
	 */
	@SuppressWarnings("deprecation")
	public PotionEffectType getAsPotionEffectType(String key, PotionEffectType defaultValue){
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
		
		return defaultValue;
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
