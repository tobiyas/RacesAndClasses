package de.tobiyas.racesandclasses.playermanagement.leveling.values;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import de.tobiyas.util.config.YAMLConfigExtended;

public class LevelValueModifyReader {

	
	private final YAMLConfigExtended config;
	private final String start;
	
	public LevelValueModifyReader(YAMLConfigExtended config, String start) {
		this.config = config;
		
		String[] split = start.split(Pattern.quote("."));
		if(split.length > 0){
			List<String> list = new LinkedList<String>(Arrays.asList(split));
			String last = list.remove(list.size()-1);
			
			String parent = String.join(".", list);
			for(String val : config.getChildren(parent)){
				if(val.equalsIgnoreCase(last)) {
					start = parent + "." + last;
				}
			}
		}
		
		this.start = start;
	}
	
	
	
	/**
	 * Parses the Config at the start point.
	 * 
	 * @return the parsed Modifier.
	 */
	public LevelModifier parse(double defaultValue){
		if(!config.contains(start)) return new LevelModifier();
		
		List<LevelModContainer> list = new LinkedList<LevelModContainer>();
		if(!config.isConfigurationSection(start)){
			return new LevelModifier(new LevelModContainer(parseToDouble(start, defaultValue)));
		}
		
		
		for(String key : config.getChildren(start)){
			double value = parseToDouble(start+"."+key, defaultValue);
			LevelModContainer container = LevelModContainer.parse(key, value);
			
			if(container != null) list.add(container);
		}
		
		return new LevelModifier(list);
	}
	
	/**
	 * Parses the Value at that point.
	 * @param path to use
	 * @param defaultValue to use.
	 * @return the parsed value.
	 */
	private double parseToDouble(String path, double defaultValue){
		if(!config.contains(path)) return defaultValue;
		
		if(config.isDouble(path)) return config.getDouble(path,defaultValue);
		if(config.isInt(path)) return config.getInt(path,(int)defaultValue);
		if(config.isString(path)) {
			try{
				return Double.parseDouble(config.getString(path,String.valueOf(defaultValue)));
			}catch (Exception e) {}
		}
		
		return defaultValue;
	}
	
	
	
	public static class LevelModContainer{
		
		private final int startLevel;
		private final int endLevel;
		
		private final double value;
		
		
		public LevelModContainer(int startLevel, int endLevel, double value) {
			this.startLevel = startLevel;
			this.endLevel = endLevel;
			this.value = value;
		}
		
		
		public LevelModContainer(double value) {
			this.startLevel = Integer.MIN_VALUE;
			this.endLevel = Integer.MAX_VALUE;
			this.value = value;
		}


		public double getValue() {
			return value;
		}

		public int getStartLevel() {
			return startLevel;
		}

		public int getEndLevel() {
			return endLevel;
		}
		
		
		public static LevelModContainer parse(String toParse, double value){
			try{
				//Check for from-To values.
				if(toParse.contains("-")){
					String[] parts = toParse.split(Pattern.quote("-"));
					if(parts.length != 2) return null;
					
					int start = Integer.parseInt(parts[0]);
					int end = Integer.parseInt(parts[1]);
					
					return new LevelModContainer(start, end, value);
				}
				
				//Smaller.
				if(toParse.startsWith("<")){
					toParse = toParse.substring(1);
					int end = Integer.parseInt(toParse);
					
					return new LevelModContainer(Integer.MIN_VALUE, end, value);
				}
				
				//greater.
				if(toParse.startsWith(">")){
					toParse = toParse.substring(1);
					int start = Integer.parseInt(toParse);
					
					return new LevelModContainer(start, Integer.MAX_VALUE, value);
				}
				
			}catch(Throwable exp){}
			
			return null;
		}
		
	}
	
	
	public static class LevelModifier{
		
		/**
		 * the default container +-0.
		 */
		private static final LevelModContainer defaultContainer = new LevelModContainer(0);
		
		private final List<LevelModContainer> list;		
		
		public LevelModifier(List<LevelModContainer> list) {
			this.list = list == null ? new LinkedList<LevelModContainer>() : list;
			if(list.isEmpty()) list.add(defaultContainer);
		}

		public LevelModifier(LevelModContainer... containers) {
			this(Arrays.asList(containers));
		}
		
		public LevelModifier() {
			this.list = new LinkedList<LevelModContainer>();
			this.list.add(defaultContainer);
		}



		/**
		 * Gets the Highest Value for the Level.
		 * 
		 * @param level to get for.
		 * 
		 * @return the best value.
		 */
		public double getForLevel(int level){
			double highest = Integer.MIN_VALUE;
			
			for(LevelModContainer container : list){
				if(container.getEndLevel() > level
						&& container.getStartLevel() < level){
					
					highest = Math.max(highest, container.getValue());
				}
			}
			
			return highest;
		}


		/**
		 * Generates an empty container.
		 * 
		 * @return the container.
		 */
		public static LevelModifier empty() {
			return single(0);
		}
		

		/**
		 * Generates an empty container.
		 * 
		 * @return the container.
		 */
		public static LevelModifier single(double value) {
			LevelModContainer empty = new LevelModContainer(value);
			List<LevelModContainer> list = new LinkedList<LevelModContainer>();
			list.add(empty);
			
			return new LevelModifier(list);
		}
		
	}

}
