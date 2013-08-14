package de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes;

import org.bukkit.configuration.file.YamlConfiguration;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.exceptions.HolderConfigParseException;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.exceptions.HolderParsingException;
import de.tobiyas.racesandclasses.util.chat.ChatColorUtils;
import de.tobiyas.racesandclasses.util.consts.Consts;
import de.tobiyas.util.config.YAMLConfigExtended;

public class ClassContainer extends AbstractTraitHolder{
	
	protected String classHealthModify;
	private double classHealthModValue;
	
	
	/**
	 * Private constructor to only build via static builder
	 * 
	 * @param config
	 * @param name
	 * @throws HolderParsingException
	 */
	private ClassContainer(YamlConfiguration config, String name) throws HolderParsingException{
		super(config, name);
	}
	
	
	/**
	 * Creates a ClassContainer for Sub-classed classes
	 * 
	 * @param name to create
	 */
	protected ClassContainer(String name){
		super(new YAMLConfigExtended(Consts.classesYML) ,name);
	}
	
	
	@Override
	protected void readConfigSection() throws HolderConfigParseException{
		try{
			if(!config.isConfigurationSection(holderName + ".config")){
				throw new Exception();
			}
			
			this.manaBonus = config.getDouble(holderName + ".config.manabonus", 0);
			this.holderTag = ChatColorUtils.decodeColors(config.getString(holderName + ".config.classtag"));
			this.classHealthModValue = evaluateValue(config.getString(holderName + ".config.health", "+0"));
			
			readArmor();
		}catch(Exception exp){
			throw new HolderConfigParseException();
		}
	}
	
	protected double evaluateValue(String val){
		char firstChar = val.charAt(0);
		
		classHealthModify = "";
		
		if(firstChar == '+')
			classHealthModify = "+";
		
		if(firstChar == '*')
			classHealthModify = "*";
		
		if(firstChar == '-')
			classHealthModify = "-";
		
		if(classHealthModify == "")
			classHealthModify = "*";
		else
			val = val.substring(1, val.length());

		return Double.valueOf(val);
	}
	
	
	@Override
	protected void addSTDTraits(){
		//None needed
	}
	
	/**
	 * loads a RaceContainer from an YamlConfig
	 * 
	 * @param config
	 * @param name
	 * @return the container
	 * @throws HolderParsingException 
	 */
	public static AbstractTraitHolder loadClass(YamlConfiguration config, String name) throws HolderParsingException{
		ClassContainer container = new ClassContainer(config, name);
		return (ClassContainer) container.load();
	}

	
	@Override
	public boolean containsPlayer(String player){
		AbstractTraitHolder container = plugin.getClassManager().getHolderOfPlayer(player);
		if(container == null) return false;
		return container.getName().equals(holderName);
	}
	
	public double modifyToClass(double maxHealth){
		return evaluateClassMod(maxHealth);
	}
	
	private double evaluateClassMod(double maxHealth){
		char firstChar = classHealthModify.charAt(0);
		if(firstChar == '+')
			return maxHealth + classHealthModValue;
		
		if(firstChar == '*')
			return maxHealth * classHealthModValue;
		
		if(firstChar == '-')
			return maxHealth - classHealthModValue;
		
		return maxHealth;
	}


	@Override
	protected String getContainerTypeAsString() {
		return "class";
	}
	
}
