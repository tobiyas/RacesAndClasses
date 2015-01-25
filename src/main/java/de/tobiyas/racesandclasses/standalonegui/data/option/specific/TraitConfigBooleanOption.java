package de.tobiyas.racesandclasses.standalonegui.data.option.specific;

import java.awt.BorderLayout;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.tobiyas.racesandclasses.standalonegui.data.option.AbstractTraitConfigOption;
import de.tobiyas.racesandclasses.standalonegui.data.option.OptionType;
import de.tobiyas.racesandclasses.standalonegui.gui.MainFrame;

public class TraitConfigBooleanOption extends AbstractTraitConfigOption {

	/**
	 * The Value to set.
	 */
	private boolean value = false;
	
	
	public TraitConfigBooleanOption(String name, boolean optional) {
		super(OptionType.Boolean, name, optional);
		
		this.setOptions(new String[]{"true", "false"});
	}

	
	public TraitConfigBooleanOption(String name, boolean optional, boolean value) {
		this(name, optional);
		
		this.value = value;
	}
	

	@Override
	public void valueSelected(String value) {
		try{
			this.value = Boolean.parseBoolean(value);
		}catch(Throwable exp){}
	}

	
	@Override
	public String getCurrentSelection() {
		return Boolean.toString(value);
	}
	
	
	@Override
	public boolean isAcceptable(String value) {
		if(super.isAcceptable(value)) return true;
		
		return value.equals("true") || value.equals("false");
	}
	
	@Override
	public String toString() {
		return name + ": " + value;
	}

	@Override
	public void reset() {
		super.reset();
		this.value = false;
	}

	@Override
	public void addWithConfigOption(JPanel panel) {
		JPanel mainPanel = generateEmptyRightPanel();
		
		JCheckBox check = new JCheckBox(name);
		check.setSelected(value);
		check.setHorizontalAlignment(JCheckBox.CENTER);
		check.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				JCheckBox checkBox = (JCheckBox) e.getSource();
				value = checkBox.isSelected();
				MainFrame.treeUpdated();
			}
		});
		
		mainPanel.add(check);
		panel.add(mainPanel, BorderLayout.CENTER);
	}
	

}
