package de.tobiyas.racesandclasses.standalonegui.data.option.specific;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.tobiyas.racesandclasses.standalonegui.data.option.AbstractTraitConfigOption;
import de.tobiyas.racesandclasses.standalonegui.data.option.OptionType;
import de.tobiyas.racesandclasses.standalonegui.gui.MainFrame;

public class TraitConfigDoubleOption extends AbstractTraitConfigOption {


	/**
	 * The Current value.
	 */
	private double value = 0;

	

	public TraitConfigDoubleOption(String name, boolean optional) {
		super(OptionType.Double, name, optional);
	}
	

	public TraitConfigDoubleOption(String name, boolean optional, double value) {
		this(name, optional);
		
		this.value = value;
	}
	
	
	@Override
	public void valueSelected(String value) {
		try{
			this.value = Double.parseDouble(value);
			this.needsSave = true;
		}catch(Throwable exp){}
	}

	
	@Override
	public String getCurrentSelection() {
		return Double.toString(value);
	}
	
	
	@Override
	public boolean isAcceptable(String value) {
		if(super.isAcceptable(value)) return true;
		
		try{
			Double.parseDouble(value);
			return true;
		}catch(Throwable exp){
			return false;
		}
	}

	
	@Override
	public String toString() {
		return name + ": " + value;
	}
	
	@Override
	public void reset() {
		super.reset();
		this.value = 0;
	}
	
	@Override
	public void addWithConfigOption(JPanel panel) {
		JPanel mainPanel = generateEmptyRightPanel();
		
        final JTextField text = new JTextField(String.valueOf(value));
        text.setToolTipText("Only numbers!");
        text.setHorizontalAlignment(JTextField.CENTER);
        
        
        mainPanel.add(text);
		
		text.getDocument().addDocumentListener(new DocumentListener() {
			  public void changedUpdate(DocumentEvent e) {
				  String newValue = text.getText();
				  valueSelected(newValue);
				  MainFrame.treeUpdated();
			  }
			  
			  public void removeUpdate(DocumentEvent e) {
				  String newValue = text.getText();
				  valueSelected(newValue);
				  MainFrame.treeUpdated();
			  }
			  
			  public void insertUpdate(DocumentEvent e) {
				  String newValue = text.getText();
				  valueSelected(newValue);
				  MainFrame.treeUpdated();
			  }

			});
		
		panel.add(mainPanel, BorderLayout.CENTER);
	}
	

}
