package de.tobiyas.racesandclasses.racbuilder;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.racesandclasses.racbuilder.container.BuildedClassContainer;

public class ClassBuilder extends AbstractHolderBuilder {

	protected String healthOperation;
	protected double healthOperationValue;
	
	/**
	 * Generates the Class Builder with the passed Name.
	 * The name is final and can not be changed.
	 * 
	 * @param name to build with
	 */
	public ClassBuilder(String name) {
		super(name);
		
		this.healthOperation = "+";
		this.healthOperationValue = 0;
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
	 * Sets the value for the operation
	 * 
	 * @param value to set
	 */
	public void setHealthValue(double value){
		this.healthOperationValue = value;
	}
	
	
	/**
	 * Builds the Class with the given Values.
	 * 
	 * @return the build container
	 */
	public ClassContainer build(){
		return new BuildedClassContainer(this.name, this.holderTag, this.armorPermission, this.traitSet, this.permissionList, this.healthOperation, this.healthOperationValue);
	}
}
