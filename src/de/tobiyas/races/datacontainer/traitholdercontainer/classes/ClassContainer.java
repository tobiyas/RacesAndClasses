package de.tobiyas.races.datacontainer.traitholdercontainer.classes;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.configuration.file.YamlConfiguration;

import de.tobiyas.races.datacontainer.traitcontainer.TraitStore;
import de.tobiyas.races.datacontainer.traitcontainer.traits.Trait;
import de.tobiyas.races.util.items.ItemUtils.ItemQuality;

public class ClassContainer {

	private YamlConfiguration config;
	private String className;
	
	private String classTag;
	private int classHealthModValue;
	private String classHealthModify;
	
	private boolean[] armorUsage;
	private HashSet<Trait> traits;
	
	private static ClassManager manager = ClassManager.getInstance();
	
	private ClassContainer(YamlConfiguration config, String name){
		this.config = config;
		this.className = name;
		
		readConfigSection();
		readTraitSection();	
	}
	
	private void readConfigSection(){
		classTag = decodeColors(config.getString("classes." + className + ".config.classtag"));
		classHealthModValue = evaluateValue(config.getString("classes." + className + ".config.health", "+0"));
		
		readArmor();
	}
	
	private void readArmor(){
		armorUsage = new boolean[]{false, false, false, false, false};
		String armorString = config.getString("classes." + className + ".config.armor", "").toLowerCase();
		if(armorString.contains("leather"))
			armorUsage[0] = true;
		
		if(armorString.contains("iron"))
			armorUsage[1] = true;
		
		if(armorString.contains("gold"))
			armorUsage[2] = true;
		
		if(armorString.contains("diamond"))
			armorUsage[3] = true;
		
		if(armorString.contains("chain"))
			armorUsage[4] = true;
	}
	
	private String decodeColors(String message){
		if(message == null) return "";
		return message.replaceAll("(&([a-f0-9]))", "§$2");
	}
	
	private int evaluateValue(String val){
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

		return Integer.valueOf(val);
	}
	
	private void readTraitSection(){
		traits = new HashSet<Trait>();
		
		Set<String> traitNames = config.getConfigurationSection("classes." + className + ".traits").getKeys(false);
		if(traitNames == null || traitNames.size() == 0) return;
		
		for(String traitName : traitNames){
			Trait trait = TraitStore.buildTraitByName(traitName, this);
			if(trait != null){
				Object value = config.getString("classes." + className + ".traits." + traitName);
				trait.setValue(value);
				traits.add(trait);
			}
		}
		
		addSTDTraits();
	}
	
	private void addSTDTraits(){
		//None needed
	}
	
	/**
	 * loads a RaceContainer from an YamlConfig
	 * 
	 * @param config
	 * @param name
	 * @return the container
	 */
	public static ClassContainer loadClass(YamlConfiguration config, String name){
		ClassContainer container = new ClassContainer(config, name);
		return container;
	}
	
	public boolean containsPlayer(String player){
		ClassContainer container = manager.getClassOfPlayer(player);
		if(container == null) return false;
		return container.getName().equals(className);
	}
	
	public String getName(){
		return className;
	}
	
	public String getTag(){
		return classTag;
	}
	
	public Set<Trait> getTraits(){
		return traits;
	}
	
	public Set<Trait> getVisibleTraits(){
		Set<Trait> traitSet = new HashSet<Trait>();
		for(Trait trait : traits)
			if(trait.isVisible())
				traitSet.add(trait);
		
		return traitSet;
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
	
	public String getArmorString(){
		HashSet<ItemQuality> qualities = getArmorPerms();
		String armorString = "";
		for(ItemQuality quality : qualities)
			armorString += quality.name() + " ";
		
		return armorString;
	}

	public HashSet<ItemQuality> getArmorPerms(){
		HashSet<ItemQuality> perms = new HashSet<ItemQuality>();
		if(armorUsage[0])
			perms.add(ItemQuality.Leather);
		if(armorUsage[1])
			perms.add(ItemQuality.Iron);
		if(armorUsage[2])
			perms.add(ItemQuality.Gold);
		if(armorUsage[3])
			perms.add(ItemQuality.Diamond);
		if(armorUsage[4])
			perms.add(ItemQuality.Chain);
		
		return perms;
	}
	
	@Override
	public String toString(){
		return className;
	}
	
}
