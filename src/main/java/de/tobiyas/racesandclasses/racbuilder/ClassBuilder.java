package de.tobiyas.racesandclasses.racbuilder;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.racesandclasses.racbuilder.container.BuildedClassContainer;
import de.tobiyas.racesandclasses.util.persistence.YAMLPersistenceProvider;
import de.tobiyas.util.config.YAMLConfigExtended;

public class ClassBuilder extends AbstractHolderBuilder {

	/**
	 * The Operator for Class Health modification
	 */
	protected String healthOperation;
	
	/**
	 * Generates the Class Builder with the passed Name.
	 * The name is final and can not be changed.
	 * 
	 * @param name to build with
	 */
	public ClassBuilder(String name) {
		super(name);
		
		this.healthOperation = "+";
		this.health = 0;
	}

	
	/**
	 * Builds the ClassBuilder from the passed class
	 * 
	 * @param classContainer
	 */
	public ClassBuilder(ClassContainer classContainer) {
		super(classContainer);
		
		this.healthOperation = classContainer.getClassHealthModify();
		this.health = classContainer.getClassHealthModValue();
	}


	/**
	 * Sets the operation of the health modification.
	 * Allowed: '+', '*', '-'.
	 * 
	 * @param healthOperation to set
	 * @return true if worked, false if Operation not found.
	 */
	public boolean setHealthOperation(String healthOperation){
		if(healthOperation == null){
			return false;
		}
		
		if(healthOperation.equalsIgnoreCase("*")){
			this.healthOperation = healthOperation;
			return true;
		}
		
		if(healthOperation.equalsIgnoreCase("-")){
			this.healthOperation = healthOperation;
			return true;
		}
		
		if(healthOperation.equalsIgnoreCase("+")){
			this.healthOperation = healthOperation;
			return true;
		}
		
		return false;
	}
	
	
	
	/**
	 * Builds the Class with the given Values.
	 * 
	 * @return the build container
	 */
	public ClassContainer build(){
		return new BuildedClassContainer(this.name, this.holderTag, this.armorPermission, this.traitSet, this.permissionList, this.healthOperation, this.health);
	}


	@Override
	protected YAMLConfigExtended getHolderYAMLFile() {
		return YAMLPersistenceProvider.getLoadedClassesFile(true);
	}


	@Override
	protected void saveFurtherToFile(YAMLConfigExtended config) {
		config.set(name + ".config.classtag", holderTag);
		config.set(name + ".config.health", healthOperation + health);
	}


	public String getHealthOperation() {
		return healthOperation;
	}
	
}
