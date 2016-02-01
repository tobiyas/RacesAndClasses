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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LevelAPI;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.racesandclasses.playermanagement.skilltree.PlayerSkillTreeManager;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.TraitWithRestrictions;
import de.tobiyas.util.inventorymenu.BasicSelectionInterface;
import de.tobiyas.util.math.Math2;

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
	 * The traits to add after apply!
	 */
	private final Collection<Trait> toAdd = new HashSet<>();
	
	/**
	 * The Apply button.
	 */
	private final ItemStack applyItem;
	
	
	@SuppressWarnings("deprecation")
	public SkillTreeGui(Player player) {
		super(player, null, 
				Bukkit.createInventory(player, 9*6, ChatColor.RED + "Skill Tree"), 
				player.getInventory(), 
				RacesAndClasses.getPlugin());
		
		this.racPlayer = RaCPlayerManager.get().getPlayer(player);
		this.applyItem = generateItem(Material.WOOL, DyeColor.GREEN.getWoolData(), ChatColor.GREEN + "Apply", ChatColor.AQUA + "Applies the changes");
		
		redraw();
	}
	
	
	/**
	 * Redraws the GUI.
	 */
	private void redraw(){
		getTopInventory().clear();
		buttons.clear();
		
		int freePoints = racPlayer.getSkillTreeManager().getFreeSkillpoints();
		for(Trait trait : toAdd) freePoints -= trait.getSkillPointCost();
		
		int level = LevelAPI.getCurrentLevel(racPlayer);
		
		Collection<Trait> allTraits = TraitHolderCombinder.getAllTraitsOfPlayer(racPlayer);
		Collection<Trait> traitsHas = racPlayer.getSkillTreeManager().getTraits();
		traitsHas.addAll(toAdd);
		
		Collection<Trait> permanent = getPermanent(allTraits);
		
		//Now generate items and populate!
		for(Trait trait : allTraits){
			boolean isPermanent = permanent.contains(trait);
			int slot = trait.getSkillTreePlace();
			
			//Do not display items that are permanent or below 0.
			if(slot < 0 || isPermanent) continue;
			//Filter items that are outside of the Box.
			if(slot >= getTopInventory().getSize()) continue;
			
			ItemStack item = generateItemForTrait(trait, traitsHas, level, freePoints);
			
			//If already present -> Skip!
			if(getTopInventory().getItem(slot) != null) continue;
			getTopInventory().setItem(slot, item);
			buttons.put(trait, item);
		}
		
		
		getTopInventory().setItem(8, generateFreeItem(freePoints, level));
		getTopInventory().setItem((9*5)+8, applyItem);
	}
	
	
	/**
	 * Generates an Item for the Trait.
	 * @param trait to use.
	 * @param hasPrequisits
	 * @param hasLevel
	 * @return
	 */
	private ItemStack generateItemForTrait(Trait trait, Collection<Trait> traitsHas, int playerLevel, int freePoints){
		//First generate flags:
		boolean hasTrait = traitsHas.contains(trait);
		int traitLevelNeeded = (trait instanceof TraitWithRestrictions) ? ((TraitWithRestrictions)trait).getMinLevel() : -1;
		boolean traitHasLevelReq = traitLevelNeeded > 0;
		boolean playerHasLevel = playerLevel >= traitLevelNeeded;
		if(traitHasLevelReq) ((TraitWithRestrictions)trait).getMinLevel();
		
		boolean hasPoints = freePoints >= trait.getSkillPointCost();
		boolean hasPrequisits = hasPrequisits(trait, traitsHas);
		boolean canUse = hasPoints && hasPrequisits && playerHasLevel;
		String title = (hasTrait ? ChatColor.BLUE : (canUse ? ChatColor.RED : ChatColor.GREEN )) + trait.getDisplayName();
		String levelString = (traitHasLevelReq ? ((playerHasLevel ? ChatColor.GREEN : ChatColor.RED) + "Needs level: " + traitLevelNeeded) : ChatColor.GREEN + "No level required");
		String pointsString = (hasPoints ? ChatColor.GREEN : ChatColor.RED) + "Needs: " + trait.getSkillPointCost() + " Points";
		String prequisitString = trait.getSkillTreePrequisits().isEmpty() ? "" : (ChatColor.YELLOW + "Prequisits: " + generatePrequisitString(trait, traitsHas));
		
		List<String> lore = new LinkedList<>();
		lore.addAll(Arrays.asList(trait.getPrettyConfiguration().split(Pattern.quote("#n"))));
		lore.add("");
		if(hasTrait) lore.add(ChatColor.AQUA + "Already learned");
		else{
			lore.add(pointsString);
			lore.add(levelString);
			if(!prequisitString.isEmpty()) lore.add(prequisitString);
		}
		
		ItemStack item = trait.getSkillTreeSymbol();
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(title);
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}
	
	private String generatePrequisitString(Trait trait, Collection<Trait> traitsHas){
		StringBuilder builder = new StringBuilder();
		for (Iterator<String> iterator = trait.getSkillTreePrequisits().iterator(); iterator.hasNext();) {
			String pre = iterator.next();
			Trait found = null;
			for(Trait has : traitsHas){
				if(has.getDisplayName().equalsIgnoreCase(pre)){
					found = has;
					break;
				}
			}
			
			builder.append(found==null?ChatColor.RED: ChatColor.GREEN);
			builder.append(found==null?pre:found.getDisplayName());
			if(iterator.hasNext()) builder.append(ChatColor.WHITE).append(",");
		}
		
		return builder.toString();
	}
	
	
	private boolean hasPrequisits(Trait trait, Collection<Trait> traitsHas){
		for(String pre : trait.getSkillTreePrequisits()){
			boolean found = false;
			for(Trait has : traitsHas){
				if(has.getDisplayName().equalsIgnoreCase(pre)){
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
	 * Returns all Permanent Traits:
	 * @param traits to use for checking
	 * @return the permanent Traits from the ones passed.
	 */
	private Collection<Trait> getPermanent(Collection<Trait> traits){
		Collection<Trait> permanent = new HashSet<>();
		if(traits == null || traits.isEmpty()) return permanent;
		for(Trait trait : traits){
			if(trait.isPermanentSkill() || trait.getSkillPointCost() < 0){
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
	protected void onControlItemPressed(int slot, ItemStack item) {
		if(item == null || item.getType() == Material.AIR) return;
		
		if(applyItem.isSimilar(item)){
			applyCurrentSettings();
			return;
		}
		
		//Check which trait was clicked. If none -> Not our problem!
		Trait trait = getTraitForItem(item);
		if(trait == null) return;
		
		
		int freePoints = racPlayer.getSkillTreeManager().getFreeSkillpoints();
		for(Trait tr : toAdd) freePoints -= tr.getSkillPointCost();
		
		int level = LevelAPI.getCurrentLevel(racPlayer);
		
		Collection<Trait> traitsHas = racPlayer.getSkillTreeManager().getTraits();
		traitsHas.addAll(toAdd);
		
		//If already has:
		if(traitsHas.contains(trait)){
			player.sendMessage(ChatColor.RED + "You alreadsy have this Trait.");
			return;
		}
		
		//If SkillPoints too low:
		if(freePoints < trait.getSkillPointCost()){
			player.sendMessage(ChatColor.RED + "You do not have enough free Skill-Points.");
			return;
		}
		
		//If not has prequisits:
		if(!hasPrequisits(trait, traitsHas)){
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
		toAdd.add(trait);
		redraw();
	}
	
	/**
	 * Applies + closes.
	 */
	private void applyCurrentSettings() {
		this.close();
		if(toAdd.isEmpty()) return;
		
		PlayerSkillTreeManager manager = racPlayer.getSkillTreeManager();
		for(Trait trait : toAdd) manager.addTrait(trait);
		
		//Rescan after:
		racPlayer.getArrowManager().rescanPlayer();
		racPlayer.getSpellManager().rescan();
		
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
