package de.tobiyas.racesandclasses.standalonegui.data.option.specific;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collections;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.bukkit.block.Biome;

import de.tobiyas.racesandclasses.standalonegui.data.option.AbstractTraitConfigOption;
import de.tobiyas.racesandclasses.standalonegui.data.option.OptionType;
import de.tobiyas.racesandclasses.standalonegui.gui.MainFrame;

public class TraitConfigBiomeOption extends AbstractTraitConfigOption {


	/**
	 * The current Biome.
	 */
	private Biome biome = Biome.BEACH;
	
	

	public TraitConfigBiomeOption(String name, boolean optional) {
		super(OptionType.Biome, name, optional);
		
		this.options.clear();
		for(Biome biome : Biome.values()){
			this.options.add(biome.name());
		}
	}
	
	
	public TraitConfigBiomeOption(String name, boolean optional, Biome biome) {
		this(name, optional);
		
		this.biome = biome;
	}
	
	
	@Override
	public void valueSelected(String value) {
		for(Biome biome : Biome.values()){
			if(biome.name().equalsIgnoreCase(value)) {
				this.biome = biome;
				this.needsSave = true;
				
				break;
			}
		}
	}

	
	@Override
	public String getCurrentSelection() {
		return biome.name();
	}
	
	
	@Override
	public boolean isAcceptable(String value) {
		return super.isAcceptable(value.toUpperCase());
	}
	
	
	@Override
	public String toString() {
		return name + ": " + biome.name();
	}


	@Override
	public void reset() {
		super.reset();
		this.biome = null;
	}
	
	@Override
	public void addWithConfigOption(JPanel panel) {
		JPanel mainPanel = generateEmptyRightPanel();
		Vector<String> biomes = new Vector<String>();
		for(Biome biome : Biome.values()) biomes.add(biome.name());
		
		Collections.sort(biomes);
		JComboBox<String> combo = new JComboBox<String>(biomes);
		if(biomes != null) combo.setSelectedItem(biome.name());
		
		combo.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() != ItemEvent.SELECTED) return;
				if(e.getItem() == null) return;
				
				String item = e.getItem().toString();
				Biome biome = Biome.valueOf(item);
				TraitConfigBiomeOption.this.biome = biome;
				MainFrame.treeUpdated();
			}
		});
		
		mainPanel.add(combo);
		panel.add(mainPanel, BorderLayout.CENTER);
	}
	
}
