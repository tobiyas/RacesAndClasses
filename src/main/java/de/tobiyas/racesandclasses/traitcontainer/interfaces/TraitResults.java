/*******************************************************************************
 * Copyright 2014 Tobias Welther
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.tobiyas.racesandclasses.traitcontainer.interfaces;

public class TraitResults {

	/**
	 * A quick false response.
	 * 
	 * @return a quick false responce.
	 */
	public static TraitResults False(){
		return new TraitResults(false);
	}
	
	
	/**
	 * A quick true response.
	 * 
	 * @return a quick true responce.
	 */
	public static TraitResults True(){
		return new TraitResults(true);
	}
	
	
	/**
	 * if the trait has triggered.
	 */
	private boolean triggered = false;
	
	/**
	 * Sets the cooldwon on a positive Trigger.
	 */
	private boolean setCooldownOnPositiveTrigger = true;
	
	/**
	 * Removes the costs if the Triggering was successfull.
	 */
	private boolean removeCostsAfterTrigger = true;
	
	
	public TraitResults() {
	}
	
	/**
	 * Forces the Triggering result.
	 * 
	 * @param force
	 */
	public TraitResults(boolean force){
		this.triggered = force;
	}

	
	public boolean isTriggered() {
		return triggered;
	}

	public TraitResults setTriggered(boolean triggered) {
		this.triggered = triggered;
		return this;
	}

	public boolean isSetCooldownOnPositiveTrigger() {
		return setCooldownOnPositiveTrigger;
	}

	public TraitResults setSetCooldownOnPositiveTrigger(boolean setCooldownOnPositiveTrigger) {
		this.setCooldownOnPositiveTrigger = setCooldownOnPositiveTrigger;
		return this;
	}

	public boolean isRemoveCostsAfterTrigger() {
		return removeCostsAfterTrigger;
	}

	public TraitResults setRemoveCostsAfterTrigger(boolean removeCostsAfterTrigger) {
		this.removeCostsAfterTrigger = removeCostsAfterTrigger;
		return this;
	}

	
	/**
	 * Copies the result of another TraitResults
	 * 
	 * @param otherResults to copy from
	 */
	public void copyFrom(TraitResults otherResults) {
		this.removeCostsAfterTrigger = otherResults.removeCostsAfterTrigger;
		this.setCooldownOnPositiveTrigger = otherResults.setCooldownOnPositiveTrigger;
		this.triggered = otherResults.triggered;
	}
	
	
}
