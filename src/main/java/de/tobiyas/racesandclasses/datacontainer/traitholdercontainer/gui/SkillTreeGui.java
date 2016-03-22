package de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.gui;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LevelAPI;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.playermanagement.skilltree.PlayerSkillTreeManager;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.TraitWithRestrictions;
import de.tobiyas.util.formating.StringFormatUtils;
import de.tobiyas.util.inventorymenu.BasicSelectionInterface;
import de.tobiyas.util.math.Math2;
import de.tobiyas.util.vollotile.VollotileCode.MCVersion;
import de.tobiyas.util.vollotile.VollotileCodeManager;

public class SkillTreeGui extends BasicSelectionInterface {

	/**
	 * The player to use.
	 */
	private final RaCPlayer racPlayer;
	
	/**
	 * The map of Trait -> Buttons.
	 */
	private final Map<Trait,ItemStack> buttons = new HashMap<>();
	
	/**
	 * The traits that should be applied!
	 */
	private final Map<Trait,Integer> toApply = new HashMap<>();
	
	/**
	 * The Apply button.
	 */
	private final ItemStack applyItem;
	
	/**
	 * The Apply button.
	 */
	private final ItemStack discardItem;
	
	/**
	 * the temp free points to remove.
	 */
	private int tempFreePointsToRemove = 0;
	
	/**
	 * If something changed.
	 */
	private boolean somethingChanged = false;

	
	
	@SuppressWarnings("deprecation")
	public SkillTreeGui(Player player) {
		super(player, null, 
				Bukkit.createInventory(player, 9*6, ChatColor.RED + "Skill Tree"), 
				player.getInventory(), 
				RacesAndClasses.getPlugin());
		
		this.racPlayer = RaCPlayerManager.get().getPlayer(player);
		this.applyItem = generateItem(Material.WOOL, DyeColor.GREEN.getWoolData(), ChatColor.GREEN + "Apply", ChatColor.AQUA + "Applies the changes");
		this.discardItem = generateItem(Material.WOOL, DyeColor.RED.getWoolData(), ChatColor.RED + "Discard", ChatColor.AQUA + "Discards the changes");
		
		//Copy traits + levels!
		this.toApply.putAll(racPlayer.getSkillTreeManager().getTraitsWithLevels());
		redraw();
	}
	
	
	/**
	 * Redraws the GUI.
	 */
	private void redraw(){
		getTopInventory().clear();
		buttons.clear();
		
		int freePoints = racPlayer.getSkillTreeManager().getFreeSkillpoints() - tempFreePointsToRemove;
		
		int level = LevelAPI.getCurrentLevel(racPlayer);
		
		Collection<Trait> allTraits = TraitHolderCombinder.getAllTraitsOfPlayer(racPlayer);
		Collection<Trait> permanent = getPermanent(allTraits);
		permanent = filterForExclusions(allTraits);
		
		//Now generate items and populate!
		for(Trait trait : allTraits){
			boolean isPermanent = permanent.contains(trait);
			int slot = trait.getSkillTreePlace();
			
			//Do not display items that are permanent or below 0.
			if(slot < 0 || isPermanent) continue;
			
			//Filter items that are outside of the Box.
			if(slot >= getTopInventory().getSize()) continue;
			
			ItemStack item = generateItemForTrait(trait, toApply, level, freePoints);
			
			//If already present -> Skip!
			if(getTopInventory().getItem(slot) != null) continue;
			getTopInventory().setItem(slot, item);
			buttons.put(trait, item);
		}
		
		
		getTopInventory().setItem(8, generateFreeItem(freePoints, level));
		if(somethingChanged) getTopInventory().setItem((9*5)+8, applyItem);
		getTopInventory().setItem((9*5)+7, discardItem);
	}
	

	/**
	 * Generates an Item for the Trait.
	 * @param trait to use.
	 * @param hasPrequisits
	 * @param hasLevel
	 * @return
	 */
	private ItemStack generateItemForTrait(Trait trait, Map<Trait,Integer> traitsHasWithLevels, int playerLevel, int freePoints){
		//First generate flags:
		int maxSkillLevel = trait.getSkillMaxLevel();
		int playerSkillLevel = traitsHasWithLevels.containsKey(trait) ? traitsHasWithLevels.get(trait) : 0;
		boolean playerHasMaxLevel = playerSkillLevel >= maxSkillLevel;
		int traitLevelNeeded = trait.getSkillMinLevel(playerSkillLevel+1);
		boolean playerHasLevel = playerLevel >= traitLevelNeeded;
		
		String excluded = getForExcusions(trait, traitsHasWithLevels);
		boolean isExcluded = !excluded.isEmpty();
		
		boolean hasPoints = freePoints >= trait.getSkillPointCost(playerSkillLevel+1);
		boolean hasPrequisits = hasPrequisits(trait, traitsHasWithLevels);
		boolean canUse = hasPoints && hasPrequisits && playerHasLevel && !isExcluded;
		ChatColor titleColor = playerHasMaxLevel ? ChatColor.BLUE : (canUse ? ChatColor.GREEN : ChatColor.DARK_RED );
		String title = titleColor + trait.getDisplayName() + " (" + playerSkillLevel + "/" + maxSkillLevel + ")";
		String levelString = (playerHasLevel ? ChatColor.GREEN : ChatColor.RED) + "Needs level: " + traitLevelNeeded;
		String pointsString = (hasPoints ? ChatColor.GREEN : ChatColor.RED) + "Needs: " + trait.getSkillPointCost(playerSkillLevel+1) + " Points";
		String prequisitString = trait.getSkillTreePrequisits(playerSkillLevel+1).isEmpty() ? "" : (ChatColor.YELLOW + "Prequisits: " + generatePrequisitString(trait, traitsHasWithLevels));
		
		List<String> lore = new LinkedList<>();
		lore.addAll(Arrays.asList(trait.getPrettyConfiguration().split(Pattern.quote("#n"))));
		lore.add("");
		if(playerHasMaxLevel) lore.add(ChatColor.AQUA + "Already learned");
		else{
			lore.add(pointsString);
			lore.add(levelString);
			if(!prequisitString.isEmpty()) lore.add(prequisitString);
			if(isExcluded) lore.add(ChatColor.DARK_RED + "Excluded by: " + ChatColor.RED + excluded);
		}
		
		ItemStack item = trait.getSkillTreeSymbol();
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(title);
		meta.setLore(lore);
		
		//Add enchantment if has:
		if(playerHasMaxLevel) {
			meta.addEnchant(Enchantment.DURABILITY, 1, true);
			if(VollotileCodeManager.getVollotileCode().getVersion().isVersionGreaterOrEqual(MCVersion.v1_8_R3)){
				meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
			}
		}
		
		item.setItemMeta(meta);
		return item;
	}
	
	/**
	 * Gets the Trait/s for Exclusions.
	 * @param trait to use
	 * @param traitsHasWithLevels to use
	 * @return the trait for exclusions or empty if none..
	 */
	private String getForExcusions(Trait trait, Map<Trait, Integer> traitsHasWithLevels) {
		StringBuilder builder = new StringBuilder();
		for(Map.Entry<Trait,Integer> entry : traitsHasWithLevels.entrySet()){
			if(entry.getValue() <= 0) continue;
			
			//Found an exclusion!
			if(entry.getKey().getExcludesOtherTraits().contains(trait)){
				if(builder.length() != 0) builder.append(", ");
				builder.append(entry.getKey().getDisplayName());
			}
		}
		
		return builder.toString();
	}


	private String generatePrequisitString(Trait trait, Map<Trait,Integer> traitsHas){
		int playerSkillLevel = traitsHas.containsKey(trait) ? traitsHas.get(trait) : 0;
		StringBuilder builder = new StringBuilder();
		for (Iterator<String> iterator = trait.getSkillTreePrequisits(playerSkillLevel+1).iterator(); iterator.hasNext();) {
			String pre = iterator.next();
			String[] split = pre.split(Pattern.quote("@"));
			String preName = split[0];
			int level = split.length == 1 ? 1 : StringFormatUtils.parseInt(split[1], 1);
			
			Trait found = null;
			for(Trait has : traitsHas.keySet()){
				if(has.getDisplayName().equalsIgnoreCase(preName)){
					found = has;
					break;
				}
			}
			
			boolean has = found != null && traitsHas.get(found) >= level;
			builder.append(has?ChatColor.GREEN:ChatColor.RED);
			builder.append(has?found.getDisplayName():preName).append(" ").append(level);
			if(iterator.hasNext()) builder.append(ChatColor.WHITE).append(", ");
		}
		
		return builder.toString();
	}
	
	
	private boolean hasPrequisits(Trait trait, Map<Trait,Integer> traitsHas){
		int playerSkillLevel = traitsHas.containsKey(trait) ? traitsHas.get(trait) : 0;
		for(String pre : trait.getSkillTreePrequisits(playerSkillLevel+1)){
			String[] preSplit = pre.split(Pattern.quote("@"));
			String preName = preSplit[0];
			int level = preSplit.length == 1 ? 1 : StringFormatUtils.parseInt(preSplit[1], 1);
			
			boolean found = false;
			for(Map.Entry<Trait,Integer> has : traitsHas.entrySet()){
				if(has.getKey().getDisplayName().equalsIgnoreCase(preName)
						&& has.getValue() >= level){
					found = true;
					break;
				}
			}
			
			if(!found) return false;
		}
		
		return true;
	}
	
	
	private ItemStack generateFreeItem(int freePoints, int level) {
		ItemStack item = new ItemStack(Material.CAKE, Math2.clamp(1, freePoints, 64));
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.AQUA + "Info - Read lore");
		meta.setLore(Arrays.asList(ChatColor.YELLOW + "Free Skillpoints: " + ChatColor.AQUA + freePoints,
				ChatColor.YELLOW + "Level: " + ChatColor.AQUA + level));
		
		item.setItemMeta(meta);
		return item;
	}

	
	/**
	 * Filters for the Exclusions
	 * @param allTraits to filter.
	 * @return the rest of the Traits.
	 */
	private Collection<Trait> filterForExclusions(Collection<Trait> allTraits) {
		Collection<Trait> filtered = new HashSet<>(allTraits);
		for(Trait contained : allTraits){
			boolean hasSkilled = racPlayer.getSkillTreeManager().getLevel(contained) > 0;
			if(hasSkilled) filtered.removeAll(contained.getExcludesOtherTraits());
		}
		
		return filtered;
	}

	

	/**
	 * Returns all Permanent Traits:
	 * @param traits to use for checking
	 * @return the permanent Traits from the ones passed.
	 */
	private Collection<Trait> getPermanent(Collection<Trait> traits){
		Collection<Trait> permanent = new HashSet<>();
		if(traits == null || traits.isEmpty()) return permanent;
		for(Trait trait : traits){
			if(trait.isPermanentSkill()){
				permanent.add(trait);
			}
		}
		
		return permanent;
	}
	

	@Override
	protected boolean onBackPressed() { return true; }
	
	
	@Override
	protected void onAcceptPressed() {}

	
	@Override
	protected void onSelectionItemPressed(int slot, ItemStack item) {
		if(item == null || item.getType() == Material.AIR) return;
		if(applyItem.isSimilar(item)){
			applyCurrentSettings();
			return;
		}

		if(discardItem.isSimilar(item)){
			discardCurrentSettings();
			return;
		}
		
		//Check which trait was clicked. If none -> Not our problem!
		Trait trait = getTraitForItem(item);
		if(trait == null) return;
		
		int playerTraitLevel = this.toApply.containsKey(trait) ? this.toApply.get(trait) : 0;
		int freePoints = racPlayer.getSkillTreeManager().getFreeSkillpoints() - tempFreePointsToRemove;
		int level = LevelAPI.getCurrentLevel(racPlayer);
		
		//If already has:
		if(playerTraitLevel >= trait.getSkillMaxLevel()){
			player.sendMessage(ChatColor.RED + "You alreadsy have this Trait at max level.");
			return;
		}
		
		//If SkillPoints too low:
		if(freePoints < trait.getSkillPointCost(playerTraitLevel+1)){
			player.sendMessage(ChatColor.RED + "You do not have enough free Skill-Points.");
			return;
		}
		
		//If not has prequisits:
		if(!hasPrequisits(trait, toApply)){
			player.sendMessage(ChatColor.RED + "You do not have all prequisits!");
			return;
		}
		
		//If Level to low:
		int neededLevel = (trait instanceof TraitWithRestrictions) ? ((TraitWithRestrictions)trait).getMinLevel() : 0;
		if(level < neededLevel){
			player.sendMessage(ChatColor.RED + "Your level is too low!");
			return;
		}
		
		//Seems like the player wants to learn the Trait:
		this.toApply.put(trait, playerTraitLevel+1);
		this.tempFreePointsToRemove += trait.getSkillPointCost(playerTraitLevel+1);
		this.somethingChanged = true;
		
		redraw();
	}
	
	/**
	 * Discards the Current Changes.
	 */
	private void discardCurrentSettings() {
		this.toApply.clear();
		this.toApply.putAll(racPlayer.getSkillTreeManager().getTraitsWithLevels());
		this.tempFreePointsToRemove = 0;

		this.somethingChanged = false;
		redraw();
	}


	/**
	 * Applies + closes.
	 */
	private void applyCurrentSettings() {
		this.close();
		
		PlayerSkillTreeManager manager = racPlayer.getSkillTreeManager();
		manager.replaceWithNew(toApply);
		
		//Rescan after:
		racPlayer.getArrowManager().rescanPlayer();
		racPlayer.getSpellManager().rescan();
		
		this.somethingChanged = false;
		player.sendMessage(ChatColor.GREEN + "Changes applied.");
	}


	/**
	 * Returns the Trait associated to the Item.
	 * @param item to use.
	 * @return the trait associated.
	 */
	private Trait getTraitForItem(ItemStack item){
		for(Entry<Trait,ItemStack> entry : buttons.entrySet()){
			if(item.isSimilar(entry.getValue())) return entry.getKey();
		}
		
		return null;
	}
	
}
