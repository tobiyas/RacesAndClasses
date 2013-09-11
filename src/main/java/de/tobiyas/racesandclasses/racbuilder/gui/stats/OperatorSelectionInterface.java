package de.tobiyas.racesandclasses.racbuilder.gui.stats;

import java.util.LinkedList;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.racbuilder.gui.BasicSelectionInterface;

public class OperatorSelectionInterface extends AbstractStatSelectionInterface {

	/**
	 * indicator for {@link Operators#PLUS}
	 */
	protected ItemStack plusIndicator;

	/**
	 * indicator for {@link Operators#MINUS}
	 */
	protected ItemStack minusIndicator;
	
	/**
	 * indicator for {@link Operators#MULT}
	 */
	protected ItemStack multIndicator;
	
	/**
	 * indicator for the current selected Indicator
	 */
	protected ItemStack currentOperationIndicator;
	
	/**
	 * current Operation
	 */
	protected Operators currentOperator;
	
	
	/**
	 * Creates an Interface for selecting the Operator
	 * 
	 * @param player
	 * @param parent
	 * @param configMap
	 * @param key
	 */
	public OperatorSelectionInterface(Player player,
			BasicSelectionInterface parent, Map<String, Object> config, String key) {
		super(player, parent, "Select a Operation (+,-,*)", config, key);
		
		
		generateIndicators();
	}
	
	
	protected void generateIndicators(){
		plusIndicator = generateItem(Material.WOOL, (short) Operators.PLUS.getWoolColor(), ChatColor.GREEN + "PLUS", new LinkedList<String>());
		minusIndicator = generateItem(Material.WOOL, (short) Operators.MINUS.getWoolColor(), ChatColor.RED + "MINUS", new LinkedList<String>());
		multIndicator = generateItem(Material.WOOL, (short) Operators.MULT.getWoolColor(), ChatColor.YELLOW + "MULT", new LinkedList<String>());

		currentOperator = Operators.MULT;
		currentOperationIndicator = generateItem(Material.WOOL, (short) Operators.MULT.getWoolColor(), ChatColor.GREEN + "PLUS", new LinkedList<String>());
	
		selectionInventory.setItem(3, plusIndicator);
		selectionInventory.setItem(4, minusIndicator);
		selectionInventory.setItem(5, multIndicator);
		
		controlInventory.setItem(4, currentOperationIndicator);
	}
	

	@Override
	protected boolean onBackPressed() {
		return true;
	}


	@Override
	protected void onSelectionItemPressed(ItemStack item) {
		if(plusIndicator.equals(item)){
			changeOperator(Operators.PLUS);
			return;
		}

		if(minusIndicator.equals(item)){
			changeOperator(Operators.MINUS);
			return;
		}
		
		if(multIndicator.equals(item)){
			changeOperator(Operators.MULT);
			return;
		}
	}
	
	
	/**
	 * Changes the Operator to a new Operator
	 * 
	 * @param newOperator
	 */
	private void changeOperator(Operators newOperator){
		if(currentOperator == newOperator) return;
		currentOperator = newOperator;
		
		controlInventory.setItem(4, null);
		currentOperationIndicator.setDurability(currentOperator.getWoolColor());
		controlInventory.setItem(4, currentOperationIndicator);
	}

	@Override
	protected void onControlItemPressed(ItemStack item) {
		if(currentOperationIndicator.equals(item)){
			//current Operator Item is not interesting.
			return;
		}
	}


	@Override
	protected Object unparseValue() {
		return currentOperator.getCharValue();
	}

}
