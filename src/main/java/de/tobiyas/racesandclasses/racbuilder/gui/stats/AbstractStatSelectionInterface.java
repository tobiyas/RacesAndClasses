package de.tobiyas.racesandclasses.racbuilder.gui.stats;

import java.util.Map;

import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.racbuilder.gui.BasicSelectionInterface;

public abstract class AbstractStatSelectionInterface extends BasicSelectionInterface {

	/**
	 * The Key of the map to save to
	 */
	protected final String key;
	
	/**
	 * The Map to save into when 
	 */
	protected final Map<String, Object> config;
	
	
	public AbstractStatSelectionInterface(Player player,
			BasicSelectionInterface parent, String selectionInventoryName, 
			Map<String, Object> config, String key) {
		
		super(player, parent, "Controls", selectionInventoryName);
		
		this.key = key;
		this.config = config;
	}


	@Override
	protected boolean onBackPressed() {
		return true;
	}

	@Override
	protected void onAcceptPressed() {
		config.put(key, unparseValue());
		closeAndReturnToParent();
	}
	
	
	/**
	 * Gets the Value returned into the Map
	 * @return
	 */
	protected abstract Object unparseValue();
	

}
