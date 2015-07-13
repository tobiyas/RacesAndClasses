package de.tobiyas.racesandclasses.playermanagement.leveling.values;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import de.tobiyas.util.config.YAMLConfigExtended;

public class LevelValueModifyReader {

	
	private final YAMLConfigExtended config;
	private final String start;
	
	public LevelValueModifyReader(YAMLConfigExtended config, String start) {
		this.config = config;
		this.start = start;
	}
	
	
	
	/**
	 * Parses the Config at the start point.
	 * 
	 * @return the parsed Modifier.
	 */
	public LevelModifier parse(double defaultValue){
		List<LevelModContainer> list = new LinkedList<LevelModContainer>();
		list.add(new LevelModContainer(Integer.MIN_VALUE, Integer.MAX_VALUE, 0));
		
		if(!config.contains(start)) return new LevelModifier(list);
		
		if(config.isInt(start)){
			list.add(new LevelModContainer(config.getInt(start)));
			return new LevelModifier(list);
		}
		
		for(String key : config.getChildren(start)){
			double value = config.getDouble(start+"."+key, defaultValue);
			LevelModContainer container = LevelModContainer.parse(key, value);
			
			if(container != null) list.add(container);
		}
		
		return new LevelModifier(list);
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
		
		
		private final List<LevelModContainer> list;		
		
		public LevelModifier(List<LevelModContainer> list) {
			this.list = list;
		}



		/**
		 * Gets the Highest Value for the Level.
		 * 
		 * @param level to get for.
		 * 
		 * @return the best value.
		 */
		public double getForLevel(int level){
			double highest = 0;
			
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
