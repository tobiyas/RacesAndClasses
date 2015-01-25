package de.tobiyas.racesandclasses.standalonegui.data.option;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.tobiyas.racesandclasses.standalonegui.data.GuiTrait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.RemoveSuperConfigField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigParser;


public class TraitGuiConfigParser {

	
	/**
	 * Parses a Trait.
	 * 
	 * @param clazz to parse.
	 * 
	 * @return the parsed Trait.
	 */
	public static GuiTrait generateEmptyConfig(Class<? extends de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait> clazz){
		//first read the needed fields and generate own data.
		List<TraitConfigurationField> annotationList = TraitConfigParser.getAllTraitConfigFieldsOfTrait(clazz);
		List<RemoveSuperConfigField> removedFields = TraitConfigParser.getAllTraitRemovedFieldsOfTrait(clazz);
		Iterator<TraitConfigurationField> it = annotationList.iterator();
		
		List<TraitConfigOption> neededOptions = new LinkedList<TraitConfigOption>();
		List<TraitConfigOption> optionalOptions = new LinkedList<TraitConfigOption>();
		Set<String> seen = new HashSet<String>();
		
		while(it.hasNext()){
			TraitConfigurationField field = it.next();
			boolean optional = field.optional();
			String name = field.fieldName();
			
			if(!optional){
				for(RemoveSuperConfigField val : removedFields){
					if(val.name().equalsIgnoreCase(name)) optional = true;
				}
			}
			
			TraitConfigOption option = ConfigOptionFactory.generateOverride(field, optional);
			if(option == null) continue;
			if(seen.contains(name)) continue;
			
			seen.add(name);
			if(optional) optionalOptions.add(option); else neededOptions.add(option);
		}
		
		
		//now we have the data.
		//We still need the name.
		String traitName = "";
		
		try{
			TraitInfos infos = clazz.getMethod("importTrait").getAnnotation(TraitInfos.class);
			if(infos == null) return null;
			
			traitName = infos.traitName();
		}catch(Throwable exp){
			exp.printStackTrace();
			return null;
		}
		
		//now we have the name and the config.
		//let's build it.
		
		GuiTrait trait = new GuiTrait(traitName, neededOptions, optionalOptions);
		return trait;
	}
	
}
