package de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.loadingerrors;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.exceptions.HolderTraitParseException;

public class TraitHolderLoadingErrorHandler {

	private static final RacesAndClasses plugin = RacesAndClasses.getPlugin();
	
	/**
	 * The List of error messages
	 */
	private final List<String> raceProblemMessages;

	/**
	 * The List of error messages
	 */
	private final List<String> classProblemMessages;
	
	
	/**
	 * 
	 */
	public TraitHolderLoadingErrorHandler() {
		this.raceProblemMessages = new LinkedList<String>();
		this.classProblemMessages = new LinkedList<String>();
	}
	
	
	/**
	 * Rescans all TraitHolders (Races / Classes) for setup Errors.
	 */
	public void rescanErrors(){
		raceProblemMessages.clear();
		classProblemMessages.clear();
		
		rescanRaceErrors();
		rescanClassErrors();
	}
	
	/**
	 * Rescans all Races for loading errors.
	 */
	private void rescanRaceErrors(){
		List<String> holders = plugin.getRaceManager().listAllVisibleHolders();
		for(String holderName : holders){
			AbstractTraitHolder holder = plugin.getRaceManager().getHolderByName(holderName);
			if(holder == null) continue;
			
			List<HolderTraitParseException> messages = holder.getParsingExceptionsHappened();
			if(messages.size() == 0) continue;
			
			for(HolderTraitParseException exp : messages){
				raceProblemMessages.add(exp.getMessage());
			}
		}
	}

	/**
	 *  Rescans all Classes for loading errors.
	 */
	private void rescanClassErrors(){
		List<String> holders = plugin.getClassManager().listAllVisibleHolders();
		for(String holderName : holders){
			AbstractTraitHolder holder = plugin.getClassManager().getHolderByName(holderName);
			if(holder == null) continue;
			
			List<HolderTraitParseException> messages = holder.getParsingExceptionsHappened();
			if(messages.size() == 0) continue;
			
			for(HolderTraitParseException exp : messages){
				classProblemMessages.add(exp.getMessage());
			}
		}
	}
	
	
	/**
	 * Saves the occurred errors to a file passed.
	 * 
	 * @param file
	 */
	public void saveErrorsToFile(File file){
		if(raceProblemMessages.size() <= 0 &&
				classProblemMessages.size() <= 0) return;
		
		if(!file.exists()){
			try{
				file.createNewFile();
			}catch(IOException exp){
				plugin.getDebugLogger().logWarning("Could not create " + file.getAbsolutePath() + " for Startup Error logging.");
				return;
			}
		}
		
		try {
			PrintWriter writer = new PrintWriter(file, "UTF-8");
			writer.println("Errors occured on Startup While reading Races / Classes:");
			
			if(raceProblemMessages.size() > 0){
				writer.println();
				writer.println();				
				writer.println("Race errors:");
				writer.println();
				
				for(String message : raceProblemMessages){
					writer.println(message);
				}
			}

			if(classProblemMessages.size() > 0){
				writer.println();
				writer.println();
				writer.println("Class errors:");
				writer.println();
				
				for(String message : classProblemMessages){
					writer.println(message);
				}				
			}
			
			writer.close();
			
		} catch (Exception exp) {
			plugin.getDebugLogger().logWarning("Could not write to: " + file.getAbsolutePath() + " for Startup Error logging." +
					" Error was: " + exp.getLocalizedMessage());
			
			plugin.getDebugLogger().logStackTrace(exp);
			return;
		}
	}
	
	
	/**
	 * Saves the Setup errors to a predefined File:
	 * <br>plugin.getDataFolder() + File.separator + "HolderStartupErrors.log"
	 * <br>
	 * <br>
	 * Early out when no errors where found.
	 * The File is deleted but not created new.
	 */
	public void saveErrorsToFile(){
		File file = new File(plugin.getDataFolder() + File.separator + "HolderStartupErrors.log");
		if(file.exists()){
			file.delete();
		}
		
		if(this.classProblemMessages.size() == 0 && 
				this.raceProblemMessages.size() == 0) return;
		
		try{
			file.createNewFile();
		}catch(Exception exp){}
		
		saveErrorsToFile(file);
	}
	
	
	/**
	 * Rescans all Errors and saves them.
	 */
	public void rescanAndSave(){
		rescanErrors();
		saveErrorsToFile();
	}
	
	
	/**
	 * Returns true if any Errors where found.
	 * Returns false if no Errors recorded.
	 * <br>
	 * WARNING: false can also indicate that it has not been scanned!
	 * 
	 * @return true if any errors present
	 */
	public boolean hasErrors(){
		return this.classProblemMessages.size() > 0 ||
				this.raceProblemMessages.size() > 0;
	}
	
	/**
	 * Evaluates all Errors and saves them.
	 * 
	 * @return true if any error found, false otherwise.
	 */
	public static boolean evalAndSave(){
		TraitHolderLoadingErrorHandler errorHandler = new TraitHolderLoadingErrorHandler();
		errorHandler.rescanAndSave();
		
		return errorHandler.hasErrors();
	}

}
