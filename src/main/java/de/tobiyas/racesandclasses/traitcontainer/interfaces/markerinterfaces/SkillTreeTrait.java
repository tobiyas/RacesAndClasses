package de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces;

import java.util.List;

import org.bukkit.inventory.ItemStack;

public interface SkillTreeTrait {

	/**
	 * Returns the cost for this skill
	 * @return the cost to choose this skill.
	 */
	public int getSkillPointCost();
	

	/**
	 * @return true if the Player ALWAYS has this spell.
	 */
	public boolean isPermanentSkill();
	
	/**
	 * @return the slot in the skill-tree gui.
	 */
	public int getSkillTreePlace();
	
	/**
	 * @return the slot in the skill-tree gui.
	 */
	public ItemStack getSkillTreeSymbol();
	
	/**
	 * @return the Prequisits for the Skill-Tree. As trait names.
	 */
	public List<String> getSkillTreePrequisits();
	
}
