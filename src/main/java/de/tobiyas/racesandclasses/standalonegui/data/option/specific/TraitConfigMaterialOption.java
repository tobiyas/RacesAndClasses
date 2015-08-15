package de.tobiyas.racesandclasses.standalonegui.data.option.specific;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collections;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.bukkit.Material;

import de.tobiyas.racesandclasses.standalonegui.data.option.AbstractTraitConfigOption;
import de.tobiyas.racesandclasses.standalonegui.data.option.OptionType;
import de.tobiyas.racesandclasses.standalonegui.gui.MainFrame;

public class TraitConfigMaterialOption extends AbstractTraitConfigOption {

	/**
	 * The Value to set.
	 */
	private Material mat = Material.AIR;
	
	
	public TraitConfigMaterialOption(String name, boolean optional) {
		super(OptionType.Material, name, optional);
		
		this.options.clear();
		for(Material mat : Material.values()){
			this.options.add(mat.name());
		}
	}

	
	public TraitConfigMaterialOption(String name, boolean optional, Material value) {
		this(name, optional);
		
		this.mat = value;
	}
	

	
	@Override
	public void valueSelected(String value) {
		try{
			this.mat = Material.matchMaterial(value.toUpperCase());
			this.needsSave = true;
			this.setCreated(true);
		}catch(Throwable exp){}
	}

	@Override
	public String getCurrentSelection() {
		return mat.name();
	}


	@Override
	public String toString() {
		return name + ": " + mat.name();
	}
	
	@Override
	public void reset() {
		super.reset();
		this.mat = null;
	}
	
	@Override
	public void addWithConfigOption(JPanel panel) {
		JPanel mainPanel = generateEmptyRightPanel();
		
        
		Vector<String> mats = new Vector<String>();
		for(Material mat : Material.values()) mats.add(mat.name());
		
		Collections.sort(mats);
		JComboBox<String> combo = new JComboBox<String>(mats);
		combo.setAlignmentX(0.5f);
		if(mat != null) combo.setSelectedItem(mat.name());
		
		combo.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() != ItemEvent.SELECTED) return;
				if(e.getItem() == null) return;
				
				String item = e.getItem().toString();
				Material mat = Material.valueOf(item);
				TraitConfigMaterialOption.this.mat = mat;
				MainFrame.treeUpdated();
			}
		});
		
		mainPanel.add(combo);
		panel.add(mainPanel, BorderLayout.CENTER);
	}
	
}
