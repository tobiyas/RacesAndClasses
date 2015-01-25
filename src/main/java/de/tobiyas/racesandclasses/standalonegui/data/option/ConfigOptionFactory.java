package de.tobiyas.racesandclasses.standalonegui.data.option;


import org.bukkit.Material;
import org.bukkit.block.Biome;

import de.tobiyas.racesandclasses.standalonegui.data.option.specific.TraitConfigBiomeOption;
import de.tobiyas.racesandclasses.standalonegui.data.option.specific.TraitConfigBooleanOption;
import de.tobiyas.racesandclasses.standalonegui.data.option.specific.TraitConfigDoubleOption;
import de.tobiyas.racesandclasses.standalonegui.data.option.specific.TraitConfigIntOption;
import de.tobiyas.racesandclasses.standalonegui.data.option.specific.TraitConfigMaterialOption;
import de.tobiyas.racesandclasses.standalonegui.data.option.specific.TraitConfigOperatorOption;
import de.tobiyas.racesandclasses.standalonegui.data.option.specific.TraitConfigStringOption;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.traits.passive.Operator;


public class ConfigOptionFactory {

	
	/**
	 * Generates a TraitConfigOption from the passed TraitConfigurationField.
	 * 
	 * @param field to parse.
	 * 
	 * @return the parsed Field.
	 */
	public static TraitConfigOption generate(TraitConfigurationField field){
		if(field == null) return null;
		
		boolean optional = field.optional();
		String name = field.fieldName();
		Class<?> clazz = field.classToExpect();
		
		if(clazz == Integer.class) return new TraitConfigIntOption(name, optional);
		if(clazz == Double.class) return new TraitConfigDoubleOption(name, optional);
		if(clazz == String.class) return new TraitConfigStringOption(name, optional);
		if(clazz == Boolean.class) return new TraitConfigBooleanOption(name, optional);
		if(clazz == Biome.class) return new TraitConfigBiomeOption(name, optional);
		if(clazz == Material.class) return new TraitConfigMaterialOption(name, optional);
		if(clazz == Operator.class) return new TraitConfigOperatorOption(name, optional);
		
		System.out.println("Could not serialize class: " + clazz.getCanonicalName());
		return null;
	}
	
	
	/**
	 * Generates a TraitConfigOption from the passed TraitConfigurationField.
	 * 
	 * @param field to parse.
	 * 
	 * @return the parsed Field.
	 */
	public static TraitConfigOption generateOverride(TraitConfigurationField field, boolean overrideOptional){
		if(field == null) return null;
		
		String name = field.fieldName();
		Class<?> clazz = field.classToExpect();
		
		if(clazz == Integer.class) return new TraitConfigIntOption(name, overrideOptional);
		if(clazz == Double.class) return new TraitConfigDoubleOption(name, overrideOptional);
		if(clazz == String.class) return new TraitConfigStringOption(name, overrideOptional);
		if(clazz == Boolean.class) return new TraitConfigBooleanOption(name, overrideOptional);
		if(clazz == Biome.class) return new TraitConfigBiomeOption(name, overrideOptional);
		if(clazz == Material.class) return new TraitConfigMaterialOption(name, overrideOptional);
		
		return null;
	}
	
	
	/**
	 * Generates a Default Boolean value.
	 * 
	 * @param field to generate from
	 * @param defaultValue to set.
	 * 
	 * @return the generated Field.
	 */
	public static TraitConfigOption generate(TraitConfigurationField field, boolean defaultValue){
		return new TraitConfigBooleanOption(field.fieldName(), field.optional(), defaultValue);
	}
	
	
	/**
	 * Generates a Default Int value.
	 * 
	 * @param field to generate from
	 * @param defaultValue to set.
	 * 
	 * @return the generated Field.
	 */
	public static TraitConfigOption generate(TraitConfigurationField field, int defaultValue){
		return new TraitConfigIntOption(field.fieldName(), field.optional(), defaultValue);
	}
	
	
	/**
	 * Generates a Default Double value.
	 * 
	 * @param field to generate from
	 * @param defaultValue to set.
	 * 
	 * @return the generated Field.
	 */
	public static TraitConfigOption generate(TraitConfigurationField field, double defaultValue){
		return new TraitConfigDoubleOption(field.fieldName(), field.optional(), defaultValue);
	}
	
	
	/**
	 * Generates a Default String value.
	 * 
	 * @param field to generate from
	 * @param defaultValue to set.
	 * 
	 * @return the generated Field.
	 */
	public static TraitConfigOption generate(TraitConfigurationField field, String defaultValue){
		return new TraitConfigStringOption(field.fieldName(), field.optional(), defaultValue);
	}
	
	
	/**
	 * Generates a Default Material value.
	 * 
	 * @param field to generate from
	 * @param defaultValue to set.
	 * 
	 * @return the generated Field.
	 */
	public static TraitConfigOption generate(TraitConfigurationField field, Material defaultValue){
		return new TraitConfigMaterialOption(field.fieldName(), field.optional(), defaultValue);
	}
	
	
	/**
	 * Generates a Default Biome value.
	 * 
	 * @param field to generate from
	 * @param defaultValue to set.
	 * 
	 * @return the generated Field.
	 */
	public static TraitConfigOption generate(TraitConfigurationField field, Biome defaultValue){
		return new TraitConfigBiomeOption(field.fieldName(), field.optional(), defaultValue);
	}
	
	
}
