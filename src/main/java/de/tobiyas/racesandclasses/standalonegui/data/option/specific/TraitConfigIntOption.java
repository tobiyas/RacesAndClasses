package de.tobiyas.racesandclasses.standalonegui.data.option.specific;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.tobiyas.racesandclasses.standalonegui.data.option.AbstractTraitConfigOption;
import de.tobiyas.racesandclasses.standalonegui.data.option.OptionType;
import de.tobiyas.racesandclasses.standalonegui.gui.MainFrame;


public class TraitConfigIntOption extends AbstractTraitConfigOption {


	/**
	 * The Option for the int.
	 */
	private int intOption = 0;


	
	public TraitConfigIntOption(String name, boolean optional) {
		super(OptionType.Int, name, optional);
	}
	
	
	public TraitConfigIntOption(String name, boolean optional, int option) {
		this(name, optional);
		
		this.intOption = option;
	}	
	
	
	@Override
	public boolean isAcceptable(String value) {
		if(super.isAcceptable(value)) return true;
		
		try{
			int parsed = Integer.parseInt(value);
			return parsed != Integer.MAX_VALUE;
		}catch(Throwable exp){
			return false;
		}
	}
	
	
	@Override
	public void valueSelected(String value) {
		try{
			int parsed = Integer.parseInt(value);
			this.intOption = parsed;
		}catch(Throwable exp){}
	}


	@Override
	public String getCurrentSelection() {
		return Integer.toString(intOption);
	}
	
	@Override
	public String toString() {
		return name + ": " + intOption;
	}
	
	@Override
	public void reset() {
		super.reset();
		this.intOption = 0;
	}


	@Override
	public void addWithConfigOption(JPanel panel) {
	JPanel mainPanel = generateEmptyRightPanel();
		
        final JTextField text = new JTextField(String.valueOf(intOption));
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
