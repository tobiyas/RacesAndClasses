package de.tobiyas.racesandclasses.standalonegui.data.option;

import java.awt.Font;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;


public abstract class AbstractTraitConfigOption implements TraitConfigOption {


	/**
	 * If the Element needs Save.
	 */
	protected boolean needsSave = false;
	
	
	/**
	 * The Name of the option.
	 */
	protected final String name;
	
	/**
	 * The Type this is.
	 */
	protected final OptionType optionType;
	
	/**
	 * The options to choose.
	 */
	protected final Set<String> options = new HashSet<String>();
	
	/**
	 * If the field is optional.
	 */
	protected final boolean optional;
	
	/**
	 * If the Value is created.
	 */
	protected boolean created = false;
	
	
	public AbstractTraitConfigOption(OptionType type, String name, boolean optional) {
		this.optionType = type;
		this.name = name;
		this.optional = optional;
	}
	
	
	@Override
	public boolean isOptional() {
		return optional;
	}
	
	
	@Override
	public boolean isCreated(){
		return created;
	}
	
	@Override
	public void setCreated(boolean created){
		this.created = created;
	}
	
	
	
	@Override
	public String getName() {
		return name;
	}

	
	@Override
	public OptionType getOptionType() {
		return optionType;
	}
	
	
	@Override
	public Set<String> options() {
		return options;
	}
	
	
	@Override
	public TraitConfigOption setOptions(Set<String> options) {
		this.options.clear();
		this.options.addAll(options);
		
		return this;
	}
	
	@Override
	public TraitConfigOption setOptions(String... options) {
		this.options.clear();
		this.options.addAll(Arrays.asList(options));
		
		return this;
	}
	
	
	@Override
	public boolean isAcceptable(String value) {
		for(String accepted : options){
			if(value.equalsIgnoreCase(accepted)) return true;
		}
		
		return false;
	}
	
	
	/**
	 * Adds idividual Panels to the Panel.
	 * 
	 * @param panel to add to 
	 */
	public abstract void addWithConfigOption(JPanel panel);

	
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof AbstractTraitConfigOption)) return false;
		
		return name.equals(((AbstractTraitConfigOption)other).name);
	}
	
	
	/**
	 * Generates an empty pane with only the Header.
	 * @return
	 */
	protected JPanel generateEmptyRightPanel(){
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(10,1));
		
		JLabel header = new JLabel("Option: " + name);
		header.setHorizontalAlignment(JLabel.CENTER);
		header.setFont(new Font(header.getFont().getFontName(), Font.BOLD, header.getFont().getSize()));
		
		mainPanel.add(header);
		return mainPanel;
	}
	
	
	@Override
	public void reset() {
		this.created = false;
	}

	
	@Override
	public int compareTo(TraitConfigOption other) {
		return name.compareTo(other.getName());
	}
	
	
	@Override
	public boolean needsSave() {
		return needsSave;
	}
	
	@Override
	public void notifySaved() {
		this.needsSave = false;
	}
	
}
