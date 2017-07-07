/*******************************************************************************
 * Copyright 2014 Tobias Welther
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.tobiyas.racesandclasses.playermanagement.leveling.manager;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.skills.SkillType;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.saving.PlayerSavingData;
import de.tobiyas.util.evaluations.EvalEvaluator;
import de.tobiyas.util.evaluations.parts.Calculation;
import de.tobiyas.util.exception.TryUtils;
import de.tobiyas.util.exception.TryUtils.Function;

public class McMMOLevelManager extends AbstractPlayerLevelingSystem {
	
	
	/**
	 * The String to calc the McMMO Level.
	 */
	private final String calcString;
	
	/**
	 * Sets up the Plugin.
	 */
	public McMMOLevelManager(RaCPlayer player, PlayerSavingData data) {
		super(player, data);
		
		calcString = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_mapExpPerLevelCalculationString();
	}
	
	
	@Override
	public int getCurrentLevel() {
		if(!isMcMMOPresent()) return 1;
		
		return calcCurrentLevel();
	}

	@Override
	public int getCurrentExpOfLevel() {
		if(!isMcMMOPresent()) return 1;

		return 0;
	}


	@Override
	public void setCurrentLevel(int level) {
		//not supported by Skill API
	}

	@Override
	public void setCurrentExpOfLevel(int currentExpOfLevel) {
		//not supported by Skill API
	}

	@Override
	public boolean addExp(int exp) {
		return false;
	}

	@Override
	public boolean removeExp(int exp) {
		return false;
	}


	@Override
	public void checkLevelChanged() {
		//nothing to do.
	}


	@Override
	public boolean canRemove(int toRemove) {
		return false;
	}

	
	/**
	 * Checks if the mcMMO Plugin is present.
	 * 
	 * @return true if is present, false if not.
	 */
	private boolean isMcMMOPresent(){
		try{
			mcMMO mcMMO = getMCMMO();
			if(mcMMO == null) return false;
			
			return mcMMO.isEnabled();
		}catch(Throwable exp){ return false; }
	}
	
	/**
	 * Returns the Skill API class.
	 * 
	 * @return the Skill API class or Null.
	 */
	private mcMMO getMCMMO(){
		try{
			return (mcMMO) Bukkit.getPluginManager().getPlugin("mcMMO");
		}catch(Throwable exp){ return null; }
	}
	
	
	/**
	 * Checks if the McMMO String works.
	 * 
	 * @param generatorString
	 * @return
	 */
	public static boolean verifyGeneratorStringWorks(String generatorString){
		try{
			generatorString = generatorString.toLowerCase();
			
			Calculation calc = EvalEvaluator.parse(generatorString);
			if(calc == null) throw new IllegalArgumentException("Evaluation String not parseable");
			
			Map<String,Double> vars = new HashMap<>();
			vars.put("powerlevel", 1d);
			
			for(SkillType type : SkillType.values()){
				vars.put(type.name(), 1d);
				vars.put(type.getName(), 1d);
			}
			
			return calc.calculate(vars) != Double.NaN;
	    }catch(Throwable exp){
	    	return false;
	    }
	}

	
	private int calcCurrentLevel(){
		Calculation calc = EvalEvaluator.parse(calcString);
		if(calc == null) return 0;
		
		
		final Player pl = getPlayer().getPlayer();
		int powerLevel = ExperienceAPI.getPowerLevel(pl);
		
		Map<String,Double> vars = new HashMap<>();
		vars.put("powerlevel".toLowerCase(), (double) powerLevel);
		
		for(final SkillType type : SkillType.values()){
			double skillLevel = TryUtils.Try(new Function<Double>() { public Double doStuff(){ return Double.valueOf(ExperienceAPI.getLevel(pl, type.name())); }}, Double.valueOf(0d));
			vars.put(type.name().toLowerCase(), skillLevel);
			vars.put(type.getName().toLowerCase(), skillLevel);
		}
		
		return (int) calc.calculate(vars);
	}


	@Override
	public void addLevel(int value) {
	}


	@Override
	public void removeLevel(int value) {
	}


	@Override
	public int getMaxEXPToNextLevel() {
		return 1;
	}
}
