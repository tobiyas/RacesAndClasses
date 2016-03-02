package de.tobiyas.racesandclasses.traitcontainer.interfaces.skills;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class SkillLevelPrequisits {

	/**
	 * The Map of level -> container.
	 */
	private final Map<Integer,SkillLevelContainer> levelContainers = new HashMap<>();
	
	
	/**
	 * Parses the List of Prequisits.
	 * @param toParse to parse.
	 */
	public SkillLevelPrequisits(List<String> toParse) {
		if(toParse != null) parse(toParse);
	}
	
	public SkillLevelPrequisits() {
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
		
		
		public SkillLevelContainer(int level, int minLevel, int points, List<String> preTraits) {
			super();
			this.level = level;
			this.minLevel = minLevel;
			this.points = points;
			this.preTraits.addAll(preTraits);
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
			}
			
			return new SkillLevelContainer(skillLevel, minLevel, points, preTraits);
		}
		
	}
	
}
