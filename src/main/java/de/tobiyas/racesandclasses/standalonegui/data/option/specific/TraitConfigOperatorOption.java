package de.tobiyas.racesandclasses.standalonegui.data.option.specific;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collections;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import de.tobiyas.racesandclasses.standalonegui.data.option.AbstractTraitConfigOption;
import de.tobiyas.racesandclasses.standalonegui.data.option.OptionType;
import de.tobiyas.racesandclasses.standalonegui.gui.MainFrame;

public class TraitConfigOperatorOption extends AbstractTraitConfigOption {

	/**
	 * The Value to set.
	 */
	private String value = "*";
	
	
	
	public TraitConfigOperatorOption(String name, boolean optoinal) {
		super(OptionType.Operator, name, optoinal);
		
		this.setOptions(new String[]{"+", "-", "*", "/"});
	}
	
	
	@Override
	public void valueSelected(String value) {
		if(isAcceptable(value)) this.value = value;
	}

	@Override
	public String getCurrentSelection() {
		return value;
	}

	@Override
	public void addWithConfigOption(JPanel panel) {
		JPanel mainPanel = generateEmptyRightPanel();
		
        
		Vector<String> selections = new Vector<String>();
		selections.add("+");
		selections.add("-");
		selections.add("*");
		selections.add("/");
		
		Collections.sort(selections);
		JComboBox<String> combo = new JComboBox<String>(selections);
		combo.setAlignmentX(0.5f);
		if(value != null) combo.setSelectedItem(value);
		
		combo.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() != ItemEvent.SELECTED) return;
				if(e.getItem() == null) return;
				
				String item = e.getItem().toString();
				valueSelected(item);
				MainFrame.treeUpdated();
			}
		});
		
		mainPanel.add(combo);
		panel.add(mainPanel, BorderLayout.CENTER);
	}

}
