package de.tobiyas.racesandclasses.racbuilder.gui.stats;

import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.tobiyas.racesandclasses.racbuilder.gui.BasicSelectionInterface;
import de.tobiyas.racesandclasses.racbuilder.gui.elements.ItemScrollView;

public class IntegerSelectionInterface extends AbstractStatSelectionInterface {

	//Indicators for Values
	protected ItemScrollView indicator1;
	protected ItemScrollView indicator10;
	protected ItemScrollView indicator100;
	protected ItemScrollView indicator1000;
	protected ItemScrollView indicator10000;
	
	/**
	 * The current Value
	 */
	protected double value = 0;
	
	
	
	public IntegerSelectionInterface(Player player,
			BasicSelectionInterface parent, Map<String, Object> config, String key) {
		this(player, parent, "Select an Integer", config, key);
	}

	
	public IntegerSelectionInterface(Player player,
			BasicSelectionInterface parent, String selectName,
			Map<String, Object> config, String key) {
		super(player, parent, selectName, config, key);

		if(config.containsKey(key)){
			if(config.get(key) instanceof Double){
				this.value = (Double) config.get(key);
			}
		}
		
		generateIndicators();
		totalRedraw();
	}
	
	
	/**
	 * Generates an Indicator for all Values.
	 */
	protected void generateIndicators(){
		indicator1 = new ItemScrollView("1s", 4, selectionInventory);
		indicator10 = new ItemScrollView("10s", 3, selectionInventory);
		indicator100 = new ItemScrollView("100s", 2, selectionInventory);
		indicator1000 = new ItemScrollView("1000s", 1, selectionInventory);
		indicator10000 = new ItemScrollView("10000s", 0, selectionInventory);
	}
	
	/**
	 * Forces Redraw of the Inventory
	 */
	protected void totalRedraw(){
		indicator1.redrawInventory();
		indicator10.redrawInventory();
		indicator100.redrawInventory();
		indicator1000.redrawInventory();
		indicator10000.redrawInventory();
	}

	
	@Override
	protected void onSelectionItemPressed(ItemStack item) {
		if(checkAnyReaction(item)){		
			rescanValue();
		}
	}

	
	/**
	 * Indicates if any Item triggers a hit.
	 * 
	 * @param item
	 * @return
	 */
	protected boolean checkAnyReaction(ItemStack item){
		if(indicator1.react(item)) return true;
		if(indicator10.react(item)) return true;
		if(indicator100.react(item)) return true;
		if(indicator1000.react(item)) return true;
		if(indicator10000.react(item)) return true;
		
		return false;
	}
	
	
	@Override
	protected void onControlItemPressed(ItemStack item) {
	}


	/**
	 * Rescans the actual Value to have a correct value
	 */
	protected void rescanValue(){
		int tempValue = 0;
		tempValue += 1 * indicator1.getCurrentValue();
		tempValue += 10 * indicator10.getCurrentValue();
		tempValue += 100 * indicator100.getCurrentValue();
		tempValue += 1000 * indicator1000.getCurrentValue();
		tempValue += 10000 * indicator10000.getCurrentValue();
		
		value = tempValue;
	}
	
	/**
	 * Sets the current value to the value passed
	 * 
	 * @param value
	 */
	protected void setValue(double value){
		this.value = value;
		
		int value_1s = getDigit(value, 1);
		int value_10s = getDigit(value, 2);
		int value_100s = getDigit(value, 3);
		int value_1000s = getDigit(value, 4);
		int value_10000s = getDigit(value, 5);
		
		indicator1.setCurrentValue(value_1s);
		indicator10.setCurrentValue(value_10s);
		indicator100.setCurrentValue(value_100s);
		indicator1000.setCurrentValue(value_1000s);
		indicator10000.setCurrentValue(value_10000s);
	}
	
	
	/**
	 * Returns the digit place of the value passed.
	 * 
	 * Example: getDigit(1234, 3) = 2
	 * Example2: getDigit(1234, 1) = 4
	 * Example3: getDigit(1234, 10) = 0
	 * Example4: getDigit(1234.567, 0) = 5
	 * Example5: getDigit(1234.567, -1) = 6
	 * 
	 * @param value
	 * @param digit
	 * @return
	 */
	protected int getDigit(double value, int digit){
		return (int)((value / Math.pow(10, digit - 1)) % 10);
	}


	@Override
	protected Object unparseValue() {
		return value;
	}
}
