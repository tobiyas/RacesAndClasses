package de.tobiyas.racesandclasses.standalonegui.data.option.specific;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.tobiyas.racesandclasses.standalonegui.data.option.AbstractTraitConfigOption;
import de.tobiyas.racesandclasses.standalonegui.data.option.OptionType;
import de.tobiyas.racesandclasses.standalonegui.gui.MainFrame;


public class TraitConfigStringOption extends AbstractTraitConfigOption {

	/**
	 * The Current value.
	 */
	private String value = "";
	
	
	public TraitConfigStringOption(String name, boolean optional) {
		super(OptionType.String, name, optional);
	}
	
	public TraitConfigStringOption(String name, boolean optional, String value) {
		this(name, optional);
		
		this.value = value;
	}


	@Override
	public boolean isAcceptable(String value) {
		if(super.isAcceptable(value)) return true;
		
		return true;
	}

	@Override
	public void valueSelected(String value) {
		this.value = value;
	}

	@Override
	public String getCurrentSelection() {
		return value;
	}
	

	@Override
	public String toString() {
		return name + ": " + value;
	}

	@Override
	public void reset() {
		super.reset();
		this.value = "";
	}
	
	
	@Override
	public void addWithConfigOption(JPanel panel) {
		JPanel mainPanel = generateEmptyRightPanel();
		
        final JTextField text = new JTextField(value);
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
		
		panel.add(mainPanel);
	}
	
}
