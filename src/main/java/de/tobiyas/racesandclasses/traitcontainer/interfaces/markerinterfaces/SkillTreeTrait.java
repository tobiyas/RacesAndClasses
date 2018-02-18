package de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;

public interface SkillTreeTrait {

	
	/**
	 * The cost for SkillPoints
	 */
	public static final String SKILL_LEVELS_CONFIG_PATH = "skillLevelsConfig";
	
	/**
	 * The MAX-Level for a Skill.
	 */
	public static final String SKILL_TREE_MAX_LEVEL_PATH = "skillTreeMaxLevel";	
	
	/**
	 * If the Skill is a permanent Trait.
	 */
	public static final String SKILL_TREE_PERMANENT_TRAIT_PATH= "permanentTrait";
	
	/**
	 * The slot for the SkillTree.
	 */
	public static final String SKILL_TREE_SLOT_PATH = "skillTreeSlot";	
	
	/**
	 * The material for the SkillTree.
	 */
	public static final String SKILL_TREE_MATERIAL_PATH = "skillTreeMaterial";	
	
	/**
	 * The damage value for the SkillTree.
	 */
	public static final String SKILL_TREE_DAMAGE_PATH = "skillTreeDamage";
	
	/**
	 * The which other skills to exclude with this one.
	 */
	public static final String SKILL_TREE_EXCLUDE_OTHERS_PATH = "skillTreeExcludeOthers";

	/**
	 * The Name for the skill tree.
	 */
	public static final String SKILL_TREE_NAME = "skillTreeName";
	
	
	
	/**
	 * Returns the cost for this skill
	 * @return the cost to choose this skill.
	 */
	public int getSkillPointCost(int level);

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
	public List<String> getSkillTreePrequisits(int level);
	
	/**
	 * Returns the max level for the Skill.
	 */
	public int getSkillMaxLevel();

	/**
	 * Gets the Min level to learn this skill.
	 * @param level to check
	 * @return the level to learn.
	 */
	public int getSkillMinLevel(int level);
	
	/**
	 * @return the name of the skill tree.
	 */
	public String getSkillTreeName();
	
	/**
	 * Gets the Traits this Trait excludes
	 * @return the traits this trait excludes. 
	 */
	public List<String> getExcludesOtherTraits();

	/**
	 * This is called when a skill level changes.
	 * @param player the player whos skill level changed.
	 */
	public void skillLevelChanged( RaCPlayer player );
	
}
