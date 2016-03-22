package de.tobiyas.racesandclasses.traitcontainer.interfaces.skills;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.bukkit.Material;

public class SkillLevelConfig {

	/**
	 * The Map of level -> container.
	 */
	private final Map<Integer,SkillLevelContainer> levelContainers = new HashMap<>();
	
	
	/**
	 * Parses the List of Prequisits.
	 * @param toParse to parse.
	 */
	public SkillLevelConfig(List<String> toParse) {
		if(toParse != null) parse(toParse);
	}
	
	public SkillLevelConfig() {
		this(null);
	}
	
	
	/**
	 * Parses the List and compiles it.
	 * @param toParse to parse.
	 */
	public void parse(List<String> toParse){
		this.levelContainers.clear();
		if(toParse == null || toParse.isEmpty()) return;
		
		for(String line : toParse){
			SkillLevelContainer container = SkillLevelContainer.parse(line);
			if(container != null && !levelContainers.containsKey(container.getLevel())){
				this.levelContainers.put(container.getLevel(), container);
			}
		}
	}
	
	
	/**
	 * Returns the Points for the level passed.
	 */
	public int getPointsForLevel(int level){
		if(levelContainers.isEmpty()) return 1;
		SkillLevelContainer nearest = getNearest(level);
		return nearest == null ? 1 : nearest.getPoints();
	}
	
	/**
	 * Returns the Cast cost for the level passed.
	 */
	public double getCastCostForLevel(int level, double defaultValue){
		if(levelContainers.isEmpty()) return defaultValue;
		SkillLevelContainer nearest = getNearest(level);
		double cost = nearest == null ? defaultValue : nearest.getCastCost();
		return cost < 0 ? defaultValue : cost;
	}
	
	/**
	 * Returns the Cast cost for the level passed.
	 */
	public Material getCastMaterialForLevel(int level, Material defaultValue){
		if(levelContainers.isEmpty()) return defaultValue;
		SkillLevelContainer nearest = getNearest(level);
		Material mat = nearest == null ? Material.AIR : nearest.getCastMaterial();
		return mat == Material.AIR ? defaultValue : nearest.getCastMaterial();
	}

	/**
	 * Returns the Cast cost for the level passed.
	 */
	public short getCastMaterialDamageForLevel(int level, short defaultValue){
		if(levelContainers.isEmpty()) return defaultValue;
		SkillLevelContainer nearest = getNearest(level);
		short damage = nearest == null ? defaultValue : nearest.getCastMaterialDamage();
		return damage <= 0 ? defaultValue : damage;
	}
	
	/**
	 * Returns the Cast material name for the level passed.
	 */
	public String getCastMaterialNameForLevel(int level, String defaultValue){
		if(levelContainers.isEmpty()) return defaultValue;
		SkillLevelContainer nearest = getNearest(level);
		String name = nearest == null ? defaultValue : nearest.getCastMaterialName();
		return name.isEmpty() ? defaultValue : name;
	}
	
	/**
	 * Returns the Points for the level passed.
	 */
	public List<String> getTraitPreForLevel(int level){
		if(levelContainers.isEmpty()) return new LinkedList<>();
		SkillLevelContainer nearest = getNearest(level);
		return nearest == null ? new LinkedList<String>() : nearest.getPreTraits();
	}
	
	/**
	 * Returns the Points for the level passed.
	 */
	public int getMinLevel(int level){
		if(levelContainers.isEmpty()) return 1;
		SkillLevelContainer nearest = getNearest(level);
		return nearest == null ? 1 : nearest.getMinLevel();
	}
	
	/**
	 * Returns the Nearest.
	 * @param level to search.
	 * @return the nearest.
	 */
	private SkillLevelContainer getNearest(int level){
		SkillLevelContainer container = null;
		for(SkillLevelContainer c : levelContainers.values()){
			int cLevel = c.getLevel();
			int oldLevel = container == null ? 0 : container.getLevel();
			if(cLevel > oldLevel && cLevel <= level) container = c;
		}
		
		return container;
	}
	
	
	
	
	private static class SkillLevelContainer {
		private final int level;
		private final int points;
		private final int minLevel;
		private final List<String> preTraits = new LinkedList<String>();
		
		private final double castCost;
		private final Material castMaterial;
		private final short castMaterialDamage;
		private final String castMaterialName;
		
		
		public SkillLevelContainer(int level, int minLevel, int points, List<String> preTraits,
				double castCost, Material castMaterial, short castMaterialDamage, String castMaterialName) {
			super();
			this.level = level;
			this.minLevel = minLevel;
			this.points = points;
			this.preTraits.addAll(preTraits);
			
			this.castCost = castCost;
			this.castMaterial = castMaterial;
			this.castMaterialDamage = castMaterialDamage;
			this.castMaterialName = castMaterialName;
		}


		public int getLevel() {
			return level;
		}


		public int getPoints() {
			return points;
		}


		public List<String> getPreTraits() {
			return preTraits;
		}
		
		public int getMinLevel() {
			return minLevel;
		}
		
		public double getCastCost() {
			return castCost;
		}
		
		public Material getCastMaterial() {
			return castMaterial;
		}
		
		public short getCastMaterialDamage() {
			return castMaterialDamage;
		}
		
		public String getCastMaterialName() {
			return castMaterialName;
		}
		
		
		
		/**
		 * Parses the Line to a container.
		 * @param line to use.
		 * @return the SkillLevel container or null if not parseable.
		 */
		public static SkillLevelContainer parse(String line) {
			String[] split = line.split(Pattern.quote("#"));
			int skillLevel = 1;
			int minLevel = 1;
			int points = 1;
			List<String> preTraits = new LinkedList<>();
			
			//Casting cost:
			int castingCost = -1;
			Material castingMaterial = Material.AIR;
			short castingMaterialDamage = -1;
			String castingMaterialName = "";
			
			for(String part : split){
				if(!part.contains(":")) continue;
				String[] parts = part.split(":", 2);
				
				String label = parts[0];
				String config = parts[1];
				
				if("level".equalsIgnoreCase(label)){
					try{ skillLevel = Integer.parseInt(config); }catch(Throwable exp){}
					continue;
				}
				
				if("minlevel".equalsIgnoreCase(label)){
					try{ minLevel = Integer.parseInt(config); }catch(Throwable exp){}
					continue;
				}
				
				if("points".equalsIgnoreCase(label)){
					try{ points = Integer.parseInt(config); }catch(Throwable exp){}
					continue;
				}
				
				if("traits".equalsIgnoreCase(label)){
					try{
						String[] preSplit = config.split(Pattern.quote(";"));
						for(String pre : preSplit) preTraits.add(pre);
					}catch(Throwable exp){}
					continue;
				}
				
				//Casting stuff:
				if("castingCost".equalsIgnoreCase(label)){
					try{ castingCost = Integer.parseInt(config); }catch(Throwable exp){}
				}
				
				if("castingMaterial".equalsIgnoreCase(label)){
					castingMaterial = Material.matchMaterial(config);
					if(castingMaterial == null) castingMaterial = Material.AIR;
				}

				if("castingMaterialDamage".equalsIgnoreCase(label)){
					try{ castingMaterialDamage = Short.parseShort(config); }catch(Throwable exp){}
				}
				
				if("castingMaterialName".equalsIgnoreCase(label)){
					castingMaterialName = config;
				}
			}
			
			return new SkillLevelContainer(
					skillLevel, minLevel, points, preTraits,
					castingCost, castingMaterial, castingMaterialDamage, castingMaterialName);
		}
		
	}
	
}
